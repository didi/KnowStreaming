package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
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
@ApiModel(description = "指标查询基础信息")
public class MetricDTO extends BaseDTO {

    @ApiModelProperty("开始时间")
    private Long         startTime;

    @ApiModelProperty("结束时间")
    private Long         endTime;

    @ApiModelProperty(value = "聚合类型:avg、max、min、sum，默认：avg", example = "avg")
    private String       aggType = "avg";

    @ApiModelProperty(value = "指标类型/指标名称", example = "[\"topics\"]")
    private List<String> metricsNames;

    @ApiModelProperty("Top-Level:5,10,15,20")
    private Integer      topNu = 5;
}
