package com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.common.IpPortData;
import com.xiaojukeji.kafka.manager.common.utils.NumberUtils;
import lombok.Data;

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
 *      "endpoints":["SASL_PLAINTEXT://127.0.0.1:9093"],
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
 * 	"endpoints":["SASL_PLAINTEXT://127.0.0.1:9093","PLAINTEXT://127.0.0.1:9092"],
 * 	"jmx_port":8099,
 * 	"host":"127.0.0.1",
 * 	"timestamp":"1628833925822",
 * 	"port":9092,
 * 	"version":4
 * }
 *
 * {
 * 	"listener_security_protocol_map":{"EXTERNAL":"SASL_PLAINTEXT","INTERNAL":"SASL_PLAINTEXT"},
 * 	"endpoints":["EXTERNAL://127.0.0.1:7092","INTERNAL://127.0.0.1:7093"],
 * 	"jmx_port":8099,
 * 	"host":null,
 * 	"timestamp":"1627289710439",
 * 	"port":-1,
 * 	"version":4
 * }
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrokerMetadata implements Serializable {
    private static final long serialVersionUID = 3918113492423375809L;

    private long clusterId;

    private int brokerId;

    private List<String> endpoints;

    // <EXTERNAL|INTERNAL, <ip, port>>
    private Map<String, IpPortData> endpointMap;

    private String host;

    private int port;

    @JsonProperty("jmx_port")
    private int jmxPort;

    private String version;

    private long timestamp;

    private String rack;

    @JsonIgnore
    public String getExternalHost() {
        if (!endpointMap.containsKey(KafkaConstant.EXTERNAL_KEY)) {
            return null;
        }
        return endpointMap.get(KafkaConstant.EXTERNAL_KEY).getIp();
    }

    @JsonIgnore
    public String getInternalHost() {
        if (!endpointMap.containsKey(KafkaConstant.INTERNAL_KEY)) {
            return null;
        }
        return endpointMap.get(KafkaConstant.INTERNAL_KEY).getIp();
    }

    public static void parseAndUpdateBrokerMetadata(BrokerMetadata brokerMetadata) {
        brokerMetadata.setEndpointMap(new HashMap<>());

        if (brokerMetadata.getEndpoints().isEmpty()) {
            return;
        }

        // example EXTERNAL://10.179.162.202:7092
        for (String endpoint: brokerMetadata.getEndpoints()) {
            int idx1 = endpoint.indexOf("://");
            int idx2 = endpoint.lastIndexOf(":");
            if (idx1 == -1 || idx2 == -1 || idx1 == idx2) {
                continue;
            }

            String brokerHost = endpoint.substring(idx1 + "://".length(), idx2);
            String brokerPort = endpoint.substring(idx2 + 1);

            brokerMetadata.getEndpointMap().put(endpoint.substring(0, idx1), new IpPortData(brokerHost, brokerPort));

            if (KafkaConstant.EXTERNAL_KEY.equals(endpoint.substring(0, idx1))) {
                // 优先使用external的地址进行展示
                brokerMetadata.setHost(brokerHost);
                brokerMetadata.setPort(NumberUtils.string2Integer(brokerPort));
            }

            if (null == brokerMetadata.getHost()) {
                brokerMetadata.setHost(brokerHost);
                brokerMetadata.setPort(NumberUtils.string2Integer(brokerPort));
            }
        }
    }
}


