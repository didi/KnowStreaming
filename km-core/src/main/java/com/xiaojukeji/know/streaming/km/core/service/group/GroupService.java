package com.xiaojukeji.know.streaming.km.core.service.group;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.Group;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.KSGroupDescription;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GroupService {
    /**
     * 从Kafka中获取消费组名称列表
     */
    List<String> listGroupsFromKafka(ClusterPhy clusterPhy) throws AdminOperateException;

    /**
     * 从Kafka中获取消费组详细信息
     */
    Group getGroupFromKafka(ClusterPhy clusterPhy, String groupName) throws NotExistException, AdminOperateException;

    Map<TopicPartition, Long> getGroupOffsetFromKafka(Long clusterPhyId, String groupName) throws NotExistException, AdminOperateException;

    KSGroupDescription getGroupDescriptionFromKafka(ClusterPhy clusterPhy, String groupName) throws AdminOperateException;

    Result<Void> resetGroupOffsets(Long clusterPhyId, String groupName, Map<TopicPartition, Long> offsetMap, String operator) throws NotExistException, AdminOperateException;

    /**
     * 批量更新DB
     * @param clusterPhyId 集群ID
     * @param newGroupList 新的group列表
     * @param getFailedGroupSet 元信息获取失败的group列表
     */
    void batchReplaceGroupsAndMembers(Long clusterPhyId, List<Group> newGroupList, Set<String> getFailedGroupSet);

    /**
     * DB-Group相关接口
     */
    GroupStateEnum getGroupStateFromDB(Long clusterPhyId, String groupName);

    Group getGroupFromDB(Long clusterPhyId, String groupName);

    List<Group> listClusterGroups(Long clusterPhyId);

    List<String> getGroupsFromDB(Long clusterPhyId);

    Integer calGroupCount(Long clusterPhyId);

    Integer calGroupStatCount(Long clusterPhyId, GroupStateEnum stateEnum);

    /**
     * DB-GroupTopic相关接口
     */
    List<GroupMemberPO> listGroupByCluster(Long clusterPhyId);
    List<GroupMemberPO> listGroupByTopic(Long clusterPhyId, String topicName);

    PaginationResult<GroupMemberPO> pagingGroupMembers(Long clusterPhyId,
                                                       String topicName,
                                                       String groupName,
                                                       String searchTopicKeyword,
                                                       String searchGroupKeyword,
                                                       PaginationBaseDTO dto);

    GroupMemberPO getGroupTopicFromDB(Long clusterPhyId, String groupName, String topicName);
}