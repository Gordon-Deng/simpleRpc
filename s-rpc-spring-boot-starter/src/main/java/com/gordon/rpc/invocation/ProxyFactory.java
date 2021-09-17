package com.gordon.rpc.invocation;

import com.gordon.rpc.model.ServiceMetadata;

public interface ProxyFactory {
    
    Object getProxy(ServiceMetadata serviceMetadata);
    
}
