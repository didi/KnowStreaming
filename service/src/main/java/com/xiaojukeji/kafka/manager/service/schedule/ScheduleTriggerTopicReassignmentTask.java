package com.xiaojukeji.kafka.manager.service.schedule;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.ReassignmentStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.MigrationTaskDO;
import com.xiaojukeji.kafka.manager.dao.MigrationTaskDao;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 分区迁移
 * @author zengqiao
 * @date 19/12/29
 */
@Component
public class ScheduleTriggerTopicReassignmentTask {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleStoreMetrics.class);

    @Autowired
    private MigrationService migrationService;

    @Autowired
    private MigrationTaskDao migrationTaskDao;

    @Scheduled(cron="0 0/1 * * * ?")
    public void reFlushTaskStatus(){
        List<MigrationTaskDO> migrationTaskDOList = migrationService.getByStatus(ReassignmentStatusEnum.RUNNING.getCode());
        if (migrationTaskDOList == null || migrationTaskDOList.isEmpty()) {
            return;
        }

        for (MigrationTaskDO migrationTaskDO: migrationTaskDOList) {
            try {
                ClusterDO clusterDO = ClusterMetadataManager.getClusterFromCache(migrationTaskDO.getClusterId());
                if (clusterDO == null) {
                    continue;
                }
                Map<Integer, Integer> statusMap = migrationService.getMigrationStatus(clusterDO, migrationTaskDO.getReassignmentJson());
                int running = 0;
                int failed = 0;
                for (Integer status: statusMap.values()) {
                    if (ReassignmentStatusEnum.RUNNING.getCode().equals(status)) {
                        running += 1;
                        break;
                    } else if (ReassignmentStatusEnum.SUCCESS.getCode().equals(status)) {
                        ;
                    } else {
                        failed += 1;
                    }
                }
                if (running > 0) {
                    continue;
                } else if (failed > 0) {
                    migrationTaskDO.setStatus(ReassignmentStatusEnum.FAILED.getCode());
                } else {
                    migrationTaskDO.setStatus(ReassignmentStatusEnum.SUCCESS.getCode());
                }
                migrationTaskDao.updateById(migrationTaskDO.getId(), migrationTaskDO.getStatus(), migrationTaskDO.getThrottle());
            } catch (Exception e) {
            }
        }
    }

}