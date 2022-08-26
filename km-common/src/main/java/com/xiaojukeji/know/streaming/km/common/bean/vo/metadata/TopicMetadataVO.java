package com.xiaojukeji.know.streaming.km.common.bean.vo.metadata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@Data
@ApiModel(description="Topic元信息")
public class TopicMetadataVO {
    @ApiModelProperty(value="Topic名称", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value="Topic分区列表", example = "[1, 2, 3]")
    private List<Integer> partitionIdList;

    @ApiModelProperty(value="Topic分区数", example = "10")
    private Integer partitionNum;

    @ApiModelProperty(value="副本数", example = "2")
    private Integer replicaNum;

    /**
     * @see com.xiaojukeji.know.streaming.km.common.enums.topic.TopicTypeEnum
     */
    @ApiModelProperty(value="Topic类型，1是Kafka内部的Topic", example = "0")
    private Integer type;
}