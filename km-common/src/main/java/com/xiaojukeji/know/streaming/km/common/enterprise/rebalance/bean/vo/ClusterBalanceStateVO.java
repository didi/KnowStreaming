package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@EnterpriseLoadReBalance
@ApiModel(description = "集群均衡状态信息")
public class ClusterBalanceStateVO implements Serializable {
    @ApiModelProperty(value = "均衡状态", example = "0:已均衡，2:未均衡")
    private Integer status;

    @ApiModelProperty(value = "是否开启均衡", example = "true:开启，false:未开启")
    private Boolean enable;

    @ApiModelProperty(value = "下次均衡开始时间")
    private Date    next;

    @ApiModelProperty(value = "子项统计详细信息", example = "cpu、disk")
    private Map<String, ClusterBalanceStateSubVO> sub;
}
