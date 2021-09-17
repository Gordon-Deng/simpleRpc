package com.gordon.rpc.invocation;

import com.gordon.rpc.domain.RpcStatusEnum;
import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;
import com.gordon.rpc.exception.SRpcException;
import com.gordon.rpc.model.ServiceMetadata;
import com.gordon.rpc.registry.ServiceURL;
import com.gordon.rpc.util.ServiceUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class JdkProxyFactory implements ProxyFactory{

    @Override
    public Object getProxy(ServiceMetadata serviceMetadata) {
        return Proxy
            .newProxyInstance(serviceMetadata.getClazz().getClassLoader(), new Class[] {serviceMetadata.getClazz()},
                new ClientInvocationHandler(serviceMetadata));
    }

    private class ClientInvocationHandler implements InvocationHandler {

        private ServiceMetadata serviceMetadata;

        public ClientInvocationHandler(ServiceMetadata serviceMetadata) {
            this.serviceMetadata = serviceMetadata;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String serviceId = ServiceUtils.getServiceId(serviceMetadata);
            // 通过负载均衡器选取一个服务提供方地址
            ServiceURL service = InvocationServiceSelector.select(serviceMetadata);

            SRpcRequest request = new SRpcRequest();
            request.setMethod(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setRequestId(UUID.randomUUID().toString());
            request.setServiceId(serviceId);

            SRpcResponse response = InvocationClientContainer.getInvocationClient(service.getServerNet()).invoke(request, service);
            if (response.getStatus() == RpcStatusEnum.SUCCESS) {
                return response.getData();
            } else if (response.getException() != null) {
                throw new SRpcException(response.getException().getMessage());
            } else {
                throw new SRpcException(response.getStatus().name());
            }
        }
    }
}
