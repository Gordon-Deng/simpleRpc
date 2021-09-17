package com.gordon.rpc.io.netty.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.gordon.rpc.io.client.ClientRequestHandler;
import com.gordon.rpc.io.client.ClientServiceAddressHandlerCache;
import com.gordon.rpc.io.client.NetClient;
import com.gordon.rpc.io.protocol.codec.SProtocolEncoder;
import com.gordon.rpc.io.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyNetClient implements NetClient {

    private static ExecutorService threadPool = new ThreadPoolExecutor(4, 10, 200,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
        .setNameFormat("rpcClient-%d")
        .build());

    private EventLoopGroup clientGroup = new NioEventLoopGroup(4);


    @Override
    public ClientRequestHandler connect(String address, Serializer serializer) {
        String[] addrInfo = address.split(":");
        String serverIp = addrInfo[0];
        String serverPort = addrInfo[1];
        NettyClientChannelRequestHandler handler = new NettyClientChannelRequestHandler(serializer, address);
        threadPool.submit(() -> {
            Bootstrap b = new Bootstrap();
            b.group(clientGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //pipeline.addLast(new DelimiterBasedFrameDecoder(2048, DelimiterUtils.getDelimiteByteBuf()));
                        pipeline.addLast(new SProtocolEncoder());
                        pipeline.addLast(new SProtocolEncoder());

                        pipeline.addLast(handler);
                    }
                });
            ChannelFuture channelFuture = b.connect(serverIp, Integer.parseInt(serverPort));
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    ClientServiceAddressHandlerCache.put(address, handler);
                }
            });
        });
        return handler;
    }

}
