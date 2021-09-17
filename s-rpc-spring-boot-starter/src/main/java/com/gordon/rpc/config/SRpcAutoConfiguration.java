package com.gordon.rpc.config;

import com.alibaba.nacos.api.exception.NacosException;

import com.gordon.rpc.context.BeanContext;
import com.gordon.rpc.registry.Registry;
import com.gordon.rpc.registry.RpcBootStarter;
import com.gordon.rpc.registry.zookeeper.ZookeeperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ SRpcProperties.class})
public class SRpcAutoConfiguration {

    @Bean
    public SRpcProperties orcRpcProperties() {
        return new SRpcProperties();
    }

    @Bean
    public BeanContext beanContext() {
        return new BeanContext();
    }

    /**
     * 服务注册中心
     *
     * @param orcRpcProperties
     * @return com.orc.rpc.registry.Registry
     */
    @Bean
    public Registry registry(
        @Autowired SRpcProperties orcRpcProperties) throws NacosException {
        // Zookeeper 注册中心
        ZookeeperRegistry serviceRegistry = new ZookeeperRegistry(
            orcRpcProperties.getRegisterAddr(),
            orcRpcProperties.getServerPort(),
            orcRpcProperties.getSerializer(),
            orcRpcProperties.getWeight());


        InvocationServiceSelector.setRegistry(serviceRegistry);
        InvocationServiceSelector.setLoadBalancer(SpiLoaderUtils.getLoadBalancer(orcRpcProperties.getLoadBalance()));
        return serviceRegistry;
    }

    /**
     * 服务注册发现初始化
     *
     * @param orcRpcProperties
     * @param serviceRegistry
     * @return com.orc.rpc.registry.RpcServiceRegistry
     */
    @Bean
    public RpcBootStarter rpcBootStarter(
        @Autowired SRpcProperties orcRpcProperties,
        @Autowired Registry serviceRegistry) {
        return new RpcBootStarter(serviceRegistry, orcRpcProperties);
    }
}
