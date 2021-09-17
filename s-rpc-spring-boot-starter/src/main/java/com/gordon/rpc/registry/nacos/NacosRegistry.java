package com.gordon.rpc.registry.nacos;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

import com.google.common.collect.Lists;
import com.gordon.rpc.exception.SRpcException;
import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.registry.Registry;
import com.gordon.rpc.registry.ServiceURL;
import com.gordon.rpc.registry.cache.ClientServiceDiscoveryCache;
import com.gordon.rpc.registry.cache.ServerServiceMetadataCache;
import com.gordon.rpc.util.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import static com.gordon.rpc.common.constants.SRpcConstant.UTF_8;

@Slf4j
public class NacosRegistry implements Registry {

    protected Integer serverPort;

    protected String serializer;

    protected Integer weight;

    private NamingService namingService;

    public NacosRegistry(String nacosAddress, Integer serverPort, String serializer, Integer weight)
        throws NacosException {
        namingService = NamingFactory.createNamingService(nacosAddress);

        this.serverPort = serverPort;
        this.serializer = serializer;
        this.weight = weight;
    }

    @Override
    public void register(ServiceMetadata serviceMetadata) throws Exception {
        String  serviceId = ServiceUtils.getServiceId(serviceMetadata);
        ServerServiceMetadataCache.put(serviceId, serviceMetadata);

        ServiceURL serviceURL = new ServiceURL();
        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + this.serverPort;
        serviceURL.setAddress(address);
        serviceURL.setServiceId(serviceId);
        serviceURL.setName(serviceMetadata.getName());
        serviceURL.setVersion(serviceMetadata.getVersion());
        serviceURL.setSerializer(this.serializer);
        serviceURL.setWeight(this.weight);

        String urlJson = JSON.toJSONString(serviceURL);
        try {
            urlJson = URLEncoder.encode(urlJson, UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException utf8 encode", e);
        }

        Instance instance = new Instance();
        instance.setIp(host);
        instance.setPort(this.serverPort);
        instance.setHealthy(false);
        instance.setWeight(2.0);
        Map<String, String> instanceMeta = new HashMap<>();
        instanceMeta.put("serviceURL", urlJson);
        instance.setMetadata(instanceMeta);

        namingService.registerInstance(serviceId, instance);
    }

    @Override
    public void subscribe(ServiceMetadata serviceMetadata) {
        getServiceList(serviceMetadata);
    }

    @Override
    public void subscribeServiceChange(ServiceMetadata serviceMetadata) {
        String  serviceId = ServiceUtils.getServiceId(serviceMetadata);
        try {
            namingService.subscribe(serviceId,
                new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        if (event instanceof NamingEvent) {
                            List<Instance> instances = ((NamingEvent) event).getInstances();
                            String lockKey = ClientServiceDiscoveryCache.getCacheLockKey(serviceId);
                            synchronized (lockKey) {
                                getAndSetServiceCache(serviceId, instances);
                            }
                        }
                    }
                });
        } catch (NacosException e) {
            log.error("subscribeServiceChange from nacos error", e);
            throw new SRpcException("subscribe service change from nacos error, serviceId= " + serviceId );
        }
    }

    @Override
    public List<ServiceURL> getServiceList(ServiceMetadata serviceMetadata) {
        String  serviceId = ServiceUtils.getServiceId(serviceMetadata);
        List<ServiceURL> services = ClientServiceDiscoveryCache.get(serviceId);

        if (CollectionUtils.isEmpty(services)) {
            String lockKey = ClientServiceDiscoveryCache.getCacheLockKey(serviceId);
            synchronized (lockKey) {
                services = ClientServiceDiscoveryCache.get(serviceId);
                if (CollectionUtils.isEmpty(services)) {
                    try {
                        List<Instance> instances = namingService.getAllInstances(serviceId);
                        if (CollectionUtils.isEmpty(instances)) {
                            throw new SRpcException("No rpc provider " + serviceId + " available!");
                        }
                        services = getAndSetServiceCache(serviceId, instances);
                    } catch (Exception e) {
                        log.error("getServiceList from nacos error", e);
                        throw new SRpcException("get service list from nacos error, serviceId= " + serviceId );
                    }
                }
            }
        }

        return services;
    }

    private List<ServiceURL> getAndSetServiceCache(String serviceId, List<Instance> instances) {
        List<ServiceURL> services = Optional.ofNullable(instances).orElse(Lists.newArrayList()).stream().map(instance->{
            String deCh = null;
            try {
                Map<String, String> metadata = instance.getMetadata();
                String serviceStr = metadata.get("serviceURL");
                deCh = URLDecoder.decode(serviceStr, UTF_8);
            } catch (UnsupportedEncodingException e) {
                log.error("find service from zookeeper error", e);
            }
            return JSON.parseObject(deCh, ServiceURL.class);
        }).collect(Collectors.toList());
        ClientServiceDiscoveryCache.put(serviceId, services);
        return services;
    }

}
