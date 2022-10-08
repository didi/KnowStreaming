package com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line;

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
@ApiModel(description = "多条指标线，由多个指标的指标线构成")
public class MetricMultiLinesVO {

    @ApiModelProperty(value = "指标名称")
    private String metricName;

    @ApiModelProperty(value = "指标名称对应的指标线")
    private List<MetricLineVO> metricLines;
}
