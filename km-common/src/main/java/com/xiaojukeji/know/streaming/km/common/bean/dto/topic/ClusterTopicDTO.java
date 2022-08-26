package com.xiaojukeji.know.streaming.km.common.bean.dto.topic;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description="Topic信息")
public class ClusterTopicDTO extends BaseDTO {
    @Min(value = 1, message = "clusterId不允许为null或者小于0")
    @ApiModelProperty(value = "集群ID, 默认为逻辑集群ID", example = "6")
    protected Long clusterId;

    @NotBlank(message = "topicName不允许为空串")
    @ApiModelProperty(value = "Topic名称", example = "know-streaming")
    protected String topicName;
}