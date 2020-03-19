package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.MigrationTaskDO;

import java.util.List;
import java.util.Map;

/**
 * migrate topic service
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
public interface MigrationService {
    /**
     * 创建迁移任务
     */
    Result<MigrationTaskDO> createMigrationTask(Long clusterId, String topicName, List<Integer> partitionList, Long throttle, List<Integer> brokerIdList, String description);

    /**
     * 获取迁移任务基本信息
     * @param taskId task id
     * @author zengqiao_cn@163.com
     * @date 19/4/16
     * @return MigrationTaskDO
     */
    MigrationTaskDO getMigrationTask(Long taskId);

    /**
     * 查看迁移进度
     * @param cluster 集群
     * @param reassignmentJson 迁移JSON
     * @author zengqiao_cn@163.com
     * @date 19/4/16
     * @return Map<partitionId, MigrationStatus>
     */
    Map<Integer, Integer> getMigrationStatus(ClusterDO cluster, String reassignmentJson);

    /**
     * 执行迁移任务
     * @param taskId 任务ID
     * @author zengqiao_cn@163.com
     * @date 19/4/16
     * @return Result
     */
    Result executeMigrationTask(Long taskId);

    Result modifyMigrationTask(Long taskId, Long throttle);

    /**
     * 列出所有的迁移任务
     * @author zengqiao_cn@163.com
     * @date 19/4/16
     * @return List<MigrationTaskDO>
     */
    List<MigrationTaskDO> getMigrationTaskList();

    List<MigrationTaskDO> getByStatus(Integer status);

    /**
     * 删除迁移任务
     * @param taskId 任务ID
     * @author zengqiao_cn@163.com
     * @date 19/4/16
     * @return Result
     */
    Result deleteMigrationTask(Long taskId);
}
