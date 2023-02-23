package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@EnterpriseLoadReBalance
public class ClusterBalanceIntervalDTO {
    @NotBlank(message = "clusterBalanceIntervalDTO.type不允许为空")
    @ApiModelProperty("均衡维度:cpu,disk,bytesIn,bytesOut")
    private String type;

    @NotNull(message = "clusterBalanceIntervalDTO.intervalPercent不允许为空")
    @ApiModelProperty("平衡区间百分比")
    private Double intervalPercent;

    @NotNull(message = "clusterBalanceIntervalDTO.priority不允许为空")
    @ApiModelProperty("优先级")
    private Integer priority;

}
