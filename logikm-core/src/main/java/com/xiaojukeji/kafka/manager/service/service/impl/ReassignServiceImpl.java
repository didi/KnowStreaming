package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicReassignActionEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.reassign.ReassignStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecSubDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.ReassignTaskDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ReassignService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import com.xiaojukeji.kafka.manager.service.utils.TopicReassignUtils;
import kafka.common.TopicAndPartition;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.*;

/**
 * Topic迁移
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
@Service("reassignService")
public class ReassignServiceImpl implements ReassignService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReassignServiceImpl.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ReassignTaskDao reassignTaskDao;

    @Autowired
    private RegionService regionService;

    @Override
    public ResultStatus createTask(List<ReassignTopicDTO> dtoList, String operator) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        Map<Long, ClusterDO> clusterMap = clusterService.listMap();

        Long taskId = System.currentTimeMillis();

        List<ReassignTaskDO> doList = new ArrayList<>();
        for (ReassignTopicDTO dto: dtoList) {
            // 集群是否存在
            ClusterDO clusterDO = clusterMap.get(dto.getClusterId());
            if (ValidateUtils.isNull(clusterDO)) {
                return ResultStatus.CLUSTER_NOT_EXIST;
            }

            // Topic是否存在
            TopicMetadata topicMetadata =
                    PhysicalClusterMetadataManager.getTopicMetadata(dto.getClusterId(), dto.getTopicName());
            if (ValidateUtils.isNull(topicMetadata)) {
                return ResultStatus.TOPIC_NOT_EXIST;
            }

            // 检查参数是否合法
            ResultStatus rs = checkParamLegal(dto.getClusterId(), topicMetadata, dto);
            if (!ResultStatus.SUCCESS.equals(rs)) {
                return rs;
            }

            // 创建迁移脚本
            String reassignmentJson = TopicReassignUtils.generateReassignmentJson(
                    clusterDO,
                    dto.getTopicName(),
                    dto.getPartitionIdList(),
                    dto.getBrokerIdList()
            );
            if (ValidateUtils.isBlank(reassignmentJson)) {
                return ResultStatus.OPERATION_FAILED;
            }

            doList.add(MetricsConvertUtils.convert2ReassignTaskDO(taskId, dto.getClusterId(), topicMetadata, dto, reassignmentJson, operator));
        }

        // step2 任务存储到数据库
        try {
            if (reassignTaskDao.batchCreate(doList) >= doList.size()) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("batch create reassign task failed, tasks:{}.", doList.toArray(), e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    /**
     * 检查参数是否合法
     * 1. 检查Broker是否存活以及存在
     * 2. 检查分区是否存在
     * 3. 旧的保存时间是否正确
     */
    private ResultStatus checkParamLegal(Long clusterId,
                                         TopicMetadata topicMetadata,
                                         ReassignTopicDTO dto) {
        // 所有的Region转成BrokerID
        List<Integer> brokerIdList = regionService.getFullBrokerIdList(
                clusterId,
                dto.getRegionId(), dto.getBrokerIdList()
        );
        if (ValidateUtils.isNull(brokerIdList)) {
            return ResultStatus.BROKER_NUM_NOT_ENOUGH;
        }
        dto.setBrokerIdList(brokerIdList);

        // Broker是否存在
        List<Integer> clusterBrokerIdList =
                PhysicalClusterMetadataManager.getBrokerIdList(clusterId);
        for (Integer brokerId : dto.getBrokerIdList()) {
            if (!clusterBrokerIdList.contains(brokerId)) {
                return ResultStatus.BROKER_NOT_EXIST;
            }
        }
        if (dto.getBrokerIdList().size() < topicMetadata.getReplicaNum()) {
            return ResultStatus.BROKER_NUM_NOT_ENOUGH;
        }

        // 旧的保存时间是否正确
        Long realRetentionTime =
                PhysicalClusterMetadataManager.getTopicRetentionTime(clusterId, dto.getTopicName());
        if (!dto.getOriginalRetentionTime().equals(realRetentionTime)) {
            return ResultStatus.PARAM_ILLEGAL;
        }

        // 分区是否合法
        if (ValidateUtils.isEmptyList(dto.getPartitionIdList())) {
            return ResultStatus.SUCCESS;
        }

        Set<Integer> topicPartitionIdSet = topicMetadata.getPartitionMap().getPartitions().keySet();
        for (Integer partitionId: dto.getPartitionIdList()) {
            if (topicPartitionIdSet.contains(partitionId)) {
                continue;
            }
            return ResultStatus.PARTITION_NOT_EXIST;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus modifyTask(ReassignExecDTO dto, TopicReassignActionEnum actionEnum) {
        List<ReassignTaskDO> doList = this.getTask(dto.getTaskId());
        if (ValidateUtils.isNull(doList)) {
            return ResultStatus.TASK_NOT_EXIST;
        }

        Set<Integer> statusSet = new HashSet<>();
        for (ReassignTaskDO elem: doList) {
            statusSet.add(elem.getStatus());
        }
        // 不处于新建的状态, 则不可执行启动或者取消
        if (!statusSet.contains(TaskStatusReassignEnum.NEW.getCode()) || statusSet.size() != 1) {
            return ResultStatus.OPERATION_FORBIDDEN;
        }

        for (ReassignTaskDO elem: doList) {
            if (TopicReassignActionEnum.CANCEL.equals(actionEnum)) {
                elem.setStatus(TaskStatusReassignEnum.CANCELED.getCode());
            } else if (TopicReassignActionEnum.START.equals(actionEnum)) {
                elem.setStatus(TaskStatusReassignEnum.RUNNABLE.getCode());
            } else {
                elem.setBeginTime(new Date(dto.getBeginTime()));
            }
        }
        try {
            reassignTaskDao.batchUpdate(doList);
        } catch (Exception e) {
            LOGGER.error("batch modify status failed, tasks:{}.", doList.toArray(), e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus modifySubTask(ReassignExecSubDTO dto) {
        ReassignTaskDO reassignTaskDO = this.getSubTask(dto.getSubTaskId());
        if (ValidateUtils.isNull(reassignTaskDO)) {
            return ResultStatus.TASK_NOT_EXIST;
        }
        reassignTaskDO.setRealThrottle(dto.getThrottle());
        reassignTaskDO.setMaxThrottle(dto.getMaxThrottle());
        reassignTaskDO.setMinThrottle(dto.getMinThrottle());
        try {
            reassignTaskDao.updateById(reassignTaskDO);
        } catch (Exception e) {
            LOGGER.error("modify task failed, task:{} req:{}.", reassignTaskDO, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }



    @Override
    public List<ReassignTaskDO> getReassignTaskList() {
        try {
            return reassignTaskDao.listAll();
        } catch (Exception e) {
            LOGGER.error("list all reassign task error.", e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<ReassignTaskDO> getTask(Long taskId) {
        try {
            return reassignTaskDao.getByTaskId(taskId);
        } catch (Exception e) {
            LOGGER.error("get task failed, taskId:{}.", taskId, e);
        }
        return null;
    }

    private ReassignTaskDO getSubTask(Long subTaskId) {
        try {
            return reassignTaskDao.getSubTask(subTaskId);
        } catch (Exception e) {
            LOGGER.error("get sub task failed, subTaskId:{}.", subTaskId, e);
        }
        return null;
    }

    @Override
    public Result<List<ReassignStatus>> getReassignStatus(Long taskId) {
        List<ReassignTaskDO> doList = this.getTask(taskId);
        if (ValidateUtils.isNull(doList)) {
            return Result.buildFrom(ResultStatus.TASK_NOT_EXIST);
        }
        Map<Long, ClusterDO> clusterMap = clusterService.listMap();

        List<ReassignStatus> statusList = new ArrayList<>();
        for (ReassignTaskDO elem: doList) {
            try {
                ReassignStatus reassignStatus = new ReassignStatus();
                reassignStatus.setSubTaskId(elem.getId());
                reassignStatus.setClusterId(elem.getClusterId());
                reassignStatus.setStatus(elem.getStatus());
                reassignStatus.setTopicName(elem.getTopicName());
                reassignStatus.setRealThrottle(elem.getRealThrottle());
                reassignStatus.setMaxThrottle(elem.getMaxThrottle());
                reassignStatus.setMinThrottle(elem.getMinThrottle());
                reassignStatus.setReassignList(KafkaZookeeperUtils.getReassignmentElemDataList(elem.getReassignmentJson()));

                ClusterDO clusterDO = clusterMap.get(elem.getClusterId());
                if (!ValidateUtils.isNull(clusterDO)) {
                    reassignStatus.setClusterName(clusterDO.getClusterName());
                }
                if (TaskStatusReassignEnum.CANCELED.getCode().equals(elem.getStatus())
                        || TaskStatusReassignEnum.NEW.getCode().equals(elem.getStatus())
                        || TaskStatusReassignEnum.RUNNABLE.getCode().equals(elem.getStatus())) {
                    reassignStatus.setReassignStatusMap(new HashMap<>());
                    statusList.add(reassignStatus);
                    continue;
                }
                Map<TopicAndPartition, TaskStatusReassignEnum> statusMap =
                        verifyAssignment(clusterDO.getZookeeper(), elem.getReassignmentJson());
                reassignStatus.setReassignStatusMap(statusMap);
                statusList.add(reassignStatus);
            } catch (Exception e) {
                LOGGER.error("get reassign status failed, taskId:{}.", taskId);
            }
        }
        return new Result<>(statusList);
    }

    @Override
    public Map<TopicAndPartition, TaskStatusReassignEnum> verifyAssignment(String zkAddr, String reassignmentJson) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(zkAddr,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled());
            return verifyAssignment(zkUtils, reassignmentJson);
        } catch (ZkInterruptedException | ZkTimeoutException | IllegalStateException e) {
            LOGGER.error("connect zookeeper failed, zkAddr:{}.", zkAddr, e);
        } catch (Throwable t) {
            LOGGER.error("verify assignment failed, reassignmentJson:{}.", reassignmentJson, t);
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
        return null;
    }

    @Override
    public Map<TopicAndPartition, TaskStatusReassignEnum> verifyAssignment(ZkUtils zkUtils,
                                                                            String reassignmentJson) {
        // 本地迁移Json转Map
        Map<TopicAndPartition, Seq<Object>> reassignMap =
                JavaConversions.asJavaMap(ZkUtils.parsePartitionReassignmentData(reassignmentJson));

        // 从zk获取哪些分区正在迁移
        Set<TopicAndPartition> beingReassignedMap =
                JavaConversions.asJavaMap(zkUtils.getPartitionsBeingReassigned()).keySet();

        // 计算迁移结果
        Map<TopicAndPartition, TaskStatusReassignEnum> reassignResult = new HashMap<>(reassignMap.size());
        for (TopicAndPartition tp: reassignMap.keySet()) {
            if (beingReassignedMap.contains(tp)) {
                reassignResult.put(tp, TaskStatusReassignEnum.RUNNING);
                continue;
            }
            boolean status = ValidateUtils.equalList(
                    JavaConversions.asJavaList(reassignMap.get(tp)),
                    JavaConversions.asJavaList(zkUtils.getReplicasForPartition(tp.topic(), tp.partition()))
            );
            reassignResult.put(tp, status? TaskStatusReassignEnum.SUCCEED: TaskStatusReassignEnum.FAILED);
        }
        return reassignResult;
    }
}