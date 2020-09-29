package com.xiaojukeji.kafka.manager.task.schedule.metadata;

import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/23
 */
@Component
public class FlushTopicRetentionTime {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ClusterService clusterService;

    @Scheduled(cron="25 0/1 * * * ?")
    public void flush() {
        List<ClusterDO> doList = clusterService.list();
        for (ClusterDO clusterDO: doList) {
            try {
                flush(clusterDO);
            } catch (Exception e) {
                LOGGER.error("flush topic retention time failed, clusterId:{}.", clusterDO.getId(), e);
            }
        }
    }

    private void flush(ClusterDO clusterDO) {
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterDO.getId());
        if (ValidateUtils.isNull(zkConfig)) {
            LOGGER.error("flush topic retention time, get zk config failed, clusterId:{}.", clusterDO.getId());
            return;
        }

        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            try {
                Long retentionTime = KafkaZookeeperUtils.getTopicRetentionTime(zkConfig, topicName);
                if (retentionTime == null) {
                    LOGGER.warn("get topic retentionTime failed, clusterId:{} topicName:{}.",
                            clusterDO.getId(), topicName);
                    continue;
                }
                PhysicalClusterMetadataManager.putTopicRetentionTime(clusterDO.getId(), topicName, retentionTime);
            } catch (Exception e) {
                LOGGER.error("get topic retentionTime failed, clusterId:{} topicName:{}.",
                        clusterDO.getId(), topicName, e);
            }
        }
    }
}