package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceStrategyDTO extends BaseDTO {

    @ApiModelProperty("是否是周期性任务")
    private boolean scheduleJob;

    @NotBlank(message = "scheduleCron不允许为空")
    @ApiModelProperty("如果是周期任务，那么任务的周期cron表达式")
    private String  scheduleCron;

    @NotNull(message = "status不允许为空")
    @ApiModelProperty("周期任务状态：0:不开启，1：开启")
    private Integer status;

    @NotNull(message = "clusterId不允许为空")
    @ApiModelProperty("集群id")
    private Long clusterId;

    @ApiModelProperty("均衡节点")
    private List<Integer> brokers;

    @ApiModelProperty("topic黑名单")
    private List<String> topicBlackList;

    @NotNull(message = "clusterBalanceIntervalDTO不允许为空")
    @ApiModelProperty("均衡区间详情")
    private List<ClusterBalanceIntervalDTO> clusterBalanceIntervalList;

    @NotNull(message = "metricCalculationPeriod不允许为空")
    @ApiModelProperty("指标计算周期，单位秒")
    private Integer metricCalculationPeriod;

    @NotNull(message = "parallelNum不允许为空")
    @ApiModelProperty("任务并行数（0代表不限）")
    private Integer parallelNum;

    @NotNull(message = "executionStrategy不允许为空")
    @ApiModelProperty("执行策略， 1：优先最大副本，2：优先最小副本")
    private Integer executionStrategy;

    @Min(value = 1, message = "throttleUnitB不允许小于1")
    @ApiModelProperty("限流值")
    private Long throttleUnitB;

    @ApiModelProperty("备注说明")
    private String description;

 }
