package com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan;

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
public class ReassignTopicPlanVO {
    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="当前BrokerId列表")
    private List<Integer> currentBrokerIdList;

    @ApiModelProperty(value="迁移BrokerId列表")
    private List<Integer> reassignBrokerIdList;

    @ApiModelProperty(value="迁移计划列表")
    private List<ReassignPartitionPlanVO> partitionPlanList;
}
