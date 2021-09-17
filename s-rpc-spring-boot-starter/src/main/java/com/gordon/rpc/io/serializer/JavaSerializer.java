package com.gordon.rpc.io.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerializer implements Serializer {

    @Override
    public String name() {
        return SerializerEnum.JAVA.getCode();
    }

    @Override
    public byte[] serialize(Object var1) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(var1);
        return bout.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] var1, Class<T> var2) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(var1));
        return (T) in.readObject();
    }
}
