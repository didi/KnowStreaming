package com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@EnterpriseLoadReBalance
@ApiModel(description = "集群均衡信息")
public class ClusterBalancePlanVO implements Serializable {
    @ApiModelProperty(value = "均衡计划类型，1：立即均衡；2：周期均衡")
    private Integer type;

    @ApiModelProperty(value = "均衡执行的节点范围")
    private List<String> brokers;

    @ApiModelProperty(value = "均衡执行的Topic黑名单")
    private List<String> blackTopics;

    @ApiModelProperty(value = "均衡执行移入的Topic名单")
    private List<String> topics;

    @ApiModelProperty(value = "均衡总迁移的磁盘大小，单位byte")
    private Double moveSize;

    @ApiModelProperty(value = "均衡总迁移的副本个数")
    private Integer replicas;

    @ApiModelProperty(value = "均衡阈值")
    private String  threshold;

    @ApiModelProperty(value = "reassignment json")
    private String  reassignmentJson;

    @ApiModelProperty(value = "均衡区间信息")
    private List<ClusterBalanceIntervalVO> clusterBalanceIntervalList;

    @ApiModelProperty(value = "均衡计划明细")
    private List<ClusterBalancePlanDetailVO> detail;
}
