package com.gordon.rpc.cluster;

import com.google.common.collect.Lists;
import com.gordon.rpc.registry.ServiceURL;
import com.gordon.rpc.util.AtomicPositiveInteger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RoundRobinLoadBalancer implements LoadBalancer{

    private final static Map<String, AtomicPositiveInteger> sequences = new ConcurrentHashMap<>();

    private final static Map<String, AtomicPositiveInteger> weightSequences = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return LoadBalancerEnum.ROUND.getCode();
    }

    @Override
    public ServiceURL selectOne(List<ServiceURL> addresses) {
        String serviceId = addresses.get(0).getServiceId();
        int length = addresses.size(); // 总个数
        int maxWeight = 0; // 最大权重
        int minWeight = Integer.MAX_VALUE; // 最小权重
        for (int i = 0; i < length; i++) {
            int weight = Optional.ofNullable(addresses.get(0).getWeight()).orElse(0);
            maxWeight = Math.max(maxWeight, weight); // 累计最大权重
            minWeight = Math.min(minWeight, weight); // 累计最小权重
        }
        // 是带加权轮询
        if (maxWeight > 0 && maxWeight != minWeight) {
            AtomicPositiveInteger weightSequence = weightSequences.get(serviceId);
            if (weightSequence == null) {
                weightSequences.putIfAbsent(serviceId, new AtomicPositiveInteger());
                weightSequence = weightSequences.get(serviceId);
            }
            int currentWeight = weightSequence.getAndIncrement() % maxWeight;
            List<ServiceURL> weightServices = Lists.newArrayList();
            for (ServiceURL service : addresses) { // 筛选权重大于当前权重基数的Invoker
                int serviceWeight = Optional.ofNullable(service.getWeight()).orElse(0);
                if (serviceWeight > currentWeight) {
                    weightServices.add(service);
                }
            }
            int weightLength = weightServices.size();
            if (weightLength == 1) {
                return weightServices.get(0);
            } else if (weightLength > 1) {
                addresses = weightServices;
                length = addresses.size();
            }
        }

        AtomicPositiveInteger sequence = sequences.get(serviceId);
        if (sequence == null) {
            sequences.putIfAbsent(serviceId, new AtomicPositiveInteger());
            sequence = sequences.get(serviceId);
        }
        // 取模轮循
        return addresses.get(sequence.getAndIncrement() % length);

    }


}
