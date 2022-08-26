package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
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
@ApiModel(description = "指标查询基础信息")
public class MetricRealTimeDTO extends BaseDTO {
    @ApiModelProperty("指标类型")
    private Integer metricType;

    @ApiModelProperty("指标名称")
    private String metricName;
}
