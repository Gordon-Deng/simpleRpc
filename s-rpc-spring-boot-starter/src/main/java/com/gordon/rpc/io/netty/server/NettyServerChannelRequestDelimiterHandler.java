package com.gordon.rpc.io.netty.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.gordon.rpc.domain.RpcStatusEnum;
import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;
import com.gordon.rpc.io.serializer.Serializer;
import com.gordon.rpc.io.server.ServerServiceInvocation;
import com.gordon.rpc.util.DelimiterUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServerChannelRequestDelimiterHandler extends ChannelInboundHandlerAdapter {

    private static final ExecutorService executor = new ThreadPoolExecutor(4, 8,
        100, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(10000),
        new ThreadFactoryBuilder().setNameFormat("orcRpcServer-%d").build());

    private ServerServiceInvocation serverServiceInvocation;

    private Serializer serializer;

    public NettyServerChannelRequestDelimiterHandler(ServerServiceInvocation serverServiceInvocation, Serializer serializer) {
        this.serverServiceInvocation = serverServiceInvocation;
        this.serializer = serializer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("netty channel active :{}", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.submit(()->{
            try {
                log.debug("the server receives message :{}", msg);
                ByteBuf byteBuf = (ByteBuf) msg;
                // 消息写入reqData字节数组
                byte[] reqData = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(reqData);
                // 手动回收buf空间
                ReferenceCountUtil.release(byteBuf);

                // 请求反序列化
                SRpcRequest req = this.serializer.deserialize(reqData, SRpcRequest.class);
                SRpcResponse response = serverServiceInvocation.handleRequest(req);

                byte[] resData = null;
                try {
                    // 返回序列化
                    resData = this.serializer.serialize(response);
                } catch (Exception e) {
                    log.error("serialize error", e);
                    SRpcResponse errRes = new SRpcResponse(RpcStatusEnum.ERROR);
                    errRes.setRequestId(req.getRequestId());
                    errRes.setException(e);
                    resData = this.serializer.serialize(errRes);
                }
                // 消息结尾增加delimiter分隔符
                byte[] respData2 = DelimiterUtils.encodeDelimiterData(resData);
                ByteBuf respBuf = Unpooled.buffer(respData2.length);
                respBuf.writeBytes(respData2);
                ctx.writeAndFlush(respBuf);
            }catch (Exception e){
                log.error("server process request error", e);
            }
        });
    }
}
