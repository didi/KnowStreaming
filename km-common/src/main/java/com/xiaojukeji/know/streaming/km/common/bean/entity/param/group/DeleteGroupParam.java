package com.xiaojukeji.know.streaming.km.common.bean.entity.param.group;

import com.xiaojukeji.know.streaming.km.common.enums.group.DeleteGroupTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteGroupParam extends GroupParam {
    protected DeleteGroupTypeEnum deleteGroupTypeEnum;

    public DeleteGroupParam(Long clusterPhyId, String groupName, DeleteGroupTypeEnum deleteGroupTypeEnum) {
        super(clusterPhyId, groupName);
        this.deleteGroupTypeEnum = deleteGroupTypeEnum;
    }
}
