package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job.content;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.content.BaseJobCreateContent;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.dto.ClusterBalanceIntervalDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.List;

@Data
@EnterpriseLoadReBalance
public class JobClusterBalanceContent extends BaseJobCreateContent {
    @Min(value = 1, message = "clusterId不允许为null或者小于0")
    @ApiModelProperty(value = "集群ID, 默认为逻辑集群ID", example = "6")
    private Long clusterId;

    @Min(value = 1, message = "throttle不允许为null或者小于0")
    @ApiModelProperty(value = "限流值", example = "102400000")
    private Long throttleUnitB;

    @ApiModelProperty("topic黑名单")
    private List<String> topicBlackList;

    @ApiModelProperty("均衡区间详情")
    private List<ClusterBalanceIntervalDTO> clusterBalanceIntervalList;

    @ApiModelProperty("指标计算周期，单位分钟")
    private Integer metricCalculationPeriod;

    @ApiModelProperty("任务并行数")
    private Integer parallelNum;

    @ApiModelProperty("执行策略， 1：优先最大副本，2：优先最小副本")
    private Integer executionStrategy;

    @ApiModelProperty("备注说明")
    private String description;

    @ApiModelProperty("是否是周期性任务")
    private boolean scheduleJob;
}
