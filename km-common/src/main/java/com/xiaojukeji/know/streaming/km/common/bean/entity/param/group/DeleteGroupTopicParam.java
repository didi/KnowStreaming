package com.xiaojukeji.know.streaming.km.common.bean.entity.param.group;

import com.xiaojukeji.know.streaming.km.common.enums.group.DeleteGroupTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteGroupTopicParam extends DeleteGroupParam {
    protected String topicName;

    public DeleteGroupTopicParam(Long clusterPhyId, String groupName, DeleteGroupTypeEnum deleteGroupTypeEnum, String topicName) {
        super(clusterPhyId, groupName, deleteGroupTypeEnum);
        this.topicName = topicName;
    }
}
