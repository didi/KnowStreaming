package com.xiaojukeji.know.streaming.km.common.bean.vo.job;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(description = "job流量")
public class JobTrafficBrokerVO extends BaseTimeVO {
    @ApiModelProperty(value = "任务id")
    private Long id;

    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "brokerHost")
    private String brokerHost;

    @ApiModelProperty(value = "Job BytesIn，单位byte")
    private Double byteInJob;

    @ApiModelProperty(value = "Job BytesOut，单位byte")
    private Double byteOutJob;

    @ApiModelProperty(value = "Total BytesIn，单位byte")
    private Double byteInTotal;

    @ApiModelProperty(value = "Total BytesOut，单位byte")
    private Double byteOutTotal;

    @ApiModelProperty(value = "流入该broker的broker信息")
    private Map<Integer, String> inBrokers;

    @ApiModelProperty(value = "流出该broker的broker信息")
    private Map<Integer, String> outBrokers;
}
