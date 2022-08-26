package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicPartitionKS;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Group&Partition指标查询信息")
public class MetricGroupPartitionDTO extends MetricDTO {
    @ApiModelProperty("Group 名称")
    private String group;

    @ApiModelProperty("Group 的 topic & partition 信息")
    private List<TopicPartitionKS> groupTopics;
}
