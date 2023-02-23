package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnterpriseLoadReBalance
@ApiModel(description = "集群均衡列表信息")
public class ClusterBalanceOverviewSubVO implements Serializable {
    @ApiModelProperty(value = "平均值", example = "cpu的平均值，43.4")
    private Double avg;

    @ApiModelProperty(value = "规格", example = "1000")
    private Double spec;

    @ApiModelProperty(value = "均衡状态", example = "0:已均衡，-1:低于均衡值，1高于均衡值")
    private Integer status ;

}
