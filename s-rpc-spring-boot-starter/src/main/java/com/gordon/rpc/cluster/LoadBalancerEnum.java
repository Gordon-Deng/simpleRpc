package com.gordon.rpc.cluster;


public enum LoadBalancerEnum {

    RANDOM("random"),
    ROUND("round");

    private String code;

    private LoadBalancerEnum(String code) {
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
