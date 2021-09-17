package com.gordon.rpc.io.protocol.codec;

import com.gordon.rpc.io.protocol.ProtocolConstant;
import com.gordon.rpc.io.protocol.ProtocolMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class SProtocolDecoder extends ByteToMessageDecoder {

    /**
     * 协议开始的标志 magic + version + type + length 占据7个字节
     */
    public final int BASE_LENGTH = 7;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
        throws Exception {
        // 可读字节小于基本长度，无法解析出payload长度，返回
        if (byteBuf.readableBytes() < BASE_LENGTH) {
            return;
        }
        // 记录包头开始的index
        int beginIndex;
        while (true) {
            // 记录包头开始的index
            beginIndex = byteBuf.readerIndex();
            // 标记包头开始的index
            byteBuf.markReaderIndex();
            // 读到了协议头魔数，结束循环
            if (byteBuf.readByte() == ProtocolConstant.MAGIC) {
                break;
            }
            // 未读到包头，略过一个字节
            // 每次略过一个字节，去读取包头信息的开始标记
            byteBuf.resetReaderIndex();
            byteBuf.readByte();

            /**
             * 当略过，一个字节之后，数据包的长度，又变得不满足
             * 此时结束。等待后面的数据到达
             */
            if (byteBuf.readableBytes() < BASE_LENGTH) {
                return;
            }
        }
        // 读取版本号
        byte version = byteBuf.readByte();
        // 读取消息类型
        byte type = byteBuf.readByte();
        // 读取消息长度
        int length = byteBuf.readInt();
        // 判断本包是否完整
        if (byteBuf.readableBytes() < length) {
            // 还原读指针
            byteBuf.readerIndex(beginIndex);
            return;
        }
        byte[] data = new byte[length];
        byteBuf.readBytes(data);

        ProtocolMsg msg = new ProtocolMsg();
        msg.setMsgType(type);
        msg.setContent(data);
        list.add(msg);
    }

}
