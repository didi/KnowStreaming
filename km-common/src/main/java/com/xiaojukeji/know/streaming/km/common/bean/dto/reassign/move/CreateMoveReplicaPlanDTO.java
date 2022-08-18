package com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.move;

import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.ClusterTopicDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description="创建迁移Json")
public class CreateMoveReplicaPlanDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "分区ID列表", example = "0,1,2")
    private List<Integer> partitionIdList;

    @NotNull(message = "brokerIdList不允许为空")
    @ApiModelProperty(value = "BrokerID列表", example = "3,4,5")
    private List<Integer> brokerIdList;

    @NotNull(message = "enableRackAwareness不允许为空")
    @ApiModelProperty(value = "是否机架感知", example = "false")
    private Boolean enableRackAwareness;
}
