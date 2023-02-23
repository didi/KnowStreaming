package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@EnterpriseLoadReBalance
@ApiModel(description = "集群均衡历史信息")
public class ClusterBalanceHistorySubVO implements Serializable {
    @ApiModelProperty(value = "均衡成功节点数")
    private Long  successNu;

    @ApiModelProperty(value = "未均衡成功节点数")
    private Long  failedNu;
}
