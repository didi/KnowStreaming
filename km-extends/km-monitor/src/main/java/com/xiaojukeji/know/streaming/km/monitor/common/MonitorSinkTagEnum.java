package com.xiaojukeji.know.streaming.km.monitor.common;

public enum MonitorSinkTagEnum {

    /**
     * cluster
     */
    CLUSTER_ID("clusterId"),

    BROKER_ID("brokerId"),

    TOPIC("topic"),

    PARTITION_ID("partitionId"),

    CONSUMER_GROUP("consumerGroup"),

    REPLICATION("replication"),

    CONNECT_CLUSTER_ID("connectClusterId"),

    CONNECT_CONNECTOR("connectConnector"),

    ;

    private final String name;

    MonitorSinkTagEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}