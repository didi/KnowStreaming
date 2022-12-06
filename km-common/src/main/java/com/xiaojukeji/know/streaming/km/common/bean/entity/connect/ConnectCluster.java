package com.xiaojukeji.know.streaming.km.common.bean.entity.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.EntityIdInterface;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConnectCluster implements Serializable, Comparable<ConnectCluster>, EntityIdInterface {
    /**
     * 集群ID
     */
    private Long id;

    /**
     * 集群名字
     */
    private String name;

    /**
     * 集群使用的消费组
     */
    private String groupName;

    /**
     * 集群使用的消费组状态，也表示集群状态
     * @see com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum
     */
    private Integer state;

    /**
     * worker中显示的leader url信息
     */
    private String memberLeaderUrl;

    /**
     * 版本信息
     */
    private String version;

    /**
     * jmx配置
     * @see com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig
     */
    private String jmxProperties;

    /**
     * Kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * 集群地址
     */
    private String clusterUrl;

    @Override
    public int compareTo(ConnectCluster connectCluster) {
        return this.id.compareTo(connectCluster.getId());
    }
}
