package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author limeng
 * @date 2017/11/21
 */
public enum OffsetLocationEnum {
    /**
     * 存储于zk
     */
    ZOOKEEPER("zookeeper"),

    /**
     * 存储于broker
     */
    BROKER("broker");

    public final String location;

    OffsetLocationEnum(String location) {
        this.location = location;
    }

    public static OffsetLocationEnum getOffsetStoreLocation(String location) {
        if (location == null) {
            return null;
        }
        
        for (OffsetLocationEnum offsetStoreLocation: OffsetLocationEnum.values()) {
            if (offsetStoreLocation.location.equals(location)) {
                return offsetStoreLocation;
            }
        }
        return null;
    }
}
