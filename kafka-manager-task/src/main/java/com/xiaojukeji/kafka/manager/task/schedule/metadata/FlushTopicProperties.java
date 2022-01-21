package com.xiaojukeji.kafka.manager.task.schedule.metadata;

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
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/7/23
 */
@Component
public class FlushTopicProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlushTopicProperties.class);

    @Autowired
    private ClusterService clusterService;

    /**
     * 定时刷新物理集群配置到缓存中
     */
    @Scheduled(cron="25 0/1 * * * ?")
    public void flush() {
        List<ClusterDO> doList = clusterService.list();
        for (ClusterDO clusterDO: doList) {
            try {
                flush(clusterDO);
            } catch (Exception e) {
                LOGGER.error("flush topic properties failed, clusterId:{}.", clusterDO.getId(), e);
            }
        }
    }

    private void flush(ClusterDO clusterDO) {
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterDO.getId());
        if (ValidateUtils.isNull(zkConfig)) {
            LOGGER.error("flush topic properties, get zk config failed, clusterId:{}.", clusterDO.getId());
            return;
        }

        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            try {
                Properties properties = KafkaZookeeperUtils.getTopicProperties(zkConfig, topicName);
                if (ValidateUtils.isNull(properties)) {
                    LOGGER.warn("get topic properties failed, clusterId:{} topicName:{}.", clusterDO.getId(), topicName);
                    continue;
                }
                PhysicalClusterMetadataManager.putTopicProperties(clusterDO.getId(), topicName, properties);
            } catch (Exception e) {
                LOGGER.error("get topic properties failed, clusterId:{} topicName:{}.", clusterDO.getId(), topicName, e);
            }
        }
    }
}