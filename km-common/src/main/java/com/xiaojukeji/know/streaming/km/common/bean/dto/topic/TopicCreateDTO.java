package com.xiaojukeji.know.streaming.km.common.bean.dto.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Properties;

/**
 * @author huangyiminghappy@163.com, zengqiao
 * @date 2019-04-21
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "创建Topic")
public class TopicCreateDTO extends ClusterTopicDTO {
    @Min(value = 1, message = "partitionNum不允许为空，且最小值为1")
    @ApiModelProperty(value = "分区数", example = "3")
    private Integer partitionNum;

    @Min(value = 1, message = "replicaNum不允许为空，且最小值为1")
    @ApiModelProperty(value = "副本数", example = "2")
    private Integer replicaNum;

    @ApiModelProperty(value = "brokerId列表，为空时则选择所有的Broker", example = "[1, 2, 3]")
    private List<Integer> brokerIdList;

    @NotNull(message = "properties不允许为null")
    @ApiModelProperty(value = "Topic属性列表", example = "{ \"retention.ms\": \"9876543210\", \"retention.bytes\": \"1234567890\",}")
    private Properties properties;

    @NotNull(message = "description不允许为null")
    @ApiModelProperty(value = "备注", example = "测试Topic")
    private String description;
}
