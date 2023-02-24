package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

/**
 * @author leewei
 * @date 2022/5/10
 */
public enum Resource {
    CPU("cpu", 0),
    NW_IN("bytesIn", 1),
    NW_OUT("bytesOut", 2),
    DISK("disk", 3);

    private final String resource;
    private final int id;

    Resource(String resource, int id) {
        this.resource = resource;
        this.id = id;
    }

    public String resource() {
        return this.resource;
    }

    public int id() {
        return this.id;
    }

}
