package com.xiaojukeji.know.streaming.km.common.bean.dto.kafkauser;

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
@ApiModel(description="kafkaUser信息")
public class ClusterKafkaUserDTO {
    @Min(value = 1, message = "clusterId不允许为null或者小于0")
    @ApiModelProperty(value = "集群ID, 默认为逻辑集群ID", example = "6")
    protected Long clusterId;

    @NotBlank(message = "kafkaUser不允许为空串")
    @ApiModelProperty(value = "kafkaUser名称", example = "know-streaming")
    protected String kafkaUser;
}