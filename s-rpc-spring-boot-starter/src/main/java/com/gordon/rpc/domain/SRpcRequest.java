package com.gordon.rpc.domain;

import lombok.Data;

@Data
public class SRpcRequest {

    private String requestId;

    private String serviceId;

    private String method;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
