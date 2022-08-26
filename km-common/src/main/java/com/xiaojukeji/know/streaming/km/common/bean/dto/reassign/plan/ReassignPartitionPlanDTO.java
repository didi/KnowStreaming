package com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.plan;

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
public class ReassignPartitionPlanDTO {
    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="分区ID")
    private Integer partitionId;

    @ApiModelProperty(value="当前BrokerId列表")
    private List<Integer> originalBrokerIdList;

    @ApiModelProperty(value="迁移BrokerId列表")
    private List<Integer> reassignBrokerIdList;
}
