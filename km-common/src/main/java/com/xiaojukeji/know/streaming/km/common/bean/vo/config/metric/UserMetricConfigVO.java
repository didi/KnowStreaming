package com.xiaojukeji.know.streaming.km.common.bean.vo.config.metric;

import com.xiaojukeji.know.streaming.km.common.bean.vo.version.VersionItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "单值指标线，一条线就代表一个指标的多个指标点")
public class UserMetricConfigVO extends VersionItemVO {
    @ApiModelProperty(value = "该指标用户是否设置展现", example = "true")
    private Boolean set;

    @ApiModelProperty(value = "该指标展示优先级", example = "1")
    private Integer rank;
}
