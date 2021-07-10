package com.xiaojukeji.kafka.manager.task.dispatch.biz;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicStatisticsDO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.TopicExpiredDao;
import com.xiaojukeji.kafka.manager.dao.TopicStatisticsDao;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 定时更新过期Topic
 * @author zengqiao
 * @date 20/4/1
 */
@CustomScheduled(name = "flushExpiredTopic", cron = "0 0 0/5 * * ?", threadNum = 1)
public class FlushExpiredTopic extends AbstractScheduledTask<ClusterDO> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private TopicExpiredDao topicExpiredDao;

    @Autowired
    private TopicStatisticsDao topicStatisticsDao;

    @Autowired
    private ClusterService clusterService;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        Date startTime = new Date(DateUtils.getDayStarTime(-1 * Constant.DEFAULT_MAX_CAL_TOPIC_EXPIRED_DAY));
        Date endTime = new Date(DateUtils.getDayStarTime(1) - 1000);
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            try {
                checkAndModifyIfTopicExpired(clusterDO, topicName, startTime, endTime);
            } catch (Exception e) {
                LOGGER.error("check topic expired failed, clusterId:{} topicName:{}.", clusterDO.getId(), topicName, e);
            }
        }
    }

    private void checkAndModifyIfTopicExpired(ClusterDO clusterDO, String topicName, Date startTime, Date endTime) {
        TopicExpiredDO expiredDO = topicExpiredDao.getByTopic(clusterDO.getId(), topicName);
        if (ValidateUtils.isNull(expiredDO)) {
            expiredDO = new TopicExpiredDO();
            expiredDO.setClusterId(clusterDO.getId());
            expiredDO.setTopicName(topicName);
            expiredDO.setGmtRetain(startTime);
            expiredDO.setStatus(0);
        }
        if (checkAndModifyIfExistMetrics(clusterDO, topicName)) {
            // Topic 未过期
            expiredDO.setExpiredDay(0);
            topicExpiredDao.replace(expiredDO);
            return;
        }

        List<TopicStatisticsDO> doList = topicStatisticsDao.getTopicStatistic(clusterDO.getId(), topicName, startTime, endTime);
        if (ValidateUtils.isEmptyList(doList)) {
            // Topic 未过期
            expiredDO.setExpiredDay(0);
            topicExpiredDao.replace(expiredDO);
            return;
        }
        Map<String, Long> dayOffsetSumMap = new HashMap<>();
        for (TopicStatisticsDO elem: doList) {
            dayOffsetSumMap.put(elem.getGmtDay(), elem.getOffsetSum());
        }

        Long todayTimestamp = endTime.getTime();
        Long todayOffsetSum = dayOffsetSumMap.get(DateUtils.getFormattedDate(todayTimestamp));
        if (ValidateUtils.isNull(todayOffsetSum) || todayOffsetSum.equals(Constant.INVALID_CODE)) {
            // 今天是否过期还未知
            expiredDO.setExpiredDay(0);
            topicExpiredDao.replace(expiredDO);
            return;
        }

        int expiredDay = 0;
        for (int i = -1 * Constant.DEFAULT_MAX_CAL_TOPIC_EXPIRED_DAY; i <= 0; ++i) {
            String gmtDay = DateUtils.getFormattedDate(DateUtils.getDayStarTime(i));
            Long dayOffsetSum = dayOffsetSumMap.get(gmtDay);
            if (todayOffsetSum.equals(dayOffsetSum)) {
                expiredDay = (i * -1);
                break;
            }
        }
        expiredDO.setExpiredDay(expiredDay);
        topicExpiredDao.replace(expiredDO);
    }

    private boolean checkAndModifyIfExistMetrics(ClusterDO clusterDO, String topicName) {
        TopicMetrics metrics = KafkaMetricsCache.getTopicMetricsFromCache(clusterDO.getId(), topicName);
        if (ValidateUtils.isNull(metrics)) {
            return false;
        }

        Double bytesIn = metrics.getBytesInPerSecOneMinuteRate(null);
        if (ValidateUtils.isNull(bytesIn) || bytesIn < 1.0) {
            return false;
        }

        // 流量大于 1.0, 则认为是有数据的, 不过期
        return true;
    }
}
