package com.xiaojukeji.kafka.manager.service.cache;

import com.google.common.collect.Sets;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.service.service.LogicalClusterService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 逻辑集群元信息
 * @author zengqiao
 * @date 20/5/11
 */
@Service
public class LogicalClusterMetadataManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalClusterMetadataManager.class);

    @Autowired
    private RegionService regionService;

    @Autowired
    private LogicalClusterService logicalClusterService;

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    /**
     * <逻辑集群ID, Topic集合>
     */
    private static final Map<Long, Set<String>> LOGICAL_CLUSTER_ID_TOPIC_NAME_MAP = new ConcurrentHashMap<>();

    /**
     * <逻辑集群ID, BrokerId集合>
     */
    private static final Map<Long, Set<Integer>> LOGICAL_CLUSTER_ID_BROKER_ID_MAP = new ConcurrentHashMap<>();

    /**
     * <逻辑集群ID, 逻辑集群DO>
     */
    private static final Map<Long, LogicalClusterDO> LOGICAL_CLUSTER_MAP = new ConcurrentHashMap<>();

    /**
     * <物理集群ID, <Topic名称, 逻辑集群ID>>
     */
    private static final Map<Long, Map<String, Long>> TOPIC_LOGICAL_MAP = new ConcurrentHashMap<>();

    public Set<String> getTopicNameSet(Long logicClusterId) {
        if (!LOADED.get()) {
            flush();
        }
        return LOGICAL_CLUSTER_ID_TOPIC_NAME_MAP.getOrDefault(logicClusterId, new HashSet<>());
    }

    public Set<Integer> getBrokerIdSet(Long logicClusterId) {
        if (!LOADED.get()) {
            flush();
        }
        return LOGICAL_CLUSTER_ID_BROKER_ID_MAP.getOrDefault(logicClusterId, new HashSet<>());
    }

    public Long getTopicLogicalClusterId(Long physicalClusterId, String topicName) {
        if (!LOADED.get()) {
            flush();
        }

        Map<String, Long> logicalClusterIdMap = TOPIC_LOGICAL_MAP.get(physicalClusterId);
        if (ValidateUtils.isNull(logicalClusterIdMap)) {
            return null;
        }

        return logicalClusterIdMap.get(topicName);
    }

    public LogicalClusterDO getTopicLogicalCluster(Long physicalClusterId, String topicName) {
        if (!LOADED.get()) {
            flush();
        }

        Map<String, Long> logicalClusterIdMap = TOPIC_LOGICAL_MAP.get(physicalClusterId);
        if (ValidateUtils.isNull(logicalClusterIdMap)) {
            return null;
        }

        Long logicalClusterId = logicalClusterIdMap.get(topicName);
        if (ValidateUtils.isNull(logicalClusterId)) {
            LOGGER.debug("class=LogicalClusterMetadataManager||method=getTopicLogicalCluster||topicName={}||msg=logicalClusterId is null!",topicName);
            return null;
        }
        return LOGICAL_CLUSTER_MAP.get(logicalClusterId);
    }

    public LogicalClusterDO getLogicalCluster(Long logicalClusterId) {
        if (!LOADED.get()) {
            flush();
        }
        return LOGICAL_CLUSTER_MAP.get(logicalClusterId);
    }

    public LogicalClusterDO getLogicalCluster(Long logicalClusterId, Boolean isPhysicalClusterId) {
        if (isPhysicalClusterId != null && isPhysicalClusterId) {
            return null;
        }
        return getLogicalCluster(logicalClusterId);
    }

    public List<LogicalClusterDO> getLogicalClusterList() {
        if (!LOADED.get()) {
            flush();
        }
        return new ArrayList<>(LOGICAL_CLUSTER_MAP.values());
    }

    public Long getPhysicalClusterId(Long logicalClusterId) {
        if (ValidateUtils.isNull(logicalClusterId)) {
            LOGGER.debug("class=LogicalClusterMetadataManager||method=getPhysicalClusterId||msg=logicalClusterId is null!");
            return null;
        }
        if (!LOADED.get()) {
            flush();
        }
        LogicalClusterDO logicalClusterDO = LOGICAL_CLUSTER_MAP.get(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            LOGGER.debug("class=LogicalClusterMetadataManager||method=getPhysicalClusterId||logicalClusterId={}||msg=logicalClusterDO is null!",logicalClusterId);
            return null;
        }
        return logicalClusterDO.getClusterId();
    }

    public Long getPhysicalClusterId(Long clusterId, Boolean isPhysicalClusterId) {
        if (isPhysicalClusterId != null && isPhysicalClusterId) {
            return clusterId;
        }
        if (ValidateUtils.isNull(clusterId)) {
            LOGGER.warn("class=LogicalClusterMetadataManager||method=getPhysicalClusterId||isPhysicalClusterId={}||msg=clusterId is null!",isPhysicalClusterId);
            return null;
        }
        if (!LOADED.get()) {
            flush();
        }
        LogicalClusterDO logicalClusterDO = LOGICAL_CLUSTER_MAP.get(clusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            LOGGER.debug("class=LogicalClusterMetadataManager||method=getPhysicalClusterId||clusterId={}||msg=logicalClusterDO is null!",clusterId);
            return null;
        }
        return logicalClusterDO.getClusterId();
    }

    @Scheduled(cron="0/30 * * * * ?")
    public void flush() {
        List<LogicalClusterDO> logicalClusterDOList = logicalClusterService.listAll();
        if (ValidateUtils.isNull(logicalClusterDOList)) {
            logicalClusterDOList = Collections.EMPTY_LIST;
        }
        Set<Long> inDbLogicalClusterIds = logicalClusterDOList.stream()
          .map(LogicalClusterDO::getId)
          .collect(Collectors.toSet());

        // inCache 和 inDb 取差集，差集结果为已删除的、新增的.
        Sets.SetView<Long> diffLogicalClusterIds = Sets.difference(LOGICAL_CLUSTER_MAP.keySet(), inDbLogicalClusterIds);
        diffLogicalClusterIds.forEach(logicalClusterId -> delLogicalClusterInCache(logicalClusterId));

        Map<Long, RegionDO> regionMap = new HashMap<>();
        List<RegionDO> regionDOList = regionService.listAll();
        if (ValidateUtils.isNull(regionDOList)) {
            regionDOList = new ArrayList<>();
        }
        for (RegionDO regionDO: regionDOList) {
            regionMap.put(regionDO.getId(), regionDO);
        }

        for (LogicalClusterDO logicalClusterDO: logicalClusterDOList) {
            try {
                LOGICAL_CLUSTER_MAP.put(logicalClusterDO.getId(), logicalClusterDO);
                flush(logicalClusterDO, regionMap);
            } catch (Exception e) {
                LOGGER.error("flush logical cluster metadata failed, logicalCluster:{}.", logicalClusterDO, e);
            }
        }
        LOADED.set(true);
    }

    private void flush(LogicalClusterDO logicalClusterDO, Map<Long, RegionDO> regionMap) {
        Set<Integer> brokerIdSet = new HashSet<>();

        // 计算逻辑集群到brokerId集合的映射
        List<Long> regionIdList = ListUtils.string2LongList(logicalClusterDO.getRegionList());
        for (Long regionId: regionIdList) {
            RegionDO regionDO = regionMap.get(regionId);
            if (ValidateUtils.isNull(regionDO) || !logicalClusterDO.getClusterId().equals(regionDO.getClusterId())) {
                LOGGER.warn("flush logical cluster metadata failed, exist illegal region, logicalCluster:{} region:{}.", logicalClusterDO, regionId);
                continue;
            }
            brokerIdSet.addAll(ListUtils.string2IntList(regionDO.getBrokerList()));
        }
        LOGICAL_CLUSTER_ID_BROKER_ID_MAP.put(logicalClusterDO.getId(), brokerIdSet);

        // 计算逻辑集群到Topic名称的映射
        Set<String> topicNameSet = PhysicalClusterMetadataManager.getBrokerTopicNum(
                logicalClusterDO.getClusterId(),
                brokerIdSet);
        LOGICAL_CLUSTER_ID_TOPIC_NAME_MAP.put(logicalClusterDO.getId(), topicNameSet);

        // 计算Topic名称到逻辑集群的映射
        Map<String, Long> subMap =
                TOPIC_LOGICAL_MAP.getOrDefault(logicalClusterDO.getClusterId(), new ConcurrentHashMap<>());
        for (String topicName: topicNameSet) {
            subMap.put(topicName, logicalClusterDO.getId());
        }
        TOPIC_LOGICAL_MAP.put(logicalClusterDO.getClusterId(), subMap);
    }

    private void delLogicalClusterInCache(Long logicalClusterId) {
        LOGICAL_CLUSTER_ID_TOPIC_NAME_MAP.remove(logicalClusterId);
        LOGICAL_CLUSTER_ID_BROKER_ID_MAP.remove(logicalClusterId);
        LOGICAL_CLUSTER_MAP.remove(logicalClusterId);
        TOPIC_LOGICAL_MAP.remove(logicalClusterId);
    }
}