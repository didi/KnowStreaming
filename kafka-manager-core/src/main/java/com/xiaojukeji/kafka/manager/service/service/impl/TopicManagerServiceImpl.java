package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.RdTopicBasic;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.MineTopicSummary;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicAppData;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicBusinessInfo;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.NumberUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.config.TopicQuotaData;
import com.xiaojukeji.kafka.manager.dao.TopicDao;
import com.xiaojukeji.kafka.manager.dao.TopicExpiredDao;
import com.xiaojukeji.kafka.manager.dao.TopicStatisticsDao;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author arthur
 * @date 2017/7/21.
 */
@Service("topicManagerService")
public class TopicManagerServiceImpl implements TopicManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicManagerServiceImpl.class);

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private TopicStatisticsDao topicStatisticsDao;

    @Autowired
    private TopicExpiredDao topicExpiredDao;

    @Autowired
    private AppService appService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private LogicalClusterService logicalClusterService;

    @Autowired
    private JmxService jmxService;

    @Autowired
    private ThrottleService throttleService;

    @Autowired
    private ClusterService clusterService;

    @Override
    public List<TopicDO> listAll() {
        try {
            return topicDao.listAll();
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    @Override
    public List<TopicDO> getByClusterId(Long clusterId) {
        if (clusterId == null) {
            return new ArrayList<>();
        }
        return topicDao.getByClusterId(clusterId);
    }

    @Override
    public TopicDO getByTopicName(Long clusterId, String topicName) {
        if (StringUtils.isEmpty(topicName) || clusterId == null) {
            return null;
        }
        try {
            return topicDao.getByTopicName(clusterId, topicName);
        } catch (Exception e) {
            LOGGER.error("select failed, clusterId:{}.topicName:{}.", clusterId, topicName, e);
        }
        return null;
    }

    @Override
    public int replaceTopicStatistics(TopicStatisticsDO topicStatisticsDO) {
        return topicStatisticsDao.replace(topicStatisticsDO);
    }

    @Override
    public Map<String, List<Double>> getTopicMaxAvgBytesIn(Long clusterId, Integer latestDay, Double minMaxAvgBytesIn) {
        Date startTime = new Date(DateUtils.getDayStarTime(latestDay));
        List<TopicStatisticsDO> doList =
                topicStatisticsDao.getTopicStatisticData(clusterId, startTime, minMaxAvgBytesIn);
        if (ValidateUtils.isEmptyList(doList)) {
            return new HashMap<>(0);
        }
        Map<String, List<Double>> doMap = new HashMap<>(2);
        for (TopicStatisticsDO elem: doList) {
            List<Double> subDOList = doMap.getOrDefault(elem.getTopicName(), new ArrayList<>());
            subDOList.add(elem.getMaxAvgBytesIn());
            doMap.put(elem.getTopicName(), subDOList);
        }
        return doMap;
    }

    @Override
    public Double getTopicMaxAvgBytesIn(Long clusterId,
                                                    String topicName,
                                                    Date startTime,
                                                    Date endTime,
                                                    Integer maxAvgDay) {
        return topicStatisticsDao.getTopicMaxAvgBytesIn(clusterId, topicName, startTime, endTime, maxAvgDay);
    }

    @Override
    public TopicStatisticsDO getByTopicAndDay(Long clusterId, String topicName, String gmtDay) {
        return topicStatisticsDao.getByTopicAndDay(clusterId, topicName, gmtDay);
    }

    @Override
    public List<TopicExpiredDO> getExpiredTopics(Integer expiredDay) {
        return topicExpiredDao.getExpiredTopics(expiredDay);
    }

    @Override
    public List<MineTopicSummary> getMyTopics(String username) {
        List<AppDO> appDOList = appService.getByPrincipal(username);
        if (ValidateUtils.isEmptyList(appDOList)) {
            return new ArrayList<>();
        }
        // 获取app创建的topic
        Set<String> appIdSet = appDOList.stream().map(appDO -> appDO.getAppId()).collect(Collectors.toSet());
        Map<String, Set<String>> appTopicNameMap = new HashMap<>();
        for (TopicDO topicDO : topicDao.listAll()) {
            if (!appIdSet.contains(topicDO.getAppId())) {
                continue;
            }
            Set<String> topicNameSet = appTopicNameMap.getOrDefault(topicDO.getAppId(), new HashSet<>());
            topicNameSet.add(topicDO.getTopicName());
            appTopicNameMap.put(topicDO.getAppId(), topicNameSet);
        }
        Map<String, Map<Long, Map<String, AuthorityDO>>> appMap = authorityService.getAllAuthority();
        // 增加权限信息和App信息
        List<MineTopicSummary> summaryList = new ArrayList<>();
        for (AppDO appDO : appDOList) {
            // 查权限
            for (Map<String, AuthorityDO> subMap : appMap.getOrDefault(appDO.getAppId(), Collections.emptyMap()).values()) {
                for (AuthorityDO authorityDO : subMap.values()) {
                    if (!PhysicalClusterMetadataManager.isTopicExist(authorityDO.getClusterId(), authorityDO.getTopicName())
                            || TopicAuthorityEnum.DENY.getCode().equals(authorityDO.getAccess())) {
                        continue;
                    }

                    MineTopicSummary mineTopicSummary = convert2MineTopicSummary(
                            appDO,
                            authorityDO,
                            appTopicNameMap.getOrDefault(authorityDO.getAppId(), Collections.emptySet())
                    );
                    if (ValidateUtils.isNull(mineTopicSummary)) {
                        continue;
                    }
                    summaryList.add(mineTopicSummary);
                }
            }
        }

        // 增加流量信息
        Map<Long, Map<String, TopicMetrics>> metricMap = KafkaMetricsCache.getAllTopicMetricsFromCache();
        for (MineTopicSummary mineTopicSummary : summaryList) {
            TopicMetrics topicMetrics = getTopicMetricsFromCacheOrJmx(
                    mineTopicSummary.getPhysicalClusterId(),
                    mineTopicSummary.getTopicName(),
                    metricMap);
            mineTopicSummary.setBytesIn(topicMetrics.getSpecifiedMetrics("BytesInPerSecOneMinuteRate"));
            mineTopicSummary.setBytesOut(topicMetrics.getSpecifiedMetrics("BytesOutPerSecOneMinuteRate"));
        }
        return summaryList;
    }

    private MineTopicSummary convert2MineTopicSummary(AppDO appDO, AuthorityDO authorityDO, Set<String> topicNameSet) {
        MineTopicSummary mineTopicSummary = new MineTopicSummary();
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getTopicLogicalCluster(
                authorityDO.getClusterId(),
                authorityDO.getTopicName()
        );
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return null;
        }
        mineTopicSummary.setLogicalClusterId(logicalClusterDO.getId());
        mineTopicSummary.setLogicalClusterName(logicalClusterDO.getName());
        mineTopicSummary.setPhysicalClusterId(logicalClusterDO.getClusterId());
        mineTopicSummary.setTopicName(authorityDO.getTopicName());
        mineTopicSummary.setBytesIn(null);
        mineTopicSummary.setBytesOut(null);
        mineTopicSummary.setAppId(appDO.getAppId());
        mineTopicSummary.setAppName(appDO.getName());
        mineTopicSummary.setAppPrincipals(appDO.getPrincipals());
        mineTopicSummary.setAccess(
                topicNameSet.contains(authorityDO.getTopicName()) ?
                        TopicAuthorityEnum.OWNER.getCode()
                        : authorityDO.getAccess()
        );
        return mineTopicSummary;
    }

    private TopicMetrics getTopicMetricsFromCacheOrJmx(Long physicalClusterId,
                                                      String topicName,
                                                      Map<Long, Map<String, TopicMetrics>> metricsMap) {
        Map<String, TopicMetrics> subMetricsMap = metricsMap.getOrDefault(physicalClusterId, new HashMap<>());
        if (subMetricsMap.containsKey(topicName)) {
            return subMetricsMap.get(topicName);
        }
        TopicMetrics topicMetrics = jmxService.getTopicMetrics(
                physicalClusterId,
                topicName,
                KafkaMetricsCollections.TOPIC_METRICS_TO_DB,
                true
        );
        if (ValidateUtils.isNull(topicMetrics)) {
            topicMetrics = new TopicMetrics(physicalClusterId, topicName);
        }
        subMetricsMap.put(topicName, topicMetrics);
        metricsMap.put(physicalClusterId, subMetricsMap);
        return topicMetrics;
    }

    @Override
    public List<TopicDTO> getTopics(String username) {
        List<ClusterDO> clusterDOList = clusterService.list();
        if (ValidateUtils.isEmptyList(clusterDOList)) {
            return new ArrayList<>();
        }

        List<AppDO> appList = appService.listAll();
        if (ValidateUtils.isNull(appList)) {
            appList = new ArrayList<>();
        }
        Map<String, AppDO> appMap = new HashMap<>(appList.size());
        for (AppDO appDO : appList) {
            appMap.put(appDO.getAppId(), appDO);
        }

        List<TopicDO> topicList = this.listAll();
        if (ValidateUtils.isNull(topicList)) {
            return new ArrayList<>();
        }
        Map<Long, Map<String, TopicDO>> topicMap = new HashMap<>(appList.size());
        for (TopicDO topicDO: topicList) {
            Map<String, TopicDO> subTopicMap = topicMap.getOrDefault(topicDO.getClusterId(), new HashMap<>());
            subTopicMap.put(topicDO.getTopicName(), topicDO);
            topicMap.put(topicDO.getClusterId(), subTopicMap);
        }

        List<TopicDTO> dtoList = new ArrayList<>();
        for (ClusterDO clusterDO: clusterDOList) {
            dtoList.addAll(getTopics(clusterDO, appMap, topicMap.getOrDefault(clusterDO.getId(), new HashMap<>())));
        }
        return dtoList;
    }


    private List<TopicDTO> getTopics(ClusterDO clusterDO,
                                     Map<String, AppDO> appMap,
                                     Map<String, TopicDO> topicMap) {
        Boolean needAuth = !ValidateUtils.isBlank(clusterDO.getSecurityProperties());
        List<TopicDTO> dtoList = new ArrayList<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getTopicLogicalCluster(
                    clusterDO.getId(),
                    topicName
            );
            if (ValidateUtils.isNull(logicalClusterDO)) {
                continue;
            }

            TopicDTO dto = new TopicDTO();
            dtoList.add(dto);

            dto.setLogicalClusterId(logicalClusterDO.getId());
            dto.setLogicalClusterName(logicalClusterDO.getName());
            dto.setTopicName(topicName);
            dto.setNeedAuth(needAuth);

            TopicDO topicDO = topicMap.get(topicName);
            if (ValidateUtils.isNull(topicDO)) {
                continue;
            }
            dto.setDescription(topicDO.getDescription());
            dto.setAppId(topicDO.getAppId());

            AppDO appDO = appMap.get(topicDO.getAppId());
            if (ValidateUtils.isNull(appDO)) {
                continue;
            }
            dto.setAppName(appDO.getName());
            dto.setAppPrincipals(appDO.getPrincipals());
        }
        return dtoList;
    }

    @Override
    public ResultStatus modifyTopic(Long clusterId, String topicName, String description, String operator) {
        try {
            if (!PhysicalClusterMetadataManager.isTopicExist(clusterId, topicName)) {
                return ResultStatus.TOPIC_NOT_EXIST;
            }
            TopicDO topicDO = topicDao.getByTopicName(clusterId, topicName);
            if (ValidateUtils.isNull(topicDO)) {
                return ResultStatus.TOPIC_NOT_EXIST;
            }
            topicDO.setDescription(description);
            if (topicDao.updateByName(topicDO) > 0) {
                return ResultStatus.SUCCESS;
            }
            return ResultStatus.MYSQL_ERROR;
        } catch (Exception e) {
            LOGGER.error("modify topic failed, clusterId:{} topicName:{} description:{} operator:{} ",
                    clusterId, topicName, description, operator, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public int deleteByTopicName(Long clusterId, String topicName) {
        try {
            return topicDao.deleteByName(clusterId, topicName);
        } catch (Exception e) {
            LOGGER.error("delete topic failed, clusterId:{} topicName:{}", clusterId, topicName, e);
        }
        return 0;
    }

    @Override
    public int addTopic(TopicDO topicDO) {
        try {
            return topicDao.insert(topicDO);
        } catch (Exception e) {
            LOGGER.error("insert topic failed, TopicDO:{}", topicDO.toString(), e);
        }
        return 0;
    }

    @Override
    public List<TopicAppData> getTopicAuthorizedApps(Long physicalClusterId, String topicName) {
        TopicMetadata topicMetaData = PhysicalClusterMetadataManager.getTopicMetadata(physicalClusterId, topicName);
        if (ValidateUtils.isNull(topicMetaData)) {
            // Topic不存在
            return new ArrayList<>();
        }

        List<AuthorityDO> authorityDOList = authorityService.getAuthorityByTopic(physicalClusterId, topicName);
        if (ValidateUtils.isEmptyList(authorityDOList)) {
            // 无任何权限
            return new ArrayList<>();
        }

        List<TopicThrottledMetrics> throttleList = throttleService.getThrottledTopicsFromJmx(
                physicalClusterId,
                topicMetaData.getBrokerIdSet(),
                Arrays.asList(KafkaClientEnum.values())
        );
        if (ValidateUtils.isNull(throttleList)) {
            throttleList = new ArrayList<>();
        }

        // idx-0: produce, idx-1: fetch
        List<Set<String>> throttledSetList = Arrays.asList(new HashSet<>(), new HashSet<>());
        for (TopicThrottledMetrics metrics : throttleList) {
            if (!topicName.equals(metrics.getTopicName())) {
                continue;
            }
            throttledSetList.get(
                    KafkaClientEnum.PRODUCE_CLIENT.equals(metrics.getClientType())? 0: 1)
                    .add(metrics.getAppId());
        }

        List<TopicAppData> dtoList = new ArrayList<>();
        for (AuthorityDO authority : authorityDOList) {
            TopicAppData dto = new TopicAppData();
            dto.setClusterId(physicalClusterId);
            dto.setTopicName(topicName);
            dto.setAppId(authority.getAppId());
            dto.setAccess(authority.getAccess());
            dto.setProduceThrottled(throttledSetList.get(0).contains(authority.getAppId()));
            dto.setFetchThrottled(throttledSetList.get(1).contains(authority.getAppId()));

            AppDO appDO = appService.getByAppId(authority.getAppId());
            if (!ValidateUtils.isNull(appDO)) {
                dto.setAppName(appDO.getName());
                dto.setAppPrincipals(appDO.getPrincipals());
            }

            TopicQuotaData quota = KafkaZookeeperUtils.getTopicQuota(
                    PhysicalClusterMetadataManager.getZKConfig(physicalClusterId),
                    authority.getAppId(),
                    topicName
            );
            if (!ValidateUtils.isNull(quota)) {
                dto.setConsumerQuota(NumberUtils.string2Long(quota.getConsumer_byte_rate()));
                dto.setProduceQuota(NumberUtils.string2Long(quota.getProducer_byte_rate()));
            }
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<TopicAppData> getTopicMineApps(Long physicalClusterId, String topicName, String username) {
        TopicMetadata topicMetaData = PhysicalClusterMetadataManager.getTopicMetadata(physicalClusterId, topicName);
        if (ValidateUtils.isNull(topicMetaData)) {
            return new ArrayList<>();
        }

        List<AppDO> appDOList = appService.getByPrincipal(username);
        if (ValidateUtils.isEmptyList(appDOList)) {
            return new ArrayList<>();
        }

        List<AuthorityDO> authorityDOList = authorityService.getAuthorityByTopic(physicalClusterId, topicName);
        if (ValidateUtils.isNull(authorityDOList)) {
            authorityDOList = new ArrayList<>();
        }
        Map<String, Integer> accessMap = new HashMap<>();
        for (AuthorityDO authorityDO: authorityDOList) {
            accessMap.put(authorityDO.getAppId(), authorityDO.getAccess());
        }

        List<TopicAppData> dataList = new ArrayList<>();
        for (AppDO appDO : appDOList) {
            TopicAppData dto = new TopicAppData();
            dto.setClusterId(physicalClusterId);
            dto.setTopicName(topicName);
            dto.setAppId(appDO.getAppId());
            dto.setAppName(appDO.getName());
            dto.setAppPrincipals(appDO.getPrincipals());
            dto.setAccess(accessMap.getOrDefault(appDO.getAppId(), TopicAuthorityEnum.DENY.getCode()));
            TopicQuotaData quota = KafkaZookeeperUtils.getTopicQuota(
                    PhysicalClusterMetadataManager.getZKConfig(physicalClusterId),
                    appDO.getAppId(),
                    topicName
            );
            if (!ValidateUtils.isNull(quota)) {
                dto.setConsumerQuota(NumberUtils.string2Long(quota.getConsumer_byte_rate()));
                dto.setProduceQuota(NumberUtils.string2Long(quota.getProducer_byte_rate()));
            }
            dataList.add(dto);
        }
        return dataList;
    }

    @Override
    public Result<RdTopicBasic> getRdTopicBasic(Long physicalClusterId, String topicName) {
        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, topicName)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }

        Properties properties = KafkaZookeeperUtils.getTopicProperties(
                PhysicalClusterMetadataManager.getZKConfig(physicalClusterId),
                topicName
        );
        TopicDO topicDO = getByTopicName(physicalClusterId, topicName);
        if (ValidateUtils.isNull(topicDO)) {
            return new Result<>(convert2RdTopicBasic(clusterDO, topicName, null, null, properties));
        }
        AppDO appDO = appService.getByAppId(topicDO.getAppId());
        return new Result<>(convert2RdTopicBasic(clusterDO, topicName, topicDO, appDO, properties));
    }

    @Override
    public List<TopicStatisticsDO> getTopicStatistic(Long clusterId, String topicName, Date startTime, Date endTime) {
        return topicStatisticsDao.getTopicStatistic(clusterId, topicName, startTime, endTime);
    }

    @Override
    public TopicBusinessInfo getTopicBusinessInfo(Long clusterId, String topicName) {
        TopicDO topicDO = getByTopicName(clusterId, topicName);
        if (ValidateUtils.isNull(topicDO)) {
            return null;
        }
        TopicBusinessInfo topicBusinessInfo = new TopicBusinessInfo();
        topicBusinessInfo.setClusterId(clusterId);
        topicBusinessInfo.setTopicName(topicName);

        AppDO appDO = appService.getByAppId(topicDO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return topicBusinessInfo;
        }

        topicBusinessInfo.setAppId(appDO.getAppId());
        topicBusinessInfo.setAppName(appDO.getName());
        topicBusinessInfo.setPrincipals(appDO.getPrincipals());
        return topicBusinessInfo;
    }

    private RdTopicBasic convert2RdTopicBasic(ClusterDO clusterDO,
                                              String topicName,
                                              TopicDO topicDO,
                                              AppDO appDO,
                                              Properties properties) {
        RdTopicBasic rdTopicBasic = new RdTopicBasic();
        rdTopicBasic.setClusterId(clusterDO.getId());
        rdTopicBasic.setClusterName(clusterDO.getClusterName());
        rdTopicBasic.setTopicName(topicName);
        if (!ValidateUtils.isNull(appDO)) {
            rdTopicBasic.setAppId(appDO.getAppId());
            rdTopicBasic.setAppName(appDO.getName());
        }
        if (!ValidateUtils.isNull(topicDO)) {
            rdTopicBasic.setDescription(topicDO.getDescription());
        }
        rdTopicBasic.setProperties(properties);
        rdTopicBasic.setRetentionTime(KafkaZookeeperUtils.getTopicRetentionTime(properties));
        return rdTopicBasic;
    }
}
