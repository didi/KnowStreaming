package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "集群大盘信息")
public class ClusterPhyDashboardVO extends ClusterPhyBaseVO {
    @ApiModelProperty(value = "多个指标的当前值, 包括健康分/Brokers/LogSize等")
    private BaseMetrics latestMetrics;

    @ApiModelProperty(value = "多个指标的历史曲线值，包括LogSize/BytesIn等")
    private List<MetricLineVO> metricLines;

    @ApiModelProperty(value="是否存活，1：Live，0：Down", example = "1")
    private Integer alive;
}
