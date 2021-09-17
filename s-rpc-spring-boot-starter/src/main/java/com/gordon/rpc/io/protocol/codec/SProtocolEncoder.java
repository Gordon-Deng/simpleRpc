package com.gordon.rpc.io.protocol.codec;

import com.gordon.rpc.io.protocol.ProtocolConstant;
import com.gordon.rpc.io.protocol.ProtocolMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SProtocolEncoder extends MessageToByteEncoder<ProtocolMsg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtocolMsg protocolMsg, ByteBuf byteBuf)
        throws Exception {
        // 写入协议头
        byteBuf.writeByte(ProtocolConstant.MAGIC);
        // 写入版本
        byteBuf.writeByte(ProtocolConstant.DEFAULT_VERSION);
        // 写入请求类型
        byteBuf.writeByte(protocolMsg.getMsgType());
        // 写入消息长度
        byteBuf.writeInt(protocolMsg.getContent().length);
        // 写入消息内容
        byteBuf.writeBytes(protocolMsg.getContent());
    }
}
