package com.gordon.rpc.model;

import lombok.Data;

@Data
public class ServiceMetadata {

    public static final String DEFAULT_VERSION = "1.0.0";
    /**
     * 名称
     */
    private String name;
    /**
     * 服务实现类
     */
    private Class<?> clazz;
    /**
     * 版本
     */
    private String version;

}
