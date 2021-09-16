package com.gordon.rpc.io.client;

import com.gordon.rpc.io.client.ClientRequestHandler;

public interface NetClient {

    ClientRequestHandler connect(String address, Serializer serializer);

}
