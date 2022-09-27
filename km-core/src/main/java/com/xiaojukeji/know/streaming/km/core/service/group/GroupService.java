package com.xiaojukeji.know.streaming.km.core.service.group;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.common.TopicPartition;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GroupService {
    /**
     * 从Kafka中获取消费组
     * @param clusterPhyId 集群ID
     * @return
     * @throws NotExistException
     * @throws AdminOperateException
     */
    List<String> listGroupsFromKafka(Long clusterPhyId) throws NotExistException, AdminOperateException;

    Map<TopicPartition, Long> getGroupOffset(Long clusterPhyId, String groupName) throws NotExistException, AdminOperateException;

    ConsumerGroupDescription getGroupDescription(Long clusterPhyId, String groupName) throws NotExistException, AdminOperateException;

    int replaceDBData(GroupMemberPO groupMemberPO);

    void batchReplace(List<GroupMemberPO> newGroupMemberList);

    GroupStateEnum getGroupStateFromDB(Long clusterPhyId, String groupName);

    List<GroupMemberPO> listGroupByTopic(Long clusterPhyId, String topicName);

    List<GroupMemberPO> listGroup(Long clusterPhyId);

    PaginationResult<GroupMemberPO> pagingGroupMembers(Long clusterPhyId,
                                                       String topicName,
                                                       String groupName,
                                                       String searchTopicKeyword,
                                                       String searchGroupKeyword,
                                                       PaginationBaseDTO dto);

    int deleteByUpdateTimeBeforeInDB(Long clusterPhyId, Date beforeTime);

    List<String> getGroupsFromDB(Long clusterPhyId);

    GroupMemberPO getGroupFromDB(Long clusterPhyId, String groupName, String topicName);

    Integer calGroupCount(Long clusterPhyId);

    Integer calGroupStatCount(Long clusterPhyId, GroupStateEnum stateEnum);

    Result<Void> resetGroupOffsets(Long clusterPhyId, String groupName, Map<TopicPartition, Long> offsetMap, String operator) throws NotExistException, AdminOperateException;
}
