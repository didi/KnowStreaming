package com.xiaojukeji.kafka.manager.common.entity.dto.op.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author huangyiminghappy@163.com, zengqiao
 * @date 2022-06-29
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Topic高可用关联|解绑")
public class HaTopicRelationDTO {
    @NotNull(message = "主集群id不能为空")
    @ApiModelProperty(value = "主集群id")
    private Long activeClusterId;

    @NotNull(message = "备集群id不能为空")
    @ApiModelProperty(value = "备集群id")
    private Long standbyClusterId;

    @NotNull(message = "是否应用于所有topic")
    @ApiModelProperty(value = "是否应用于所有topic")
    private Boolean all;

    @ApiModelProperty(value = "需要关联|解绑的topic名称列表")
    private List<String> topicNames;

    @ApiModelProperty(value = "解绑是否保留备集群资源（topic,kafkaUser,group）")
    private Boolean retainStandbyResource;

    @Override
    public String toString() {
        return "HaTopicRelationDTO{" +
                ", activeClusterId=" + activeClusterId +
                ", standbyClusterId=" + standbyClusterId +
                ", all=" + all +
                ", topicNames=" + topicNames +
                ", retainStandbyResource=" + retainStandbyResource +
                '}';
    }

    public boolean paramLegal() {
        if(!all && ValidateUtils.isEmptyList(topicNames)) {
            return false;
        }
        return true;
    }
}
