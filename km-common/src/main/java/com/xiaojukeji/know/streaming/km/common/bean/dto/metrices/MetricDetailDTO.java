package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "指标详细属性信息")
public class MetricDetailDTO extends BaseDTO {

    @ApiModelProperty("指标名称")
    private String metric;

    @ApiModelProperty("指标是否显示")
    private Boolean set;

    @NotNull(message = "MetricDetailDTO的rank字段应不为空")
    @ApiModelProperty("指标优先级")
    private Integer rank;

}
