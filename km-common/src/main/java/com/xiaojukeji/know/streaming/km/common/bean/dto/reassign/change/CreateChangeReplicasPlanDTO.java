package com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.change;

import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.ClusterTopicDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description="创建迁移Json")
public class CreateChangeReplicasPlanDTO extends ClusterTopicDTO {
    @NotNull(message = "newReplicaNum必须大于等于1")
    @Min(value = 1, message = "newReplicaNum必须大于等于1")
    @ApiModelProperty(value = "副本数", example = "3")
    private Integer newReplicaNum;

    @NotNull(message = "brokerIdList不允许为空")
    @ApiModelProperty(value = "BrokerID列表", example = "3,4,5")
    private List<Integer> brokerIdList;
}
