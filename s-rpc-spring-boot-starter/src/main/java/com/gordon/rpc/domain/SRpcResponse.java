package com.gordon.rpc.domain;

import lombok.Data;

@Data
public class SRpcResponse {

    private String requestId;

    private RpcStatusEnum status;

    private Object data;

    private Exception exception;

    public SRpcResponse(RpcStatusEnum status) {
        this.status = status;
    }
}
