package com.xiaojukeji.kafka.manager.common.entity.ao.ha.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Job详情")
public class HaJobDetail {
    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value="主集群ID")
    private Long activeClusterPhyId;

    @ApiModelProperty(value="备集群ID")
    private Long standbyClusterPhyId;

    @ApiModelProperty(value="Lag和")
    private Long sumLag;

    @ApiModelProperty(value="状态")
    private Integer status;
}
