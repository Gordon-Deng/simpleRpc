package com.gordon.rpc.io.netty.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.gordon.rpc.domain.RpcStatusEnum;
import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;
import com.gordon.rpc.io.protocol.ProtocolConstant;
import com.gordon.rpc.io.protocol.ProtocolMsg;
import com.gordon.rpc.io.serializer.Serializer;
import com.gordon.rpc.io.server.ServerServiceInvocation;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyServerChannelRequestHandler extends ChannelInboundHandlerAdapter {

    private static final ExecutorService executor = new ThreadPoolExecutor(4, 8,
        100, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(10000),
        new ThreadFactoryBuilder().setNameFormat("sRpcServer-%d").build());

    private ServerServiceInvocation serverServiceInvocation;

    private Serializer serializer;

    public NettyServerChannelRequestHandler(ServerServiceInvocation serverServiceInvocation, Serializer serializer) {
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
                ProtocolMsg reqMsg = (ProtocolMsg)msg;
                byte[] reqData = reqMsg.getContent();
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

                ProtocolMsg resMsg = new ProtocolMsg();
                resMsg.setMsgType(ProtocolConstant.RES_TYPE);
                resMsg.setContent(resData);

                ctx.writeAndFlush(resMsg);
            }catch (Exception e){
                log.error("server process request error", e);
            }
        });
    }
}
