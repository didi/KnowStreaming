package com.xiaojukeji.kafka.manager.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.ReassignmentStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.po.MigrationTaskDO;
import com.xiaojukeji.kafka.manager.dao.MigrationTaskDao;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.MigrationService;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import kafka.admin.*;
import kafka.common.TopicAndPartition;
import kafka.controller.ReassignedPartitionsContext;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.*;

/**
 * migrate topic service impl
 * @author zengqiao_cn@163.com,huangyiminghappy@163.com
 * @date 19/4/16
 */
@Service("migrationService")
public class MigrationServiceImpl implements MigrationService{
    private static final Logger logger = LoggerFactory.getLogger(MigrationServiceImpl.class);

    private static final int DEFAULT_SESSION_TIMEOUT = 90000;

    @Autowired
    private MigrationTaskDao migrationTaskDao;

    @Autowired
    private ClusterService clusterService;

    @Override
    public Result<MigrationTaskDO> createMigrationTask(Long clusterId, String topicName, List<Integer> partitionList, Long throttle, List<Integer> brokerIdList, String description) {
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "cluster not exist");
        }
        Result<MigrationTaskDO> result = checkAddMigrateTaskParamIllegal(clusterId, topicName, brokerIdList, partitionList);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        String reassignmentJson = createReassignmentJson(cluster, topicName, new ArrayList<>(brokerIdList));
        if (StringUtils.isEmpty(reassignmentJson)) {
            return new Result<>(StatusCode.PARAM_ERROR, "create reassignment json failed");
        }

        // 任务存储到数据库
        MigrationTaskDO migrationTaskDO = MigrationTaskDO.createInstance(clusterId, topicName, reassignmentJson, throttle, description);
        if (!addMigrationTask(migrationTaskDO)) {
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(migrationTaskDO);
    }

    private boolean addMigrationTask(MigrationTaskDO migrationTaskDO) {
        migrationTaskDO.setOperator(SpringContextHolder.getUserName());
        try {
            if (migrationTaskDO.getStatus() == null) {
                migrationTaskDO.setStatus(ReassignmentStatusEnum.WAITING.getCode());
            }
            migrationTaskDao.addMigrationTask(migrationTaskDO);
        } catch (Exception e) {
            logger.error("addMigrationTask@MigrationServiceImpl, add failed, migrationTaskDO:{}.", migrationTaskDO, e);
            return false;
        }
        return true;
    }

    private Result<MigrationTaskDO> checkAddMigrateTaskParamIllegal(Long clusterId, String topicName, List<Integer> brokerIdList, List<Integer> partitionIdList) {
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "topic not exist");
        }
        List<Integer> allBrokerIdList = ClusterMetadataManager.getBrokerIdList(clusterId);
        for (Integer brokerId : brokerIdList) {
            if (!allBrokerIdList.contains(brokerId)) {
                return new Result<>(StatusCode.PARAM_ERROR, String.format("brokerId:%d not alive", brokerId));
            }
        }
        if (brokerIdList.size() < topicMetadata.getReplicaNum()) {
            return new Result<>(StatusCode.PARAM_ERROR, "brokerId num is less than topic replica num");
        }
        if (partitionIdList == null) {
            return new Result<>();
        }

        List<Integer> allPartitionIdList = new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet());
        for (Integer partitionId: partitionIdList) {
            if (!allPartitionIdList.contains(partitionId)) {
                return new Result<>(StatusCode.PARAM_ERROR, String.format("partitionId:%d not exist", partitionId));
            }
        }
        return new Result<>();
    }

    @Override
    public MigrationTaskDO getMigrationTask(Long taskId) {
        try {
            return migrationTaskDao.getById(taskId);
        } catch (Exception e) {
            logger.error("getMigrationTask@MigrationServiceImpl, get failed, taskId:{}.", taskId, e);
        }
        return null;
    }

    @Override
    public Map<Integer, Integer> getMigrationStatus(ClusterDO cluster, String reassignmentJson) {
        Map<TopicAndPartition, ReassignmentStatus> reassignmentStatusMap = verifyAssignment(cluster, reassignmentJson);
        if (reassignmentStatusMap == null) {
            return null;
        }
        Map<Integer, Integer> result = new HashMap<>();
        for(Map.Entry<TopicAndPartition, ReassignmentStatus> entry: reassignmentStatusMap.entrySet()){
            TopicAndPartition tp =  entry.getKey();
            if (ReassignmentCompleted.status() == entry.getValue().status()) {
                result.put(tp.partition(), ReassignmentStatusEnum.SUCCESS.getCode());
            } else if (ReassignmentInProgress.status() == entry.getValue().status()) {
                result.put(tp.partition(), ReassignmentStatusEnum.RUNNING.getCode());
            } else {
                result.put(tp.partition(), ReassignmentStatusEnum.FAILED.getCode());
            }
        }
        return result;
    }

    @Override
    public Result executeMigrationTask(Long taskId) {
        MigrationTaskDO migrationTaskDO = getMigrationTask(taskId);
        if (migrationTaskDO == null || !ReassignmentStatusEnum.WAITING.getCode().equals(migrationTaskDO.getStatus())) {
            return new Result<>(StatusCode.PARAM_ERROR, "task may not exist or can't be execute");
        }
        ClusterDO cluster = clusterService.getById(migrationTaskDO.getClusterId());
        if (cluster == null) {
            return new Result<>(StatusCode.OPERATION_ERROR, "cluster not exist");
        }
        try {
            if (!executeAssignment(cluster, migrationTaskDO.getReassignmentJson(), migrationTaskDO.getThrottle())) {
                return new Result(StatusCode.OPERATION_ERROR, "execute migration failed");
            }
            int status = migrationTaskDao.updateById(taskId, ReassignmentStatusEnum.RUNNING.getCode(), migrationTaskDO.getThrottle());
            if (status != 1) {
                return new Result(StatusCode.MY_SQL_UPDATE_ERROR, "update throttle failed");
            }
        } catch (Exception e) {
            logger.error("executeMigrationTask@MigrationServiceImpl, update mysql:migration_task throttle failed, taskId:{}.", taskId, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, "update throttle failed");
        }
        return new Result();
    }

    @Override
    public Result modifyMigrationTask(Long taskId, Long throttle) {
        MigrationTaskDO migrationTaskDO = getMigrationTask(taskId);
        if (migrationTaskDO == null || !ReassignmentStatusEnum.triggerTask(migrationTaskDO.getStatus())) {
            return new Result<>(StatusCode.PARAM_ERROR, "task may not exist or can't be execute");
        }
        ClusterDO cluster = clusterService.getById(migrationTaskDO.getClusterId());
        if (cluster == null) {
            return new Result<>(StatusCode.OPERATION_ERROR, "cluster not exist");
        }
        if (throttle == null) {
            throttle = migrationTaskDO.getThrottle();
        }
        try {
            if (ReassignmentStatusEnum.RUNNING.getCode().equals(migrationTaskDO.getStatus()) && !executeAssignment(cluster, migrationTaskDO.getReassignmentJson(), throttle)) {
                return new Result(StatusCode.OPERATION_ERROR, "execute migration failed");
            }
            int status = migrationTaskDao.updateById(taskId, migrationTaskDO.getStatus(), throttle);
            if (status != 1) {
                return new Result(StatusCode.MY_SQL_UPDATE_ERROR, "update throttle failed");
            }
        } catch (Exception e) {
            logger.error("executeMigrationTask@MigrationServiceImpl, update mysql:migration_task throttle failed, taskId:{} throttle:{}.", taskId, throttle, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, "update throttle failed");
        }
        return new Result();
    }

    @Override
    public List<MigrationTaskDO> getMigrationTaskList() {
        return migrationTaskDao.listAll();
    }

    @Override
    public List<MigrationTaskDO> getByStatus(Integer status) {
        return migrationTaskDao.getByStatus(status);
    }

    @Override
    public Result deleteMigrationTask(Long taskId) {
        MigrationTaskDO migrationTaskDO = getMigrationTask(taskId);
        if (migrationTaskDO == null || !ReassignmentStatusEnum.cancelTask(migrationTaskDO.getStatus())) {
            return new Result<>(StatusCode.PARAM_ERROR, "task may not exist or can't be cancel");
        }
        try {
            int status = migrationTaskDao.updateById(taskId, ReassignmentStatusEnum.CANCELED.getCode(), migrationTaskDO.getThrottle());
            if (status != 1) {
                return new Result(StatusCode.MY_SQL_UPDATE_ERROR, "delete failed");
            }
        } catch (Exception e) {
            logger.error("deleteMigrationTask@MigrationServiceImpl, delete mysql:migration_task failed, taskId:{}.", taskId, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, "delete failed");
        }
        return new Result();
    }

    private String createReassignmentJson(ClusterDO cluster, String topicName, List<Object> brokerIdList) {
        ZkUtils zkUtils = createZkUtils(cluster);
        if (zkUtils == null) {
            return null;
        }

        JSONObject topicsToMoveJson = new JSONObject();
        List<JSONObject> topicList = new ArrayList<>();
        JSONObject topicJson = new JSONObject();
        topicJson.put("topic", topicName);
        topicList.add(topicJson);
        topicsToMoveJson.put("topics", topicList);
        topicsToMoveJson.put("version", 1);

        Seq<Object> brokerListToReassign = JavaConverters.asScalaIteratorConverter(brokerIdList.iterator()).asScala().toSeq();
        scala.collection.Map<TopicAndPartition, Seq<Object>> reassignmentJsonMap = ReassignPartitionsCommand.generateAssignment(zkUtils, brokerListToReassign, topicsToMoveJson.toJSONString(), false)._1();
        String reassignmentJson = ZkUtils.formatAsReassignmentJson(reassignmentJsonMap);
        closeZkUtils(zkUtils);
        return reassignmentJson;
    }

    private boolean executeAssignment(ClusterDO cluster, String reassignmentJson, Long throttle) {
        ZkUtils zkUtils = createZkUtils(cluster);
        if (zkUtils == null) {
            return false;
        }
        try {
            ReassignPartitionsCommand.executeAssignment(zkUtils, reassignmentJson, throttle);
        } catch (Throwable e) {
            logger.error("executeAssignment@MigrationServiceImpl  execute exception:",e);
            return false;
        } finally {
            closeZkUtils(zkUtils);
        }
        return true;
    }

    private Map<TopicAndPartition, ReassignmentStatus> verifyAssignment(ClusterDO cluster, String reassignmentJson) {
        ZkUtils zkUtils = createZkUtils(cluster);
        if (zkUtils == null) {
            return null;
        }

        scala.collection.Map<TopicAndPartition, Seq<Object>> partitionsToBeReassigned = ZkUtils.parsePartitionReassignmentData(reassignmentJson);
        Map<TopicAndPartition, Seq<Object>> partitionsToBeReassignedJMap = JavaConversions.asJavaMap(partitionsToBeReassigned);

        /*
           翻译这句代码:
           val partitionsBeingReassigned = zkUtils.getPartitionsBeingReassigned().mapValues(_.newReplicas)
         */
        scala.collection.Map<TopicAndPartition, ReassignedPartitionsContext> partitionsBeingReassignedContext = zkUtils.getPartitionsBeingReassigned();
        Map<TopicAndPartition, ReassignedPartitionsContext> partitionsBeingReassignedContextJMap = JavaConversions.asJavaMap(partitionsBeingReassignedContext);
        Map<TopicAndPartition, Seq<Object>> partitionsBeingReassignedJMap = new HashMap<>();

        for(Map.Entry<TopicAndPartition, ReassignedPartitionsContext> entry: partitionsBeingReassignedContextJMap.entrySet()){
            TopicAndPartition topicAndPartition = entry.getKey();
            ReassignedPartitionsContext reassignedPartitionsContext =  entry.getValue();
            partitionsBeingReassignedJMap.put(topicAndPartition, reassignedPartitionsContext.newReplicas());
        }
        scala.collection.Map<TopicAndPartition, Seq<Object>> partitionsBeingReassigned = JavaConversions.asScalaMap(partitionsBeingReassignedJMap);

        //初始化result并填装结果
        Map<TopicAndPartition, ReassignmentStatus> result = new HashMap<>();
        for (TopicAndPartition topicAndPartition : partitionsToBeReassignedJMap.keySet()) {
            result.put(topicAndPartition, ReassignPartitionsCommand.checkIfPartitionReassignmentSucceeded(zkUtils, topicAndPartition, partitionsToBeReassigned, partitionsBeingReassigned));
        }

        closeZkUtils(zkUtils);
        return result;
    }

    private ZkUtils createZkUtils(ClusterDO cluster) {
        try {
            return ZkUtils.apply(cluster.getZookeeper(), DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT, JaasUtils.isZkSecurityEnabled());
        } catch (Exception e) {
            logger.error("getZkUtils@MigrationServiceImpl, connect ZOOKEEPER failed, clusterId:{} ZOOKEEPER:{}.", cluster.getId(), cluster.getZookeeper(), e);
        }
        return null;
    }

    private void closeZkUtils(ZkUtils zkUtils) {
        if (zkUtils == null) {
            return ;
        }
        try {
            zkUtils.close();
        } catch (Exception e) {
            logger.error("closeZkUtils@MigrationServiceImpl, close zkUtils failed.", e);
        }
    }
}