package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicReportDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public interface TopicReportService {
    List<TopicReportDO> getNeedReportTopic(Long clusterId);
}
