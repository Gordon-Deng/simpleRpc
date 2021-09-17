package com.gordon.rpc.cluster;

import com.gordon.rpc.registry.ServiceURL;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    private Random random = new Random();

    @Override
    public String name() {
        return LoadBalancerEnum.RANDOM.getCode();
    }

    @Override
    public ServiceURL selectOne(List<ServiceURL> addresses) {
        int length = addresses.size(); // 总个数
        int maxWeight = 0; // 最大权重
        int minWeight = Integer.MAX_VALUE; // 最小权重
        int totalWeight = 0;
        for (int i = 0; i < length; i++) {
            int weight = Optional.ofNullable(addresses.get(i).getWeight()).orElse(0);
            maxWeight = Math.max(maxWeight, weight); // 累计最大权重
            minWeight = Math.min(minWeight, weight); // 累计最小权重
            totalWeight += weight;
        }
        // 带权重
        if (maxWeight > 0 && maxWeight != minWeight) {
            int offset = this.random.nextInt(totalWeight);
            for (int i = 0; i < length; i++) {
                int weight = Optional.ofNullable(addresses.get(i).getWeight()).orElse(0);
                offset -= weight;
                if (offset < 0) {
                    return addresses.get(i);
                }
            }
        }

        return addresses != null && addresses.size() != 0 ? addresses.get(this.random.nextInt(addresses.size())) : null;
    }

}
