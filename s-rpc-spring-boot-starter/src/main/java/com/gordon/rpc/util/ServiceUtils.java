package com.gordon.rpc.util;

import com.gordon.rpc.model.ServiceMetadata;

import static com.gordon.rpc.common.constants.SRpcConstant.*;

public class ServiceUtils {

    /**
     * 获取服务id
     *
     * @param serviceMetadata
     * @return java.lang.String
     */
    public static String getServiceId(ServiceMetadata serviceMetadata) {
        return serviceMetadata.getName() + SERVICE_VERSION_DELIMITER + serviceMetadata.getVersion();
    }

    /**
     * 注册中心上的parent path
     *
     * @param serviceId
     * @return java.lang.String
     */
    public static String getRegisterServiceParentPath(String serviceId) {
        return ZK_SERVICE_PATH + SERVICE_PATH_DELIMITER + serviceId;
    }

}
