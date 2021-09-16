package com.gordon.rpc.io.netty.server;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.Channel;

@Slf4j
public class NettyNetServer implements NetServer {

    private Channel channel;
    /**
     * 服务端口
     */
    private int serverPort;
    /**
     * 服务端序列化方式
     */
    private String serializer;
    /**
     * 请求处理者
     */
    private ServerServiceInvocation requestProcessor;

    public NettyNetServer(int serverPort, String serializer) {
       this.serverPort = serverPort;
       this.serializer = serializer;
       init();
    }

    /**
     * 初始化请求处理器
     *
     * @param
     * @return void
     */
    private void init() {
        this.requestProcessor = new DefaultServerServiceInvocation();
    }


    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //pipeline.addLast(new DelimiterBasedFrameDecoder(2048, DelimiterUtils.getDelimiteByteBuf()));
                        pipeline.addLast(new OrcProtocolEncoder());
                        pipeline.addLast(new OrcProtocolDecoder());
                        pipeline.addLast(new NettyServerChannelRequestHandler(requestProcessor, SpiLoaderUtils.getSerializer(serializer)));
                    }
                });
            // 启动netty服务
            ChannelFuture future = b.bind(serverPort).sync();
            log.info("netty server started successfully.");
            channel = future.channel();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty sever started failed, error msg", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        this.channel.close();
    }

}
