package com.gordon.rpc.io.netty.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.orc.rpc.domain.OrcRpcRequest;
import com.orc.rpc.domain.OrcRpcResponse;
import com.orc.rpc.domain.RpcStatusEnum;
import com.orc.rpc.io.protocol.ProtocolConstant;
import com.orc.rpc.io.protocol.ProtocolMsg;
import com.orc.rpc.io.serializer.Serializer;
import com.orc.rpc.io.server.ServerServiceInvocation;
import com.orc.rpc.util.DelimiterUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luxing.hss
 * @date 2021/2/19
 **/
@Slf4j
public class NettyServerChannelRequestHandler extends ChannelInboundHandlerAdapter {

    private static final ExecutorService executor = new ThreadPoolExecutor(4, 8,
        100, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(10000),
        new ThreadFactoryBuilder().setNameFormat("orcRpcServer-%d").build());

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
                OrcRpcRequest req = this.serializer.deserialize(reqData, OrcRpcRequest.class);
                OrcRpcResponse response = serverServiceInvocation.handleRequest(req);

                byte[] resData = null;
                try {
                    // 返回序列化
                    resData = this.serializer.serialize(response);
                } catch (Exception e) {
                    log.error("serialize error", e);
                    OrcRpcResponse errRes = new OrcRpcResponse(RpcStatusEnum.ERROR);
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