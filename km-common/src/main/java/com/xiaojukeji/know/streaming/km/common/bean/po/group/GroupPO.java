package com.xiaojukeji.know.streaming.km.common.bean.po.group;


import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "group")
public class GroupPO extends BasePO {
    /**
     * 集群id
     */
    private Long clusterPhyId;

    /**
     * group类型
     *
     * @see GroupTypeEnum
     */
    private Integer type;

    /**
     * group名称
     */
    private String name;

    /**
     * group状态
     *
     * @see GroupStateEnum
     */
    private String state;

    /**
     * group成员数量
     */
    private Integer memberCount;

    /**
     * group消费的topic列表
     */
    private String topicMembers;

    /**
     * group分配策略
     */
    private String partitionAssignor;

    /**
     * group协调器brokerId
     */
    private int coordinatorId;

    public boolean equal2GroupPO(GroupPO groupPO) {
        if (groupPO == null) {
            return false;
        }

        return coordinatorId == groupPO.coordinatorId
                && Objects.equals(clusterPhyId, groupPO.clusterPhyId)
                && Objects.equals(type, groupPO.type)
                && Objects.equals(name, groupPO.name)
                && Objects.equals(state, groupPO.state)
                && Objects.equals(memberCount, groupPO.memberCount)
                && Objects.equals(topicMembers, groupPO.topicMembers)
                && Objects.equals(partitionAssignor, groupPO.partitionAssignor);
    }
}
