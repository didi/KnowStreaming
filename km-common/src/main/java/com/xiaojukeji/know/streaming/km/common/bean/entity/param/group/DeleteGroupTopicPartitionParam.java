package com.xiaojukeji.know.streaming.km.common.bean.entity.param.group;

import com.xiaojukeji.know.streaming.km.common.enums.group.DeleteGroupTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteGroupTopicPartitionParam extends DeleteGroupTopicParam {
    protected Integer partitionId;

    public DeleteGroupTopicPartitionParam(Long clusterPhyId, String groupName, DeleteGroupTypeEnum deleteGroupTypeEnum, String topicName, Integer partitionId) {
        super(clusterPhyId, groupName, deleteGroupTypeEnum, topicName);
        this.partitionId = partitionId;
    }
}
