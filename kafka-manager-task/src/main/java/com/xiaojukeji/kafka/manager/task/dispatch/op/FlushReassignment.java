package com.xiaojukeji.kafka.manager.task.dispatch.op;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.dao.ReassignTaskDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ReassignService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.component.EmptyEntry;
import kafka.admin.ReassignPartitionsCommand;
import kafka.common.TopicAndPartition;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 分区迁移
 * @author zengqiao
 * @date 19/12/29
 */
@Component
@CustomScheduled(name = "flushReassignment", cron = "0 0/1 * * * ?", threadNum = 1)
public class FlushReassignment extends AbstractScheduledTask<EmptyEntry> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ReassignService reassignService;

    @Autowired
    private ReassignTaskDao reassignTaskDao;

    @Autowired
    private AdminService adminService;

    @Override
    public List<EmptyEntry> listAllTasks() {
        EmptyEntry emptyEntry = new EmptyEntry();
        emptyEntry.setId(System.currentTimeMillis() / 1000);
        return Arrays.asList(emptyEntry);
    }

    @Override
    public void processTask(EmptyEntry entryEntry) {
        // 查询近7天的迁移任务
        List<ReassignTaskDO> doList =
                reassignTaskDao.listAfterTime(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        if (ValidateUtils.isEmptyList(doList)) {
            return;
        }
        Map<Long, Long> runningClusterIdTaskIdMap = new HashMap<>();
        Map<Long, Long> runnableClusterIdTaskIdMap = new HashMap<>();

        Map<Long, List<ReassignTaskDO>> doMap = new HashMap<>();
        for (ReassignTaskDO elem: doList) {
            List<ReassignTaskDO> subDOList = doMap.getOrDefault(elem.getTaskId(), new ArrayList<>());
            subDOList.add(elem);
            doMap.put(elem.getTaskId(), subDOList);

            // 获取正在 running 和 runnable 的任务
            if (TaskStatusReassignEnum.RUNNABLE.getCode().equals(elem.getStatus())
                    && elem.getBeginTime().getTime() <= System.currentTimeMillis()) {
                LOGGER.debug("reassignment tasks, runnable task:{}.", elem);
                runnableClusterIdTaskIdMap.put(elem.getClusterId(), elem.getTaskId());
            }
            if (TaskStatusReassignEnum.RUNNING.getCode().equals(elem.getStatus())) {
                LOGGER.debug("reassignment tasks, running task:{}.", elem);
                runningClusterIdTaskIdMap.put(elem.getClusterId(), elem.getTaskId());
            }
        }

        // 处理正在执行的任务
        for (Map.Entry<Long, Long> entry: runningClusterIdTaskIdMap.entrySet()) {
            modifyRunning(doMap.get(entry.getValue()));
        }

        // 尝试启动待执行的任务
        for (Map.Entry<Long, Long> entry: runnableClusterIdTaskIdMap.entrySet()) {
            if (runningClusterIdTaskIdMap.containsKey(entry.getKey())) {
                // 如果这个集群已经有迁移任务, 则忽略
                continue;
            }
            if (startRunnable(doMap.get(entry.getValue()))) {
                // 启动任务成功后直接返回, 每次调度仅启动一个迁移任务
                return;
            }
        }
    }

    private void modifyRunning(List<ReassignTaskDO> subDOList) {
        if (ValidateUtils.isEmptyList(subDOList)) {
            return;
        }

        for (ReassignTaskDO elem: subDOList) {
            if (!TaskStatusReassignEnum.RUNNING.getCode().equals(elem.getStatus())) {
                continue;
            }

            ZkUtils zkUtils = null;
            try {
                ClusterDO clusterDO = clusterService.getById(elem.getClusterId());
                if (ValidateUtils.isNull(clusterDO)) {
                    continue;
                }

                zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                        Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                        Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                        JaasUtils.isZkSecurityEnabled());

                Map<TopicAndPartition, TaskStatusReassignEnum> statusMap =
                        reassignService.verifyAssignment(zkUtils, elem.getReassignmentJson());
                if (ValidateUtils.isNull(statusMap)) {
                    return;
                }

                Set<TaskStatusReassignEnum> statusSet = new HashSet<>();
                for (Map.Entry<TopicAndPartition, TaskStatusReassignEnum> entry: statusMap.entrySet()) {
                    statusSet.add(entry.getValue());
                }
                if (statusSet.contains(TaskStatusReassignEnum.RUNNING)) {
                    // 迁移任务未完成, 则执行限流, 并结束调用
                    ReassignPartitionsCommand.executeAssignment(
                            zkUtils,
                            elem.getReassignmentJson(),
                            elem.getRealThrottle()
                    );
                    return;
                }

                // 迁移任务已经完成
                ReassignPartitionsCommand.verifyAssignment(zkUtils, elem.getReassignmentJson());

                Thread.sleep(10000);

                // 恢复Topic保存时间
                changeTopicRetentionTime(clusterDO, elem.getTopicName(), elem.getOriginalRetentionTime());

                if (statusSet.contains(TaskStatusReassignEnum.FAILED)) {
                    elem.setStatus(TaskStatusReassignEnum.FAILED.getCode());
                } else {
                    elem.setStatus(TaskStatusReassignEnum.SUCCEED.getCode());
                }
                if (reassignTaskDao.updateById(elem) < 1) {
                    LOGGER.error("modify mysql failed, task:{}.", elem);
                    return;
                }
            } catch (Exception e) {
                LOGGER.error("modify running failed, task:{}.", elem, e);
            } finally {
                if (zkUtils != null) {
                    zkUtils.close();
                }
                zkUtils = null;
            }
        }
    }

    private boolean startRunnable(List<ReassignTaskDO> subDOList) {
        if (ValidateUtils.isEmptyList(subDOList)) {
            return false;
        }

        ZkUtils zkUtils = null;
        for (ReassignTaskDO elem : subDOList) {
            if (!TaskStatusReassignEnum.RUNNABLE.getCode().equals(elem.getStatus())) {
                continue;
            }
            if (elem.getBeginTime().getTime() > System.currentTimeMillis()) {
                continue;
            }

            ClusterDO clusterDO = clusterService.getById(elem.getClusterId());
            if (ValidateUtils.isNull(clusterDO)) {
                continue;
            }

            try {
                zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                        Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                        Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                        JaasUtils.isZkSecurityEnabled());
                if (zkUtils.pathExists(ZkPathUtil.REASSIGN_PARTITIONS_ROOT_NODE)) {
                    // 任务已经存在, 不知道是谁弄的, 因为未知, 就将其当作是本次触发的
                    return true;
                }

                // 缩短Topic保存时间
                changeTopicRetentionTime(clusterDO, elem.getTopicName(), elem.getReassignRetentionTime());

                // 启动Topic迁移
                ReassignPartitionsCommand.executeAssignment(
                        zkUtils,
                        elem.getReassignmentJson(),
                        elem.getRealThrottle()
                );
            } catch (Throwable t) {
                LOGGER.error("execute assignment failed, task:{}.", elem, t);
                return false;
            } finally {
                if (zkUtils != null) {
                    zkUtils.close();
                }
                zkUtils = null;
            }

            try {
                // 修改状态
                elem.setStatus(TaskStatusEnum.RUNNING.getCode());
                if (reassignTaskDao.updateById(elem) < 1) {
                    LOGGER.error("modify mysql failed, task:{}.", elem);
                }
            } catch (Throwable t) {
                LOGGER.error("execute assignment failed, task:{}.", elem, t);
            }
            return true;
        }
        return false;
    }

    private void changeTopicRetentionTime(ClusterDO clusterDO, String topicName, Long retentionTime) throws Exception {
        Properties properties = adminService.getTopicConfig(clusterDO, topicName);
        if (ValidateUtils.isNull(properties)) {
            throw new RuntimeException("get topic config failed");
        }

        properties.setProperty(TopicCreationConstant.TOPIC_RETENTION_TIME_KEY_NAME, String.valueOf(retentionTime));
        ResultStatus rs = adminService.modifyTopicConfig(clusterDO, topicName, properties, Constant.AUTO_HANDLE_USER_NAME);
        if (ResultStatus.SUCCESS.equals(rs)) {
            return;
        }
        throw new RuntimeException("modify topic config failed");
    }
}
