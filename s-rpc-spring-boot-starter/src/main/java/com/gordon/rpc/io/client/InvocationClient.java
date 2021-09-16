package com.gordon.rpc.io.client;

import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;
import com.gordon.rpc.registry.ServiceURL;

public interface InvocationClient {

    SRpcResponse invoke(SRpcRequest orcRpcRequest, ServiceURL serviceURL);

}
