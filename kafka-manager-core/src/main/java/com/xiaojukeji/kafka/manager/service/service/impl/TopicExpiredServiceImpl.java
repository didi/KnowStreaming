package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicExpiredData;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.TopicExpiredDao;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.TopicExpiredService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/9/2
 */
@Service("topicExpiredServiceImpl")
public class TopicExpiredServiceImpl implements TopicExpiredService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicExpiredServiceImpl.class);

    @Autowired
    private AppService appService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private TopicExpiredDao topicExpiredDao;

    @Override
    public List<TopicExpiredData> getExpiredTopicDataList(String username) {
        List<TopicExpiredDO> expiredDOList = topicExpiredDao.getExpiredTopics(30);
        if (ValidateUtils.isEmptyList(expiredDOList)) {
            return new ArrayList<>();
        }

        List<AppDO> appDOList = appService.getByPrincipal(username);
        if (ValidateUtils.isEmptyList(appDOList)) {
            return new ArrayList<>();
        }
        Map<String, AppDO> appMap = appDOList.stream().collect(Collectors.toMap(AppDO::getAppId, elem -> elem));

        return new ArrayList<>();
    }

    @Override
    public ResultStatus retainExpiredTopic(Long physicalClusterId, String topicName, Integer retainDays) {
        if (ValidateUtils.isNullOrLessThanZero(retainDays)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, topicName)) {
            return ResultStatus.TOPIC_NOT_EXIST;
        }
        try {
            topicExpiredDao.modifyTopicExpiredTime(
                    physicalClusterId,
                    topicName,
                    new Date(System.currentTimeMillis() + retainDays * 24 * 60 * 60 * 1000)
            );
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("retain expired topic failed, clusterId:{} topicName:{} retainDays:{}."
                    ,physicalClusterId, topicName, retainDays);
        }
        return ResultStatus.MYSQL_ERROR;
    }
}