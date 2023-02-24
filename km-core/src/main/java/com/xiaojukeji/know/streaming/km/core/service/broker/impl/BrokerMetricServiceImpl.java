package com.xiaojukeji.know.streaming.km.core.service.broker.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsBrokerDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.BrokerMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BrokerMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.cache.CollectedMetricsLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.replica.ReplicaMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.BrokerMetricVersionItems;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ReplicaMetricVersionItems;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.BrokerMetricESDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.apache.kafka.clients.admin.LogDirDescription;
import org.apache.kafka.clients.admin.ReplicaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;

/**
 * @author didi
 */
@Service
public class BrokerMetricServiceImpl extends BaseMetricService implements BrokerMetricService {
    protected static final ILog  LOGGER = LogFactory.getLog(BrokerMetricServiceImpl.class);

    public static final String BROKER_METHOD_DO_NOTHING                             = "doNothing";
    public static final String BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX           = "getMetricFromKafkaByJMX";
    public static final String BROKER_METHOD_GET_CONNECTION_FROM_KAFKA_BY_JMX       = "getConnectCountFromKafkaByJMX";
    public static final String BROKER_METHOD_GET_HEALTH_SCORE                       = "getMetricHealthScore";
    public static final String BROKER_METHOD_GET_PARTITIONS_SKEW                    = "getPartitionsSkew";
    public static final String BROKER_METHOD_GET_LEADERS_SKEW                       = "getLeadersSkew";
    public static final String BROKER_METHOD_GET_LOG_SIZE_FROM_CLIENT               = "getLogSizeFromClient";
    public static final String BROKER_METHOD_GET_LOG_SIZE_FROM_JMX                  = "getLogSizeFromJmx";
    public static final String BROKER_METHOD_IS_BROKER_ALIVE                        = "isBrokerAlive";

    @Autowired
    private TopicService topicService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private ReplicaMetricService replicaMetricService;

