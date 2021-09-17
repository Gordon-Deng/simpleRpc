package com.gordon.rpc.invocation;

import com.gordon.rpc.cluster.LoadBalancer;
import com.gordon.rpc.exception.SRpcException;
import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.registry.Registry;
import com.gordon.rpc.registry.ServiceURL;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class InvocationServiceSelector {

    private static LoadBalancer loadBalancer;

    private static Registry registry;

    public static ServiceURL select(ServiceMetadata serviceMetadata) {
        List<ServiceURL> serviceList = registry.getServiceList(serviceMetadata);
        if (CollectionUtils.isEmpty(serviceList)) {
            throw new SRpcException("No rpc provider: " + serviceMetadata.getName() + " version: " + serviceMetadata.getVersion() + " available!");
        }
        ServiceURL serviceURL = loadBalancer.selectOne(serviceList);
        return serviceURL;
    }

    public static void setLoadBalancer(LoadBalancer loadBalancer) {
        InvocationServiceSelector.loadBalancer = loadBalancer;
    }

    public static void setRegistry(Registry registry) {
        InvocationServiceSelector.registry = registry;
    }
}
