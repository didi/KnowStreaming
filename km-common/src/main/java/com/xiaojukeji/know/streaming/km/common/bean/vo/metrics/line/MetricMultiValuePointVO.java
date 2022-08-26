package com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "多值指标点，一个点只代表多个指标的多个值")
public class MetricMultiValuePointVO extends BaseTimeVO implements Comparable<MetricMultiValuePointVO>{

    @ApiModelProperty(value = "指标时间，毫秒时间戳", example = "13459484543")
    private Long    timeStamp;

    @ApiModelProperty(value = "多指标多个值的map组合", example = "")
    private Map<String, String> values;

    @ApiModelProperty(value = "指标值聚合方式：avg、max、min、sum")
    private String  aggType;

    @Override
    public int compareTo(MetricMultiValuePointVO o) {
        if(null == o){return 0;}

        return this.getTimeStamp().intValue() - o.getTimeStamp().intValue();
    }
}
