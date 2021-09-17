package com.gordon.rpc.io.protocol;

import lombok.Data;


@Data
public class ProtocolMsg {

    private byte msgType;

    private byte[] content;

}
