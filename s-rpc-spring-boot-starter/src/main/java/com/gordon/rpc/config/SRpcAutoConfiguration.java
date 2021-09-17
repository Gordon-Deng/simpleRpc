package com.gordon.rpc.config;

import com.alibaba.nacos.api.exception.NacosException;

import com.gordon.rpc.context.BeanContext;
import com.gordon.rpc.invocation.InvocationServiceSelector;
import com.gordon.rpc.registry.Registry;
import com.gordon.rpc.registry.RpcBootStarter;
import com.gordon.rpc.registry.nacos.NacosRegistry;
import com.gordon.rpc.registry.zookeeper.ZookeeperRegistry;
import com.gordon.rpc.util.SpiLoaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ SRpcProperties.class})
public class SRpcAutoConfiguration {

    @Bean
    public SRpcProperties sRpcProperties() {
        return new SRpcProperties();
    }

    @Bean
    public BeanContext beanContext() {
        return new BeanContext();
    }

    /**
     * 服务注册中心
     *
     * @param sRpcProperties
     * @return com.s.rpc.registry.Registry
     */
    @Bean
    public Registry registry(
        @Autowired SRpcProperties sRpcProperties) throws NacosException {
        // Zookeeper 注册中心
        //ZookeeperRegistry serviceRegistry = new ZookeeperRegistry(
        //    sRpcProperties.getRegisterAddr(),
        //    sRpcProperties.getServerPort(),
        //    sRpcProperties.getSerializer(),
        //    sRpcProperties.getWeight());

        // nacos注册中心
        NacosRegistry serviceRegistry = new NacosRegistry(
                sRpcProperties.getRegisterAddr(),
                sRpcProperties.getServerPort(),
                sRpcProperties.getSerializer(),
                sRpcProperties.getWeight());
        
        InvocationServiceSelector.setRegistry(serviceRegistry);
        InvocationServiceSelector.setLoadBalancer(SpiLoaderUtils.getLoadBalancer(sRpcProperties.getLoadBalance()));
        return serviceRegistry;
    }

    /**
     * 服务注册发现初始化
     *
     * @param sRpcProperties
     * @param serviceRegistry
     * @return com.s.rpc.registry.RpcServiceRegistry
     */
    @Bean
    public RpcBootStarter rpcBootStarter(
        @Autowired SRpcProperties sRpcProperties,
        @Autowired Registry serviceRegistry) {
        return new RpcBootStarter(serviceRegistry, sRpcProperties);
    }
}
