package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.xiaojukeji.kafka.manager.dao.gateway.TopicReportDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicReportDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/29
 */
@Service("topicReportService")
public class TopicReportServiceImpl implements TopicReportService {
    @Autowired
    private TopicReportDao topicReportDao;

    @Override
    public List<TopicReportDO> getNeedReportTopic(Long clusterId) {
        return topicReportDao.getNeedReportTopic(clusterId);
    }
}