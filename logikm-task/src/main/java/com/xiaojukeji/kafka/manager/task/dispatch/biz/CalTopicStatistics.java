package com.xiaojukeji.kafka.manager.task.dispatch.biz;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetPosEnum;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicStatisticsDO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时计算Topic的统计数据
 * 1. 统计今天的maxAvgBytesIn
 * 2. 统计今天的sum(offset)
 * @author zengqiao
 * @date 20/3/29
 */
@CustomScheduled(name = "calTopicStatistics", cron = "0 0 0/4 * * ?", threadNum = 5)
public class CalTopicStatistics extends AbstractScheduledTask<ClusterDO> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        // 获取今天的起始时间和结束时间
        Date startTime = new Date(DateUtils.getDayStarTime(0));
        Date endTime = new Date(DateUtils.getDayStarTime(1) - 1);
        String gmtDay = DateUtils.getFormattedDate(System.currentTimeMillis());

        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            try {
                calTopicStatistics(clusterDO, topicName, startTime, endTime, gmtDay);
            } catch (Exception e) {
                LOGGER.error("cal topic metrics failed, clusterId:{} topicName:{}.", clusterDO.getId(), topicName);
            }
        }
    }

    private void calTopicStatistics(ClusterDO clusterDO,
                                    String topicName,
                                    Date startTime,
                                    Date endTime,
                                    String gmtDay) {
        TopicStatisticsDO dataDO = topicManagerService.getByTopicAndDay(clusterDO.getId(), topicName, gmtDay);
        if (dataDO == null) {
            dataDO = new TopicStatisticsDO();
        }

        // 获取offset
        Map<TopicPartition, Long> offsetMap =
                topicService.getPartitionOffset(clusterDO, topicName, OffsetPosEnum.END);
        Long offsetSum = null;
        if (!ValidateUtils.isEmptyMap(offsetMap)) {
            offsetSum = 0L;
            for (Long offset: offsetMap.values()) {
                offsetSum += offset;
            }
        }

        // 获取mxAvgBytesIn
        Double maxAvgBytesIn = topicService.getMaxAvgBytesInFromDB(clusterDO.getId(), topicName, startTime, endTime);

        dataDO.setClusterId(clusterDO.getId());
        dataDO.setTopicName(topicName);
        dataDO.setGmtDay(gmtDay);
        dataDO.setMaxAvgBytesIn(ValidateUtils.isNull(maxAvgBytesIn)? dataDO.getMaxAvgBytesIn(): maxAvgBytesIn);
        dataDO.setOffsetSum(offsetSum == null? dataDO.getOffsetSum(): offsetSum);
        topicManagerService.replaceTopicStatistics(dataDO);
    }
}