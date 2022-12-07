package com.xiaojukeji.know.streaming.km.common.bean.entity.group;

import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.KSGroupDescription;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyb
 * @date 2022/10/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    /**
     * 集群id
     */
    private Long clusterPhyId;

    /**
     * group类型
     * @see GroupTypeEnum
     */
    private GroupTypeEnum type;

    /**
     * group名称
     */
    private String name;

    /**
     * group状态
     * @see GroupStateEnum
     */
    private GroupStateEnum state;

    /**
     * group成员数量
     */
    private Integer memberCount;

    /**
     * group消费的topic列表
     */
    private List<GroupTopicMember> topicMembers;

    /**
     * group分配策略
     */
    private String partitionAssignor;

    /**
     * group协调器brokerId
     */
    private int coordinatorId;

    public Group(Long clusterPhyId, String groupName, KSGroupDescription groupDescription) {
        this.clusterPhyId = clusterPhyId;
        this.type = GroupTypeEnum.getTypeByProtocolType(groupDescription.protocolType());
        this.name = groupName;
        this.state = GroupStateEnum.getByRawState(groupDescription.state());
        this.memberCount = groupDescription.members() == null ? 0 : groupDescription.members().size();
        this.topicMembers = new ArrayList<>();
        this.partitionAssignor = groupDescription.partitionAssignor();
        this.coordinatorId = groupDescription.coordinator() == null ? Constant.INVALID_CODE : groupDescription.coordinator().id();
    }
}
