package com.xiaojukeji.kafka.manager.service.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicReassignActionEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionAttributeDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroupSummary;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbTaskDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbTaskVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbTopicProcessVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbTopicVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassigncmbMerticsVO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.ReassignmentElemData;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.ReassignTaskDao;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.service.ReassignCmbService;
import com.xiaojukeji.kafka.manager.service.service.ReassignService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import kafka.common.TopicAndPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("reassignCmbService")
public class ReassignCmbServiceImpl implements ReassignCmbService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReassignServiceImpl.class);

  @Autowired
  private ClusterService clusterService;

  @Autowired
  private ReassignTaskDao reassignTaskDao;

  @Autowired
  private RegionService regionService;

  @Autowired
  private ReassignService reassignService;

  @Autowired
  private JmxService jmxService;

  @Autowired
  private ConsumerService consumerService;

  @Autowired
  private TopicManagerService topicManagerService;

  @Autowired
  private LogicalClusterMetadataManager logicalClusterMetadataManager;

  @Override
  public List<ReassignCmbVO> getReassignTasksByCondition(ReassignCmbDTO dto) {
    // 逻辑集群id非空，则获取物理集群id
    if (!ValidateUtils.isEmptyList(dto.getLogicClusterIds())) {
      Set<Long> clusters = Sets.newHashSet();
      dto.getLogicClusterIds().forEach(logicCluster -> {
        clusters.add(logicalClusterMetadataManager.getPhysicalClusterId(logicCluster));
      });
      dto.setLogicClusterIds(new ArrayList<>(clusters));
    }
    Map<String,Object> params = Maps.newHashMap();
    params.put("logicClusterIds",dto.getLogicClusterIds());
    params.put("taskStatuss",dto.getTaskStatuss());
    params.put("field",dto.getField());
    params.put("sort",dto.getSort());
    params.put("startIndex",(dto.getPageNo() - 1) * dto.getPageSize());
    params.put("pageSize",dto.getPageSize());
    List<ReassignTaskDO> doList = reassignTaskDao.getReassignTasksByCondition(params);
    List<ReassignCmbVO> voList = Lists.newArrayList();
    doList.forEach(elem -> {
      ReassignCmbVO vo = new ReassignCmbVO();
      ClusterDO clusterDO = clusterService.getById(elem.getClusterId());
      if (ValidateUtils.isNull(clusterDO)) {
        vo.setLogicClusterId(elem.getClusterId());
        vo.setLogicClusterName(clusterDO.getClusterName());
      }
      vo.setStatus(elem.getStatus());
      vo.setGmtModify(elem.getGmtModify().getTime());
      vo.setExecuteTime(0L);
      vo.setDescription(elem.getDescription());
      vo.setCreator(elem.getOperator());
      vo.setBeginTime(elem.getBeginTime().getTime());
      vo.setTaskName("");
      vo.setTaskId(elem.getTaskId());
      voList.add(vo);
    });
    return voList;
  }

  @Override
  public Result createReassignTask(ReassignCmbTaskDTO dto) {
    if (ValidateUtils.isNull(dto) || ValidateUtils.isEmptyList(dto.getTopicList())) {
      return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
    }
    List<ReassignTopicDTO> dtoList = Lists.newArrayList();
    // 转换
    reaCmbTopicDTO2ReaTopicDTO(dto, dtoList);
    return Result.buildFrom(reassignService.createTask(dtoList, SpringTool.getUserName()));
  }

  private void reaCmbTopicDTO2ReaTopicDTO(ReassignCmbTaskDTO dto, List<ReassignTopicDTO> dtoList) {
    dto.getTopicList().forEach(reassignCmbTopicDTO -> {
      ReassignTopicDTO reassignTopicDTO = new ReassignTopicDTO();
      reassignTopicDTO.setClusterId(dto.getClusterId());
      reassignTopicDTO.setTopicName(reassignCmbTopicDTO.getTopicName());
      reassignTopicDTO.setBrokerIdList(dto.getBrokerIdList());
      reassignTopicDTO.setRegionId(dto.getRegionId());
      reassignTopicDTO.setPartitionIdList(reassignCmbTopicDTO.getPartitionIdList());
      reassignTopicDTO.setThrottle(dto.getThrottle());
      reassignTopicDTO.setOriginalRetentionTime(reassignCmbTopicDTO.getOriginalRetentionTime());
      reassignTopicDTO.setReassignRetentionTime(reassignCmbTopicDTO.getReassignRetentionTime());
      reassignTopicDTO.setBeginTime(dto.getBeginTime());
      reassignTopicDTO.setDescription(dto.getDescription());
      dtoList.add(reassignTopicDTO);
    });
  }

  @Override
  public Result<ReassigncmbMerticsVO> getReassignTopicMetrics(Long clusterId, String topicName) {
    ReassigncmbMerticsVO vo = new ReassigncmbMerticsVO();
    vo.setTopicName(topicName);

    // 近三天峰值流量
    List<Double> peakFlow = Lists.newArrayList();
    for (int i = 1; i < 4; i++) {
      peakFlow.add(topicManagerService.getTopicMaxAvgBytesIn(
          clusterId, topicName, new Date(DateUtils.getDayStarTime(-1 * i)), new Date(), 1));
    }
    vo.setPeakFlow(peakFlow);

    // 原数据保存时间
    Long topicRetentionTime = PhysicalClusterMetadataManager.getTopicRetentionTime(clusterId, topicName);
    vo.setOriginalRetentionTime(topicRetentionTime);

    // maxlogsize
    TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
    if (!ValidateUtils.isNull(topicMetadata)) {
      List<PartitionState> partitionStateList = KafkaZookeeperUtils.getTopicPartitionState(
          PhysicalClusterMetadataManager.getZKConfig(clusterId),
          topicName,
          new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet())
      );
      Map<Integer, PartitionAttributeDTO> partitionMap = new HashMap<>();
      try {
        partitionMap = jmxService.getPartitionAttribute(clusterId, topicName, partitionStateList);
      } catch (Exception e) {
        LOGGER.error("get topic partition failed, clusterId:{}, topicName:{}.", clusterId, topicName, e);
      }
      for (PartitionState partitionState : partitionStateList) {
        PartitionAttributeDTO partitionAttributeDTO =
            partitionMap.getOrDefault(partitionState.getPartitionId(), null);
        if (!ValidateUtils.isNull(partitionAttributeDTO)) {
          vo.setMaxLogSize(partitionAttributeDTO.getLogSize());
        }
      }
    }

    // byteIn
    TopicMetrics topicMetrics = jmxService.getTopicMetrics(clusterId, topicName, KafkaMetricsCollections.COMMON_DETAIL_METRICS, true);
    vo.setByteIn(topicMetrics.getSpecifiedMetrics("BytesInPerSecMeanRate", Double.class));

    // 消费延迟
    vo.setCosumeDelay("1分钟以内");
    List<ConsumerGroupSummary> consumerGroupSummaries = consumerService.getConsumerGroupSummaries(clusterId, topicName);
    for (ConsumerGroupSummary summary : consumerGroupSummaries) {
      ClusterDO clusterDO = clusterService.getById(summary.getClusterId());
      OffsetLocationEnum offsetStoreLocation = OffsetLocationEnum.getOffsetStoreLocation(summary.getOffsetStoreLocation().location.toLowerCase());
      ConsumerGroup consumerGroup = new ConsumerGroup(clusterDO.getId(), summary.getConsumerGroup(), offsetStoreLocation);
      List<ConsumeDetailDTO> consumeDetails = consumerService.getConsumeDetail(clusterDO, topicName, consumerGroup);
      for (ConsumeDetailDTO detail : consumeDetails) {
        if (detail.getConsumeOffset() < detail.getOffset()) {
          vo.setCosumeDelay("5分钟以内");
        }
      }
    }
    return Result.buildSuc(vo);
  }

  @Override
  public Result modifyReassignTask(ReassignCmbTaskDTO dto) {
    if (ValidateUtils.isNull(dto) || ValidateUtils.isNullOrLessThanZero(dto.getTaskId())) {
      return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
    }
    List<ReassignTaskDO> doList = reassignTaskDao.getByTaskId(dto.getTaskId());
    if (ValidateUtils.isEmptyList(doList)) {
      // 迁移任务不存在
      return Result.buildFrom(ResultStatus.TASK_NOT_EXIST);
    }
    List<ReassignTopicDTO> dtoList = Lists.newArrayList();
    // 转换
    reaCmbTopicDTO2ReaTopicDTO(dto, dtoList);
    try {
      // 更新
      reassignTaskDao.batchUpdate(doList);
    } catch (Exception e) {
      LOGGER.error("batch modify status failed, tasks:{}.", doList.toArray(), e);
      return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }
    return Result.buildFrom(ResultStatus.SUCCESS);
  }

  @Override
  public Result executeReassignTask(ReassignCmbExecDTO dto) {
    if (ValidateUtils.isNull(dto)
          || ValidateUtils.isNullOrLessThanZero(dto.getTaskId())
          || ValidateUtils.isBlank(dto.getAction())
          || ValidateUtils.isNullOrLessThanZero(dto.getBeginTime())) {
      return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
    }
    List<ReassignTaskDO> doList = reassignTaskDao.getByTaskId(dto.getTaskId());
    if (ValidateUtils.isEmptyList(doList)) {
      // 迁移任务不存在
      return Result.buildFrom(ResultStatus.TASK_NOT_EXIST);
    }
    Set<Integer> statusSet = new HashSet<>();
    for (ReassignTaskDO elem: doList) {
      statusSet.add(elem.getStatus());
    }
    // 不处于新建的状态, 则不可执行启动或者取消
    if (!statusSet.contains(TaskStatusReassignEnum.NEW.getCode()) || statusSet.size() != 1) {
      return Result.buildFrom(ResultStatus.OPERATION_FORBIDDEN);
    }
    // 更改迁移任务状态
    for (ReassignTaskDO elem: doList) {
      if (TopicReassignActionEnum.CANCEL.getAction().equals(dto.getAction())) {
        elem.setStatus(TaskStatusReassignEnum.CANCELED.getCode());
      }
      if (TopicReassignActionEnum.START.getAction().equals(dto.getAction())) {
        elem.setStatus(TaskStatusReassignEnum.RUNNABLE.getCode());
        elem.setBeginTime(new Date(dto.getBeginTime()));
      }
    }
    try {
      // 更新
      reassignTaskDao.batchUpdate(doList);
    } catch (Exception e) {
      LOGGER.error("batch modify status failed, tasks:{}.", doList.toArray(), e);
      return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }
    return Result.buildFrom(ResultStatus.SUCCESS);
  }

  @Override
  public Result<ReassignCmbTaskVO> getReassignTasksByTaskId(Long taskId) {
    if (ValidateUtils.isNullOrLessThanZero(taskId)) {
      return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
    }
    List<ReassignTaskDO> doList = reassignTaskDao.getByTaskId(taskId);
    if (ValidateUtils.isEmptyList(doList)) {
      return Result.buildFrom(ResultStatus.TASK_NOT_EXIST);
    }
    // 迁移任务详情
    ReassignCmbTaskVO vo = new ReassignCmbTaskVO();
    vo.setClusterId(doList.get(0).getClusterId());
    vo.setTaskId(doList.get(0).getTaskId());
    vo.setTaskName("");
    vo.setThrottle(doList.get(0).getRealThrottle());
    vo.setBeginTime(0L);
    vo.setActualBeginTime(0L);
    vo.setEndTime(0L);

    List<Integer> destBrokers = ListUtils.string2IntList(doList.get(0).getDestBrokers());
    if (ValidateUtils.isEmptyList(destBrokers)) {
      vo.setReassginWay(true);
    } else {
      vo.setReassginWay(false);
    }
    vo.setBrokerIdList(destBrokers);
    List<RegionDO> regions = regionService.getByClusterId(doList.get(0).getClusterId());
    if (!ValidateUtils.isEmptyList(regions)) {
      regions.forEach(region -> {
        if (region.getBrokerList().equals(doList.get(0).getDestBrokers())) {
          vo.setRegionId(region.getId());
          vo.setRegionName(region.getName());
        }
      });
    }

    Set<Integer> statusSet = new HashSet<>();
    for (ReassignTaskDO elem: doList) {
      vo.setGmtCreate(elem.getGmtCreate().getTime());
      vo.setGmtModify(elem.getGmtModify().getTime());
      vo.setCreator(elem.getOperator());
      vo.setDescription(elem.getDescription());
      if (TaskStatusReassignEnum.isFinished(elem.getStatus())) {
        statusSet.add(elem.getStatus());
        // 任务结束时间
        vo.setEndTime(Math.max(elem.getGmtModify().getTime(), vo.getEndTime()));
      } else {
        statusSet.add(elem.getStatus());
      }
      // 任务计划开始时间
      vo.setBeginTime(elem.getBeginTime().getTime());
    }

    // 任务整体状态
    if (statusSet.contains(TaskStatusReassignEnum.RUNNING.getCode())) {
      vo.setStatus(TaskStatusReassignEnum.RUNNING.getCode());
      vo.setEndTime(null);
    } else if (statusSet.contains(TaskStatusReassignEnum.RUNNABLE.getCode())) {
      vo.setStatus(TaskStatusReassignEnum.RUNNABLE.getCode());
      vo.setEndTime(null);
    } else if (statusSet.contains(TaskStatusReassignEnum.NEW.getCode()) && statusSet.size() == 1) {
      // 所有的都是新建状态
      vo.setStatus(TaskStatusReassignEnum.NEW.getCode());
      vo.setEndTime(null);
    } else if (statusSet.contains(TaskStatusReassignEnum.CANCELED.getCode()) && statusSet.size() == 1) {
      // 所有的都是取消状态
      vo.setStatus(TaskStatusReassignEnum.CANCELED.getCode());
    } else if (statusSet.contains(TaskStatusReassignEnum.SUCCEED.getCode()) && statusSet.size() == 1) {
      // 所有的都是成功状态
      vo.setStatus(TaskStatusReassignEnum.SUCCEED.getCode());
    } else if (statusSet.contains(TaskStatusReassignEnum.FAILED.getCode())) {
      // 所有的都是成功状态
      vo.setStatus(TaskStatusReassignEnum.FAILED.getCode());
    } else {
      vo.setStatus(TaskStatusReassignEnum.UNKNOWN.getCode());
      vo.setEndTime(null);
    }

    // 迁移任务里topic列表信息
    List<ReassignCmbTopicVO> subVOList = Lists.newArrayList();
    Map<Long, ClusterDO> clusterMap = clusterService.listMap();
    doList.forEach(reassignTaskDO -> {
      ReassignCmbTopicVO reaVO = new ReassignCmbTopicVO();
      reaVO.setTopicName(reassignTaskDO.getTopicName());
      reaVO.setOriginalRetentionTime(reassignTaskDO.getOriginalRetentionTime());
      reaVO.setReassignRetentionTime(reassignTaskDO.getReassignRetentionTime());
      reaVO.setSubStatus(reassignTaskDO.getStatus());
      reaVO.setCompletedPartitionNum(0);
      List<ReassignmentElemData> elemDataList = KafkaZookeeperUtils.getReassignmentElemDataList(reassignTaskDO.getReassignmentJson());
      reaVO.setTotalPartitionNum(elemDataList.size());

      Map<TopicAndPartition, TaskStatusReassignEnum> statusMap =
          reassignService.verifyAssignment(clusterMap.get(reassignTaskDO.getClusterId()).getZookeeper(),
              reassignTaskDO.getReassignmentJson());
      elemDataList.forEach(elemData -> {
        TaskStatusReassignEnum reassignEnum = statusMap.get(
            new TopicAndPartition(reassignTaskDO.getTopicName(),
                elemData.getPartition())
        );
        if (!ValidateUtils.isNull(reassignEnum) && TaskStatusReassignEnum.isFinished(reassignEnum.getCode())) {
          reaVO.setCompletedPartitionNum(reaVO.getCompletedPartitionNum() + 1);
        }
      });

      reaVO.setMaxLogSize(0L);
      TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(reassignTaskDO.getClusterId(), reassignTaskDO.getTopicName());
      if (!ValidateUtils.isNull(topicMetadata)) {
        List<PartitionState> partitionStateList = KafkaZookeeperUtils.getTopicPartitionState(
            PhysicalClusterMetadataManager.getZKConfig(reassignTaskDO.getClusterId()),
            reassignTaskDO.getTopicName(),
            new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet())
        );
        Map<Integer, PartitionAttributeDTO> partitionMap = new HashMap<>();
        try {
          partitionMap = jmxService.getPartitionAttribute(reassignTaskDO.getClusterId(), reassignTaskDO.getTopicName(), partitionStateList);
        } catch (Exception e) {
          LOGGER.error("get topic partition failed, clusterId:{}, topicName:{}.", reassignTaskDO.getClusterId(), reassignTaskDO.getTopicName(), e);
        }
        for (PartitionState partitionState : partitionStateList) {
          PartitionAttributeDTO partitionAttributeDTO =
              partitionMap.getOrDefault(partitionState.getPartitionId(), null);
          if (!ValidateUtils.isNull(partitionAttributeDTO)) {
            reaVO.setMaxLogSize(partitionAttributeDTO.getLogSize());
          }
        }
      }

      TopicMetrics topicMetrics = jmxService.getTopicMetrics(doList.get(0).getClusterId(),
          doList.get(0).getTopicName(), KafkaMetricsCollections.COMMON_DETAIL_METRICS, true);
      reaVO.setByteIn(topicMetrics.getSpecifiedMetrics("BytesInPerSecMeanRate", Double.class));
      reaVO.setReassignFlow(topicMetrics.getSpecifiedMetrics("ReassignPerSecMeanRate", Double.class));
      reaVO.setRemainTime(0L);
      subVOList.add(reaVO);
    });
    vo.setTopicList(subVOList);
    return Result.buildSuc(vo);
  }

  @Override
  public Result<List<ReassignCmbTopicProcessVO>> getReassignTopicProcess(Long clusterId, String topicName) {
    // topic元信息
    TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
    List<ReassignCmbTopicProcessVO> voList = Lists.newArrayList();
    if (!ValidateUtils.isNull(topicMetadata)) {
        List<PartitionState> partitionStateList = KafkaZookeeperUtils.getTopicPartitionState(
            PhysicalClusterMetadataManager.getZKConfig(clusterId),
            topicName,
            new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet())
        );
        Map<Integer, PartitionAttributeDTO> partitionMap = new HashMap<>();
        try {
          partitionMap = jmxService.getPartitionAttribute(clusterId, topicName, partitionStateList);
        } catch (Exception e) {
          LOGGER.error("get topic partition failed, clusterId:{}, topicName:{}.", clusterId, topicName, e);
        }
        // <partitionId, brokerList>
        Map<Integer, List<Integer>> partitionBrokerMap = PhysicalClusterMetadataManager
            .getTopicMetadata(clusterId, topicName).getPartitionMap().getPartitions();
        for (PartitionState partitionState : partitionStateList) {
          ReassignCmbTopicProcessVO vo = new ReassignCmbTopicProcessVO();
          vo.setPartitionId(partitionState.getPartitionId());
          PartitionAttributeDTO partitionAttributeDTO =
              partitionMap.getOrDefault(partitionState.getPartitionId(), null);
          if (!ValidateUtils.isNull(partitionAttributeDTO)) {
            vo.setLogSize(partitionAttributeDTO.getLogSize());
          }
          vo.setSrcBrokerIdList(partitionBrokerMap.get(partitionState.getPartitionId()));
        // 目标BrokerId
        vo.setDestBrokerIdList(null);
        // 已完成logsize
        vo.setCompleteLogSize(0L);
        // 状态
        vo.setStatus(0);
        // 进度
        vo.setReassignProcess(null);
        voList.add(vo);
      }

    }
    return Result.buildSuc(voList);
  }

  @Override
  public long getTotal() {
    return reassignTaskDao.listAll().size();
  }
}
