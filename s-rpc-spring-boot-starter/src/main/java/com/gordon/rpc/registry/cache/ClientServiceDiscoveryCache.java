package com.gordon.rpc.registry.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.registry.ServiceURL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientServiceDiscoveryCache {

    private static Map<String, List<ServiceURL>> SERVICE_URL_CACHE = Maps.newConcurrentMap();

    private static List<ServiceMetadata> SERVICE_METADATA_LIST = Lists.newArrayList();

    private static Map<String, String> serviceIdLockMap = Maps.newConcurrentMap();

    public static void put(String serviceId, List<ServiceURL> serviceList) {
        SERVICE_URL_CACHE.put(serviceId, serviceList);
    }

    public static void remove(String serviceId, ServiceURL serviceURL) {
        SERVICE_URL_CACHE.computeIfPresent(serviceId, (key, value) ->
            value.stream().filter(o -> !o.equals(serviceURL)).collect(Collectors.toList())
        );
    }

    public static void removeAll(String serviceId) {
        SERVICE_URL_CACHE.remove(serviceId);
    }

    public static boolean isNotExists(String serviceId) {
        return SERVICE_URL_CACHE.get(serviceId) == null || SERVICE_URL_CACHE.get(serviceId).size() == 0;
    }

    public static List<ServiceURL> get(String serviceId) {
        return SERVICE_URL_CACHE.get(serviceId);
    }

    public static void addServiceMetadata(ServiceMetadata serviceMetadata) {
        SERVICE_METADATA_LIST.add(serviceMetadata);
    }

    public static List<ServiceMetadata> getAllServiceMetadatas() {
        return ImmutableList.copyOf(SERVICE_METADATA_LIST);
    }

    public static String getCacheLockKey(String serviceId) {
        return serviceIdLockMap.computeIfAbsent(serviceId, key -> key);
    }
}
