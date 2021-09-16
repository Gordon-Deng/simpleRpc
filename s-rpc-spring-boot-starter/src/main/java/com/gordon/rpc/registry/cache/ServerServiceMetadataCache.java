package com.gordon.rpc.registry.cache;

import com.google.common.collect.Maps;
import com.gordon.rpc.model.ServiceMetadata;

import java.util.Map;

public class ServerServiceMetadataCache {

    private static Map<String, ServiceMetadata> SERVICE_CACHE = Maps.newConcurrentMap();

    /**
     * 将服务端提供的服务具体信息保存到缓存中
     *
     * @param serviceId
     * @param serviceMetadata
     * @return void
     */
    public static void put(String serviceId, ServiceMetadata serviceMetadata) {
        SERVICE_CACHE.put(serviceId, serviceMetadata);
    }

    /**
     * 服务端从缓存中获取服务具体信息
     *
     * @param serviceId
     * @return com.s.rpc.model.ServiceMetadata
     */
    public static ServiceMetadata get(String serviceId) {
        return SERVICE_CACHE.get(serviceId);
    }

}
