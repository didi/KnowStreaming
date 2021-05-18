package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.PeakFlowStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.TopicDiskLocation;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ClusterBrokerStatus;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerDO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.BrokerMetricsDao;
import com.xiaojukeji.kafka.manager.dao.BrokerDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.ThreadPool;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author tukun, zengqiao
 * @date 2015/11/9
 */
@Service("brokerService")
public class BrokerServiceImpl implements BrokerService {
    private final static Logger LOGGER = LoggerFactory.getLogger(BrokerServiceImpl.class);

    @Autowired
    private BrokerDao brokerDao;

    @Autowired
    private JmxService jmxService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BrokerMetricsDao brokerMetricDao;

    @Autowired
    private PhysicalClusterMetadataManager physicalClusterMetadataManager;

    @Override
    public ClusterBrokerStatus getClusterBrokerStatus(Long clusterId) {
        // 副本同步状态
        Set<Integer> brokerIdSet = new HashSet<>(PhysicalClusterMetadataManager.getBrokerIdList(clusterId));
        List<BrokerMetrics> metricsList = getBrokerMetricsFromJmx(
                clusterId,
                brokerIdSet,
                KafkaMetricsCollections.BROKER_STATUS_PAGE_METRICS
        );
        int underBroker = 0;
        for (BrokerMetrics brokerMetrics: metricsList) {
            Integer underReplicated = brokerMetrics.getSpecifiedMetrics("UnderReplicatedPartitionsValue", Integer.class);
            if (!ValidateUtils.isNull(underReplicated) && underReplicated > 0) {
                underBroker++;
            }
        }
        List<Integer> brokerReplicaStatusList = Arrays.asList(
                brokerIdSet.size(),
                brokerIdSet.size() - underBroker,
                underBroker
        );
        Map<Integer, Integer> peakFlowStatusMap = new HashMap<>();

        // 峰值状态
        List<BrokerDO> brokerDOList = brokerDao.getByClusterId(clusterId);
        Long peakFlow = configService.getLongValue(
                ConfigConstant.BROKER_CAPACITY_LIMIT_CONFIG_KEY, ConfigConstant.DEFAULT_BROKER_CAPACITY_LIMIT);
        for (BrokerDO brokerDO : brokerDOList) {
            PeakFlowStatusEnum peakFlowStatus = getPeakFlowStatus(brokerDO.getMaxAvgBytesIn(), peakFlow);
            peakFlowStatusMap.put(
                    peakFlowStatus.getCode(),
                    peakFlowStatusMap.getOrDefault(peakFlowStatus.getCode(), 0) + 1
            );
        }
        List<Integer> brokerBytesInStatusList = Arrays.asList(
                brokerDOList.size(),
                peakFlowStatusMap.getOrDefault(PeakFlowStatusEnum.BETWEEN_00_60.getCode(), 0),
                peakFlowStatusMap.getOrDefault(PeakFlowStatusEnum.BETWEEN_60_80.getCode(), 0),
                peakFlowStatusMap.getOrDefault(PeakFlowStatusEnum.BETWEEN_80_100.getCode(), 0),
                peakFlowStatusMap.getOrDefault(PeakFlowStatusEnum.BETWEEN_100_PLUS.getCode(), 0),
                peakFlowStatusMap.getOrDefault(PeakFlowStatusEnum.BETWEEN_EXCEPTION.getCode(), 0)
        );

        ClusterBrokerStatus clusterBrokerStatus = new ClusterBrokerStatus();
        clusterBrokerStatus.setBrokerReplicaStatusList(brokerReplicaStatusList);
        clusterBrokerStatus.setBrokerBytesInStatusList(brokerBytesInStatusList);
        return clusterBrokerStatus;
    }

    private PeakFlowStatusEnum getPeakFlowStatus(Double maxAvgBytesIn, Long peakFlow) {
        if (ValidateUtils.isNullOrLessThanZero(maxAvgBytesIn)) {
            return PeakFlowStatusEnum.BETWEEN_EXCEPTION;
        }

        double rate = maxAvgBytesIn / peakFlow;
        if (rate <= 0.6) {
            return PeakFlowStatusEnum.BETWEEN_00_60;
        } else if (rate <= 0.8) {
            return PeakFlowStatusEnum.BETWEEN_60_80;
        } else if (rate <= 1) {
            return PeakFlowStatusEnum.BETWEEN_80_100;
        } else {
            return PeakFlowStatusEnum.BETWEEN_100_PLUS;
        }
    }

