package com.gordon.rpc.cluster;

import com.gordon.rpc.registry.ServiceURL;

import java.util.List;

public interface LoadBalancer {

    String name();

    ServiceURL selectOne(List<ServiceURL> addresses);
}
