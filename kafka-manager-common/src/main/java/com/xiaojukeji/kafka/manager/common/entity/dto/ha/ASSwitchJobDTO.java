package com.xiaojukeji.kafka.manager.common.entity.dto.ha;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description="主备切换任务")
public class ASSwitchJobDTO {
    @NotNull(message = "all不允许为NULL")
    @ApiModelProperty(value = "所有Topic")
    private Boolean all;

    @NotNull(message = "mustContainAllKafkaUserTopics不允许为NULL")
    @ApiModelProperty(value = "是否需要包含KafkaUser关联的所有Topic")
    private Boolean mustContainAllKafkaUserTopics;

    @NotNull(message = "activeClusterPhyId不允许为NULL")
    @ApiModelProperty(value="主集群ID")
    private Long activeClusterPhyId;

    @NotNull(message = "standbyClusterPhyId不允许为NULL")
    @ApiModelProperty(value="备集群ID")
    private Long standbyClusterPhyId;

    @NotNull(message = "topicNameList不允许为NULL")
    @ApiModelProperty(value="切换的Topic名称列表")
    private List<String> topicNameList;

    /**
     * kafkaUser+Client列表
     */
    @Valid
    @ApiModelProperty(value="切换的KafkaUser&ClientId列表，Client可以为空串")
    private List<KafkaUserAndClientDTO> kafkaUserAndClientIdList;
}
