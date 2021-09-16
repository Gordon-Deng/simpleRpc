package com.gordon.rpc.io.server;


public interface ServerServiceInvocation {
     OrcRpcResponse handleRequest(OrcRpcRequest req) throws Exception ;
}
