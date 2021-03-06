package com.gordon.rpc.registry;

import com.gordon.rpc.exception.SRpcException;
import com.gordon.rpc.model.ServiceMetadata;

import java.util.List;

public interface Registry {

    /**
     * 服务端 服务注册
     *
     * @param serviceMetadata 服务元数据
     * @return void
     */
    void register(ServiceMetadata serviceMetadata) throws SRpcException;

    /**
     * 客户端 服务订阅
     *
     * @param serviceMetadata 服务元数据
     * @return void
     */
    void subscribe(ServiceMetadata serviceMetadata);

    /**
     * 客户端 监听服务地址变动
     *
     * @param serviceMetadata 服务元数据
     * @return void
     */
    void subscribeServiceChange(ServiceMetadata serviceMetadata);

    /**
     * 从注册中心获取服务地址列表
     *
     * @param serviceMetadata 服务元数据
     * @return java.util.List<com.s.rpc.registry.ServiceURL>
     */
    List<ServiceURL> getServiceList(ServiceMetadata serviceMetadata);

}
