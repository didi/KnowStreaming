package com.xiaojukeji.know.streaming.km.common.bean.po.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "group_member")
public class GroupMemberPO extends BasePO {
    private Long clusterPhyId;

    private String topicName;

    private String groupName;

    private String state;

    private Integer memberCount;

    public GroupMemberPO(Long clusterPhyId, String topicName, String groupName, Date updateTime) {
        this.clusterPhyId = clusterPhyId;
        this.topicName = topicName;
        this.groupName = groupName;
        this.state = GroupStateEnum.UNKNOWN.getState();
        this.memberCount = 0;
        this.updateTime = updateTime;
    }
}