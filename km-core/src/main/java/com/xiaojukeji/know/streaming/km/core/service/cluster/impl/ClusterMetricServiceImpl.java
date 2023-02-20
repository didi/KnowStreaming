package com.xiaojukeji.know.streaming.km.core.service.cluster.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsClusterPhyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.offset.KSOffsetSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.ClusterMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.*;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ClusterMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.*;
import com.xiaojukeji.know.streaming.km.persistence.cache.DataBaseDataLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.acl.KafkaAclService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.job.JobService;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseMetricService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.ClusterMetricESDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.resource.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics.initWithMetrics;
import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems.*;

/**
 * @author didi
 */
@Service
public class ClusterMetricServiceImpl extends BaseMetricService implements ClusterMetricService {
    private static final ILog LOGGER  = LogFactory.getLog(ClusterMetricServiceImpl.class);

    public static final String CLUSTER_METHOD_DO_NOTHING                                    = "doNothing";

    public static final String CLUSTER_METHOD_GET_TOPIC_SIZE                                = "getTopicSize";
    public static final String CLUSTER_METHOD_GET_MESSAGE_SIZE                              = "getMessageSize";
    public static final String CLUSTER_METHOD_GET_TOTAL_LOG_SIZE                            = "getTotalLogSize";
    public static final String CLUSTER_METHOD_GET_PARTITION_SIZE                            = "getPartitionSize";
    public static final String CLUSTER_METHOD_GET_PARTITION_NO_LEADER_SIZE                  = "getPartitionNoLeaderSize";
    public static final String CLUSTER_METHOD_GET_HEALTH_METRICS                            = "getClusterHealthMetrics";
    public static final String CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX    = "getMetricFromKafkaByTotalBrokersJMX";
    public static final String CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_CONTROLLER_JMX       = "getMetricFromKafkaByControllerJMX";
    public static final String CLUSTER_METHOD_GET_ZK_COUNT                                  = "getZKCount";
    public static final String CLUSTER_METHOD_GET_ZK_AVAILABLE                              = "getZKAvailable";
    public static final String CLUSTER_METHOD_GET_BROKERS_COUNT                             = "getBrokersCount";
    public static final String CLUSTER_METHOD_GET_BROKERS_ALIVE_COUNT                       = "getBrokersAliveCount";
    public static final String CLUSTER_METHOD_GET_BROKERS_NOT_ALIVE_COUNT                   = "getBrokersNotAliveCount";
    public static final String CLUSTER_METHOD_GET_REPLICAS_COUNT                            = "getReplicasCount";
    public static final String CLUSTER_METHOD_GET_GROUP_COUNT                               = "getGroupsCount";
    public static final String CLUSTER_METHOD_GET_GROUP_ACTIVE_COUNT                        = "getGroupActivesCount";
    public static final String CLUSTER_METHOD_GET_GROUP_EMPTY_COUNT                         = "getGroupEmptyCount";
    public static final String CLUSTER_METHOD_GET_GROUP_REBALANCED_COUNT                    = "getGroupRebalancedCount";
    public static final String CLUSTER_METHOD_GET_GROUP_DEAD_COUNT                          = "getGroupDeadCount";
    public static final String CLUSTER_METHOD_GET_ALIVE                                     = "isClusterAlive";

    public static final String CLUSTER_METHOD_GET_ACL_ENABLE                                = "getAclEnable";
    public static final String CLUSTER_METHOD_GET_ACLS                                      = "getAcls";
    public static final String CLUSTER_METHOD_GET_ACL_USERS                                 = "getAclUsers";
    public static final String CLUSTER_METHOD_GET_ACL_TOPICS                                = "getAclTopics";
    public static final String CLUSTER_METHOD_GET_ACL_GROUPS                                = "getAclGroups";

    public static final String CLUSTER_METHOD_GET_JOBS                                       = "getJobs";
    public static final String CLUSTER_METHOD_GET_JOBS_RUNNING                               = "getJobsRunning";
    public static final String CLUSTER_METHOD_GET_JOBS_WAITING                               = "getJobsWaiting";
    public static final String CLUSTER_METHOD_GET_JOBS_SUCCESS                               = "getJobsSuccess";
    public static final String CLUSTER_METHOD_GET_JOBS_FAILED                                = "getJobsFailed";