    @Override
    public List<BrokerOverviewDTO> getBrokerOverviewList(Long clusterId, Set<Integer> brokerIdSet) {
        Boolean isGetAll = brokerIdSet == null? Boolean.TRUE: Boolean.FALSE;
        if (isGetAll) {
            brokerIdSet = new HashSet<>(PhysicalClusterMetadataManager.getBrokerIdList(clusterId));
        }
        List<BrokerMetrics> metricsList = getBrokerMetricsFromJmx(
                clusterId,
                brokerIdSet,
                KafkaMetricsCollections.BROKER_OVERVIEW_PAGE_METRICS
        );
        if (ValidateUtils.isNull(metricsList)) {
            metricsList = new ArrayList<>();
        }
        Map<Integer, BrokerMetrics> brokerMap = new HashMap<>(metricsList.size());
        for (BrokerMetrics metrics: metricsList) {
            brokerMap.put(metrics.getBrokerId(), metrics);
        }

        Map<Integer, BrokerOverviewDTO> overviewDTOMap = new HashMap<>();
        for (Integer brokerId: brokerIdSet) {
            BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (ValidateUtils.isNull(brokerMetadata)) {
                LOGGER.warn("class=BrokerServiceImpl||method=getBrokerOverviewList||brokerId={}|||msg=brokerMetadata is null!",
                        brokerId);
                continue;
            }
            overviewDTOMap.put(brokerId, BrokerOverviewDTO.newInstance(
                    brokerMetadata,
                    brokerMap.get(brokerId),
                    physicalClusterMetadataManager.getKafkaVersionFromCache(clusterId, brokerId)
            ));
        }

        Long peakFlow = configService.getLongValue(
                ConfigConstant.BROKER_CAPACITY_LIMIT_CONFIG_KEY, ConfigConstant.DEFAULT_BROKER_CAPACITY_LIMIT);
        List<BrokerDO> brokerDOList = brokerDao.getByClusterId(clusterId);
        for (BrokerDO brokerDO : brokerDOList) {
            if ((!isGetAll && !brokerIdSet.contains(brokerDO))) {
                continue;
            }
            if (overviewDTOMap.containsKey(brokerDO.getBrokerId())) {
                BrokerOverviewDTO brokerOverviewDTO = overviewDTOMap.get(brokerDO.getBrokerId());
                brokerOverviewDTO.setPeakFlowStatus(getPeakFlowStatus(brokerDO.getMaxAvgBytesIn(), peakFlow).getCode());
                continue;
            }
            BrokerOverviewDTO brokerOverviewDTO = new BrokerOverviewDTO();
            brokerOverviewDTO.setBrokerId(brokerDO.getBrokerId());
            brokerOverviewDTO.setHost(brokerDO.getHost());
            brokerOverviewDTO.setPort(brokerDO.getPort());
            brokerOverviewDTO.setJmxPort(DBStatusEnum.DEAD.getStatus());
            brokerOverviewDTO.setStartTime(brokerDO.getTimestamp());
            brokerOverviewDTO.setStatus(DBStatusEnum.DEAD.getStatus());
            brokerOverviewDTO.setPeakFlowStatus(getPeakFlowStatus(brokerDO.getMaxAvgBytesIn(), peakFlow).getCode());
            overviewDTOMap.put(brokerDO.getBrokerId(), brokerOverviewDTO);
        }
        return new ArrayList<>(overviewDTOMap.values());
    }

