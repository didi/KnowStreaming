package com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@EnterpriseLoadReBalance
public class ClusterBalanceIntervalVO {
    @ApiModelProperty("均衡维度:cpu,disk,bytesIn,bytesOut")
    private String type;

    @ApiModelProperty("平衡区间百分比")
    private Double intervalPercent;

    @ApiModelProperty("优先级")
    private Integer priority;

}
