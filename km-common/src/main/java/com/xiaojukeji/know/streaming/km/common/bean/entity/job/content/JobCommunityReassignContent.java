package com.xiaojukeji.know.streaming.km.common.bean.entity.job.content;

import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.plan.ReassignTopicPlanDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description="创建/修改迁移及扩缩副本任务")
public class JobCommunityReassignContent extends BaseJobCreateContent {
    @Min(value = 1, message = "clusterId不允许为null或者小于0")
    @ApiModelProperty(value = "集群ID, 默认为逻辑集群ID", example = "6")
    private Long clusterId;

    @Min(value = 1, message = "throttle不允许为null或者小于0")
    @ApiModelProperty(value = "限流值", example = "102400000")
    private Long throttleUnitB;

    @NotNull(message = "description不允许为null")
    @ApiModelProperty(value = "备注信息", example = "测试")
    private String description;

    @NotNull(message = "topicPlanList不允许为空")
    @ApiModelProperty("迁移计划")
    private List<ReassignTopicPlanDTO> topicPlanList;
}
