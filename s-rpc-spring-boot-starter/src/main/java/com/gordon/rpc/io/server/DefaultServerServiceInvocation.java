package com.gordon.rpc.io.server;

import com.gordon.rpc.context.BeanContext;
import com.gordon.rpc.domain.SRpcResponse;
import com.gordon.rpc.model.ServiceMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class DefaultServerServiceInvocation implements ServerServiceInvocation{

    public DefaultServerServiceInvocation() {

    }

    @Override
    public SRpcResponse handleRequest(SRpcResponse req) throws Exception {
        // 获取服务元数据
        ServiceMetadata serviceMetadata = ServerServiceMetadataCache.get(req.getServiceId());

        SRpcResponse response = null;
        if (serviceMetadata == null){
            response = new OrcRpcResponse(RpcStatusEnum.NOT_FOUND);
        }else {
            try {
                // 反射调用
                Method method = serviceMetadata.getClazz().getMethod(req.getMethod(), req.getParameterTypes());
                Object returnValue = method.invoke(BeanContext.getBean(serviceMetadata.getClazz()), req.getParameters());
                response = new SRpcResponse(RpcStatusEnum.SUCCESS);
                response.setData(returnValue);
            }catch (Exception e){
                response = new SRpcResponse(RpcStatusEnum.ERROR);
                response.setException(e);
            }
        }
        // 填充请求id
        response.setRequestId(req.getRequestId());

        return response;

    }

}
