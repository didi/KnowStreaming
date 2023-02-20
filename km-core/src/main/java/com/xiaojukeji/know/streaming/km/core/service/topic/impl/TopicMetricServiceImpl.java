package com.xiaojukeji.know.streaming.km.core.service.topic.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsTopicDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.TopicMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.*;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.TopicMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.ESConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.cache.CollectedMetricsLocalCache;
import com.xiaojukeji.know.streaming.km.persistence.cache.DataBaseDataLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseMetricService;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.TopicMetricESDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.PartitionMetricVersionItems.PARTITION_METRIC_MESSAGES;

/**
 */
@Service
public class TopicMetricServiceImpl extends BaseMetricService implements TopicMetricService {
    private static final ILog LOGGER = LogFactory.getLog(TopicMetricServiceImpl.class);

    public static final String TOPIC_METHOD_DO_NOTHING                                              = "doNothing";
    public static final String TOPIC_METHOD_GET_HEALTH_SCORE                                        = "getMetricHealthScore";
    public static final String TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX               = "getMetricFromKafkaByTotalBrokerJmx";
    public static final String TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_PARTITION_OF_BROKER_JMX  = "getMetricFromKafkaByTotalPartitionOfBrokerJmx";
    public static final String TOPIC_METHOD_GET_MESSAGES                                            = "getMessages";
    public static final String TOPIC_METHOD_GET_REPLICAS_COUNT                                      = "getReplicasCount";
    public static final String TOPIC_METHOD_GET_TOPIC_MIRROR_FETCH_LAG                              = "getTopicMirrorFetchLag";
    @Autowired
    private HealthStateService healthStateService;

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private PartitionMetricService partitionMetricService;

