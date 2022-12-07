package com.xiaojukeji.know.streaming.km.common.bean.dto.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
public class ClusterConnectorsOverviewDTO extends PaginationSortDTO {
    @NotNull(message = "latestMetricNames不允许为空")
    @ApiModelProperty("需要指标点的信息")
    private List<String> latestMetricNames;

    @NotNull(message = "metricLines不允许为空")
    @ApiModelProperty("需要指标曲线的信息")
    private MetricDTO metricLines;

    @ApiModelProperty("需要排序的指标名称列表,比较第一个不为空的metric")
    private List<String> sortMetricNameList;
}
