package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 集群Connector信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Connector概览信息")
public class ClusterConnectorOverviewVO extends ConnectorBasicVO {
    @ApiModelProperty(value = "Connector插件名称", example = "know-streaming")
    private String connectorClassName;

    @ApiModelProperty(value = "Connector类型", example = "source")
    private String connectorType;

    /**
     * @see org.apache.kafka.connect.runtime.AbstractStatus.State
     */
    @ApiModelProperty(value = "状态", example = "RUNNING")
    private String state;

    @ApiModelProperty(value = "Task数", example = "100")
    private Integer taskCount;

    @ApiModelProperty(value = "访问的Topic列表", example = "")
    private List<String> topicNameList;

    @ApiModelProperty(value = "多个指标的当前值, 包括健康分/LogSize等")
    private BaseMetrics latestMetrics;

    @ApiModelProperty(value = "多个指标的历史曲线值，包括LogSize/BytesIn等")
    private List<MetricLineVO> metricLines;
}
