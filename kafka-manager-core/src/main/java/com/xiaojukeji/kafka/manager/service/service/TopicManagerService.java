package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.RdTopicBasic;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicAppData;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicBusinessInfo;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.MineTopicSummary;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicStatisticsDO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author arthur
 * @date 2017/7/20.
 */
public interface TopicManagerService {
    List<TopicDO> listAll();

    List<TopicDO> getByClusterIdFromCache(Long clusterId);

    List<TopicDO> getByClusterId(Long clusterId);

    TopicDO getByTopicName(Long clusterId, String topicName);

    int replaceTopicStatistics(TopicStatisticsDO topicStatisticsDO);

    Map<String, List<Double>> getTopicMaxAvgBytesIn(Long clusterId, Integer latestDay, Double minMaxAvgBytesIn);

    /**
     * 获取指定时间范围内Topic的峰值均值流量
     * @param clusterId 集群ID
     * @param topicName Topic名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param maxAvgDay 最大几天的均值
     * @return
     */
    Double getTopicMaxAvgBytesIn(Long clusterId, String topicName, Date startTime, Date endTime, Integer maxAvgDay);

    TopicStatisticsDO getByTopicAndDay(Long clusterId, String topicName, String gmtDay);

    List<TopicExpiredDO> getExpiredTopics(Integer expiredDay);

    /**
     * 获取 username 的 Topic
     * @param username 用户名
     * @author zengqiao
     * @date 20/5/12
     * @return java.util.List<TopicMineDTO>
     */
    List<MineTopicSummary> getMyTopics(String username);

    /**
     * 获取 username 可见的 Topic
     * @param username 用户名
     * @author zengqiao
     * @date 20/5/12
     * @return java.util.List<TopicDTO>
     */
    List<TopicDTO> getTopics(String username);

    /**
     * 修改Topic
     * @param clusterId 集群ID
     * @param topicName Topic名称
     * @param description 备注
     * @param operator 操作人
     * @author zengqiao
     * @date 20/5/12
     * @return ResultStatus
     */
    ResultStatus modifyTopic(Long clusterId, String topicName, String description, String operator);

    /**
     * 修改Topic
     * @param clusterId 集群ID
     * @param topicName Topic名称
     * @param appId 所属应用
     * @param description 备注
     * @param operator 操作人
     * @author zengqiao
     * @date 20/5/12
     * @return ResultStatus
     */
    ResultStatus modifyTopicByOp(Long clusterId, String topicName, String appId, String description, String operator);

    /**
     * 通过topictopic名称删除
     * @param clusterId 集群id
     * @param topicName topic名称
     * @return int
     */
    int deleteByTopicName(Long clusterId, String topicName);

    /**
     * 新增topic
     * @param topicDO topicDO
     * @return int
     */
    int addTopic(TopicDO topicDO);

    List<TopicAppData> getTopicAuthorizedApps(Long physicalClusterId, String topicName);

    List<TopicAppData> getTopicMineApps(Long physicalClusterId, String topicName, String username);

    /**
     * RD视角获取Topic基本信息
     * @param physicalClusterId 物理集群ID
     * @param topicName Topic名称
     * @author zengqiao
     * @date 20/6/10
     * @return Result<RdTopicBasic>
     */
    Result<RdTopicBasic> getRdTopicBasic(Long physicalClusterId, String topicName);

    List<TopicStatisticsDO> getTopicStatistic(Long clusterId, String topicName, Date startTime, Date endTime);

    TopicBusinessInfo getTopicBusinessInfo(Long physicalClusterId, String topicName);
}

