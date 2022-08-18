package com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.plan;

import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@ApiModel(description = "迁移计划")
public class ReassignTopicPlanDTO {
    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="需要迁移Topic分区列表", example = "[1, 2, 3]")
    private List<Integer> partitionIdList;

    @ApiModelProperty(value="需要迁移Topic分区数", example = "10")
    private Integer partitionNum;

    @ApiModelProperty(value="当前副本数", example = "2")
    private Integer presentReplicaNum;

    @ApiModelProperty(value="新的副本数", example = "2")
    private Integer newReplicaNum;

    @ApiModelProperty(value="当前BrokerId列表")
    private List<Integer> originalBrokerIdList;

    @ApiModelProperty(value="迁移BrokerId列表")
    private List<Integer> reassignBrokerIdList;

    @ApiModelProperty(value="迁移计划列表")
    private List<ReassignPartitionPlanDTO> partitionPlanList;

    @ApiModelProperty(value="Topic当前数据保存时间", example = "10")
    private Long originalRetentionTimeUnitMs;

    @ApiModelProperty(value="Topic迁移时数据保存时间", example = "10")
    private Long reassignRetentionTimeUnitMs;

    @ApiModelProperty(value="近N天的avg的BytesIn")
    private List<MetricPointVO> latestDaysAvgBytesInList;

    @ApiModelProperty(value="近N天的max的BytesIn")
    private List<MetricPointVO> latestDaysMaxBytesInList;
}
