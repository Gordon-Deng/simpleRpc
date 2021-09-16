package com.gordon.rpc.registry;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.gordon.rpc.annotation.SRpcProvider;
import com.gordon.rpc.config.SRpcProperties;
import com.gordon.rpc.io.server.SRpcServerContainer;
import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.registry.cache.ClientServiceDiscoveryCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

@Slf4j
public class RpcBootStarter implements ApplicationListener<ContextRefreshedEvent> {

    private Registry serviceRegistry;

    private SRpcProperties orcRpcProperties;

    public RpcBootStarter(Registry serviceRegistry,
        SRpcProperties orcRpcProperties) {
        this.serviceRegistry = serviceRegistry;
        this.orcRpcProperties = orcRpcProperties;
    }

    /**
     * spring启动完成后执行
     *
     * @param event
     * @return void
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (Objects.isNull(event.getApplicationContext().getParent())){
            ApplicationContext context = event.getApplicationContext();
            // 提供服务
            registerService(context);
            // 订阅服务
            subscribeService(context);
        }
    }

    private void registerService(ApplicationContext context)  {
        Map<String, Object> beanMap = context.getBeansWithAnnotation(SRpcProvider.class);
        if (beanMap.size() > 0) {
            for (Object obj : beanMap.values()) {
                Class<?> clazz = obj.getClass();
                try {
                    SRpcProvider sRpcProvider = clazz.getAnnotation(SRpcProvider.class);
                    if (StringUtils.isEmpty(sRpcProvider.name())) {
                        throw new RuntimeException(
                            "orc rpc service class: " + clazz.getName() + " need service name config");
                    }
                    ServiceMetadata serviceMetadata = new ServiceMetadata();
                    serviceMetadata.setName(sRpcProvider.name());
                    serviceMetadata.setVersion(sRpcProvider.version());
                    serviceMetadata.setClazz(clazz);
                    serviceRegistry.register(serviceMetadata);
                } catch (Exception e) {
                    log.error("orc rpc service class: {}, register error", clazz.getName(), e);
                    throw new RuntimeException(
                        "orc rpc service class: " + clazz.getName() + " register error");
                }
            }

            new Thread() {
                 @Override
                 public void run() {
                     SRpcServerContainer.initServer(orcRpcProperties).start();
                 }
            }.start();

        }
    }

    private void subscribeService(ApplicationContext context) {
        List<ServiceMetadata> allServiceMetadatas = ClientServiceDiscoveryCache.getAllServiceMetadatas();
        for (ServiceMetadata serviceMetadata : allServiceMetadatas) {
            serviceRegistry.subscribe(serviceMetadata);
            // 服务变动监听
            serviceRegistry.subscribeServiceChange(serviceMetadata);

        }
    }


}
