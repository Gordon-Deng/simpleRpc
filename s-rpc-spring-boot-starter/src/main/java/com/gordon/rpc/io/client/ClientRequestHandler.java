package com.gordon.rpc.io.client;

import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;

public interface ClientRequestHandler {

    SRpcResponse send(SRpcRequest request);

}
