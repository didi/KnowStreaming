package com.xiaojukeji.know.streaming.km.common.bean.dto.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/1/2
 */
@Data
@ApiModel(value = "Topic扩分区")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicExpansionDTO extends ClusterTopicDTO {
    @Min(value = 1, message = "incPartitionNum不允许为null或者小于0")
    @ApiModelProperty(value = "新增分区数", example = "1")
    private Integer incPartitionNum;

    @ApiModelProperty(value = "brokerId列表，为空时则选择所有的Broker", example = "[1, 2, 3]")
    private List<Integer> brokerIdList;
}