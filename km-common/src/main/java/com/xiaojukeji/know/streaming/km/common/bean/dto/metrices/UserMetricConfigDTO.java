package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户指标显示设置配置信息")
public class UserMetricConfigDTO extends BaseDTO {
    @ApiModelProperty("指标展示设置项，key：指标名；value：是否展现(true展现/false不展现)")
    private Map<String, Boolean> metricsSet;
}
