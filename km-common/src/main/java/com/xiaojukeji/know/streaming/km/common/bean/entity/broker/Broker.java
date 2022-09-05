package com.xiaojukeji.know.streaming.km.common.bean.entity.broker;


import com.alibaba.fastjson.TypeReference;
import com.xiaojukeji.know.streaming.km.common.bean.entity.common.IpPortData;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.zookeeper.znode.brokers.BrokerMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.Node;

import java.io.Serializable;
import java.util.Map;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Broker implements Serializable {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * BrokerID
     */
    private Integer brokerId;

    /**
     * 主机
     */
    private String host;

    /**
     * 服务端口
     */
    private Integer port;

    /**
     * JMX端口
     */
    private Integer jmxPort;

    /**
     * 启动时间
     */
    private Long startTimestamp;

    /**
     * rack信息
     */
    private String rack;

    /**
     * 是否存活
     */
    private Integer status;

    /**
     * 监听信息
     */
    private Map<String, IpPortData> endpointMap;

    public static Broker buildFrom(Long clusterPhyId, Node node, Long startTimestamp) {
        Broker metadata = new Broker();
        metadata.setClusterPhyId(clusterPhyId);
        metadata.setBrokerId(node.id());
        metadata.setHost(node.host());
        metadata.setPort(node.port());
        metadata.setJmxPort(-1);
        metadata.setStartTimestamp(startTimestamp);
        metadata.setRack(node.rack());
        metadata.setStatus(1);
        return metadata;
    }

    public static Broker buildFrom(Long clusterPhyId, Integer brokerId, BrokerMetadata brokerMetadata) {
        Broker metadata = new Broker();
        metadata.setClusterPhyId(clusterPhyId);
        metadata.setBrokerId(brokerId);
        metadata.setHost(brokerMetadata.getHost());
        metadata.setPort(brokerMetadata.getPort());
        metadata.setJmxPort(brokerMetadata.getJmxPort());
        metadata.setStartTimestamp(brokerMetadata.getTimestamp());
        metadata.setRack(brokerMetadata.getRack());
        metadata.setStatus(1);
        metadata.setEndpointMap(brokerMetadata.getEndpointMap());
        return metadata;
    }

    public static Broker buildFrom(BrokerPO brokerPO) {
        Broker broker = ConvertUtil.obj2Obj(brokerPO, Broker.class);
        String endpointMapStr = brokerPO.getEndpointMap();
        if (broker == null || endpointMapStr == null || endpointMapStr.equals("")) {
            return broker;
        }

        // 填充endpoint信息
        Map<String, IpPortData> endpointMap = ConvertUtil.str2ObjByJson(endpointMapStr, new TypeReference<Map<String, IpPortData>>(){});
        broker.setEndpointMap(endpointMap);
        return broker;
    }

    public String getJmxHost(String endPoint) {
        if (endPoint == null || endpointMap == null) {
            return host;
        }
        IpPortData ip = endpointMap.get(endPoint);
        return ip == null ? ip.getIp() : host;
    }

    public boolean alive() {
        return status != null && status > 0;
    }
}
