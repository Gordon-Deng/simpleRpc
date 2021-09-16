package com.gordon.rpc.io.client;

import java.util.Map;

import com.google.common.collect.Maps;
import com.gordon.rpc.io.client.ClientRequestHandler;

public class ClientServiceAddressHandlerCache {

    public static Map<String, ClientRequestHandler> HANDLER_CACHE = Maps.newConcurrentMap();

    /**
     * 设置地址handler
     *
     * @param address
     * @param handler
     * @return void
     */
    public static void put(String address, ClientRequestHandler handler) {
        HANDLER_CACHE.put(address, handler);
    }

    /**
     * 移除地址
     *
     * @param address
     * @return void
     */
    public static void remove(String address) {
        HANDLER_CACHE.remove(address);
    }

    /**
     * 获取地址handler
     *
     * @param address
     * @return com.orc.rpc.io.client.ClientRequestHandler
     */
    public static ClientRequestHandler get(String address) {
        return HANDLER_CACHE.get(address);
    }

    /**
     * 是否存在地址处理器
     *
     * @param address
     * @return boolean
     */
    public static boolean exists(String address) {
        return HANDLER_CACHE.containsKey(address);
    }

}
