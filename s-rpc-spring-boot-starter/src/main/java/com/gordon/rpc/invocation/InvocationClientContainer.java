package com.gordon.rpc.invocation;

import com.gordon.rpc.io.client.DefaultInvocationClient;
import com.gordon.rpc.io.client.InvocationClient;

public class InvocationClientContainer {

    public static InvocationClient getInvocationClient(String serverNet) {
        return DefaultInvocationClient.getInstance(serverNet);
    }

}
