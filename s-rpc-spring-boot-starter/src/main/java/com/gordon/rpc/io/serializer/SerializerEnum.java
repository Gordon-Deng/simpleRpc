package com.gordon.rpc.io.serializer;

public enum SerializerEnum {

    JAVA("java"),

    PROTOBUF("protobuf"),

    HESSIAN("hessian");

    private String code;

    private SerializerEnum(String code) {
        this.code = code;
    }

    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public String getCode() {
        return code;
    }
}
