package com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo;


import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@EnterpriseLoadReBalance
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "集群均衡状态子项的详细统计信息")
public class ClusterBalanceStateSubVO {

    @ApiModelProperty(value = "平均值", example = "cpu的平均值，43.4")
    private Double avg;

    @ApiModelProperty(value = "周期均衡时的均衡区间", example = "cpu的均衡值")
    private Double interval;

    @ApiModelProperty(value = "处于周期均衡时的均衡区间的最小值以下的broker个数", example = "4")
    private Long    smallNu;

    @ApiModelProperty(value = "处于周期均衡时的均衡区间的broker个数", example = "4")
    private Long    betweenNu;

    @ApiModelProperty(value = "处于周期均衡时的均衡区间的最大值以上的broker个数", example = "4")
    private Long    bigNu;
}