    @Autowired
    private TopicMetricESDAO topicMetricESDAO;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_TOPIC;
    }

    @Override
    protected List<String> listMetricPOFields(){
        return BeanUtil.listBeanFields(TopicMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler(){
        registerVCHandler( TOPIC_METHOD_DO_NOTHING,                                                 this::doNothing);
        registerVCHandler( TOPIC_METHOD_GET_HEALTH_SCORE,                                           this::getMetricHealthScore);
        registerVCHandler( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX,                  this::getMetricFromKafkaByTotalBrokerJmx);
        registerVCHandler( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_PARTITION_OF_BROKER_JMX,     this::getMetricFromKafkaByTotalPartitionOfBrokerJmx );
        registerVCHandler( TOPIC_METHOD_GET_REPLICAS_COUNT,                                         this::getReplicasCount);
        registerVCHandler( TOPIC_METHOD_GET_MESSAGES,                                               this::getMessages);
        registerVCHandler( TOPIC_METHOD_GET_TOPIC_MIRROR_FETCH_LAG,                                 this::getTopicMirrorFetchLag);
    }

    @Override
    public Result<List<TopicMetrics>> collectTopicMetricsFromKafkaWithCacheFirst(Long clusterPhyId, String topicName, String metricName) {
        String topicMetricsKey = CollectedMetricsLocalCache.genClusterTopicMetricKey(clusterPhyId, topicName, metricName);

        List<TopicMetrics> metricsList = CollectedMetricsLocalCache.getTopicMetrics(topicMetricsKey);
        if(null != metricsList) {
            return Result.buildSuc(metricsList);
        }

        Result<List<TopicMetrics>> metricsResult = this.collectTopicMetricsFromKafka(clusterPhyId, topicName, metricName);
        if(null == metricsResult || metricsResult.failed() || null == metricsResult.getData() || metricsResult.getData().isEmpty()) {
            return metricsResult;
        }

        // 更新cache
        TopicMetrics metrics = metricsResult.getData().get(0);
        metrics.getMetrics().entrySet().forEach(
                metricEntry -> CollectedMetricsLocalCache.putTopicMetrics(topicMetricsKey, metricsResult.getData())
        );

        return metricsResult;
    }

    @Override
    public Result<List<TopicMetrics>> collectTopicMetricsFromKafka(Long clusterId, String topic, String metric){
        try {
            TopicMetricParam topicMetricParam = new TopicMetricParam(clusterId, topic, metric);
            return (Result<List<TopicMetrics>>)doVCHandler(clusterId, metric, topicMetricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Map<String, TopicMetrics> getLatestMetricsFromCache(Long clusterPhyId) {
        Map<String, TopicMetrics> metricsMap = DataBaseDataLocalCache.getTopicMetrics(clusterPhyId);
        if (metricsMap == null) {
            return new HashMap<>();
        }

        return metricsMap;
    }

    @Override
    public TopicMetrics getTopicLatestMetricsFromES(Long clusterPhyId, Integer brokerId, String topicName, List<String> metricNames) {
        TopicMetricPO topicMetricPO = topicMetricESDAO.getTopicLatestMetricByBrokerId(clusterPhyId,
                                                        topicName, brokerId, metricNames);

        return ConvertUtil.obj2Obj(topicMetricPO, TopicMetrics.class);
    }

    @Override
    public List<TopicMetrics> listTopicLatestMetricsFromES(Long clusterPhyId, List<String> topicNames, List<String> metricNames) {
        List<TopicMetricPO> poList = new ArrayList<>();

        for (int i = 0; i < topicNames.size(); i += ESConstant.SEARCH_LATEST_TOPIC_METRIC_CNT_PER_REQUEST) {
            poList.addAll(topicMetricESDAO.listTopicLatestMetric(
                    clusterPhyId,
                    topicNames.subList(i, Math.min(i + ESConstant.SEARCH_LATEST_TOPIC_METRIC_CNT_PER_REQUEST, topicNames.size())),
                    Collections.emptyList())
            );
        }

        return ConvertUtil.list2List(poList, TopicMetrics.class);
    }

    @Override
    public TopicMetrics getTopicLatestMetricsFromES(Long clusterPhyId, String topicName, List<String> metricNames) {
        TopicMetricPO topicMetricPO = topicMetricESDAO.getTopicLatestMetric(clusterPhyId, topicName, metricNames);

        return ConvertUtil.obj2Obj(topicMetricPO, TopicMetrics.class);
    }

    @Override
    public Result<List<MetricMultiLinesVO>> listTopicMetricsFromES(Long clusterId, MetricsTopicDTO dto) {
        Long         startTime       = dto.getStartTime();
        Long         endTime         = dto.getEndTime();
        Integer      topN            = dto.getTopNu();
        String       aggType         = dto.getAggType();
        List<String> topics          = dto.getTopics();
        List<String> metrics         = dto.getMetricsNames();

        Table<String/*metric*/, String/*topics*/, List<MetricPointVO>> retTable;
        if(CollectionUtils.isEmpty(topics)) {
            //如果 es 中获取不到topN的topic就使用从数据库中获取的topics
            List<String> defaultTopics = this.listTopNTopics(clusterId, topN);
            retTable = topicMetricESDAO.listTopicMetricsByTopN(clusterId, defaultTopics, metrics, aggType, topN, startTime, endTime );
        }else {
            retTable = topicMetricESDAO.listTopicMetricsByTopics(clusterId, metrics, aggType, topics, startTime, endTime);
        }

        List<MetricMultiLinesVO> multiLinesVOS = metricMap2VO(clusterId, retTable.rowMap());
        return Result.buildSuc(multiLinesVOS);
    }

    @Override
    public Result<List<TopicMetrics>> listTopicMaxMinMetrics(Long clusterPhyId, List<String> topics, String metric,
                                                                              boolean max, Long startTime, Long endTime){
        List<TopicMetricPO> ret = topicMetricESDAO.listTopicMaxMinMetrics(clusterPhyId, topics, metric, max, startTime, endTime);
        return Result.buildSuc(ConvertUtil.list2List(ret, TopicMetrics.class));
    }

    @Override
    public Result<Map<String, MetricPointVO>> getAggMetricPointFromES(Long clusterPhyId,
                                                                      List<String> topicNames,
                                                                      String metricName,
                                                                      AggTypeEnum aggTypeEnum,
                                                                      Long startTime,
                                                                      Long endTime) {
        if (!(AggTypeEnum.MAX.equals(aggTypeEnum) || AggTypeEnum.MIN.equals(aggTypeEnum))) {
            // 除了max和min方式之外的聚合方式
            Table<String/*topics*/, String/*metric*/, MetricPointVO> metricPointTable = topicMetricESDAO.getTopicsAggsMetricsValue(clusterPhyId, topicNames,
                    Arrays.asList(metricName), aggTypeEnum.getAggType(), startTime, endTime);

            Map<String/*Topic名称*/, MetricPointVO> voMap = new HashMap<>();
            for(String topic : metricPointTable.rowKeySet()){
                List<MetricPointVO> metricPoints = new ArrayList<>(metricPointTable.values()).stream().filter(elem -> elem.getName().equals(metricName)).collect(Collectors.toList());
                if (ValidateUtils.isEmptyList(metricPoints)) {
                    continue;
                }
                voMap.put(topic, metricPoints.get(0));
            }

            return Result.buildSuc(voMap);
        }

        // max和min方式之外的聚合方式
        Result<List<TopicMetrics>> metricListResult = this.listTopicMaxMinMetrics(clusterPhyId, topicNames, metricName, AggTypeEnum.MAX.equals(aggTypeEnum), startTime, endTime);
        if (metricListResult.failed()) {
            return Result.buildFromIgnoreData(metricListResult);
        }

        Map<String, MetricPointVO> voMap = new HashMap<>();
        for (TopicMetrics metrics: metricListResult.getData()) {
            Float value = metrics.getMetric(metricName);
            if (value == null) {
                continue;
            }
            voMap.put(metrics.getTopic(), new MetricPointVO(metricName, metrics.getTimestamp(), String.valueOf(value), aggTypeEnum.getAggType()));
        }

        return Result.buildSuc(voMap);
    }

    @Override
    public Result<List<MetricPointVO>> getMetricPointsFromES(Long clusterId, String topicName, MetricDTO dto) {
        Table<String/*topics*/, String/*metric*/, MetricPointVO> metricPointTable = topicMetricESDAO.getTopicsAggsMetricsValue(clusterId, Arrays.asList(topicName),
                dto.getMetricsNames(), dto.getAggType(), dto.getStartTime(), dto.getEndTime());

        List<MetricPointVO> metricPoints = new ArrayList<>(metricPointTable.values());
        return Result.buildSuc(metricPoints);
    }

    @Override
    public Result<Map<String/*Topic*/, List<MetricPointVO>>> getMetricPointsFromES(Long clusterId, List<String> topicNames, MetricDTO dto) {
        Table<String/*topics*/, String/*metric*/, MetricPointVO> metricPointTable = topicMetricESDAO.getTopicsAggsMetricsValue(clusterId, topicNames,
                dto.getMetricsNames(), dto.getAggType(), dto.getStartTime(), dto.getEndTime());

        Map<String/*Topic名称*/, List<MetricPointVO>> retMap = new HashMap<>();
        for(String topic : metricPointTable.rowKeySet()){
            retMap.put(topic, new ArrayList<>(metricPointTable.row(topic).values()));
        }

        return Result.buildSuc(retMap);
    }

    @Override
    public PaginationResult<TopicMetrics> pagingTopicWithLatestMetricsFromES(Long clusterId, List<String> metricNameList,
                                                                             SearchSort sort, SearchFuzzy fuzzy,
                                                                             List<SearchShould> shoulds, List<SearchTerm> terms,
                                                                             SearchPage page){
        setQueryMetricFlag(sort);
        setQueryMetricFlag(fuzzy);
        setQueryMetricFlag(terms);
        setQueryMetricFlag(shoulds);

        List<TopicMetricPO> topicMetricPOS = topicMetricESDAO.listTopicWithLatestMetrics(clusterId, sort, fuzzy, shoulds, terms);

        int startIdx = Math.min((page.getPageNo() - 1) * page.getPageSize(), topicMetricPOS.size());
        int endIdx = Math.min(startIdx +page.getPageSize(), topicMetricPOS.size());

        List<TopicMetricPO> subList = topicMetricPOS.subList(startIdx, endIdx);

        return PaginationResult.buildSuc(ConvertUtil.list2List(subList, TopicMetrics.class), topicMetricPOS.size(), page.getPageNo(), page.getPageSize());
    }

    @Override
    public Result<Integer> countMetricValueOccurrencesFromES(Long clusterPhyId, String topicName,
                                                             SearchTerm searchMatch, Long startTime, Long endTime) {
        setQueryMetricFlag(searchMatch);
        int count = topicMetricESDAO.countMetricValue(clusterPhyId, topicName, searchMatch, startTime, endTime);
        if(count < 0){
            return Result.buildFail();
        }

        return Result.buildSuc(count);
    }


    /**************************************************** private method ****************************************************/


    private List<String> listTopNTopics(Long clusterId, int topN){
        List<Topic> topics = topicService.listTopicsFromDB(clusterId);
        if(CollectionUtils.isEmpty(topics)){return new ArrayList<>();}

        return topics.subList(0, Math.min(topN, topics.size())).stream().map(Topic::getTopicName).collect(Collectors.toList());
    }

    private Result<List<TopicMetrics>> doNothing(VersionItemParam metricParam) {
        TopicMetricParam topicMetricParam = (TopicMetricParam)metricParam;

        return Result.buildSuc(Arrays.asList(new TopicMetrics(topicMetricParam.getTopic(), topicMetricParam.getClusterId(), true)));
    }

    private Result<List<TopicMetrics>> getMessages(VersionItemParam param) {
        TopicMetricParam topicMetricParam = (TopicMetricParam)param;

        String      metric      = topicMetricParam.getMetric();
        String      topic       = topicMetricParam.getTopic();
        Long        clusterId   = topicMetricParam.getClusterId();

        Result<List<PartitionMetrics>> metricsResult = partitionMetricService.collectPartitionsMetricsFromKafkaWithCache(clusterId, topic, PARTITION_METRIC_MESSAGES);
        if (!metricsResult.hasData()) {
            return Result.buildFromIgnoreData(metricsResult);
        }

        Float sumMessages = 0.0f;
        for(PartitionMetrics metrics : metricsResult.getData()) {
            Float messages = metrics.getMetric(PARTITION_METRIC_MESSAGES);
            if (messages == null) {
                return Result.buildFromRSAndMsg(KAFKA_OPERATE_FAILED, MsgConstant.getPartitionNotExist(clusterId, topic, metrics.getPartitionId()));
            }

            sumMessages += messages;
        }

        TopicMetrics metrics = new TopicMetrics(topic, clusterId, true);
        metrics.putMetric(metric, sumMessages);
        return Result.buildSuc(Arrays.asList(metrics));
    }

    private Result<List<TopicMetrics>> getReplicasCount(VersionItemParam param) {
        TopicMetricParam topicMetricParam = (TopicMetricParam)param;

        String      metric      = topicMetricParam.getMetric();
        String      topicName   = topicMetricParam.getTopic();
        Long        clusterId   = topicMetricParam.getClusterId();

        Topic   topic = topicService.getTopicFromCacheFirst(clusterId, topicName);
        if (topic == null) {
            return Result.buildFailure(TOPIC_NOT_EXIST);
        }

        Integer replicasCount = topic.getReplicaNum() * topic.getPartitionNum();

        TopicMetrics topicMetric = new TopicMetrics(topicName, clusterId, true);
        topicMetric.putMetric(metric, replicasCount.floatValue());

        return Result.buildSuc(Arrays.asList(topicMetric));
    }

    /**
     * 回去 topic 健康分指标
     * @param param
     * @return
     */
    private Result<List<TopicMetrics>> getMetricHealthScore(VersionItemParam param) {
        TopicMetricParam topicMetricParam = (TopicMetricParam)param;

        String      topic       = topicMetricParam.getTopic();
        Long        clusterId   = topicMetricParam.getClusterId();

        TopicMetrics topicMetric = healthStateService.calTopicHealthMetrics(clusterId, topic);
        return Result.buildSuc(Arrays.asList(topicMetric));
    }

    /**
     * 从JMX中获取LogSize
     */
    private Result<List<TopicMetrics>> getMetricFromKafkaByTotalPartitionOfBrokerJmx(VersionItemParam param) {
        TopicMetricParam topicMetricParam = (TopicMetricParam)param;

        String      metric      = topicMetricParam.getMetric();
        String      topicName   = topicMetricParam.getTopic();
        Long        clusterId   = topicMetricParam.getClusterId();

        //1、获取topic所在的broker以及partition分布信息
        Topic topic = topicService.getTopicFromCacheFirst(clusterId, topicName);
        Map<Integer, List<Integer>> broker2PartitionMap = new HashMap<>();
        topic.getPartitionMap().entrySet().stream().forEach(entry -> {
            for (Integer brokerId: entry.getValue()) {
                broker2PartitionMap.putIfAbsent(brokerId, new ArrayList<>());
                broker2PartitionMap.get(brokerId).add(entry.getKey());
            }
        });

        //2、获取jmx的属性信息
        VersionJmxInfo jmxInfo = getJMXInfo(clusterId, metric);
        if(null == jmxInfo){return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);}

        //3、获取broker列表的jmx连接，开始采集指标
        float topicLogSize = 0f;
        List<TopicMetrics> topicMetricsList = new ArrayList<>();
        for(Map.Entry<Integer, List<Integer>> brokerAndPartitionEntry : broker2PartitionMap.entrySet()){
            JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterId, brokerAndPartitionEntry.getKey());

            if (ValidateUtils.isNull(jmxConnectorWrap)){
                return Result.buildFailure(VC_JMX_CONNECT_ERROR);
            }

            float brokerTopicLogSize = 0f;
            for(Integer partitionId : brokerAndPartitionEntry.getValue()){
                try {
                    String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName()
                                    + ",topic=" + topicName + ",partition=" + partitionId),
                                    jmxInfo.getJmxAttribute()).toString();

                    brokerTopicLogSize += Double.valueOf(value);
                } catch (InstanceNotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    LOGGER.error("getMetricFromKafkaByTotalPartitionOfBrokerJmx||cluster={}||brokerId={}||topic={}||metrics={}||jmx={}||msg={}",
                            clusterId, brokerAndPartitionEntry.getKey(), topicName, metric, jmxInfo.getJmxObjectName(), e.getClass().getName());
                }
            }

            topicLogSize += brokerTopicLogSize;
            TopicMetrics topicBrokerMetric = new TopicMetrics(topicName, clusterId, brokerAndPartitionEntry.getKey(), false);
            topicBrokerMetric.putMetric(metric, brokerTopicLogSize);
            topicMetricsList.add(topicBrokerMetric);
        }

        TopicMetrics topicMetrics = new TopicMetrics(topicName, clusterId, true);
        topicMetrics.putMetric(metric, topicLogSize);
        topicMetricsList.add(topicMetrics);

        return Result.buildSuc(topicMetricsList);
    }

    /**
     * 从JMX中获取指标
     */
    private Result<List<TopicMetrics>> getMetricFromKafkaByTotalBrokerJmx(VersionItemParam param){
        TopicMetricParam topicMetricParam = (TopicMetricParam)param;

        String      metric      = topicMetricParam.getMetric();
        String      topic       = topicMetricParam.getTopic();
        Long        clusterId   = topicMetricParam.getClusterId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = getJMXInfo(clusterId, metric);
        if(null == jmxInfo){return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);}

        //2、获取topic所在的broker信息
        List<Broker> brokers = this.listAliveBrokersByTopic(clusterId, topic);
        if(CollectionUtils.isEmpty(brokers)){return Result.buildFailure(BROKER_NOT_EXIST);}

        //3、获取jmx连接，开始采集指标
        float topicMetricValue = 0f;
        List<TopicMetrics> topicMetrics = new ArrayList<>();
        for(Broker broker : brokers){
            Integer brokerId = broker.getBrokerId();
            JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterId, brokerId);

            if (ValidateUtils.isNull(jmxConnectorWrap)){return Result.buildFailure(VC_JMX_INIT_ERROR);}

            try {
                String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName() + ",topic=" + topic),
                        jmxInfo.getJmxAttribute()).toString();

                topicMetricValue += Float.valueOf(value);

                TopicMetrics topicBrokerMetric = new TopicMetrics(topic, clusterId, brokerId, false);
                topicBrokerMetric.putMetric(metric, Float.valueOf(value));

                topicMetrics.add(topicBrokerMetric);
            } catch (InstanceNotFoundException e) {
                // ignore
            } catch (Exception e) {
                LOGGER.error("method=getMetricFromKafkaByTotalBrokerJmx||cluster={}||brokerId={}||topic={}||metrics={}||jmx={}||msg={}",
                        clusterId, brokerId, topic, metric, jmxInfo.getJmxObjectName(), e.getClass().getName());
            }
        }

        TopicMetrics topicMetric = new TopicMetrics(topic, clusterId, true);
        topicMetric.putMetric(metric, topicMetricValue);
        topicMetrics.add(topicMetric);

        return Result.buildSuc(topicMetrics);
    }

    private List<Broker> listAliveBrokersByTopic(Long clusterPhyId, String topicName) {
        List<Broker> aliveBrokerList = brokerService.listAliveBrokersFromCacheFirst(clusterPhyId);

        Topic topic = topicService.getTopicFromCacheFirst(clusterPhyId, topicName);
        if (topic == null) {
            return aliveBrokerList;
        }

        return aliveBrokerList.stream().filter(elem -> topic.getBrokerIdSet().contains(elem.getBrokerId())).collect(Collectors.toList());
    }

    private Result<List<TopicMetrics>> getTopicMirrorFetchLag(VersionItemParam param) {
        TopicMetricParam topicMetricParam = (TopicMetricParam)param;

        String      topic       = topicMetricParam.getTopic();
        Long        clusterId   = topicMetricParam.getClusterId();
        String      metric      = topicMetricParam.getMetric();

        VersionJmxInfo jmxInfo = getJMXInfo(clusterId, metric);
        if(null == jmxInfo){return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);}

        if (!DataBaseDataLocalCache.isHaTopic(clusterId, topic)) {
            return Result.buildFailure(NOT_EXIST);
        }

        List<Broker> brokers = this.listAliveBrokersByTopic(clusterId, topic);
        if(CollectionUtils.isEmpty(brokers)){return Result.buildFailure(BROKER_NOT_EXIST);}

        Float sumLag = 0f;
        for (Broker broker : brokers) {
            JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterId, broker.getBrokerId());
            try {
                String jmxObjectName = String.format(jmxInfo.getJmxObjectName(), topic);
                Set<ObjectName> objectNameSet = jmxConnectorWrap.queryNames(new ObjectName(jmxObjectName), null);
                for (ObjectName name : objectNameSet) {
                    Object attribute = jmxConnectorWrap.getAttribute(name, jmxInfo.getJmxAttribute());
                    sumLag += Float.valueOf(attribute.toString());
                }
            } catch (Exception e) {
                LOGGER.error("method=getTopicMirrorFetchLag||cluster={}||brokerId={}||topic={}||metrics={}||jmx={}||msg={}",
                        clusterId, broker.getBrokerId(), topic, metric, jmxInfo.getJmxObjectName(), e.getClass().getName());
            }
        }
        TopicMetrics topicMetric = new TopicMetrics(topic, clusterId, true);
        topicMetric.putMetric(metric, sumLag);
        return Result.buildSuc(Arrays.asList(topicMetric));
    }
}
