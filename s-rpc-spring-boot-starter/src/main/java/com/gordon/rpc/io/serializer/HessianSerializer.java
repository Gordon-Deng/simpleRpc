package com.gordon.rpc.io.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.gordon.rpc.io.common.SerializerEnum;

public class HessianSerializer implements Serializer{

    @Override
    public String name() {
        return SerializerEnum.HESSIAN.getCode();
    }

    @Override
    public byte[] serialize(Object var1) throws Exception {
        // 创建字节输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(bos);
        hessianOutput.writeObject(var1);
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] var1, Class<T> c) throws Exception {
        // 将字节数组转换成字节输入流
        ByteArrayInputStream bis = new ByteArrayInputStream(var1);
        HessianInput hessianInput = new HessianInput(bis);

        return (T) hessianInput.readObject();
    }

}
