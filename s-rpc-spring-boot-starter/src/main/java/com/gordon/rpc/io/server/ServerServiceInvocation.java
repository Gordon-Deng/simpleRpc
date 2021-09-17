package com.gordon.rpc.io.server;

import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;

public interface ServerServiceInvocation {
    
     SRpcResponse handleRequest(SRpcRequest req) throws Exception ;
}
