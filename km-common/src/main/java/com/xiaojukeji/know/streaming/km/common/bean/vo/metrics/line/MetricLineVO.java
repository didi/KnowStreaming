package com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line;

import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "单值指标线，一条线就代表一个指标的多个指标点")
public class MetricLineVO implements Serializable {
    @ApiModelProperty(value = "指标对象名称：brokerId、topicName")
    private String  name;

    @ApiModelProperty(value = "指标名称")
    private String  metricName;

    @ApiModelProperty(value = "指标点集合")
    private List<MetricPointVO> metricPoints;
}
