package com.xiaojukeji.kafka.manager.task.schedule.metadata;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
       List<ClusterDO> doList = clusterService.list();

       Set<Long> newClusterIdSet = new HashSet<>();
       Set<Long> oldClusterIdSet = physicalClusterMetadataManager.getClusterIdSet();
       for (ClusterDO clusterDO: doList) {
           newClusterIdSet.add(clusterDO.getId());

           // 添加集群
           physicalClusterMetadataManager.addNew(clusterDO);
       }

       for (Long clusterId: oldClusterIdSet) {
           if (newClusterIdSet.contains(clusterId)) {
               continue;
           }

           // 移除集群
           physicalClusterMetadataManager.remove(clusterId);
       }
    }
}