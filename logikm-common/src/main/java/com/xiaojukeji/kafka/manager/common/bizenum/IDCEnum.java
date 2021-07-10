package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/5/26
 */
public enum IDCEnum {
    CN("cn", "国内"),
    ;

    private String idc;

    private String name;

    IDCEnum(String idc, String name) {
        this.idc = idc;
        this.name = name;
    }

    public String getIdc() {
        return idc;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "IDCEnum{" +
                "idc='" + idc + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}