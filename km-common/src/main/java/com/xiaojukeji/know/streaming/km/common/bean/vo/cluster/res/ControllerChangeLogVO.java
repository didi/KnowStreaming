package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res;

import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "集群ControllerChangeLog信息")
public class ControllerChangeLogVO extends BasePO {

    @ApiModelProperty(value = "改变时间")
    private Long changeTime;

    @ApiModelProperty(value = "BrokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "rack")
    private String rack;

    @ApiModelProperty(value = "主机")
    private String host;

    @ApiModelProperty(value = "kafka 集群 id")
    private Long clusterPhyId;

}