    @Override
    public List<BrokerMetrics> getBrokerMetricsFromJmx(Long clusterId, Set<Integer> brokerIdSet, Integer metricsCode) {
        if (ValidateUtils.isNull(brokerIdSet)) {
            return new ArrayList<>();
        }
        List<Integer> brokerIdList = new ArrayList<>(brokerIdSet);
        FutureTask<BrokerMetrics>[] taskList = new FutureTask[brokerIdList.size()];
        for (int i = 0; i < brokerIdList.size(); i++) {
            Integer brokerId = brokerIdList.get(i);
            taskList[i] = new FutureTask<BrokerMetrics>(new Callable<BrokerMetrics>() {
                @Override
                public BrokerMetrics call() throws Exception {
                    return getBrokerMetricsFromJmx(clusterId, brokerId, metricsCode);
                }
            });
            ThreadPool.submitApiCallTask(taskList[i]);
        }
        List<BrokerMetrics> metricsList = new ArrayList<>(brokerIdSet.size());
        for (int i = 0; i < brokerIdList.size(); i++) {
            try {
                BrokerMetrics brokerMetrics = taskList[i].get();
                if (ValidateUtils.isNull(brokerMetrics)) {
                    continue;
                }
                metricsList.add(brokerMetrics);
            } catch (Exception e) {
                LOGGER.error("get broker metrics from jmx error, clusterId:{}, brokerId:{}, metricsCode:{}.",
                        clusterId, brokerIdList.get(i), metricsCode, e);
            }
        }
        return metricsList;
    }

    @Override
    public BrokerMetrics getBrokerMetricsFromJmx(Long clusterId, Integer brokerId, Integer metricsCode) {
        if (clusterId == null || brokerId == null || metricsCode == null) {
            return null;
        }
        BrokerMetrics brokerMetrics = jmxService.getBrokerMetrics(clusterId, brokerId, metricsCode);
        if (brokerMetrics == null) {
            return null;
        }
        brokerMetrics.setClusterId(clusterId);
        brokerMetrics.setBrokerId(brokerId);
        return brokerMetrics;
    }

    @Override
    public List<BrokerMetricsDO> getBrokerMetricsFromDB(Long clusterId,
                                                        Integer brokerId,
                                                        Date startTime,
                                                        Date endTime) {
        return brokerMetricDao.getBrokerMetrics(clusterId, brokerId, startTime, endTime);
    }

    @Override
    public List<TopicDiskLocation> getBrokerTopicLocation(Long clusterId, Integer brokerId) {
        Map<TopicPartition, String> diskNameMap = jmxService.getBrokerTopicLocation(clusterId, brokerId);
        Map<String, List<PartitionState>> stateMap = topicService.getTopicPartitionState(clusterId, brokerId);

        // 整理数据格式<topicName-diskName, DiskTopicDTO>>
        Map<String, TopicDiskLocation> locationMap = new HashMap<>();
        for (Map.Entry<TopicPartition, String> entry: diskNameMap.entrySet()) {
            String key = new StringBuilder().append(entry.getKey().topic()).append(entry.getValue()).toString();
            TopicDiskLocation diskLocation = locationMap.get(key);
            if (ValidateUtils.isNull(diskLocation)) {
                diskLocation = new TopicDiskLocation();
                diskLocation.setClusterId(clusterId);
                diskLocation.setBrokerId(brokerId);
                diskLocation.setDiskName(entry.getValue());
                diskLocation.setTopicName(entry.getKey().topic());
                diskLocation.setLeaderPartitions(new ArrayList<>());
                diskLocation.setFollowerPartitions(new ArrayList<>());
                diskLocation.setUnderReplicated(false);
                diskLocation.setUnderReplicatedPartitions(new ArrayList<>());
                locationMap.put(key, diskLocation);
            }
            diskLocation.getFollowerPartitions().add(entry.getKey().partition());
        }

        List<TopicDiskLocation> locationList = new ArrayList<>();
        for (TopicDiskLocation diskLocation: locationMap.values()) {
            locationList.add(diskLocation);
            List<PartitionState> stateList = stateMap.get(diskLocation.getTopicName());
            if (ValidateUtils.isNull(stateList)) {
                continue;
            }
            for (PartitionState state: stateList) {
                if (!diskLocation.getFollowerPartitions().contains(state.getPartitionId())) {
                    continue;
                }
                if (!state.isUnderReplicated()) {
                    diskLocation.getUnderReplicatedPartitions().add(state.getPartitionId());
                    diskLocation.setUnderReplicated(true);
                }
                if (brokerId.equals(state.getLeader())) {
                    diskLocation.getLeaderPartitions().add(state.getPartitionId());
                }
            }
        }
        return locationList;
    }

