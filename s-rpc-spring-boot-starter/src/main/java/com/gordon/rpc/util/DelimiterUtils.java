package com.gordon.rpc.util;

import com.gordon.rpc.common.constants.SRpcConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

public class DelimiterUtils {

    public static byte[] DELIMITE_BYTES;

    static {
        try {
            DELIMITE_BYTES ="$_*_#_@_$".getBytes(SRpcConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static ByteBuf getDelimiteByteBuf() {
        return Unpooled.copiedBuffer(DELIMITE_BYTES);
    }

    public static byte[] encodeDelimiterData(byte[] data) {
        byte[] dest = new byte[data.length + DELIMITE_BYTES.length];
        System.arraycopy(data, 0, dest, 0, data.length);
        System.arraycopy(DELIMITE_BYTES, 0, dest, data.length, DELIMITE_BYTES.length);
        return dest;
    }

}
