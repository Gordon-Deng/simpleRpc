package com.gordon.rpc.io.serializer;

import java.io.Serializable;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ProtobufSerializer implements Serializer{

    @Override
    public String name() {
        return SerializerEnum.PROTOBUF.getCode();
    }

    @Override
    public byte[] serialize(Object var1) throws Exception {
        Schema schema = RuntimeSchema.getSchema(var1.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        final byte[] result;
        try {
            result = ProtobufIOUtil.toByteArray(var1, schema, buffer);
        } finally {
            buffer.clear();
        }
        return result;
    }

    @Override
    public <T> T deserialize(byte[] var1, Class<T> c) throws Exception {
        Schema<T> schema = RuntimeSchema.getSchema(c);
        T t = schema.newMessage();
        ProtobufIOUtil.mergeFrom(var1, t, schema);
        return t;
    }


    public static void main(String[] args) throws Exception {
        User user = new User();
        user.setName("123");

        Serializer serializer = new ProtobufSerializer();
        byte[] bytes = serializer.serialize(user);
        User a = serializer.deserialize(bytes, User.class);
    }

    private static class User implements Serializable {

        private String name;

        /**
         * Getter method for property <tt>name</tt>.
         *
         * @return property value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Setter method for property <tt>name</tt>.
         *
         * @param name value to be assigned to property name
         */
        public void setName(String name) {
            this.name = name;
        }
    }

}
