package com.gordon.rpc.util;

import com.google.common.collect.Maps;
import com.gordon.rpc.cluster.LoadBalancer;
import com.gordon.rpc.exception.SRpcException;
import com.gordon.rpc.io.serializer.Serializer;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class SpiLoaderUtils {

    /**
     * 消息序列协议
     *
     * @param name
     * @return com.s.rpc.io.serializer.Serializer
     */
    public static Serializer getSerializer(String name) {
        Serializer serializer = getSupportSerializer().get(name);
        return Optional.ofNullable(serializer).orElseThrow(()->new SRpcException("serializer config " + name +" not exist!"));
    }

    /**
     * 获取支持的序列化方式
     *
     * @param
     * @return java.util.Map<java.lang.String,com.s.rpc.io.serializer.Serializer>
     */
    public static Map<String, Serializer> getSupportSerializer() {
        Map<String, Serializer> map = Maps.newHashMap();
        ServiceLoader<Serializer> loader = ServiceLoader.load(Serializer.class);
        Iterator<Serializer> iterator = loader.iterator();
        while (iterator.hasNext()) {
            Serializer serializer = iterator.next();
            map.put(serializer.name(), serializer);
        }
        return map;
    }


    /**
     *  负载均衡器获取
     *
     * @param name
     * @return com.s.rpc.cluster.LoadBalancer
     */
    public static LoadBalancer getLoadBalancer(String name) {
        ServiceLoader<LoadBalancer> loader = ServiceLoader.load(LoadBalancer.class);
        Iterator<LoadBalancer> iterator = loader.iterator();
        while (iterator.hasNext()) {
            LoadBalancer loadBalancer = iterator.next();
            if (name.equals(loadBalancer.name())) {
                return loadBalancer;
            }
        }
        throw new SRpcException("load balance config " + name +" not exist!");
    }


}

