package com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description="集群信息")
public class ClusterDetailVO extends ClusterBaseVO {
    @ApiModelProperty(value="Broker数")
    private Integer brokerNum;

    @ApiModelProperty(value="Topic数")
    private Integer topicNum;

    @ApiModelProperty(value="ConsumerGroup数")
    private Integer consumerGroupNum;

    @ApiModelProperty(value="ControllerID")
    private Integer controllerId;

    @ApiModelProperty(value="Region数")
    private Integer regionNum;

    @ApiModelProperty(value = "高可用关系：1:主, 0:备 , 其他:非高可用")
    private Integer haRelation;

    @ApiModelProperty(value = "互备集群名称")
    private String mutualBackupClusterName;

    @Override
    public String toString() {
        return "ClusterDetailVO{" +
                "brokerNum=" + brokerNum +
                ", topicNum=" + topicNum +
                ", consumerGroupNum=" + consumerGroupNum +
                ", controllerId=" + controllerId +
                ", regionNum=" + regionNum +
                ", haRelation=" + haRelation +
                ", mutualBackupClusterName='" + mutualBackupClusterName + '\'' +
                '}';
    }
}