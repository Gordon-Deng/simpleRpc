package com.gordon.rpc.registry;

import java.util.Objects;

import lombok.Data;

@Data
public class ServiceURL {
    /**
     * 服务唯一标示
     */
    private String serviceId;
    /**
     * 服务名称
     */
    private String name;
    /**
     * 版本
     */
    private String version;
    /**
     * 序列化协议
     */
    private String serializer;
    /**
     *  服务地址，格式：ip:port
     */
    private String address;
    /**
     * 权重，越大优先级越高
     */
    private Integer weight;
    /**
     * server服务类型 tcp/http
     */
    private String serverNet;

    @Override
    public String toString() {
        return "Service{" +
            "serviceId='" + serviceId + '\'' +
            ", serializer='" + serializer + '\'' +
            ", address='" + address + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, serializer, address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceURL service = (ServiceURL) o;
        return Objects.equals(serviceId, service.getServiceId()) &&
            Objects.equals(serializer, service.getSerializer()) &&
            Objects.equals(address, service.getAddress());
    }

}
