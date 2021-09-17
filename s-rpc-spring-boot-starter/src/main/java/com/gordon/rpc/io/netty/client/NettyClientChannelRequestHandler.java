package com.gordon.rpc.io.netty.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;
import com.gordon.rpc.exception.SRpcException;
import com.gordon.rpc.io.client.ClientRequestHandler;
import com.gordon.rpc.io.client.ClientServiceAddressHandlerCache;
import com.gordon.rpc.io.protocol.ProtocolConstant;
import com.gordon.rpc.io.protocol.ProtocolMsg;
import com.gordon.rpc.io.serializer.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyClientChannelRequestHandler extends ChannelInboundHandlerAdapter implements ClientRequestHandler {

    /**
     * 等待通道建立最大时间
     */
    static final int CHANNEL_WAIT_TIME = 8;
    /**
     * 等待响应最大时间
     */
    static final int RESPONSE_WAIT_TIME = 8;

    private volatile Channel channel;

    private volatile  ChannelHandlerContext ctx;

    private String remoteAddr;

    private static Map<String, InvokeFuture<SRpcResponse>> requestMap = new ConcurrentHashMap<>();

    private Serializer serializer;

    private CountDownLatch latch = new CountDownLatch(1);

    public NettyClientChannelRequestHandler(Serializer serializer, String remoteAddr) {
        this.serializer = serializer;
        this.remoteAddr = remoteAddr;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        this.channel = ctx.channel();
        this.ctx = ctx;
        latch.countDown();
        log.info("netty channel active :{}", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("received message:{}", msg);
        ProtocolMsg resMsg = (ProtocolMsg)msg;
        SRpcResponse response = serializer.deserialize(resMsg.getContent(), SRpcResponse.class);
        InvokeFuture<SRpcResponse> future = requestMap.get(response.getRequestId());
        future.setResponse(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("exists  exception :{}", cause.getMessage());
        ClientServiceAddressHandlerCache.remove(remoteAddr);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.error("channel inactive remoteAddr:{}", remoteAddr);
        ClientServiceAddressHandlerCache.remove(remoteAddr);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public SRpcResponse send(SRpcRequest request) {
        SRpcResponse response;
        InvokeFuture<SRpcResponse> future = new InvokeFuture<>();
        requestMap.put(request.getRequestId(), future);
        try {
            byte[] data = serializer.serialize(request);
            //byte[] reqData = DelimiterUtils.encodeDelimiterData(data);
            //ByteBuf reqBuf = Unpooled.buffer(reqData.length);
            //reqBuf.writeBytes(reqData);
            if (latch.await(CHANNEL_WAIT_TIME, TimeUnit.SECONDS)){
                ProtocolMsg msg = new ProtocolMsg();
                msg.setContent(data);
                msg.setMsgType(ProtocolConstant.REQ_TYPE);
                ctx.writeAndFlush(msg);
                // 等待响应
                response = future.get(RESPONSE_WAIT_TIME, TimeUnit.SECONDS);
                if (response == null) {
                    throw new SRpcException(request.getServiceId() + " request time out");
                }
            }else {
                ClientServiceAddressHandlerCache.remove(remoteAddr);
                try {
                    ctx.close();
                } catch (Exception e) {
                    log.error("close channel exception", e);
                }
                throw new SRpcException("establish channel time out");
            }
        } catch (Exception e) {
            throw new SRpcException(e.getMessage());
        } finally {
            requestMap.remove(request.getRequestId());
        }
        return response;
    }

}