    @Override
    public Double calBrokerMaxAvgBytesIn(Long clusterId,
                                         Integer brokerId,
                                         Integer duration,
                                         Date startTime,
                                         Date endTime) {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(brokerId)
                || ValidateUtils.isNullOrLessThanZero(duration)
                || ValidateUtils.isNull(startTime)
                || ValidateUtils.isNull(endTime)) {
            return -1.0;
        }
        List<BrokerMetricsDO> doList = this.getBrokerMetricsFromDB(clusterId, brokerId, startTime, endTime);
        if (ValidateUtils.isEmptyList(doList)) {
            return 0.0;
        }
        List<BrokerMetrics> metricsList = MetricsConvertUtils.convert2BrokerMetricsList(doList);

        Double maxAvgBytesIn = 0.0;
        for (int i = 0; i < duration && i < metricsList.size(); ++i) {
            maxAvgBytesIn += metricsList.get(i).getBytesInPerSecOneMinuteRate(0.0);
        }

        Double tempMaxAvgBytesIn = maxAvgBytesIn;
        for (int i = duration; i < metricsList.size(); ++i) {
            tempMaxAvgBytesIn += (
                    metricsList.get(i).getBytesInPerSecOneMinuteRate(0.0)
                            - metricsList.get(i - duration).getBytesInPerSecOneMinuteRate(0.0)
            );
            if (tempMaxAvgBytesIn >= maxAvgBytesIn) {
                maxAvgBytesIn = tempMaxAvgBytesIn;
            }
        }
        return maxAvgBytesIn / Math.min(duration, metricsList.size());
    }

    @Override
    public BrokerBasicDTO getBrokerBasicDTO(Long clusterId, Integer brokerId) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isNull(brokerId)) {
            return null;
        }

        BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
        if (ValidateUtils.isNull(brokerMetadata)) {
            return null;
        }
        BrokerBasicDTO brokerBasicDTO = new BrokerBasicDTO();
        brokerBasicDTO.setHost(brokerMetadata.getHost());
        brokerBasicDTO.setPort(brokerMetadata.getPort());
        brokerBasicDTO.setJmxPort(brokerMetadata.getJmxPort());
        brokerBasicDTO.setStartTime(brokerMetadata.getTimestamp());

        BrokerMetrics brokerMetrics = jmxService.getBrokerMetrics(
                clusterId,
                brokerId,
                KafkaMetricsCollections.BROKER_BASIC_PAGE_METRICS
        );
        if (!ValidateUtils.isNull(brokerMetrics)) {
            brokerBasicDTO.setPartitionCount(brokerMetrics.getSpecifiedMetrics("PartitionCountValue", Integer.class));
            brokerBasicDTO.setLeaderCount(brokerMetrics.getSpecifiedMetrics("LeaderCountValue", Integer.class));
        }

        Integer topicNum = 0;
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }
            if (topicMetadata.getBrokerIdSet().contains(brokerId)) {
                topicNum += 1;
            }
        }
        brokerBasicDTO.setTopicNum(topicNum);
        return brokerBasicDTO;
    }

    @Override
    public String getBrokerVersion(Long clusterId, Integer brokerId) {
        return jmxService.getBrokerVersion(clusterId, brokerId);
    }

    @Override
    public List<BrokerDO> listAll() {
        try {
            return brokerDao.listAll();
        } catch (Exception e) {
            LOGGER.error("get all broker failed.", e);
        }
        return new ArrayList<>();
    }

    @Override
    public int replace(BrokerDO brokerDO) {
        try {
            return brokerDao.replace(brokerDO);
        } catch (Exception e) {
            LOGGER.error("replace broker failed, brokerDO:{}.", brokerDO, e);
        }
        return 0;
    }

    @Override
    public ResultStatus delete(Long clusterId, Integer brokerId) {
        try {
            if (brokerDao.deleteById(clusterId, brokerId) < 1) {
                return ResultStatus.OPERATION_FAILED;
            }
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("delete broker failed, clusterId:{} brokerId:{}.", clusterId, brokerId);
        }
        return ResultStatus.MYSQL_ERROR;
    }
}
