package com.gordon.rpc.io.protocol;


public class ProtocolConstant {
    // 协议头 魔数
    public static final byte MAGIC = 0X35;
    // 协议版本 默认1
    public static final byte DEFAULT_VERSION = 1;
    // 请求
    public static final byte REQ_TYPE = 0;
    // 响应
    public static final byte RES_TYPE = 1;
}