    @Autowired
    private HealthStateService healthStateService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerMetricService brokerMetricService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Autowired
    private ClusterMetricESDAO clusterMetricESDAO;

    @Autowired
    private KafkaControllerService kafkaControllerService;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private KafkaAclService kafkaAclService;

    @Autowired
    private JobService jobService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_CLUSTER;
    }

    @Override
    protected List<String> listMetricPOFields(){
        return BeanUtil.listBeanFields(ClusterMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler(){
        registerVCHandler( CLUSTER_METHOD_DO_NOTHING,                                   this::doNothing);
        registerVCHandler( CLUSTER_METHOD_GET_TOPIC_SIZE,                               this::getTopicSize);
        registerVCHandler( CLUSTER_METHOD_GET_MESSAGE_SIZE,                             this::getMessageSize);
        registerVCHandler( CLUSTER_METHOD_GET_TOTAL_LOG_SIZE,                           this::getTotalLogSize);

        registerVCHandler( CLUSTER_METHOD_GET_PARTITION_SIZE,                           this::getPartitionSize);
        registerVCHandler( CLUSTER_METHOD_GET_PARTITION_NO_LEADER_SIZE,                 this::getPartitionNoLeaderSize);

        registerVCHandler( CLUSTER_METHOD_GET_HEALTH_METRICS,                           this::getClusterHealthMetrics);

        registerVCHandler( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX,   this::getMetricFromKafkaByTotalBrokersJMX);
        registerVCHandler( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_CONTROLLER_JMX,      this::getMetricFromKafkaByControllerJMX);
        registerVCHandler( CLUSTER_METHOD_GET_ZK_COUNT,                                 this::getZKCount);
        registerVCHandler( CLUSTER_METHOD_GET_ZK_AVAILABLE,                             this::getZKAvailable);
        registerVCHandler( CLUSTER_METHOD_GET_BROKERS_COUNT,                            this::getBrokersCount);
        registerVCHandler( CLUSTER_METHOD_GET_BROKERS_ALIVE_COUNT,                      this::getBrokersAliveCount);
        registerVCHandler( CLUSTER_METHOD_GET_BROKERS_NOT_ALIVE_COUNT,                  this::getBrokersNotAliveCount);
        registerVCHandler( CLUSTER_METHOD_GET_REPLICAS_COUNT,                           this::getReplicasCount);

        registerVCHandler( CLUSTER_METHOD_GET_GROUP_COUNT,                              this::getGroupsCount);
        registerVCHandler( CLUSTER_METHOD_GET_GROUP_ACTIVE_COUNT,                       this::getGroupActivesCount);
        registerVCHandler( CLUSTER_METHOD_GET_GROUP_EMPTY_COUNT,                        this::getGroupEmptyCount);
        registerVCHandler( CLUSTER_METHOD_GET_GROUP_REBALANCED_COUNT,                   this::getGroupRebalancedCount);
        registerVCHandler( CLUSTER_METHOD_GET_GROUP_DEAD_COUNT,                         this::getGroupDeadCount);

        registerVCHandler( CLUSTER_METHOD_GET_ALIVE,                                    this::isClusterAlive);

        registerVCHandler( CLUSTER_METHOD_GET_ACL_ENABLE,                               this::getAclEnable);
        registerVCHandler( CLUSTER_METHOD_GET_ACLS,                                     this::getAcls);
        registerVCHandler( CLUSTER_METHOD_GET_ACL_USERS,                                this::getAclUsers);
        registerVCHandler( CLUSTER_METHOD_GET_ACL_TOPICS,                               this::getAclTopics);
        registerVCHandler( CLUSTER_METHOD_GET_ACL_GROUPS,                               this::getAclGroups);

        registerVCHandler( CLUSTER_METHOD_GET_JOBS,                                     this::getJobs);
        registerVCHandler( CLUSTER_METHOD_GET_JOBS_RUNNING,                             this::getJobsRunning);
        registerVCHandler( CLUSTER_METHOD_GET_JOBS_WAITING,                             this::getJobsWaiting);
        registerVCHandler( CLUSTER_METHOD_GET_JOBS_SUCCESS,                             this::getJobsSuccess);
        registerVCHandler( CLUSTER_METHOD_GET_JOBS_FAILED,                              this::getJobsFailed);
    }

    @Override
    public Result<ClusterMetrics> collectClusterMetricsFromKafka(Long clusterId, String metric){
        try {
            ClusterMetricParam clusterMetricParam = new ClusterMetricParam(clusterId, metric );
            return (Result<ClusterMetrics>)doVCHandler(clusterId, metric, clusterMetricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<ClusterMetrics> collectClusterMetricsFromKafka(Long clusterId, List<String> metrics){
        ClusterMetrics clusterMetrics = new ClusterMetrics();
        clusterMetrics.setClusterPhyId(clusterId);

        for(String metric :metrics){
            if(null != clusterMetrics.getMetrics().get(metric)){continue;}

            Result<ClusterMetrics> ret = collectClusterMetricsFromKafka(clusterId, metric);
            if(null != ret && ret.successful()){
                clusterMetrics.putMetric(ret.getData().getMetrics());
            }
        }

        return Result.buildSuc(clusterMetrics);
    }

    @Override
    public Result<List<MetricMultiLinesVO>> listClusterMetricsFromES(MetricsClusterPhyDTO dto) {
        Long         startTime       = dto.getStartTime();
        Long         endTime         = dto.getEndTime();
        Integer      topN            = dto.getTopNu();
        String       aggType         = dto.getAggType();
        List<Long>   clusterPhyIds   = dto.getClusterPhyIds();
        List<String> metrics         = dto.getMetricsNames();

        try {
            Table<String/*metric*/, Long/*clusterId*/, List<MetricPointVO>> retTable;
            if(CollectionUtils.isEmpty(clusterPhyIds)) {
                return Result.buildFailure( "clusterPhyIds is empty!" );
            }else {
                retTable = clusterMetricESDAO.listClusterMetricsByClusterIds(metrics, aggType, clusterPhyIds, startTime, endTime);
            }

            List<MetricMultiLinesVO> multiLinesVOS = metricMap2VO(clusterPhyIds.get(0), retTable.rowMap());
            return Result.buildSuc(multiLinesVOS);
        }catch (Exception e){
            LOGGER.error("method=listClusterMetricsFromES||clusters={}||metrics={}||topN={}||aggType={}",
                    clusterPhyIds, metrics, topN, aggType);

            return Result.buildFailure(e.getMessage());
        }
    }

    @Override
    public Result<ClusterMetrics> getLatestMetricsFromES(Long clusterPhyId, List<String> metricNames) {
        ClusterMetricPO clusterMetricPO = clusterMetricESDAO.getClusterLatestMetrics(clusterPhyId, metricNames);
        return Result.buildSuc(ConvertUtil.obj2Obj(clusterMetricPO, ClusterMetrics.class));
    }

    @Override
    public ClusterMetrics getLatestMetricsFromCache(Long clusterPhyId) {
        ClusterMetrics metrics = DataBaseDataLocalCache.getClusterLatestMetrics(clusterPhyId);
        if (metrics != null) {
            return metrics;
        }

        return new ClusterMetrics(clusterPhyId);
    }

    @Override
    public Result<List<MetricPointVO>> getMetricPointsFromES(Long clusterId, MetricDTO dto) {
        Map<String/*metric*/, MetricPointVO> metricPointMap = clusterMetricESDAO.getClusterMetricsPoint(clusterId,
                dto.getMetricsNames(), dto.getAggType(), dto.getStartTime(), dto.getEndTime());

        List<MetricPointVO> metricPoints = new ArrayList<>(metricPointMap.values());
        return Result.buildSuc(metricPoints);
    }

    @Override
    public PaginationResult<ClusterMetrics> pagingClusterWithLatestMetricsFromES(List<Long> clusterIds,
                                                                                 List<SearchTerm> terms,
                                                                                 List<SearchShould> shoulds,
                                                                                 SearchSort sort,
                                                                                 SearchRange range,
                                                                                 SearchPage page){
        setQueryMetricFlag(sort);
        setQueryMetricFlag(range);
        setQueryMetricFlag(terms);
        setQueryMetricFlag(shoulds);

        List<ClusterMetricPO> clusterMetricPOS = clusterMetricESDAO.pagingClusterWithLatestMetrics(terms, shoulds, sort, range);

        List<ClusterMetricPO> filterClusterMetricPOs = clusterMetricPOS.stream()
                                                                       .filter(c -> clusterIds.contains(c.getClusterPhyId()))
                                                                       .collect(Collectors.toList());

        if(null == page){
            return PaginationResult.buildSuc(ConvertUtil.list2List(filterClusterMetricPOs, ClusterMetrics.class), clusterIds.size(),
                    1, 10);
        }

        int startIdx = Math.min((page.getPageNo() - 1) * page.getPageSize(), filterClusterMetricPOs.size());
        int endIdx = Math.min(startIdx +page.getPageSize(), filterClusterMetricPOs.size());

        List<ClusterMetricPO> subClusterMetricPOSs = filterClusterMetricPOs.subList(startIdx, endIdx);

        return PaginationResult.buildSuc(ConvertUtil.list2List(subClusterMetricPOSs, ClusterMetrics.class), clusterIds.size(),
                page.getPageNo(), page.getPageSize());
    }


    /**************************************************** private method ****************************************************/

    /**
     * doNothing
     */
    private Result<ClusterMetrics> doNothing(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;
        return Result.buildSuc(initWithMetrics(param.getClusterId(), param.getMetric(), -1.0f));
    }


    private Result<ClusterMetrics> getClusterHealthMetrics(VersionItemParam metricParam){
        ClusterMetricParam param = (ClusterMetricParam)metricParam;
        ClusterMetrics clusterMetrics = healthStateService.calClusterHealthMetrics(param.getClusterId());
        return Result.buildSuc(clusterMetrics);
    }


    /**
     * 获取集群的 totalLogSize
     * @param metricParam
     * @return
     */
    private Result<ClusterMetrics> getTotalLogSize(VersionItemParam metricParam){
        ClusterMetricParam param = (ClusterMetricParam)metricParam;
        return getMetricFromKafkaByTotalTopics(param.getClusterId(), param.getMetric(), TOPIC_METRIC_LOG_SIZE);
    }

    /**
     * 获取集群的 messageSize
     */
    private Result<ClusterMetrics> getMessageSize(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        Result<Map<TopicPartition, Long>> beginOffsetMapResult = partitionService.getAllPartitionOffsetFromKafka(param.getClusterId(), KSOffsetSpec.earliest());

        Result<Map<TopicPartition, Long>> endOffsetMapResult = partitionService.getAllPartitionOffsetFromKafka(param.getClusterId(), KSOffsetSpec.latest());
        if (endOffsetMapResult.failed() || beginOffsetMapResult.failed()) {
            // 有一个失败，直接返回失败
            return Result.buildFromIgnoreData(endOffsetMapResult);
        }

        long msgCount = 0;
        for (Map.Entry<TopicPartition, Long> entry: endOffsetMapResult.getData().entrySet()) {
            Long beginOffset = beginOffsetMapResult.getData().get(entry.getKey());
            if (beginOffset == null) {
                continue;
            }

            msgCount += Math.max(0, entry.getValue() - beginOffset);
        }

        return Result.buildSuc(initWithMetrics(param.getClusterId(), param.getMetric(), (float)msgCount));
    }

    /**
     * 获取集群的Topic个数
     */
    private Result<ClusterMetrics> getTopicSize(VersionItemParam metricParam){
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();
        Integer topicNu     = topicService.getTopicSizeFromCacheFirst(clusterId);

        return Result.buildSuc(initWithMetrics(clusterId, metric, topicNu.floatValue()));
    }

    /**
     * 获取集群的Partition个数
     */
    private Result<ClusterMetrics> getPartitionSize(VersionItemParam metricParam){
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     partitionNu  = partitionService.listPartitionFromCacheFirst(clusterId).size();

        return Result.buildSuc(initWithMetrics(clusterId, metric, partitionNu.floatValue()));
    }

    /**
     * 获取集群无Leader的partition个数
     */
    private Result<ClusterMetrics> getPartitionNoLeaderSize(VersionItemParam metricParam){
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();
        Integer noLeaders   = (int) partitionService.listPartitionFromCacheFirst(clusterId)
                .stream()
                .filter(partition -> partition.getLeaderBrokerId().equals(KafkaConstant.NO_LEADER))
                .count();
        return Result.buildSuc(initWithMetrics(clusterId, metric, noLeaders.floatValue()));
    }

    private Result<ClusterMetrics> getZKCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();

        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterId);
        if(null == clusterPhy || StringUtils.isEmpty(clusterPhy.getZookeeper())) {
            return Result.buildSuc(initWithMetrics(clusterId, metric, 0f));
        }

        String   zookeepers = clusterPhy.getZookeeper();
        String[] zookeeper  = zookeepers.split(",");

        return Result.buildSuc(initWithMetrics(clusterId, metric, zookeeper.length));
    }

    private Result<ClusterMetrics> getZKAvailable(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();

        try {
            ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterId);
            if (clusterPhy == null) {
                // 集群未加载，或者不存在，当前信息未知
                return Result.buildFromRSAndMsg(NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterId));
            }

            if (ValidateUtils.isBlank(clusterPhy.getZookeeper())) {
                return Result.buildSuc(initWithMetrics(clusterId, metric, Constant.INVALID_CODE));
            }

            kafkaAdminZKClient.getClient(clusterId);

            return Result.buildSuc(initWithMetrics(clusterId, metric, Constant.YES));
        } catch (NotExistException nee) {
            return Result.buildSuc(initWithMetrics(clusterId, metric, Constant.NO));
        }
    }

    private Result<ClusterMetrics> getBrokersCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();

        List<Broker> brokers = brokerService.listAllBrokersFromDB(clusterId);
        int brokerNu = (CollectionUtils.isEmpty(brokers)) ? 0 : brokers.size();

        return Result.buildSuc(initWithMetrics(clusterId, metric, brokerNu));
    }

    private Result<ClusterMetrics> getBrokersAliveCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();

        List<Broker> brokers = brokerService.listAliveBrokersFromDB(clusterId);
        int brokerNu = (CollectionUtils.isEmpty(brokers)) ? 0 : brokers.size();

        return Result.buildSuc(initWithMetrics(clusterId, metric, brokerNu));
    }

    private Result<ClusterMetrics> getBrokersNotAliveCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();

        List<Broker> brokers = brokerService.listNotAliveBrokersFromDB(clusterId);
        int brokerNu = (CollectionUtils.isEmpty(brokers)) ? 0 : brokers.size();

        return Result.buildSuc(initWithMetrics(clusterId, metric, brokerNu));
    }

    private Result<ClusterMetrics> getReplicasCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        Integer replicasCount = 0;
        List<Topic> topicList = topicService.listTopicsFromCacheFirst(param.getClusterId());
        for (Topic topic: topicList) {
            replicasCount += (topic.getPartitionNum() * topic.getReplicaNum());
        }

        return Result.buildSuc(initWithMetrics(param.getClusterId(), param.getMetric(), replicasCount.floatValue()));
    }

    private Result<ClusterMetrics> getGroupsCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = groupService.calGroupCount(clusterId);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getGroupActivesCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = groupService.calGroupStatCount(clusterId, GroupStateEnum.ACTIVE);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getGroupEmptyCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = groupService.calGroupStatCount(clusterId, GroupStateEnum.EMPTY);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getGroupRebalancedCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = groupService.calGroupStatCount(clusterId, GroupStateEnum.PREPARING_RE_BALANCE);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getGroupDeadCount(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = groupService.calGroupStatCount(clusterId, GroupStateEnum.DEAD);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> isClusterAlive(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric          = param.getMetric();
        Long        clusterId       = param.getClusterId();
        KafkaController controller  = kafkaControllerService.getKafkaControllerFromDB(clusterId);
        return Result.buildSuc(initWithMetrics(clusterId, metric, (null != controller) ? 1 : 0));
    }

    private Result<ClusterMetrics> getAclEnable(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        ClusterPhy  clusterPhy   = LoadedClusterPhyCache.getByPhyId(clusterId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterId));
        }

        return Result.buildSuc(initWithMetrics(clusterId, metric, ClusterAuthTypeEnum.enableAuth(clusterPhy.getAuthType())? Constant.YES: Constant.NO));
    }

    private Result<ClusterMetrics> getAcls(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = kafkaAclService.countKafkaAclFromDB(clusterId);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getAclUsers(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = kafkaAclService.countKafkaUserAndDistinctFromDB(clusterId);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getAclTopics(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = kafkaAclService.countResTypeAndDistinctFromDB(clusterId, ResourceType.TOPIC);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getAclGroups(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = kafkaAclService.countResTypeAndDistinctFromDB(clusterId, ResourceType.GROUP);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getJobs(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = jobService.countJobsByCluster(clusterId);

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getJobsRunning(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = jobService.countJobsByClusterAndJobStatus(clusterId, JobStatusEnum.RUNNING.getStatus());

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getJobsWaiting(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = jobService.countJobsByClusterAndJobStatus(clusterId, JobStatusEnum.WAITING.getStatus());

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getJobsSuccess(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = jobService.countJobsByClusterAndJobStatus(clusterId, JobStatusEnum.SUCCESS.getStatus());

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    private Result<ClusterMetrics> getJobsFailed(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String      metric       = param.getMetric();
        Long        clusterId    = param.getClusterId();
        Integer     count        = jobService.countJobsByClusterAndJobStatus(clusterId, JobStatusEnum.FAILED.getStatus());

        return Result.buildSuc(initWithMetrics(clusterId, metric, count));
    }

    /**
     * 从某一个 controller 的 JMX 中获取指标再聚合得到集群的指标
     * @param metricParam
     * @return
     */
    private Result<ClusterMetrics> getMetricFromKafkaByControllerJMX(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = getJMXInfo(clusterId, metric);
        if(null == jmxInfo){return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);}

        //2、获取集群 controller 信息
        KafkaController controller = kafkaControllerService.getKafkaControllerFromDB(clusterId);
        if(null == controller){return Result.buildFailure(CONTROLLER_NOT_EXIST);}

        //3、获取jmx连接
        JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterId, controller.getBrokerId());
        if (ValidateUtils.isNull(jmxConnectorWrap)){
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }

        //3、获取jmx指标
        try {
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName()),
                    jmxInfo.getJmxAttribute()).toString();

            return Result.buildSuc(initWithMetrics(clusterId, metric, Float.valueOf(value)));
        } catch (InstanceNotFoundException e) {
            return Result.buildFailure(VC_JMX_INSTANCE_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("method=getMetricFromKafkaByControllerJMX||cluster={}||brokerId={}||metrics={}||jmx={}||msg={}",
                    clusterId, controller.getBrokerId(), metric, jmxInfo.getJmxObjectName(), e.getClass().getName());
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }

    /**
     * 从所有broker的 JMX 中获取指标再聚合得到集群的指标
     */
    private Result<ClusterMetrics> getMetricFromKafkaByTotalBrokersJMX(VersionItemParam metricParam) {
        ClusterMetricParam param = (ClusterMetricParam)metricParam;

        String metric       = param.getMetric();
        Long   clusterId    = param.getClusterId();

        //1、获取jmx的属性信息
        List<Broker> brokers = brokerService.listAliveBrokersFromDB(clusterId);

        float metricVale = 0f;
        for(Broker broker : brokers) {
            Result<BrokerMetrics> ret = brokerMetricService.collectBrokerMetricsFromKafkaWithCacheFirst(clusterId, broker.getBrokerId(), metric);

            if(null == ret || ret.failed() || null == ret.getData()){continue;}

            BrokerMetrics brokerMetrics = ret.getData();
            if(null != brokerMetrics && null != brokerMetrics.getMetrics().get(metric)){
                metricVale += Double.valueOf(brokerMetrics.getMetrics().get(metric));
            }
        }

        return Result.buildSuc(initWithMetrics(clusterId, metric, metricVale));
    }

    /**
     * 从所有的 Topic 的指标中加总聚合得到集群的指标
     */
    private Result<ClusterMetrics> getMetricFromKafkaByTotalTopics(Long clusterId, String metric, String topicMetric) {
        List<Topic> topics = topicService.listTopicsFromCacheFirst(clusterId);

        float sumMetricValue = 0f;
        for(Topic topic : topics) {
            Result<List<TopicMetrics>> ret = topicMetricService.collectTopicMetricsFromKafkaWithCacheFirst(
                    clusterId,
                    topic.getTopicName(),
                    topicMetric
            );

            if(null == ret || ret.failed() || CollectionUtils.isEmpty(ret.getData())) {
                continue;
            }

            for (TopicMetrics metrics : ret.getData()) {
                if(metrics.isBBrokerAgg()) {
                    Float metricValue = metrics.getMetric(topicMetric);
                    sumMetricValue += (metricValue == null? 0f: metricValue);
                    break;
                }
            }
        }

        return Result.buildSuc(initWithMetrics(clusterId, metric, sumMetricValue));
    }
}
