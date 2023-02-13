package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/8
 */
@Data
@ApiModel(value = "Topic信息")
public class TopicHaVO {
    @ApiModelProperty(value = "物理集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "物理集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "高可用关系：1:主topic, 0:备topic , 其他:非高可用topic")
    private Integer haRelation;

}