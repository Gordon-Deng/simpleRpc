package com.gordon.rpc.util;

import com.gordon.rpc.invocation.JdkProxyFactory;
import com.gordon.rpc.invocation.ProxyFactory;

public class ProxyFactoryUtils {

    public static final String PROXY_TYPE_JDK = "jdk";

    private static JdkProxyFactory jdkProxyFactory = new JdkProxyFactory();

    public static ProxyFactory getProxyFactory(String type) {
        return jdkProxyFactory;
    }

}
