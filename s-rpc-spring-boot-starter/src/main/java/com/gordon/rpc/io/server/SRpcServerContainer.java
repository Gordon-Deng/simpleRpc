package com.gordon.rpc.io.server;

import com.gordon.rpc.config.SRpcProperties;
import com.gordon.rpc.io.netty.server.NettyNetServer;

public class SRpcServerContainer {

    private static NetServer orcRpcServer;

    /**
     * todo 根据配置选择不同的server，tcp/http
     *
     * @param properties
     * @return 
     */
    public static NetServer initServer(SRpcProperties properties) {
        if (orcRpcServer == null) {
            orcRpcServer = new NettyNetServer(properties.getServerPort(),
                properties.getSerializer());
        }
        return orcRpcServer;
    }

}
