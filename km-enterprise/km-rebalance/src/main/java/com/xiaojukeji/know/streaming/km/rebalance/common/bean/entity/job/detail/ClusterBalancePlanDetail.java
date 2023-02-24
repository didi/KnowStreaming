package com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.detail;

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
@ApiModel(description = "集群均衡详细信息")
public class ClusterBalancePlanDetail implements Serializable {
    @ApiModelProperty(value = "是否均衡，1：已均衡；2：未均衡")
    private Integer status;

    @ApiModelProperty(value = "brokerId")
    private Integer  brokerId;

    @ApiModelProperty(value = "broker host")
    private String  host;

    @ApiModelProperty(value = "均衡前 cpu")
    private Double  cpuBefore;

    @ApiModelProperty(value = "均衡前 disk")
    private Double  diskBefore;

    @ApiModelProperty(value = "均衡前 byteIn")
    private Double  byteInBefore;

    @ApiModelProperty(value = "均衡前 byteOut")
    private Double  byteOutBefore;

    @ApiModelProperty(value = "均衡后 cpu")
    private Double  cpuAfter;

    @ApiModelProperty(value = "是否均衡，1：已均衡；2：未均衡")
    private Integer cpuStatus;

    @ApiModelProperty(value = "均衡后 disk")
    private Double  diskAfter;

    @ApiModelProperty(value = "是否均衡，1：已均衡；2：未均衡")
    private Integer diskStatus;

    @ApiModelProperty(value = "均衡后 byteIn")
    private Double  byteInAfter;

    @ApiModelProperty(value = "是否均衡，1：已均衡；2：未均衡")
    private Integer byteInStatus;

    @ApiModelProperty(value = "均衡后 byteOut")
    private Double  byteOutAfter;

    @ApiModelProperty(value = "是否均衡，1：已均衡；2：未均衡")
    private Integer byteOutStatus;

    @ApiModelProperty(value = "均衡流入大小")
    private Double  inSize;

    @ApiModelProperty(value = "均衡流入副本个数")
    private Double  inReplica;

    @ApiModelProperty(value = "均衡流出大小")
    private Double  outSize;

    @ApiModelProperty(value = "均衡流出副本个数")
    private Double  outReplica;

}
