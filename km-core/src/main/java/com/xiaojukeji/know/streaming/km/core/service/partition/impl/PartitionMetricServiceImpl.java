package com.xiaojukeji.know.streaming.km.core.service.partition.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.TopicMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.PartitionMetricPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.cache.CollectedMetricsLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionMetricService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseMetricService;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.PartitionMetricESDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.PartitionMetricVersionItems.*;

/**
 * @author didi
 */
@Service
public class PartitionMetricServiceImpl extends BaseMetricService implements PartitionMetricService {
    private static final ILog LOGGER  = LogFactory.getLog(PartitionMetricServiceImpl.class);

    public static final String PARTITION_METHOD_GET_METRIC_FROM_JMX             = "getMetricFromJmx";
    public static final String PARTITION_METHOD_GET_OFFSET_RELEVANT_METRICS     = "getOffsetRelevantMetrics";
    public static final String PARTITION_METHOD_GET_TOPIC_AVG_METRIC_MESSAGES   = "getTopicAvgMetricFromJmx";

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private PartitionMetricESDAO partitionMetricESDAO;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_PARTITION;
    }

    @Override
    protected List<String> listMetricPOFields(){
        return BeanUtil.listBeanFields(PartitionMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler(){
        registerVCHandler( PARTITION_METHOD_GET_METRIC_FROM_JMX,            this::getMetricFromJmx);
        registerVCHandler( PARTITION_METHOD_GET_OFFSET_RELEVANT_METRICS,    this::getOffsetRelevantMetrics);
        registerVCHandler( PARTITION_METHOD_GET_TOPIC_AVG_METRIC_MESSAGES,  this::getTopicAvgMetricFromJmx);
    }

    @Override
    public Result<List<PartitionMetrics>> collectPartitionsMetricsFromKafkaWithCache(Long clusterPhyId, String topicName, String metricName) {
        String partitionMetricsKey = CollectedMetricsLocalCache.genClusterTopicMetricKey(clusterPhyId, topicName, metricName);

        List<PartitionMetrics> metricsList = CollectedMetricsLocalCache.getPartitionMetricsList(partitionMetricsKey);
        if(null != metricsList) {
            return Result.buildSuc(metricsList);
        }

        Result<List<PartitionMetrics>> metricsResult = this.collectPartitionsMetricsFromKafka(clusterPhyId, topicName, metricName);
        if(null == metricsResult || metricsResult.failed() || null == metricsResult.getData() || metricsResult.getData().isEmpty()) {
            return metricsResult;
        }

        // 更新cache
        PartitionMetrics metrics = metricsResult.getData().get(0);
        metrics.getMetrics().entrySet().forEach(
                metricEntry -> CollectedMetricsLocalCache.putPartitionMetricsList(partitionMetricsKey, metricsResult.getData())
        );

        return metricsResult;
    }

    @Override
    public Result<List<PartitionMetrics>> collectPartitionsMetricsFromKafka(Long clusterPhyId, String topicName, List<String> metricNameList) {
        Set<String> collectedMetricSet = new HashSet<>();

        Map<Integer, PartitionMetrics> metricsMap = new HashMap<>();
        for (String metricName: metricNameList) {
            if (collectedMetricSet.contains(metricName)) {
                continue;
            }

            Result<List<PartitionMetrics>> metricsResult = this.collectPartitionsMetricsFromKafka(clusterPhyId, topicName, metricName);
            if(null == metricsResult || metricsResult.failed() || null == metricsResult.getData() || metricsResult.getData().isEmpty()) {
                continue;
            }

            collectedMetricSet.addAll(metricsResult.getData().get(0).getMetrics().keySet());

            for (PartitionMetrics metrics: metricsResult.getData()) {
                metricsMap.putIfAbsent(metrics.getPartitionId(), metrics);
                metricsMap.get(metrics.getPartitionId()).putMetric(metrics.getMetrics());
            }
        }

        return Result.buildSuc(new ArrayList<>(metricsMap.values()));
    }

    @Override
    public Result<PartitionMetrics> collectPartitionMetricsFromKafka(Long clusterPhyId, String topicName, Integer partitionId, String metricName){
        Result<List<PartitionMetrics>> metricsResult = this.collectPartitionsMetricsFromKafka(clusterPhyId, topicName, metricName);
        if (!metricsResult.hasData()) {
            return Result.buildFromIgnoreData(metricsResult);
        }

        for (PartitionMetrics metrics: metricsResult.getData()) {
            if (metrics.getPartitionId().equals(partitionId)) {
                return Result.buildSuc(metrics);
            }
        }

        return Result.buildFromRSAndMsg(KAFKA_OPERATE_FAILED, "Topic分区指标获取成功，但是该分区的指标不存在");
    }

    @Override
    public Result<List<PartitionMetrics>> collectPartitionsMetricsFromKafka(Long clusterPhyId, String topicName, String metricName) {
        try {
            TopicMetricParam param = new TopicMetricParam(clusterPhyId, topicName, metricName);

            return (Result<List<PartitionMetrics>>) doVCHandler(clusterPhyId, metricName, param);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public PartitionMetrics getLatestMetricsFromES(Long clusterPhyId, String topic, Integer brokerId, Integer partitionId, List<String> metricNames) {
        PartitionMetricPO po = partitionMetricESDAO.getPartitionLatestMetrics(clusterPhyId, topic, brokerId, partitionId, metricNames);

        return ConvertUtil.obj2Obj(po, PartitionMetrics.class);
    }

    @Override
    public Result<List<PartitionMetrics>> getLatestMetricsFromES(Long clusterPhyId, String topicName, List<String> metricNames) {
        List<PartitionMetricPO> poList = partitionMetricESDAO.listPartitionLatestMetricsByTopic(clusterPhyId, topicName, metricNames);

        return Result.buildSuc(ConvertUtil.list2List(poList, PartitionMetrics.class));
    }

    /**************************************************** private method ****************************************************/

    private Result<List<PartitionMetrics>> getOffsetRelevantMetrics(VersionItemParam param) {
        TopicMetricParam    metricParam         =   (TopicMetricParam) param;
        String              metricName          =   metricParam.getMetric();
        String              topicName           =   metricParam.getTopic();
        Long                clusterPhyId        =   metricParam.getClusterId();

        List<Partition> partitionList = partitionService.listPartitionFromCacheFirst(clusterPhyId, topicName);
        Map<Integer, Partition> partitionMap = partitionList.stream().collect(Collectors.toMap(Partition::getPartitionId, Function.identity()));

        Map<Integer, PartitionMetrics> metricsMap = new HashMap<>();

        // offset 指标
        Result<Tuple<Map<TopicPartition, Long>, Map<TopicPartition, Long>>> offsetResult = partitionService.getPartitionBeginAndEndOffsetFromKafka(clusterPhyId, topicName);
        if (offsetResult.failed()) {
            LOGGER.warn(
                    "method=getOffsetRelevantMetrics||clusterPhyId={}||topicName={}||result={}||msg=get offset failed",
                    clusterPhyId, topicName, offsetResult
            );

            return Result.buildFromIgnoreData(offsetResult);
        }

        // begin offset 指标
        for (Map.Entry<TopicPartition, Long> entry: offsetResult.getData().v1().entrySet()) {
            Partition partition = partitionMap.get(entry.getKey().partition());
            PartitionMetrics metrics = metricsMap.getOrDefault(
                    entry.getKey().partition(),
                    new PartitionMetrics(clusterPhyId, topicName, partition != null? partition.getLeaderBrokerId(): KafkaConstant.NO_LEADER, entry.getKey().partition())
            );

            metrics.putMetric(PARTITION_METRIC_LOG_START_OFFSET, entry.getValue().floatValue());
            metricsMap.put(entry.getKey().partition(), metrics);
        }

        // end offset 指标
        for (Map.Entry<TopicPartition, Long> entry: offsetResult.getData().v2().entrySet()) {
            Partition partition = partitionMap.get(entry.getKey().partition());
            PartitionMetrics metrics = metricsMap.getOrDefault(
                    entry.getKey().partition(),
                    new PartitionMetrics(clusterPhyId, topicName, partition != null? partition.getLeaderBrokerId(): KafkaConstant.NO_LEADER, entry.getKey().partition())
            );

            metrics.putMetric(PARTITION_METRIC_LOG_END_OFFSET, entry.getValue().floatValue());
            metricsMap.put(entry.getKey().partition(), metrics);
        }

        // messages 指标
        if (!ValidateUtils.isEmptyMap(offsetResult.getData().v1()) && !ValidateUtils.isEmptyMap(offsetResult.getData().v2())) {
            for (Map.Entry<TopicPartition, Long> entry: offsetResult.getData().v2().entrySet()) {
                Long beginOffset = offsetResult.getData().v1().get(entry.getKey());
                if (beginOffset == null) {
                    continue;
                }

                Partition partition = partitionMap.get(entry.getKey().partition());
                PartitionMetrics metrics = metricsMap.getOrDefault(
                        entry.getKey().partition(),
                        new PartitionMetrics(clusterPhyId, topicName, partition != null? partition.getLeaderBrokerId(): KafkaConstant.NO_LEADER, entry.getKey().partition())
                );

                metrics.putMetric(PARTITION_METRIC_MESSAGES, Math.max(0, entry.getValue() - beginOffset) * 1.0f);
                metricsMap.put(entry.getKey().partition(), metrics);
            }
        } else {
            LOGGER.warn(
                    "method=getOffsetRelevantMetrics||clusterPhyId={}||topicName={}||offsetData={}||msg=get messages failed",
                    clusterPhyId, topicName, ConvertUtil.obj2Json(offsetResult.getData())
            );
        }

        return Result.buildSuc(new ArrayList<>(metricsMap.values()));
    }

    private Result<List<PartitionMetrics>> getMetricFromJmx(VersionItemParam param) {
        TopicMetricParam    metricParam         =   (TopicMetricParam) param;
        String              metricName          =   metricParam.getMetric();
        String              topicName           =   metricParam.getTopic();
        Long                clusterPhyId        =   metricParam.getClusterId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = this.getJMXInfo(clusterPhyId, metricName);
        if(null == jmxInfo) {
            return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);
        }

        List<PartitionMetrics> metricsList = new ArrayList<>();

        List<Partition> partitionList = partitionService.listPartitionFromCacheFirst(clusterPhyId, topicName);
        for (Partition partition: partitionList) {
            if (KafkaConstant.NO_LEADER.equals(partition.getLeaderBrokerId())) {
                // 2、没有leader，则直接忽略
                continue;
            }

            // 3、获取jmx连接
            JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterPhyId, partition.getLeaderBrokerId());
            if (ValidateUtils.isNull(jmxConnectorWrap)){
                continue;
            }

            try {
                // 4、获取jmx指标
                String value = jmxConnectorWrap.getAttribute(
                        new ObjectName(jmxInfo.getJmxObjectName() + ",topic=" + topicName + ",partition=" + partition.getPartitionId()),
                        jmxInfo.getJmxAttribute()
                ).toString();

                PartitionMetrics metrics = new PartitionMetrics(clusterPhyId, topicName, partition.getLeaderBrokerId(), partition.getPartitionId());
                metrics.putMetric(metricName, Float.valueOf(value));
                metricsList.add(metrics);

            } catch (InstanceNotFoundException e) {
                // ignore
            } catch (Exception e) {
                LOGGER.error(
                        "method=getMetricFromJmx||clusterPhyId={}||topicName={}||partitionId={}||leaderBrokerId={}||metricName={}||msg={}",
                        clusterPhyId, topicName, partition.getPartitionId(), partition.getLeaderBrokerId(), metricName, e.getClass().getName()
                );
            }
        }

        return Result.buildSuc(metricsList);
    }

    private Result<List<PartitionMetrics>> getTopicAvgMetricFromJmx(VersionItemParam param) {
        TopicMetricParam    metricParam         =   (TopicMetricParam) param;
        String              metricName          =   metricParam.getMetric();
        String              topicName           =   metricParam.getTopic();
        Long                clusterPhyId        =   metricParam.getClusterId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = this.getJMXInfo(clusterPhyId, metricName);
        if(null == jmxInfo) {
            return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);
        }

        List<PartitionMetrics> metricsList = new ArrayList<>();

        List<Partition> partitionList = partitionService.listPartitionFromCacheFirst(clusterPhyId, topicName);
        for (Partition partition: partitionList) {
            if (KafkaConstant.NO_LEADER.equals(partition.getLeaderBrokerId())) {
                // 2、没有leader，则直接忽略
                continue;
            }

            // 3、获取jmx连接
            JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterPhyId, partition.getLeaderBrokerId());
            if (ValidateUtils.isNull(jmxConnectorWrap)){
                continue;
            }

            try {
                // 4、获取jmx指标
                String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName() + ",topic=" + topicName), jmxInfo.getJmxAttribute()).toString();

                long leaderCount = partitionList.stream().filter(elem -> elem.getLeaderBrokerId().equals(partition.getLeaderBrokerId())).count();
                if (leaderCount <= 0) {
                    // leader已经切换走了
                    continue;
                }

                PartitionMetrics metrics = new PartitionMetrics(clusterPhyId, topicName, partition.getLeaderBrokerId(), partition.getPartitionId());
                metrics.putMetric(metricName, Float.valueOf(value) / leaderCount);
                metricsList.add(metrics);

            } catch (InstanceNotFoundException e) {
                // ignore
            } catch (Exception e) {
                LOGGER.error(
                        "method=getTopicAvgMetricFromJmx||clusterPhyId={}||topicName={}||partitionId={}||leaderBrokerId={}||metricName={}||msg={}",
                        clusterPhyId, topicName, partition.getPartitionId(), partition.getLeaderBrokerId(), metricName, e.getClass().getName()
                );
            }
        }

        return Result.buildSuc(metricsList);
    }
}
