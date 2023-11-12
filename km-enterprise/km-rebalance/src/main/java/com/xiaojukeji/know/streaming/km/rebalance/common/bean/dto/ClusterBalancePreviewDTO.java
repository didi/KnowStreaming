package com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalancePreviewDTO extends BaseDTO {

    @ApiModelProperty("集群id")
    private Long clusterId;

    @ApiModelProperty("均衡节点")
    private List<Integer> brokers;

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

    @ApiModelProperty("限流值")
    private Long throttleUnitB;

 }
