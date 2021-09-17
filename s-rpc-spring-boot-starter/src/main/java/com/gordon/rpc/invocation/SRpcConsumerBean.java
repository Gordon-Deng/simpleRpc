package com.gordon.rpc.invocation;

import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.util.ProxyFactoryUtils;
import org.springframework.beans.factory.FactoryBean;

public class SRpcConsumerBean implements FactoryBean {

    private ServiceMetadata serviceMetadata;

    @Override
    public Object getObject() throws Exception {
        ProxyFactory proxyFactory = ProxyFactoryUtils.getProxyFactory(ProxyFactoryUtils.PROXY_TYPE_JDK);
        return proxyFactory.getProxy(serviceMetadata);
    }

    public void setServiceMetadata(ServiceMetadata serviceMetadata) {
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public Class<?> getObjectType() {
        if (this.serviceMetadata == null) {
            return null;
        } else {
            return serviceMetadata.getClazz();
        }
    }
}
