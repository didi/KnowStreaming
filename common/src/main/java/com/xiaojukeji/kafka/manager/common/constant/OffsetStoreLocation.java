package com.xiaojukeji.kafka.manager.common.constant;

/**
 * @author limeng
 * @date 2017/11/21
 */
public enum OffsetStoreLocation {

    ZOOKEEPER("zookeeper"),

    BROKER("broker");

    private final String location;

    OffsetStoreLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public static OffsetStoreLocation getOffsetStoreLocation(String location) {
        if (location == null) {
            return null;
        }
        
        for (OffsetStoreLocation offsetStoreLocation: OffsetStoreLocation.values()) {
            if (offsetStoreLocation.location.equals(location)) {
                return offsetStoreLocation;
            }
        }
        return null;
    }
}
