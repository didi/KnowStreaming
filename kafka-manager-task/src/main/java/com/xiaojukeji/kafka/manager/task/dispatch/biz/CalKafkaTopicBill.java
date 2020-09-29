package com.xiaojukeji.kafka.manager.task.dispatch.biz;


import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.task.config.TopicBillConfig;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.TopicStatisticsDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaBillDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 计算账单
 * @author zengqiao
 * @date 20/5/11
 */
@CustomScheduled(name = "calKafkaBill", cron = "0 0 1 * * *", threadNum = 1)
public class CalKafkaTopicBill extends AbstractScheduledTask<ClusterDO> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private AppService appService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KafkaBillService kafkaBillService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private TopicStatisticsDao topicStatisticsDao;

    private static final String KAFKA_TOPIC_BILL_CONFIG_KEY = "KAFKA_TOPIC_BILL_CONFIG";

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        TopicBillConfig kafkaBillConfig =
                configService.getByKey(KAFKA_TOPIC_BILL_CONFIG_KEY, TopicBillConfig.class);
        if (ValidateUtils.isNull(kafkaBillConfig) || !kafkaBillConfig.paramLegal()) {
            return ;
        }

        Date now = new Date();
        Date startTime = DateUtils.getMonthStartTime(now);
        Date endTime = DateUtils.getMonthEndTime(now);
        String gmtDay = DateUtils.getFormattedDate(now).substring(0, 7);

        List<AppDO> appDOList = appService.listAll();
        Map<String, String> appMap = new HashMap<>();
        for (AppDO appDO: appDOList) {
            List<String> principalList = ListUtils.string2StrList(appDO.getPrincipals());
            appMap.put(
                    appDO.getAppId(),
                    ValidateUtils.isEmptyList(principalList)? Constant.UNKNOWN_USER: principalList.get(0)
            );
        }

        calAndUpdateBill(clusterDO.getId(), startTime, endTime, gmtDay, kafkaBillConfig, appMap);
    }

    private void calAndUpdateBill(Long clusterId,
                                  Date startTime,
                                  Date endTime,
                                  String gmtDay,
                                  TopicBillConfig kafkaBillConfig,
                                  Map<String, String> appMap) {
        List<TopicDO> topicDOList = topicManagerService.getByClusterId(clusterId);
        if (ValidateUtils.isEmptyList(topicDOList)) {
            topicDOList = new ArrayList<>();
        }
        Map<String, String> topicNamePrincipalList = new HashMap<>();
        for (TopicDO topicDO: topicDOList) {
            topicNamePrincipalList.put(
                    topicDO.getTopicName(),
                    appMap.getOrDefault(topicDO.getAppId(), Constant.UNKNOWN_USER)
            );
        }

        // 获取今天的起始时间和结束时间
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            try {
                Double maxAvgBytesIn = topicStatisticsDao.getTopicMaxAvgBytesIn(
                        clusterId,
                        topicName,
                        startTime,
                        endTime,
                        kafkaBillConfig.getMaxAvgDay()
                );
                if (ValidateUtils.isNull(maxAvgBytesIn)) {
                    continue;
                }
                KafkaBillDO kafkaBillDO = new KafkaBillDO();
                kafkaBillDO.setClusterId(clusterId);
                kafkaBillDO.setTopicName(topicName);
                kafkaBillDO.setPrincipal(topicNamePrincipalList.getOrDefault(topicName, Constant.UNKNOWN_USER));
                Double quotaUnitMB = maxAvgBytesIn * kafkaBillConfig.getQuotaRatio() / 1024.0 / 1024.0;
                kafkaBillDO.setQuota(quotaUnitMB);
                kafkaBillDO.setCost(quotaUnitMB * kafkaBillConfig.getPriseUnitMB());
                kafkaBillDO.setGmtDay(gmtDay);
                kafkaBillService.replace(kafkaBillDO);
            } catch (Exception e) {
                LOGGER.error("cal and update bill failed, clusterId:{}, startTime:{}, endTime:{}, gmtDay:{}.",
                        clusterId, startTime, endTime, gmtDay, e);
            }
        }
    }
}