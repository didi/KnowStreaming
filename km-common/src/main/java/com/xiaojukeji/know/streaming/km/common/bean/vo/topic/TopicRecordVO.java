package com.xiaojukeji.know.streaming.km.common.bean.vo.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.record.RecordHeaderKS;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "TopicRecord信息")
public class TopicRecordVO {
    @ApiModelProperty(value = "Topic名称", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value = "分区ID", example = "6")
    private Integer partitionId;

    @ApiModelProperty(value = "记录的offset", example = "534234232234")
    private Long offset;

    @ApiModelProperty(value = "记录的时间", example = "1645603045040")
    private Long timestampUnitMs;

    @ApiModelProperty(value = "记录的headers")
    private List<RecordHeaderKS> headerList;

    @ApiModelProperty(value = "记录Key", example = "测试Key")
    private String key;

    @ApiModelProperty(value = "记录值", example = "测试Value测试Value测试Value测试Value")
    private String value;
}
