package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Topic信息概览")
public class ClusterPhyTopicsOverviewVO extends BaseTimeVO {
    @ApiModelProperty(value = "Topic名称", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value = "说明", example = "测试")
    private String description;

    @ApiModelProperty(value = "分区数", example = "3")
    private Integer partitionNum;

    @ApiModelProperty(value = "保存时间(ms)", example = "172800000")
    private Long retentionTimeUnitMs;

    @ApiModelProperty(value = "副本数", example = "2")
    private Integer replicaNum;

    @ApiModelProperty(value = "处于镜像复制中", example = "true")
    private Boolean inMirror;

    @ApiModelProperty(value = "多个指标的当前值, 包括健康分/LogSize等")
    private BaseMetrics latestMetrics;

    @ApiModelProperty(value = "多个指标的历史曲线值，包括LogSize/BytesIn等")
    private List<MetricLineVO> metricLines;
}
