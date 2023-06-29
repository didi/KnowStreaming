package com.xiaojukeji.know.streaming.km.common.bean.po.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "group_member")
public class GroupMemberPO extends BasePO {
    private Long clusterPhyId;

    private String topicName;

    private String groupName;

    private String state;

    private Integer memberCount;

    public GroupMemberPO(Long clusterPhyId, String topicName, String groupName, String state, Integer memberCount) {
        this.clusterPhyId = clusterPhyId;
        this.topicName = topicName;
        this.groupName = groupName;
        this.state = state;
        this.memberCount = memberCount;
    }
    public GroupMemberPO(Long clusterPhyId, String topicName, String groupName, String state, Integer memberCount, Date updateTime) {
        this.clusterPhyId = clusterPhyId;
        this.topicName = topicName;
        this.groupName = groupName;
        this.state = state;
        this.memberCount = memberCount;
        this.updateTime = updateTime;
    }

    public boolean equal2GroupMemberPO(GroupMemberPO that) {
        if (that == null) {
            return false;
        }

        return Objects.equals(clusterPhyId, that.clusterPhyId)
                && Objects.equals(topicName, that.topicName)
                && Objects.equals(groupName, that.groupName)
                && Objects.equals(state, that.state)
                && Objects.equals(memberCount, that.memberCount);
    }
}