package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xiaojukeji.know.streaming.km.common.bean.entity.common.IpPortData;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *
 * {
 * 	"listener_security_protocol_map":{"SASL_PLAINTEXT":"SASL_PLAINTEXT","PLAINTEXT":"PLAINTEXT"},
 * 	"endpoints":["SASL_PLAINTEXT://10.179.162.202:9093","PLAINTEXT://10.179.162.202:9092"],
 * 	"jmx_port":8099,
 * 	"host":"10.179.162.202",
 * 	"timestamp":"1628833925822",
 * 	"port":9092,
 * 	"version":4
 * }
 *
 * {
 * 	"listener_security_protocol_map":{"EXTERNAL":"SASL_PLAINTEXT","INTERNAL":"SASL_PLAINTEXT"},
 * 	"endpoints":["EXTERNAL://10.179.162.202:7092","INTERNAL://10.179.162.202:7093"],
 * 	"jmx_port":8099,
 * 	"host":null,
 * 	"timestamp":"1627289710439",
 * 	"port":-1,
 * 	"version":4
 * }
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrokerMetadata implements Serializable {
    private static final long serialVersionUID = 3918113492423375809L;

    private List<String> endpoints;

    // <EXTERNAL|INTERNAL, <ip, port>>
    private Map<String, IpPortData> endpointMap;

    private String host;

    private Integer port;

    @JsonProperty("jmx_port")
    private Integer jmxPort;

    private Integer version;

    private Long timestamp;

    private String rack;

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public Map<String, IpPortData> getEndpointMap() {
        if (endpointMap == null) {
            this.parseBrokerMetadata();
        }

        return endpointMap;
    }

    public String getHost() {
        if (endpointMap == null) {
            this.parseBrokerMetadata();
        }

        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        if (endpointMap == null) {
            this.parseBrokerMetadata();
        }

        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    private synchronized void parseBrokerMetadata() {
        if (this.endpointMap != null) {
            return;
        }

        if (this.endpoints == null || this.endpoints.isEmpty()) {
            this.endpointMap = new HashMap<>(0);
            return;
        }

        Map<String, IpPortData> tempEndpointMap = new HashMap<>();

        // example EXTERNAL://10.179.162.202:7092
        for (String endpoint: this.endpoints) {
            int idx1 = endpoint.indexOf("://");
            int idx2 = endpoint.lastIndexOf(":");
            if (idx1 == -1 || idx2 == -1 || idx1 == idx2) {
                continue;
            }

            String brokerHost = endpoint.substring(idx1 + "://".length(), idx2);
            String brokerPort = endpoint.substring(idx2 + 1);

            tempEndpointMap.put(endpoint.substring(0, idx1), new IpPortData(brokerHost, brokerPort));

            if (KafkaConstant.INTERNAL_KEY.equals(endpoint.substring(0, idx1))) {
                // 优先使用internal的地址进行展示
                this.host = brokerHost;
                this.port = ConvertUtil.string2Integer(brokerPort);
            }

            if (null == this.host) {
                this.host = brokerHost;
                this.port = ConvertUtil.string2Integer(brokerPort);
            }
        }

        this.endpointMap = tempEndpointMap;
    }

    public static void main(String[] args) {
        String str = "{\t\n" +
                "\t\"listener_security_protocol_map\":{\"EXTERNAL\":\"SASL_PLAINTEXT\",\"INTERNAL\":\"SASL_PLAINTEXT\"},\n" +
                "\t\"endpoints\":[\"EXTERNAL://10.179.162.202:7092\",\"INTERNAL://10.179.162.202:7093\"],\n" +
                "\t\"jmx_port\":8099,\n" +
                "\t\"host\":null,\n" +
                "\t\"timestamp\":\"1627289710439\",\n" +
                "\t\"port\":-1,\n" +
                "\t\"version\":4\n" +
                "}";

        BrokerMetadata bm = JSON.parseObject(str, BrokerMetadata.class);
        System.out.println(bm.getHost());
        System.out.println(JSON.toJSON(bm));
    }
}

