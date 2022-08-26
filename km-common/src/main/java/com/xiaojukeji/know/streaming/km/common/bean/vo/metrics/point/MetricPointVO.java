package com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point;

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
@ApiModel(description = "指标点")
public class MetricPointVO implements Comparable<MetricPointVO> {
    @ApiModelProperty(value = "指标名", example = "HealthScore")
    private String  name;

    @ApiModelProperty(value = "指标时间，毫秒时间戳", example = "13459484543")
    private Long    timeStamp;

    @ApiModelProperty(value = "指标值", example = "12.345")
    private String  value;

    @ApiModelProperty(value = "指标值聚合方式：avg、max、min、sum")
    private String  aggType;

    @Override
    public int compareTo(MetricPointVO o) {
        if(null == o){return 0;}

        return this.getTimeStamp().intValue() - o.getTimeStamp().intValue();
    }
}
