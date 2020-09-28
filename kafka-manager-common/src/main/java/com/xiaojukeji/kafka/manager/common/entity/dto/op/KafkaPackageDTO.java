package com.xiaojukeji.kafka.manager.common.entity.dto.op;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/26
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="集群任务")
public class KafkaPackageDTO {
    @ApiModelProperty(value="名称")
    private String name;

    @ApiModelProperty(value="文件类型")
    private Integer fileType;

    @ApiModelProperty(value="md5")
    private String md5;

    @ApiModelProperty(value="描述备注")
    private String description;
}