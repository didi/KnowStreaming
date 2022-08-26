package com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line;

import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<MetricPointVO> getMetricPoints(String resName) {
        if (ValidateUtils.isNull(metricLines)) {
            return new ArrayList<>();
        }

        List<MetricLineVO> voList = metricLines.stream().filter(elem -> elem.getName().equals(resName)).collect(Collectors.toList());
        if (ValidateUtils.isEmptyList(voList)) {
            return new ArrayList<>();
        }

        // 仅获取idx=0的指标
        return voList.get(0).getMetricPoints();
    }

}