    @Autowired
    private HealthStateService healthStateService;

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Autowired
    private BrokerMetricESDAO brokerMetricESDAO;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_BROKER;
    }

    @Override
    protected List<String> listMetricPOFields(){
        return BeanUtil.listBeanFields(BrokerMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler(){
        registerVCHandler( BROKER_METHOD_DO_NOTHING,                              this::doNothing);
        registerVCHandler( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX,            this::getMetricFromKafkaByJMX);
        registerVCHandler( BROKER_METHOD_GET_CONNECTION_FROM_KAFKA_BY_JMX,        this::getConnectCountFromKafkaByJMX);
        registerVCHandler( BROKER_METHOD_GET_HEALTH_SCORE,                        this::getMetricHealthScore);
        registerVCHandler( BROKER_METHOD_GET_PARTITIONS_SKEW,                     this::getPartitionsSkew);
        registerVCHandler( BROKER_METHOD_GET_LEADERS_SKEW,                        this::getLeadersSkew);

        registerVCHandler( BROKER_METHOD_GET_LOG_SIZE_FROM_JMX,                   this::getLogSizeFromJmx);
        registerVCHandler( BROKER_METHOD_GET_LOG_SIZE_FROM_CLIENT,                this::getLogSizeFromClient);

        registerVCHandler( BROKER_METHOD_IS_BROKER_ALIVE,                         this::isBrokerAlive);
    }

    @Override
    public Result<BrokerMetrics> collectBrokerMetricsFromKafkaWithCacheFirst(Long clusterId, Integer brokerId, String metric) {
        String brokerMetricKey = CollectedMetricsLocalCache.genBrokerMetricKey(clusterId, brokerId, metric);

        Float keyValue = CollectedMetricsLocalCache.getBrokerMetrics(brokerMetricKey);
        if(null != keyValue) {
            BrokerMetrics brokerMetrics = new BrokerMetrics(clusterId, brokerId);
            brokerMetrics.putMetric(metric, keyValue);
            return Result.buildSuc(brokerMetrics);
        }

        Result<BrokerMetrics> ret = this.collectBrokerMetricsFromKafka(clusterId, brokerId, metric);
        if(null == ret || ret.failed() || null == ret.getData()) {return ret;}

        Map<String, Float> metricsMap = ret.getData().getMetrics();
        for(Map.Entry<String, Float> metricNameAndValueEntry : metricsMap.entrySet()){
            CollectedMetricsLocalCache.putBrokerMetrics(brokerMetricKey, metricNameAndValueEntry.getValue());
        }

        return ret;
    }

    @Override
    public Result<BrokerMetrics> collectBrokerMetricsFromKafka(Long clusterId, Integer brokerId, String metric){
        try {
            BrokerMetricParam brokerMetricParam = new BrokerMetricParam(clusterId, brokerId, metric );
            return (Result<BrokerMetrics>) doVCHandler(clusterId, metric, brokerMetricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<BrokerMetrics> collectBrokerMetricsFromKafka(Long clusterId, Integer brokerId, List<String> metrics) {
        BrokerMetrics brokerMetrics = new BrokerMetrics(clusterId, brokerId);

        for(String metric : metrics){
            if(null != brokerMetrics.getMetrics().get(metric)){continue;}

            Result<BrokerMetrics> ret = collectBrokerMetricsFromKafka(clusterId, brokerId, metric);
            if(null != ret && !ret.failed()){
                brokerMetrics.putMetric(ret.getData().getMetrics());
            }
        }
        return Result.buildSuc(brokerMetrics);
    }

    @Override
    public Result<List<MetricMultiLinesVO>> listBrokerMetricsFromES(Long clusterId, MetricsBrokerDTO dto){
        Long         startTime       = dto.getStartTime();
        Long         endTime         = dto.getEndTime();
        Integer      topN            = dto.getTopNu();
        String       aggType         = dto.getAggType();
        List<Long>   brokerIds       = dto.getBrokerIds();
        List<String> metrics         = dto.getMetricsNames();

        Table<String/*metric*/, Long/*brokerId*/, List<MetricPointVO>> retTable;
        if(CollectionUtils.isEmpty(brokerIds)){
            List<Long>   defaultBrokerIds = listTopNBrokerIds(clusterId, topN);
            retTable = brokerMetricESDAO.listBrokerMetricsByTop(clusterId, defaultBrokerIds, metrics, aggType, topN, startTime, endTime);
        }else {
            retTable = brokerMetricESDAO.listBrokerMetricsByBrokerIds(clusterId, metrics, aggType, brokerIds, startTime, endTime);
        }

        List<MetricMultiLinesVO> multiLinesVOS = metricMap2VO(clusterId, convertId2Host(clusterId, retTable).rowMap());
        return Result.buildSuc(multiLinesVOS);
    }

    @Override
    public Result<List<MetricPointVO>> getMetricPointsFromES(Long clusterPhyId, Integer brokerId, MetricDTO dto) {
        Map<String/*metric*/, MetricPointVO> metricPointMap = brokerMetricESDAO.getBrokerMetricsPoint(
                clusterPhyId,
                brokerId,
                dto.getMetricsNames(),
                dto.getAggType(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        return Result.buildSuc(new ArrayList<>(metricPointMap.values()));
    }

    @Override
    public Result<List<BrokerMetrics>> getLatestMetricsFromES(Long clusterPhyId, List<Integer> brokerIdList) {
        List<BrokerMetrics> brokerMetrics = new ArrayList<>();

        for(Integer brokerId : brokerIdList) {
            try {
                BrokerMetricPO brokerMetricPO = brokerMetricESDAO.getBrokerLatestMetrics(clusterPhyId, brokerId);
                if (brokerMetricPO == null) {
                    // 如果为null，则忽略该po
                    continue;
                }

                brokerMetrics.add(ConvertUtil.obj2Obj(brokerMetricPO, BrokerMetrics.class));
            } catch (Exception e) {
                LOGGER.error(
                        "method=getLatestMetricsFromES||clusterPhyId={}||brokerId={}||errMsg=exception",
                        clusterPhyId, brokerId, e
                );
            }
        }

        return Result.buildSuc(brokerMetrics);
    }

    @Override
    public Result<BrokerMetrics> getLatestMetricsFromES(Long clusterPhyId, Integer brokerId) {
        BrokerMetricPO brokerMetricPO = brokerMetricESDAO.getBrokerLatestMetrics(clusterPhyId, brokerId);
        return Result.buildSuc(ConvertUtil.obj2Obj(brokerMetricPO, BrokerMetrics.class));
    }

    @Override
    public boolean isMetricName(String str) {
        return super.isMetricName(str);
    }

    /**************************************************** private method ****************************************************/

    private List<Long> listTopNBrokerIds(Long clusterId, Integer      topN){
        List<Broker> brokers = brokerService.listAliveBrokersFromDB(clusterId);
        if(CollectionUtils.isEmpty(brokers)){return new ArrayList<>();}

        return brokers.subList(0, Math.min(topN, brokers.size()))
                .stream().map(b -> b.getBrokerId().longValue()).collect(Collectors.toList());
    }

    private Result<BrokerMetrics> getConnectCountFromKafkaByJMX(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;
        String      metric      = param.getMetric();
        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = getJMXInfo(clusterId, metric);
        if(null == jmxInfo){return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);}

        //2、获取jmx连接
        JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)){return Result.buildFailure(VC_JMX_INIT_ERROR);}

        try {
            ObjectName queryListenerRegX = new ObjectName(jmxInfo.getJmxObjectName() + ",listener=*,networkProcessor=*");
            QueryExp   exp               = Query.gt(Query.attr(jmxInfo.getJmxAttribute()), Query.value(0.0));

            Set<ObjectName> objectNames = jmxConnectorWrap.queryNames(queryListenerRegX, exp);
            float totalConnections = 0f;
            for(ObjectName objectName : objectNames){
                totalConnections += Double.valueOf(jmxConnectorWrap.getAttribute(objectName, jmxInfo.getJmxAttribute()).toString());
            }

            BrokerMetrics brokerMetrics = new BrokerMetrics(clusterId, brokerId);
            brokerMetrics.putMetric(metric, totalConnections);
            return Result.buildSuc(brokerMetrics);
        } catch (InstanceNotFoundException e) {
            return Result.buildFailure(VC_JMX_INSTANCE_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("method=getConnectCountFromKafkaByJMX||cluster={}||brokerId={}||metrics={}||jmx={}" +
                    "||msg={}", clusterId, brokerId, metric, jmxInfo.getJmxObjectName(), e.getClass().getName());
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }

    private Result<BrokerMetrics> getMetricFromKafkaByJMX(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;

        String      metric      = param.getMetric();
        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = getJMXInfo(clusterId, metric);
        if(null == jmxInfo){return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);}

        //2、获取jmx连接
        JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)){return Result.buildFailure(VC_JMX_INIT_ERROR);}

        try {
            //2、获取jmx指标
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName()), jmxInfo.getJmxAttribute()).toString();

            BrokerMetrics brokerMetrics = new BrokerMetrics(clusterId, brokerId);
            brokerMetrics.putMetric(metric, Float.valueOf(value));

            return Result.buildSuc(brokerMetrics);
        } catch (Exception e) {
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }

    /**
     * 获取 broker 的健康分
     * @param metricParam
     * @return
     */
    private Result<BrokerMetrics> getMetricHealthScore(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;

        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        BrokerMetrics brokerMetrics = healthStateService.calBrokerHealthMetrics(clusterId, brokerId);
        return Result.buildSuc(brokerMetrics);
    }

    private Result<BrokerMetrics> getPartitionsSkew(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;

        String      metric      = param.getMetric();
        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        Result<BrokerMetrics> metricsResult = this.collectBrokerMetricsFromKafkaWithCacheFirst(clusterId, brokerId, BrokerMetricVersionItems.BROKER_METRIC_PARTITIONS);
        if (metricsResult.failed()) {
            return metricsResult;
        }

        Float brokerReplicaCount = metricsResult.getData().getMetric( BrokerMetricVersionItems.BROKER_METRIC_PARTITIONS);

        Integer globalReplicaCount = topicService.getReplicaSizeFromCacheFirst(clusterId);

        Integer globalBrokerCount = brokerService.listAllBrokersFromDB(clusterId).size();
        if (globalReplicaCount <= 0 || globalBrokerCount <= 0) {
            // 集群无分区或者集群无broker，则倾斜率直接设置为0
            return Result.buildSuc(BrokerMetrics.initWithMetric(
                    clusterId,
                    brokerId,
                    metric,
                    0f)
            );
        }

        Float agvReplicaCount = globalReplicaCount.floatValue() / globalBrokerCount;
        return Result.buildSuc(BrokerMetrics.initWithMetric(
                clusterId,
                brokerId,
                metric,
                (brokerReplicaCount - agvReplicaCount) / agvReplicaCount)
        );
    }

    private Result<BrokerMetrics> getLogSizeFromJmx(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;

        String      metric      = param.getMetric();
        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        List<Partition> partitions = partitionService.listPartitionFromCacheFirst(clusterId, brokerId);

        Float logSizeSum = 0f;
        for(Partition p : partitions) {
            try {
                Result<ReplicationMetrics> metricsResult = replicaMetricService.collectReplicaMetricsFromKafka(
                        clusterId,
                        p.getTopicName(),
                        p.getPartitionId(),
                        brokerId,
                        ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_SIZE
                );

                if(null == metricsResult || metricsResult.failed() || null == metricsResult.getData()) {
                    continue;
                }

                Float replicaLogSize = metricsResult.getData().getMetric(ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_SIZE);

                logSizeSum += (replicaLogSize == null? 0.0f: replicaLogSize);
            } catch (Exception e) {
                LOGGER.error(
                        "method=getLogSize||clusterPhyId={}||brokerId={}||topicName={}||partitionId={}||metricName={}||errMsg=exception",
                        clusterId, brokerId, p.getTopicName(), p.getPartitionId(), metric, e.getClass().getName()
                );
            }
        }

        return Result.buildSuc(BrokerMetrics.initWithMetric(clusterId, brokerId, metric, logSizeSum));
    }

    private Result<BrokerMetrics> getLogSizeFromClient(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;

        String      metric      = param.getMetric();
        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        Result<Map<String, LogDirDescription>> descriptionMapResult = brokerService.getBrokerLogDirDescFromKafka(clusterId, brokerId);
        if(null == descriptionMapResult || descriptionMapResult.failed() || null == descriptionMapResult.getData()) {
            return Result.buildFromIgnoreData(descriptionMapResult);
        }

        Float logSizeSum = 0f;
        for (LogDirDescription logDirDescription: descriptionMapResult.getData().values()) {
            for (ReplicaInfo replicaInfo: logDirDescription.replicaInfos().values()) {
                logSizeSum += replicaInfo.size();
            }
        }

        return Result.buildSuc(BrokerMetrics.initWithMetric(clusterId, brokerId, metric, logSizeSum));
    }

    private Result<BrokerMetrics> getLeadersSkew(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;

        String      metric      = param.getMetric();
        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        Result<BrokerMetrics> metricsResult = this.collectBrokerMetricsFromKafkaWithCacheFirst(clusterId, brokerId, BrokerMetricVersionItems.BROKER_METRIC_LEADERS);
        if (metricsResult.failed()) {
            return metricsResult;
        }

        Float brokerLeaderCount = metricsResult.getData().getMetric( BrokerMetricVersionItems.BROKER_METRIC_LEADERS);

        Integer globalLeaderCount = (int) partitionService.listPartitionFromCacheFirst(clusterId)
                .stream()
                .filter(partition -> !partition.getLeaderBrokerId().equals(KafkaConstant.NO_LEADER)).count();

        Integer globalBrokerCount = brokerService.listAllBrokersFromDB(clusterId).size();
        if (globalLeaderCount <= 0 || globalBrokerCount <= 0) {
            // 集群无leader分区或者集群无broker，则倾斜率直接设置为0
            return Result.buildSuc(BrokerMetrics.initWithMetric(
                    clusterId,
                    brokerId,
                    metric,
                    0f)
            );
        }

        Float agvPartitionCount = globalLeaderCount.floatValue() / globalBrokerCount;
        return Result.buildSuc(BrokerMetrics.initWithMetric(
                clusterId,
                brokerId,
                metric,
                (brokerLeaderCount - agvPartitionCount) / agvPartitionCount)
        );
    }

    private Result<BrokerMetrics> isBrokerAlive(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;

        String      metric      = param.getMetric();
        Long        clusterId   = param.getClusterId();
        Integer     brokerId    = param.getBrokerId();

        Broker broker = brokerService.getBroker(clusterId, brokerId);
        if (broker == null) {
            return Result.buildFromRSAndMsg(NOT_EXIST, MsgConstant.getBrokerNotExist(clusterId, brokerId));
        }

        return Result.buildSuc(BrokerMetrics.initWithMetric(clusterId, brokerId, metric, broker.alive()? Constant.YES.floatValue(): Constant.NO.floatValue()));
    }

    private Result<BrokerMetrics> doNothing(VersionItemParam metricParam) {
        BrokerMetricParam param = (BrokerMetricParam)metricParam;
        return Result.buildSuc(new BrokerMetrics(param.getClusterId(), param.getBrokerId()));
    }

    private Table<String/*metric*/, String/*host:port*/, List<MetricPointVO>> convertId2Host(
            Long clusterId,
            Table<String/*metric*/, Long/*brokerId*/, List<MetricPointVO>> table){
        Table<String, String, List<MetricPointVO>> destTable = HashBasedTable.create();

        Map<Long, Broker> brokerMap = new HashMap<>();

        for(String metric : table.rowKeySet()){
            Map<Long, List<MetricPointVO>> brokerIdMetricPointMap = table.row(metric);
            for(Long brokerId : brokerIdMetricPointMap.keySet()){
                Broker broker = brokerMap.getOrDefault(brokerId, brokerService.getBroker(clusterId, brokerId.intValue()));
                brokerMap.put(brokerId, broker);
                destTable.put(metric, broker.getHost() + ":" + broker.getPort(), brokerIdMetricPointMap.get(brokerId));
            }
        }

        return destTable;
    }
}
