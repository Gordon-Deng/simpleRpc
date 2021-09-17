package com.gordon.rpc.invocation;

import com.gordon.rpc.annotation.SRpcConsumer;
import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.registry.cache.ClientServiceDiscoveryCache;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

public class SRpcConsumerBeanDefinitionBuilder {

    private final Class<?> interfaceClass;

    private final SRpcConsumer sRpcConsumer;

    public SRpcConsumerBeanDefinitionBuilder(Class<?> interfaceClass, SRpcConsumer sRpcConsumer) {
        this.interfaceClass = interfaceClass;
        this.sRpcConsumer = sRpcConsumer;
    }

    public BeanDefinition build() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SRpcConsumerBean.class);

        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setClazz(interfaceClass);
        serviceMetadata.setName(interfaceClass.getName());
        serviceMetadata.setVersion(sRpcConsumer.version());

        builder.addPropertyValue("serviceMetadata", serviceMetadata);

        ClientServiceDiscoveryCache.addServiceMetadata(serviceMetadata);

        return builder.getBeanDefinition();
    }

}
