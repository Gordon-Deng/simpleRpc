package com.gordon.rpc.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.gordon.rpc.exception.SRpcException;
import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.registry.Registry;
import com.gordon.rpc.registry.ServiceURL;
import com.gordon.rpc.registry.cache.ClientServiceDiscoveryCache;
import com.gordon.rpc.registry.cache.ServerServiceMetadataCache;
import com.gordon.rpc.util.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gordon.rpc.common.constants.SRpcConstant.SERVICE_PATH_DELIMITER;
import static com.gordon.rpc.common.constants.SRpcConstant.UTF_8;

@Slf4j
public class ZookeeperRegistry implements Registry {

    protected Integer serverPort;

    protected String serializer;

    protected Integer weight;

    private ZkClient zkClient;

    public ZookeeperRegistry(String zkAddress, Integer serverPort, String serializer, Integer weight) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZkSerializer(){

            @Override
            public byte[] serialize(Object o) throws ZkMarshallingError {
                return String.valueOf(o).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        });
        this.serverPort = serverPort;
        this.serializer = serializer;
        this.weight = weight;
    }

    /**
     * service register
     *
     * @param serviceMetadata
     * @return void
     */
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
        createZookeeperServiceNode(serviceURL);
    }

    /**
     * ???zk?????????????????????
     *
     * @param serviceURL
     * @return void
     */
    private void createZookeeperServiceNode(ServiceURL serviceURL) {
        String urlJson = JSON.toJSONString(serviceURL);
        try {
            urlJson = URLEncoder.encode(urlJson, UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException utf8 encode", e);
        }

        String servicePath = ServiceUtils.getRegisterServiceParentPath(serviceURL.getServiceId());
        // ??????????????????
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
        }
        String urlPath = servicePath + SERVICE_PATH_DELIMITER + urlJson;
        if (zkClient.exists(urlPath)) {
            // ?????????????????????
            zkClient.delete(urlPath);
        }
        // ???????????????????????????????????????????????????
        zkClient.createEphemeral(urlPath);
    }


    @Override
    public void subscribe(ServiceMetadata serviceMetadata) {
        getServiceList(serviceMetadata);
    }

    /**
     * ????????????meta data????????????????????????
     *
     * @param serviceMetadata
     * @return java.util.List<com.s.rpc.registry.ServiceURL>
     */
    @Override
    public List<ServiceURL> getServiceList(ServiceMetadata serviceMetadata) {
        String  serviceId = ServiceUtils.getServiceId(serviceMetadata);
        List<ServiceURL> services = ClientServiceDiscoveryCache.get(serviceId);

        if (CollectionUtils.isEmpty(services)) {
            String lockKey = ClientServiceDiscoveryCache.getCacheLockKey(serviceId);
            synchronized (lockKey) {
                services = ClientServiceDiscoveryCache.get(serviceId);
                if (CollectionUtils.isEmpty(services)) {
                    String servicePath = ServiceUtils.getRegisterServiceParentPath(serviceId);
                    List<String> children = zkClient.getChildren(servicePath);
                    if (CollectionUtils.isEmpty(children)) {
                        throw new SRpcException("No rpc provider " + serviceId + " available!");
                    }
                    services = getAndSetServiceCache(serviceId, children);
                }
            }
        }

        return services;
    }

    /**
     * ????????????????????????
     *
     * @param serviceMetadata
     * @return void
     */
    @Override
    public void subscribeServiceChange(ServiceMetadata serviceMetadata) {
        String  serviceId = ServiceUtils.getServiceId(serviceMetadata);
        zkClient.subscribeChildChanges(ServiceUtils.getRegisterServiceParentPath(serviceId),

            new IZkChildListener() {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                    log.debug("parentPath:{}, currentChilds:{}", parentPath, currentChilds);
                    // ???????????????????????????????????????
                    String[] arr = parentPath.split("/");
                    String serviceId = arr[2];

                    ZookeeperRegistry.this.serviceChangeCallBack(serviceId, currentChilds);
                }
            });
    }

    public void serviceChangeCallBack(String serviceId, List<String> currentChildren) {
        String lockKey = ClientServiceDiscoveryCache.getCacheLockKey(serviceId);
        synchronized (lockKey) {
            getAndSetServiceCache(serviceId, currentChildren);
        }
    }


    private List<ServiceURL> getAndSetServiceCache(String serviceId, List<String> children) {
        List<ServiceURL> services = Optional.ofNullable(children).orElse(Lists.newArrayList()).stream().map(serviceStr->{
            String deCh = null;
            try {
                deCh = URLDecoder.decode(serviceStr, UTF_8);
            } catch (UnsupportedEncodingException e) {
                log.error("find service from zookeeper error", e);
            }
            return JSON.parseObject(deCh, ServiceURL.class);
        }).collect(Collectors.toList());
        ClientServiceDiscoveryCache.put(serviceId, services);
        return services;
    }

    /**
     * Getter method for property <tt>zkClient</tt>.
     *
     * @return property value of zkClient
     */
    public ZkClient getZkClient() {
        return zkClient;
    }
}
