package com.xiaojukeji.know.streaming.km.common.bean.vo.topic.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "TopicPartition信息")
public class TopicPartitionVO {
    @ApiModelProperty(value = "Topic名字", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value = "分区ID", example = "1")
    private Integer partitionId;

    @ApiModelProperty(value = "近期指标")
    private BaseMetrics latestMetrics;

    @ApiModelProperty(value = "leaderBrokerId", example = "12")
    private Integer leaderBrokerId;

    @ApiModelProperty(value = "AR", example = "[1, 2, 3]")
    private List<Integer> assignReplicas;

    @ApiModelProperty(value = "ISR", example = "[1, 2, 3]")
    private List<Integer> inSyncReplicas;
}
