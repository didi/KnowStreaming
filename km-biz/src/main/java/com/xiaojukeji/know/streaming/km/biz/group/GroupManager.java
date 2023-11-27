package com.xiaojukeji.know.streaming.km.biz.group;

import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterGroupSummaryDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.group.GroupOffsetDeleteDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.group.GroupOffsetResetDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicPartitionKS;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicConsumedDetailVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;

import java.util.List;
import java.util.Set;

public interface GroupManager {
    PaginationResult<GroupTopicOverviewVO> pagingGroupMembers(Long clusterPhyId,
                                                              String topicName,
                                                              String groupName,
                                                              String searchTopicKeyword,
                                                              String searchGroupKeyword,
                                                              PaginationBaseDTO dto);

    PaginationResult<GroupTopicOverviewVO> pagingGroupTopicMembers(Long clusterPhyId, String groupName, PaginationBaseDTO dto) throws Exception;

    PaginationResult<GroupOverviewVO> pagingClusterGroupsOverview(Long clusterPhyId, ClusterGroupSummaryDTO dto);

    PaginationResult<GroupTopicConsumedDetailVO> pagingGroupTopicConsumedMetrics(Long clusterPhyId,
                                                                                 String topicName,
                                                                                 String groupName,
                                                                                 List<String> latestMetricNames,
                                                                                 PaginationSortDTO dto)throws NotExistException, AdminOperateException;

    Result<Set<TopicPartitionKS>> listClusterPhyGroupPartitions(Long clusterPhyId, String groupName, Long startTime, Long endTime);

    Result<Void> resetGroupOffsets(GroupOffsetResetDTO dto, String operator) throws Exception;

    Result<Void> deleteGroupOffsets(GroupOffsetDeleteDTO dto, String operator) throws Exception;

    @Deprecated
    List<GroupTopicOverviewVO> getGroupTopicOverviewVOList(Long clusterPhyId, List<GroupMemberPO> groupMemberPOList);
    List<GroupTopicOverviewVO> getGroupTopicOverviewVOList(Long clusterPhyId, List<GroupMemberPO> groupMemberPOList, Integer timeoutUnitMs);
}
