package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ClusterModeEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalCluster;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalClusterMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.LogicalClusterDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.LogicalClusterService;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/6/29
 */
@Service("logicalClusterService")
public class LogicalClusterServiceImpl implements LogicalClusterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalClusterServiceImpl.class);

    @Autowired
    private LogicalClusterDao logicalClusterDao;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private AppService appService;

    @Autowired
    private LogicalClusterMetadataManager logicClusterMetadataManager;

    @Autowired
    private PhysicalClusterMetadataManager physicalClusterMetadataManager;

    @Override
    public List<LogicalCluster> getAllLogicalCluster() {
        return convert2LogicalClusterList(logicClusterMetadataManager.getLogicalClusterList());
    }

    @Override
    public List<LogicalCluster> getLogicalClusterListByPrincipal(String principal) {
        List<LogicalClusterDO> clusterList = logicClusterMetadataManager.getLogicalClusterList()
                .stream()
                .filter(elem -> ClusterModeEnum.SHARED_MODE.getCode().equals(elem.getMode()))
                .collect(Collectors.toList());
        if (ValidateUtils.isBlank(principal)) {
            return convert2LogicalClusterList(clusterList);
        }

        // 获取principal名下的应用
        List<AppDO> appDOList = appService.getByPrincipal(principal);
        if (ValidateUtils.isEmptyList(appDOList)) {
            return convert2LogicalClusterList(clusterList);
        }
        Set<String> appIdSet = appDOList.stream().map(elem -> elem.getAppId()).collect(Collectors.toSet());

        // 获取principal名下的集群
        for (LogicalClusterDO logicalClusterDO : logicClusterMetadataManager.getLogicalClusterList()) {
            if (ClusterModeEnum.SHARED_MODE.getCode().equals(logicalClusterDO.getMode()) ||
                    !appIdSet.contains(logicalClusterDO.getAppId())) {
                continue;
            }
            clusterList.add(logicalClusterDO);
        }
        return convert2LogicalClusterList(clusterList);
    }

    @Override
    public LogicalCluster getLogicalCluster(Long logicalClusterId) {
        LogicalClusterDO logicalClusterDO = logicClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            LOGGER.warn("class=LogicalClusterServiceImpl||method=getLogicalCluster||logicalClusterId={}||msg=logicalClusterDO is null!", logicalClusterId);
            return null;
        }
        return convert2LogicalCluster(logicalClusterDO);
    }

    private List<LogicalCluster> convert2LogicalClusterList(List<LogicalClusterDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }

        List<LogicalCluster> dtoList = new ArrayList<>();
        for (LogicalClusterDO elem: doList) {
            dtoList.add(convert2LogicalCluster(elem));
        }
        return dtoList;
    }

    private LogicalCluster convert2LogicalCluster(LogicalClusterDO logicalClusterDO) {
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return null;
        }
        LogicalCluster logicalCluster = new LogicalCluster();
        logicalCluster.setLogicalClusterId(logicalClusterDO.getId());
        logicalCluster.setLogicalClusterName(logicalClusterDO.getName());
        logicalCluster.setLogicalClusterIdentification(logicalClusterDO.getIdentification());
        logicalCluster.setClusterVersion(
                physicalClusterMetadataManager.getKafkaVersion(
                        logicalClusterDO.getClusterId(),
                        new ArrayList<>(logicClusterMetadataManager.getBrokerIdSet(logicalClusterDO.getId()))
                )
        );
        logicalCluster.setMode(logicalClusterDO.getMode());
        logicalCluster.setTopicNum(logicClusterMetadataManager.getTopicNameSet(logicalClusterDO.getId()).size());
        logicalCluster.setPhysicalClusterId(logicalClusterDO.getClusterId());
        logicalCluster.setBootstrapServers("");
        logicalCluster.setDescription(logicalClusterDO.getDescription());
        logicalCluster.setGmtCreate(logicalClusterDO.getGmtCreate().getTime());
        logicalCluster.setGmtModify(logicalClusterDO.getGmtModify().getTime());
        return logicalCluster;
    }

    @Override
    public List<TopicMetadata> getTopicMetadatas(LogicalClusterDO logicalClusterDO) {
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return new ArrayList<>();
        }
        List<TopicMetadata> metadataList = new ArrayList<>();
        for (String topicName: logicClusterMetadataManager.getTopicNameSet(logicalClusterDO.getId())) {
            TopicMetadata topicMetadata =
                    PhysicalClusterMetadataManager.getTopicMetadata(logicalClusterDO.getClusterId(), topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }
            metadataList.add(topicMetadata);
        }
        return metadataList;
    }

    @Override
    public List<BrokerMetadata> getBrokerMetadatas(LogicalClusterDO logicalClusterDO) {
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return new ArrayList<>();
        }
        List<BrokerMetadata> metadataList = new ArrayList<>();
        for (Integer brokerId: logicClusterMetadataManager.getBrokerIdSet(logicalClusterDO.getId())) {
            BrokerMetadata brokerMetadata =
                    PhysicalClusterMetadataManager.getBrokerMetadata(logicalClusterDO.getClusterId(), brokerId);
            if (ValidateUtils.isNull(brokerMetadata)) {
                continue;
            }
            metadataList.add(brokerMetadata);
        }
        return metadataList;
    }

    @Override
    public List<LogicalClusterMetrics> getLogicalClusterMetricsFromDB(LogicalClusterDO logicalClusterDO,
                                                                      Date startTime,
                                                                      Date endTime) {
        Map<Long, LogicalClusterMetrics> metricsMap = new TreeMap<>();
        for (Integer brokerId: logicClusterMetadataManager.getBrokerIdSet(logicalClusterDO.getId())) {
            List<BrokerMetricsDO> doList =
                    brokerService.getBrokerMetricsFromDB(logicalClusterDO.getClusterId(), brokerId, startTime, endTime);
            if (ValidateUtils.isEmptyList(doList)) {
                continue;
            }
            for (BrokerMetricsDO brokerMetricsDO: doList) {
                Long timestamp = brokerMetricsDO.getGmtCreate().getTime() / 1000;
                LogicalClusterMetrics logicalClusterMetrics =
                        metricsMap.getOrDefault(timestamp, new LogicalClusterMetrics());
                BrokerMetrics brokerMetrics = MetricsConvertUtils.convert2BrokerMetrics(brokerMetricsDO);
                logicalClusterMetrics.setBytesInPerSec(
                        logicalClusterMetrics.getBytesInPerSec()
                                + brokerMetrics.getBytesInPerSecOneMinuteRate(0.0)
                );
                logicalClusterMetrics.setBytesOutPerSec(
                        logicalClusterMetrics.getBytesOutPerSec()
                                + brokerMetrics.getBytesOutPerSecOneMinuteRate(0.0));
                logicalClusterMetrics.setBytesRejectedPerSec(
                        logicalClusterMetrics.getBytesRejectedPerSec()
                                + brokerMetrics.getBytesRejectedPerSecOneMinuteRate(0.0));
                logicalClusterMetrics.setMessagesInPerSec(
                        logicalClusterMetrics.getMessagesInPerSec()
                                + brokerMetrics.getMessagesInPerSecOneMinuteRate(0.0));
                logicalClusterMetrics.setTotalProduceRequestsPerSec(
                        logicalClusterMetrics.getTotalProduceRequestsPerSec()
                                + brokerMetrics.getTotalProduceRequestsPerSecOneMinuteRate(0.0));
                logicalClusterMetrics.setGmtCreate(timestamp * 1000);
                metricsMap.put(timestamp, logicalClusterMetrics);
            }
        }
        return new ArrayList<>(metricsMap.values());
    }

    @Override
    public List<LogicalClusterDO> listAll() {
        return logicalClusterDao.listAll();
    }

    @Override
    public ResultStatus createLogicalCluster(LogicalClusterDO logicalClusterDO) {
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            if (existRegionAlreadyInUse(
                    logicalClusterDO.getClusterId(),
                    null,
                    ListUtils.string2LongList(logicalClusterDO.getRegionList()))) {
                return ResultStatus.RESOURCE_ALREADY_USED;
            }

            if (logicalClusterDao.insert(logicalClusterDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (DuplicateKeyException e) {
            LOGGER.error("create logical cluster failed, name already existed, newLogicalClusterDO:{}.", logicalClusterDO, e);
            return ResultStatus.RESOURCE_ALREADY_EXISTED;
        } catch (Exception e) {
            LOGGER.error("create logical cluster failed, mysql error, newLogicalClusterDO:{}.", logicalClusterDO, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public List<LogicalClusterDO> getByPhysicalClusterId(Long physicalClusterId) {
        try {
            return logicalClusterDao.getByClusterId(physicalClusterId);
        } catch (Exception e) {
            LOGGER.error("get logical cluster failed, physicalClusterId:{}.", physicalClusterId, e);
        }
        return new ArrayList<>();
    }

    @Override
    public LogicalClusterDO getById(Long id) {
        try {
            return logicalClusterDao.getById(id);
        } catch (Exception e) {
            LOGGER.error("get logical cluster failed, id:{}.", id, e);
        }
        return null;
    }

    @Override
    public ResultStatus deleteById(Long logicalClusterId) {
        if (ValidateUtils.isNull(logicalClusterId)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            if (logicalClusterDao.deleteById(logicalClusterId) > 0) {
                return ResultStatus.SUCCESS;
            }
            return ResultStatus.RESOURCE_NOT_EXIST;
        } catch (Exception e) {
            LOGGER.error("class=LogicalClusterServiceImpl||method=getById||errMsg={}||logicalClusterId={}", e.getMessage(), logicalClusterId, e);
            return ResultStatus.MYSQL_ERROR;
        }
    }

    @Override
    public ResultStatus updateById(LogicalClusterDO logicalClusterDO) {
        if (ValidateUtils.isNull(logicalClusterDO) || ValidateUtils.isNull(logicalClusterDO.getId())) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            LogicalClusterDO oldLogicalClusterDO = logicalClusterDao.getById(logicalClusterDO.getId());
            if (ValidateUtils.isNull(oldLogicalClusterDO)) {
                return ResultStatus.RESOURCE_NOT_EXIST;
            }
            if (existRegionAlreadyInUse(
                    logicalClusterDO.getClusterId(),
                    logicalClusterDO.getId(),
                    ListUtils.string2LongList(logicalClusterDO.getRegionList()))) {
                return ResultStatus.RESOURCE_ALREADY_USED;
            }

            if (logicalClusterDao.updateById(logicalClusterDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("update logical cluster failed, newLogicalClusterDO:{}.", logicalClusterDO, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus updateById(Long logicalClusterId, List<Long> regionIdList) {
        if (ValidateUtils.isNullOrLessThanZero(logicalClusterId)
                || ValidateUtils.isEmptyList(regionIdList)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            LogicalClusterDO logicalClusterDO = logicalClusterDao.getById(logicalClusterId);
            if (ValidateUtils.isNull(logicalClusterDO)) {
                return ResultStatus.RESOURCE_NOT_EXIST;
            }
            if (existRegionAlreadyInUse(
                    logicalClusterDO.getClusterId(),
                    logicalClusterId,
                    regionIdList)) {
                return ResultStatus.RESOURCE_ALREADY_USED;
            }

            logicalClusterDO.setRegionList(ListUtils.longList2String(regionIdList));
            if (logicalClusterDao.updateById(logicalClusterDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("update logical cluster failed, logicalClusterId:{} regionIdList:{}.",
                    logicalClusterId, regionIdList, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    private boolean existRegionAlreadyInUse(Long physicalClusterId,
                                            Long logicalClusterId,
                                            List<Long> newRegionIdList) {
        if (ValidateUtils.isNull(physicalClusterId) || ValidateUtils.isEmptyList(newRegionIdList)) {
            return true;
        }

        List<LogicalClusterDO> doList = this.getByPhysicalClusterId(physicalClusterId);
        if (ValidateUtils.isEmptyList(doList)) {
            return false;
        }

        for (LogicalClusterDO logicalClusterDO : doList) {
            if (logicalClusterDO.getId().equals(logicalClusterId)) {
                // 被logicalClusterId自己使用的忽略
                continue;
            }
            List<Long> regionIdList = ListUtils.string2LongList(logicalClusterDO.getRegionList());
            if (ValidateUtils.isEmptyList(regionIdList)) {
                continue;
            }
            if (regionIdList.stream().filter(elem -> newRegionIdList.contains(elem)).count() > 0) {
                // 存在被使用的情况
                return true;
            }
        }
        return false;
    }
}