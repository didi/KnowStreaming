package com.xiaojukeji.kafka.manager.task.schedule.metadata;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/6/3
 */
@Component
public class FlushClusterMetadata {
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private PhysicalClusterMetadataManager physicalClusterMetadataManager;

    @Scheduled(cron="0/30 * * * * ?")
    public void flush() {
        Map<Long, ClusterDO> dbClusterMap = clusterService.list().stream().collect(Collectors.toMap(ClusterDO::getId, Function.identity(), (key1, key2) -> key2));

        Map<Long, ClusterDO> cacheClusterMap = PhysicalClusterMetadataManager.getClusterMap();

        // 新增的集群
        for (ClusterDO clusterDO: dbClusterMap.values()) {
            if (cacheClusterMap.containsKey(clusterDO.getId())) {
                // 已经存在
                continue;
            }
            add(clusterDO);
        }

        // 移除的集群
        for (ClusterDO clusterDO: cacheClusterMap.values()) {
            if (dbClusterMap.containsKey(clusterDO.getId())) {
                // 已经存在
                continue;
            }
            remove(clusterDO.getId());
        }

        // 被修改配置的集群
        for (ClusterDO dbClusterDO: dbClusterMap.values()) {
            ClusterDO cacheClusterDO = cacheClusterMap.get(dbClusterDO.getId());
            if (ValidateUtils.anyNull(cacheClusterDO) || dbClusterDO.equals(cacheClusterDO)) {
                // 不存在 || 相等
                continue;
            }
            modifyConfig(dbClusterDO);
        }
    }

    private void add(ClusterDO clusterDO) {
        if (ValidateUtils.anyNull(clusterDO)) {
            return;
        }
        physicalClusterMetadataManager.addNew(clusterDO);
    }

    private void modifyConfig(ClusterDO clusterDO) {
        if (ValidateUtils.anyNull(clusterDO)) {
            return;
        }
        PhysicalClusterMetadataManager.updateClusterMap(clusterDO);
        KafkaClientPool.closeKafkaConsumerPool(clusterDO.getId());
    }

    private void remove(Long clusterId) {
        if (ValidateUtils.anyNull(clusterId)) {
            return;
        }
        // 移除缓存信息
        physicalClusterMetadataManager.remove(clusterId);

        // 清除客户端池子
        KafkaClientPool.closeKafkaConsumerPool(clusterId);
    }

}