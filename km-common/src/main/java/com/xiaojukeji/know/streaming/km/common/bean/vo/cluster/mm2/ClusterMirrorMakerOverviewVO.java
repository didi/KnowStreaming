package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 集群MM2信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "MM2概览信息")
public class ClusterMirrorMakerOverviewVO extends MirrorMakerBasicVO {
    @ApiModelProperty(value = "源Kafka集群Id", example = "1")
    private Long sourceKafkaClusterId;

    @ApiModelProperty(value = "源Kafka集群名称", example = "aaa")
    private String sourceKafkaClusterName;

    @ApiModelProperty(value = "目标Kafka集群Id", example = "1")
    private Long destKafkaClusterId;

    @ApiModelProperty(value = "目标Kafka集群名称", example = "aaa")
    private String destKafkaClusterName;

    /**
     * @see org.apache.kafka.connect.runtime.AbstractStatus.State
     */
    @ApiModelProperty(value = "状态", example = "RUNNING")
    private String state;

    @ApiModelProperty(value = "Task数", example = "100")
    private Integer taskCount;

    @ApiModelProperty(value = "心跳检测connector", example = "heartbeatConnector")
    private String heartbeatConnector;

    @ApiModelProperty(value = "进度确认connector", example = "checkpointConnector")
    private String checkpointConnector;


    @ApiModelProperty(value = "多个指标的当前值, 包括健康分/LogSize等")
    private BaseMetrics latestMetrics;

    @ApiModelProperty(value = "多个指标的历史曲线值，包括LogSize/BytesIn等")
    private List<MetricLineVO> metricLines;
}
