package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Group&Topic指标查询信息")
public class MetricGroupTopicDTO extends MetricDTO {
    @ApiModelProperty("Group名称")
    private String group;

    @ApiModelProperty("Topic名称")
    private String topic;
}
