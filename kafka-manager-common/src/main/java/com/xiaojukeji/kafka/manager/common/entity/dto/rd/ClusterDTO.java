package com.xiaojukeji.kafka.manager.common.entity.dto.rd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description = "集群接入&修改")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterDTO {
    @ApiModelProperty(value="集群Id, 修改时传")
    private Long clusterId;

    @ApiModelProperty(value="集群名称")
    private String clusterName;

    @ApiModelProperty(value="ZK地址, 不允许修改")
    private String zookeeper;

    @ApiModelProperty(value="bootstrap地址")
    private String bootstrapServers;

    @ApiModelProperty(value="数据中心")
    private String idc;

    @ApiModelProperty(value="Kafka安全配置")
    private String securityProperties;

    @ApiModelProperty(value="Jmx配置")
    private String jmxProperties;

    @ApiModelProperty(value="主集群Id")
    private Long activeClusterId;

    @ApiModelProperty(value="是否高可用")
    private boolean isHa;

    public boolean legal() {
        if (ValidateUtils.isNull(clusterName)
                || ValidateUtils.isNull(zookeeper)
                || ValidateUtils.isNull(idc)
                || ValidateUtils.isNull(bootstrapServers)
                || (isHa && ValidateUtils.isNull(activeClusterId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ClusterDTO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", idc='" + idc + '\'' +
                ", securityProperties='" + securityProperties + '\'' +
                ", jmxProperties='" + jmxProperties + '\'' +
                ", activeClusterId=" + activeClusterId +
                ", isHa=" + isHa +
                '}';
    }
}