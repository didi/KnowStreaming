package com.xiaojukeji.know.streaming.km.core.service.replica.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.ReplicationMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ReplicationMetricPO;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.replica.ReplicaMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseMetricService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_REPLICATION;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_END_OFFSET;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_START_OFFSET;

/**
 * @author didi
 */
@Service
public class ReplicaMetricServiceImpl extends BaseMetricService implements ReplicaMetricService {
    private static final ILog LOGGER = LogFactory.getLog( ReplicaMetricServiceImpl.class);

    public static final String REPLICATION_METHOD_DO_NOTHING                            = "doNothing";
    public static final String REPLICATION_METHOD_GET_METRIC_FROM_JMX                   = "getMetricFromJmx";
    public static final String REPLICATION_METHOD_GET_IN_SYNC                           = "getMetricInSync";
    public static final String REPLICATION_METHOD_GET_METRIC_MESSAGES                   = "getMetricMessages";

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Autowired
    private PartitionService partitionService;

    @Override
    protected List<String> listMetricPOFields(){
        return BeanUtil.listBeanFields(ReplicationMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler(){
        registerVCHandler( REPLICATION_METHOD_DO_NOTHING,                       this::doNothing);
        registerVCHandler( REPLICATION_METHOD_GET_METRIC_FROM_JMX,              this::getMetricFromJmx);
        registerVCHandler( REPLICATION_METHOD_GET_METRIC_MESSAGES,              this::getMetricMessages);
        registerVCHandler( REPLICATION_METHOD_GET_IN_SYNC,                      this::getMetricInSync);
    }

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return METRIC_REPLICATION;
    }

    @Override
    public Result<ReplicationMetrics> collectReplicaMetricsFromKafka(Long clusterId, String topicName, Integer partitionId, Integer brokerId, List<String> metricNameList) {
        ReplicationMetrics metrics = new ReplicationMetrics(clusterId, topicName, brokerId, partitionId);
        for (String metricName: metricNameList) {
            try {
                if (metrics.getMetrics().containsKey(metricName)) {
                    continue;
                }

                Result<ReplicationMetrics> ret = this.collectReplicaMetricsFromKafka(
                        clusterId,
                        metrics.getTopic(),
                        metrics.getPartitionId(),
                        metrics.getBrokerId(),
                        metricName
                );

                if (null == ret || ret.failed() || null == ret.getData()) {
                    continue;
                }

                metrics.putMetric(ret.getData().getMetrics());
            } catch (Exception e) {
                LOGGER.error(
                        "method=collectReplicaMetricsFromKafka||clusterPhyId={}||topicName={}||partition={}||brokerId={}||metricName={}||errMsg=exception!",
                        clusterId, topicName, partitionId, brokerId, e
                );
            }
        }

        return Result.buildSuc(metrics);
    }

    @Override
    public Result<ReplicationMetrics> collectReplicaMetricsFromKafka(Long clusterId, String topic, Integer partitionId, Integer brokerId, String metric){
        try {
            ReplicationMetricParam metricParam = new ReplicationMetricParam(clusterId, topic, brokerId, partitionId, metric);
            return (Result<ReplicationMetrics>)doVCHandler(clusterId, metric, metricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    /**************************************************** private method ****************************************************/
    private Result<ReplicationMetrics> doNothing(VersionItemParam param) {
        ReplicationMetricParam metricParam = (ReplicationMetricParam)param;

        return Result.buildSuc(new ReplicationMetrics(metricParam.getClusterPhyId(), metricParam.getTopic(), metricParam.getBrokerId(), metricParam.getPartitionId()));
    }

    private Result<ReplicationMetrics> getMetricInSync(VersionItemParam param) {
        ReplicationMetricParam metricParam = (ReplicationMetricParam)param;
        String      metric      = metricParam.getMetric();
        String      topic       = metricParam.getTopic();
        Long        clusterId   = metricParam.getClusterPhyId();
        Integer     brokerId    = metricParam.getBrokerId();
        Integer     partitionId = metricParam.getPartitionId();

        Partition partition = partitionService.getPartitionFromCacheFirst(clusterId, topic, partitionId);
        if(null == partition){
            return Result.buildFail();
        }

        List<Integer> inSyncReplicaList = partition.getInSyncReplicaList();
        Float metricValue = inSyncReplicaList.contains(brokerId) ? 1f : 0f;

        ReplicationMetrics replicationMetrics = new ReplicationMetrics(clusterId, topic, brokerId, partitionId);
        replicationMetrics.putMetric(metric, metricValue);

        return Result.buildSuc(replicationMetrics);
    }

    private Result<ReplicationMetrics> getMetricMessages(VersionItemParam param) {
        ReplicationMetricParam metricParam = (ReplicationMetricParam)param;
        String      metric      = metricParam.getMetric();
        String      topic       = metricParam.getTopic();
        Long        clusterId   = metricParam.getClusterPhyId();
        Integer     brokerId    = metricParam.getBrokerId();
        Integer     partitionId = metricParam.getPartitionId();

        Result<ReplicationMetrics> endRet   = this.collectReplicaMetricsFromKafka(clusterId, topic, partitionId, brokerId, REPLICATION_METRIC_LOG_END_OFFSET);
        Result<ReplicationMetrics> startRet = this.collectReplicaMetricsFromKafka(clusterId, topic, partitionId, brokerId, REPLICATION_METRIC_LOG_START_OFFSET);

        ReplicationMetrics replicationMetrics = new ReplicationMetrics(clusterId, topic, brokerId, partitionId);
        if(null != endRet && endRet.successful() && null != startRet && startRet.successful()){
            Float endOffset   = endRet.getData().getMetrics().get(REPLICATION_METRIC_LOG_END_OFFSET);
            Float startOffset = startRet.getData().getMetrics().get(REPLICATION_METRIC_LOG_START_OFFSET);

            replicationMetrics.putMetric(metric, endOffset - startOffset);
            replicationMetrics.putMetric(REPLICATION_METRIC_LOG_END_OFFSET, endOffset);
            replicationMetrics.putMetric(REPLICATION_METRIC_LOG_START_OFFSET, startOffset);
        }

        return Result.buildSuc(replicationMetrics);
    }

    private Result<ReplicationMetrics> getMetricFromJmx(VersionItemParam param) {
        ReplicationMetricParam metricParam = (ReplicationMetricParam)param;
        String      metric      = metricParam.getMetric();
        String      topic       = metricParam.getTopic();
        Long        clusterId   = metricParam.getClusterPhyId();
        Integer     brokerId    = metricParam.getBrokerId();
        Integer     partitionId = metricParam.getPartitionId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = getJMXInfo(clusterId, metric);
        if(null == jmxInfo){return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);}

        //2、获取jmx连接
        JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)){
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }

        try {
            //3、获取jmx指标
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName() + ",topic=" + topic + ",partition=" + partitionId),
                    jmxInfo.getJmxAttribute()).toString();

            ReplicationMetrics metrics = new ReplicationMetrics(clusterId, topic, brokerId, partitionId);
            metrics.putMetric(metric, Float.valueOf(value));

            return Result.buildSuc(metrics);
        } catch (InstanceNotFoundException e) {
            return Result.buildFailure(VC_JMX_INSTANCE_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("getMetricFromJmx||cluster={}||brokerId={}||metrics={}||jmx={}||msg={}",
                    clusterId, brokerId, metric, jmxInfo.getJmxObjectName(), e.getClass().getName());
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }
}
