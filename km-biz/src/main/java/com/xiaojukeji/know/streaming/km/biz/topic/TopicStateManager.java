package com.xiaojukeji.know.streaming.km.biz.topic;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicRecordDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicBrokersPartitionsSummaryVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicRecordVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.broker.TopicBrokerAllVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.partition.TopicPartitionVO;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;

import java.util.List;

public interface TopicStateManager {
    TopicBrokerAllVO getTopicBrokerAll(Long clusterPhyId, String topicName, String searchBrokerHost) throws NotExistException;

    Result<List<TopicRecordVO>> getTopicMessages(Long clusterPhyId, String topicName, TopicRecordDTO dto) throws AdminOperateException;

    Result<TopicStateVO> getTopicState(Long clusterPhyId, String topicName);

    Result<List<TopicPartitionVO>> getTopicPartitions(Long clusterPhyId, String topicName, List<String> metricsNames);

    Result<TopicBrokersPartitionsSummaryVO> getTopicBrokersPartitionsSummary(Long clusterPhyId, String topicName);

    PaginationResult<GroupTopicOverviewVO> pagingTopicGroupsOverview(Long clusterPhyId, String topicName, String searchGroupName, PaginationBaseDTO dto);
}
