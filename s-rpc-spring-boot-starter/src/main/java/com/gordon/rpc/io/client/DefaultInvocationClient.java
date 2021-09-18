package com.gordon.rpc.io.client;

import com.gordon.rpc.domain.SRpcRequest;
import com.gordon.rpc.domain.SRpcResponse;
import com.gordon.rpc.io.netty.client.NettyNetClient;
import com.gordon.rpc.io.serializer.Serializer;
import com.gordon.rpc.registry.ServiceURL;
import com.gordon.rpc.util.SpiLoaderUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DefaultInvocationClient implements InvocationClient {

    private static DefaultInvocationClient INSTANCE_TCP;

    static {
        INSTANCE_TCP = new DefaultInvocationClient(new NettyNetClient());
    }

    private NetClient sRpcClient;

    private Map<String, Serializer> supportSerializerMap;

    private DefaultInvocationClient() {

    }

    private DefaultInvocationClient(NetClient sRpcClient) {
        this.sRpcClient = sRpcClient;
        init();
    }

    private void init() {
        supportSerializerMap = SpiLoaderUtils.getSupportSerializer();
    }

    // todo only netty tcp
    public static DefaultInvocationClient getInstance(String serverNet) {
        return INSTANCE_TCP;
    }

    @Override
    public SRpcResponse invoke(SRpcRequest orcRpcRequest, ServiceURL serviceURL) {
        String address = serviceURL.getAddress();

        ClientRequestHandler handler = ClientServiceAddressHandlerCache.get(address);
        if (handler == null) {
            Serializer serializer = supportSerializerMap.get(serviceURL.getSerializer());
            handler = sRpcClient.connect(address, serializer);
            log.debug("establish new channel");
        }
        return handler.send(orcRpcRequest);
    }

}
