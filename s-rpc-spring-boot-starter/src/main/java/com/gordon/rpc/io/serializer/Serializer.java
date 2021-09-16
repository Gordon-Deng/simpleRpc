package com.gordon.rpc.io.serializer;

public interface Serializer {

    String name();

    byte[] serialize(Object var1) throws Exception;

    <T> T deserialize(byte[] var1, Class<T> c) throws Exception;

}
