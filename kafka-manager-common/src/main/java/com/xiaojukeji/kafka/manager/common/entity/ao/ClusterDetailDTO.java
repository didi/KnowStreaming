package com.xiaojukeji.kafka.manager.common.entity.ao;

import lombok.Data;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
public class ClusterDetailDTO {
    private Long clusterId;

    private String clusterName;

    private String zookeeper;

    private String bootstrapServers;

    private String kafkaVersion;

    private String idc;

    private Integer mode;

    private String securityProperties;

    private String jmxProperties;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer brokerNum;

    private Integer topicNum;

    private Integer consumerGroupNum;

    private Integer controllerId;

    private Integer regionNum;

    private Integer haRelation;

    private String mutualBackupClusterName;

    @Override
    public String toString() {
        return "ClusterDetailDTO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", kafkaVersion='" + kafkaVersion + '\'' +
                ", idc='" + idc + '\'' +
                ", mode=" + mode +
                ", securityProperties='" + securityProperties + '\'' +
                ", jmxProperties='" + jmxProperties + '\'' +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", brokerNum=" + brokerNum +
                ", topicNum=" + topicNum +
                ", consumerGroupNum=" + consumerGroupNum +
                ", controllerId=" + controllerId +
                ", regionNum=" + regionNum +
                ", haRelation=" + haRelation +
                ", mutualBackupClusterName='" + mutualBackupClusterName + '\'' +
                '}';
    }
}