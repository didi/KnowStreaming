package com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 *
 * 存储Broker的元信息, 元信息对应的ZK节点是/brokers/ids/{brokerId}
 * 节点结构:
 * {
 *      "listener_security_protocol_map":{"SASL_PLAINTEXT":"SASL_PLAINTEXT"},
 *      "endpoints":["SASL_PLAINTEXT://10.179.162.202:9093"],
 *      "jmx_port":9999,
 *      "host":null,
 *      "timestamp":"1546632983233",
 *      "port":-1,
 *      "version":4,
 *      "rack": "CY"
 * }
 */
public class BrokerMetadata implements Cloneable {
    private long clusterId;

    private int brokerId;

    private List<String> endpoints;

    private String host;

    private int port;

    /*
     * ZK上对应的字段就是这个名字, 不要进行修改
     */
    private int jmx_port;

    private String version;

    private long timestamp;

    private String rack;

    public long getClusterId() {
        return clusterId;
    }

    public void setClusterId(long clusterId) {
        this.clusterId = clusterId;
    }

    public int getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(int brokerId) {
        this.brokerId = brokerId;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getJmxPort() {
        return jmx_port;
    }

    public void setJmxPort(int jmxPort) {
        this.jmx_port = jmxPort;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    @Override
    public String toString() {
        return "BrokerMetadata{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", endpoints=" + endpoints +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmx_port +
                ", version='" + version + '\'' +
                ", timestamp=" + timestamp +
                ", rack='" + rack + '\'' +
                '}';
    }
}

