package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicAnomalyFlow;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicInsufficientPartition;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicRegionHot;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;

import java.util.List;

/**
 * 专家服务
 * @author zengqiao
 * @date 20/3/20
 */
public interface ExpertService {
    /**
     * Region内热点Topic
     * @author zengqiao
     * @date 20/3/30
     * @return java.util.List<com.xiaojukeji.kafka.manager.common.entity.dto.expert.RegionHotTopicDTO>
     */
    List<TopicRegionHot> getRegionHotTopics();

    /**
     * 分区不足Topic
     */
    List<TopicInsufficientPartition> getPartitionInsufficientTopics();

    /**
     * 流量陡增
     * @param timestamp 时间戳
     * @author zengqiao
     * @date 20/3/30
     * @return java.util.List<com.xiaojukeji.kafka.manager.common.entity.dto.expert.AnomalyFlowTopicDTO>
     */
    List<TopicAnomalyFlow> getAnomalyFlowTopics(Long timestamp);

    /**
     * 过期Topic列表
     * @author zengqiao
     * @date 20/3/30
     * @return java.util.List<com.xiaojukeji.kafka.manager.common.entity.po.TopicExpiredDO>
     */
    List<TopicExpiredDO> getExpiredTopics();
}