package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@EnterpriseLoadReBalance
@ApiModel(description = "集群均衡列表信息")
public class ClusterBalanceOverviewVO implements Serializable {
    @ApiModelProperty(value = "brokerId", example = "123")
    private Integer brokerId;

    @ApiModelProperty(value = "broker host")
    private String host;

    @ApiModelProperty(value = "broker 对应的 rack")
    private String  rack;

    @ApiModelProperty(value = "leader")
    private Integer  leader;

    @ApiModelProperty(value = "replicas")
    private Integer  replicas;

    @ApiModelProperty(value = "子项统计详细信息", example = "cpu、disk")
    private Map<String, ClusterBalanceOverviewSubVO> sub;
}
