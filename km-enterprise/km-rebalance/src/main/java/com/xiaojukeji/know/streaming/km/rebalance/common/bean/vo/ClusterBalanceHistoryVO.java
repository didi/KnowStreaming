package com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo;

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
@ApiModel(description = "集群均衡历史信息")
public class ClusterBalanceHistoryVO implements Serializable {
    @ApiModelProperty(value = "均衡开始执行时间")
    private Date begin;

    @ApiModelProperty(value = "均衡执行结束时间")
    private Date end;

    @ApiModelProperty(value = "均衡任务id")
    private Long jobId;

    @ApiModelProperty(value = "子项均衡历史信息", example = "cpu、disk")
    private Map<String, ClusterBalanceHistorySubVO> sub;


}
