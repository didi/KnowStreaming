package com.xiaojukeji.kafka.manager.dao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicReportDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public interface TopicReportDao {
    int replace(TopicReportDO topicReportDO);

    List<TopicReportDO> getNeedReportTopic(Long clusterId);
}