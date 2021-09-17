package com.gordon.rpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = "s.rpc"
)
@Data
public class SRpcProperties {
    
    /**
     * 服务注册发现中心地址
     */
    private String registerAddr = "127.0.0.1:2181";
    
    /**
     * 服务提供方端口
     */
    private Integer serverPort = 22100;
    
    /**
     * 服务协议
     */
    private String serializer = "java";
    
    /**
     * 负载均衡算法
     */
    private String loadBalance = "random";
    
    /**
     * 权重，默认为1
     */
    private Integer weight = 1;
    
}
