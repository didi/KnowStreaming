package com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.*;
import static com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum.SINK;
import static com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum.SOURCE;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_CONNECTOR;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.*;
import static com.xiaojukeji.know.streaming.km.core.service.connect.connector.impl.ConnectorMetricServiceImpl.*;


@Component
public class ConnectorMetricVersionItems extends BaseMetricVersionMetric {

    public static final String CONNECTOR_METRIC_COLLECT_COST_TIME                    = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;

    public static final String CONNECTOR_METRIC_HEALTH_STATE                         = "HealthState";

    public static final String CONNECTOR_METRIC_RUNNING_STATUS                       = "RunningStatus";

    public static final String CONNECTOR_METRIC_CONNECTOR_TOTAL_TASK_COUNT           = "ConnectorTotalTaskCount";

    public static final String CONNECTOR_METRIC_HEALTH_CHECK_PASSED                  = "HealthCheckPassed";

    public static final String CONNECTOR_METRIC_HEALTH_CHECK_TOTAL                   = "HealthCheckTotal";

    public static final String CONNECTOR_METRIC_CONNECTOR_RUNNING_TASK_COUNT         = "ConnectorRunningTaskCount";

    public static final String CONNECTOR_METRIC_CONNECTOR_PAUSED_TASK_COUNT          = "ConnectorPausedTaskCount";

    public static final String CONNECTOR_METRIC_CONNECTOR_FAILED_TASK_COUNT          = "ConnectorFailedTaskCount";

    public static final String CONNECTOR_METRIC_CONNECTOR_UNASSIGNED_TASK_COUNT      = "ConnectorUnassignedTaskCount";

    public static final String CONNECTOR_METRIC_BATCH_SIZE_AVG                       = "BatchSizeAvg";

    public static final String CONNECTOR_METRIC_BATCH_SIZE_MAX                       = "BatchSizeMax";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_AVG_TIME_MS            = "OffsetCommitAvgTimeMs";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_MAX_TIME_MS            = "OffsetCommitMaxTimeMs";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_FAILURE_PERCENTAGE     = "OffsetCommitFailurePercentage";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_SUCCESS_PERCENTAGE     = "OffsetCommitSuccessPercentage";

    public static final String CONNECTOR_METRIC_POLL_BATCH_AVG_TIME_MS               = "PollBatchAvgTimeMs";

    public static final String CONNECTOR_METRIC_POLL_BATCH_MAX_TIME_MS               = "PollBatchMaxTimeMs";

    public static final String CONNECTOR_METRIC_SOURCE_RECORD_ACTIVE_COUNT           = "SourceRecordActiveCount";

    public static final String CONNECTOR_METRIC_SOURCE_RECORD_ACTIVE_COUNT_AVG       = "SourceRecordActiveCountAvg";

    public static final String CONNECTOR_METRIC_SOURCE_RECORD_ACTIVE_COUNT_MAX       = "SourceRecordActiveCountMax";

    public static final String CONNECTOR_METRIC_SOURCE_RECORD_POLL_RATE              = "SourceRecordPollRate";

    public static final String CONNECTOR_METRIC_SOURCE_RECORD_POLL_TOTAL             = "SourceRecordPollTotal";

    public static final String CONNECTOR_METRIC_SOURCE_RECORD_WRITE_RATE             = "SourceRecordWriteRate";

    public static final String CONNECTOR_METRIC_SOURCE_RECORD_WRITE_TOTAL            = "SourceRecordWriteTotal";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_COMPLETION_RATE        = "OffsetCommitCompletionRate";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_COMPLETION_TOTAL       = "OffsetCommitCompletionTotal";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_SKIP_RATE              = "OffsetCommitSkipRate";

    public static final String CONNECTOR_METRIC_OFFSET_COMMIT_SKIP_TOTAL             = "OffsetCommitSkipTotal";

    public static final String CONNECTOR_METRIC_PARTITION_COUNT                      = "PartitionCount";

    public static final String CONNECTOR_METRIC_PUT_BATCH_AVG_TIME_MS                = "PutBatchAvgTimeMs";

    public static final String CONNECTOR_METRIC_PUT_BATCH_MAX_TIME_MS                = "PutBatchMaxTimeMs";

    public static final String CONNECTOR_METRIC_SINK_RECORD_ACTIVE_COUNT             = "SinkRecordActiveCount";

    public static final String CONNECTOR_METRIC_SINK_RECORD_ACTIVE_COUNT_AVG         = "SinkRecordActiveCountAvg";

    public static final String CONNECTOR_METRIC_SINK_RECORD_ACTIVE_COUNT_MAX         = "SinkRecordActiveCountMax";

    public static final String CONNECTOR_METRIC_SINK_RECORD_LAG_MAX                  = "SinkRecordLagMax";

    public static final String CONNECTOR_METRIC_SINK_RECORD_READ_RATE                = "SinkRecordReadRate";

    public static final String CONNECTOR_METRIC_SINK_RECORD_READ_TOTAL               = "SinkRecordReadTotal";

    public static final String CONNECTOR_METRIC_SINK_RECORD_SEND_RATE                = "SinkRecordSendRate";

    public static final String CONNECTOR_METRIC_SINK_RECORD_SEND_TOTAL               = "SinkRecordSendTotal";

    public static final String CONNECTOR_METRIC_DEADLETTERQUEUE_PRODUCE_FAILURES     = "DeadletterqueueProduceFailures";

    public static final String CONNECTOR_METRIC_DEADLETTERQUEUE_PRODUCE_REQUESTS     = "DeadletterqueueProduceRequests";

    public static final String CONNECTOR_METRIC_LAST_ERROR_TIMESTAMP                 = "LastErrorTimestamp";

    public static final String CONNECTOR_METRIC_TOTAL_ERRORS_LOGGED                  = "TotalErrorsLogged";

    public static final String CONNECTOR_METRIC_TOTAL_RECORD_ERRORS                  = "TotalRecordErrors";

    public static final String CONNECTOR_METRIC_TOTAL_RECORD_FAILURES                = "TotalRecordFailures";

    public static final String CONNECTOR_METRIC_TOTAL_RECORDS_SKIPPED                = "TotalRecordsSkipped";

    public static final String CONNECTOR_METRIC_TOTAL_RETRIES                        = "TotalRetries";

    @Override
    public int versionItemType() {
        return METRIC_CONNECT_CONNECTOR.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init() {
        List<VersionMetricControlItem> items = new ArrayList<>();
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_COLLECT_COST_TIME).unit("秒").desc("采集connector指标的耗时").category(CATEGORY_PERFORMANCE)
                .extendMethod(CONNECTOR_METHOD_DO_NOTHING));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_HEALTH_STATE).unit("0:好 1:中 2:差 3:宕机").desc("健康状态(0:好 1:中 2:差 3:宕机)").category(CATEGORY_HEALTH)
                .extendMethod(CONNECTOR_METHOD_GET_METRIC_HEALTH_SCORE));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_RUNNING_STATUS).unit("0:UNASSIGNED 1:RUNNING 2:PAUSED 3:FAILED 4:DESTROYED -1:UNKNOWN").desc("运行状态(0:UNASSIGNED 1:RUNNING 2:PAUSED 3:FAILED 4:DESTROYED -1:UNKNOWN)").category(CATEGORY_PERFORMANCE)
                .extendMethod(CONNECTOR_METHOD_GET_METRIC_RUNNING_STATUS));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_HEALTH_CHECK_PASSED).unit("个").desc("健康项检查通过数").category(CATEGORY_HEALTH)
                .extendMethod(CONNECTOR_METHOD_GET_METRIC_HEALTH_SCORE));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_HEALTH_CHECK_TOTAL).unit("个").desc("健康项检查总数").category(CATEGORY_HEALTH)
                .extendMethod(CONNECTOR_METHOD_GET_METRIC_HEALTH_SCORE));

        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_CONNECTOR_TOTAL_TASK_COUNT).unit("个").desc("所有任务数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECT_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_CONNECTOR_METRIC).jmxAttribute(CONNECTOR_TOTAL_TASK_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_CONNECTOR_RUNNING_TASK_COUNT).unit("个").desc("运行状态的任务数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECT_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_CONNECTOR_METRIC).jmxAttribute(CONNECTOR_RUNNING_TASK_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_CONNECTOR_PAUSED_TASK_COUNT).unit("个").desc("暂停状态的任务数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECT_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_CONNECTOR_METRIC).jmxAttribute(CONNECTOR_PAUSED_TASK_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_CONNECTOR_FAILED_TASK_COUNT).unit("个").desc("失败状态的任务数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECT_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_CONNECTOR_METRIC).jmxAttribute(CONNECTOR_FAILED_TASK_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_CONNECTOR_UNASSIGNED_TASK_COUNT).unit("个").desc("未被分配的任务数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECT_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_CONNECTOR_METRIC).jmxAttribute(CONNECTOR_UNASSIGNED_TASK_COUNT)));


        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_BATCH_SIZE_AVG).unit("条").desc("批次数量平均值").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG)
                        .jmxObjectName(JMX_CONNECTOR_TASK_CONNECTOR_METRIC).jmxAttribute(BATCH_SIZE_AVG)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_BATCH_SIZE_MAX).unit("条").desc("批次数量最大值").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_MAX)
                        .jmxObjectName(JMX_CONNECTOR_TASK_CONNECTOR_METRIC).jmxAttribute(BATCH_SIZE_MAX)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_AVG_TIME_MS).unit("ms").desc("位点提交平均耗时").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG)
                        .jmxObjectName(JMX_CONNECTOR_TASK_CONNECTOR_METRIC).jmxAttribute(OFFSET_COMMIT_AVG_TIME_MS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_MAX_TIME_MS).unit("ms").desc("位点提交最大耗时").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_MAX)
                        .jmxObjectName(JMX_CONNECTOR_TASK_CONNECTOR_METRIC).jmxAttribute(OFFSET_COMMIT_MAX_TIME_MS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_FAILURE_PERCENTAGE).unit("%").desc("位点提交失败概率").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG)
                        .jmxObjectName(JMX_CONNECTOR_TASK_CONNECTOR_METRIC).jmxAttribute(OFFSET_COMMIT_FAILURE_PERCENTAGE)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_SUCCESS_PERCENTAGE).unit("%").desc("位点提交成功概率").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG)
                        .jmxObjectName(JMX_CONNECTOR_TASK_CONNECTOR_METRIC).jmxAttribute(OFFSET_COMMIT_SUCCESS_PERCENTAGE)));


        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_POLL_BATCH_AVG_TIME_MS).unit("ms").desc("POLL平均耗时").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(POLL_BATCH_AVG_TIME_MS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_POLL_BATCH_MAX_TIME_MS).unit("ms").desc("POLL最大耗时").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_MAX, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(POLL_BATCH_MAX_TIME_MS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SOURCE_RECORD_ACTIVE_COUNT).unit("条").desc("pending状态消息数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(SOURCE_RECORD_ACTIVE_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SOURCE_RECORD_ACTIVE_COUNT_AVG).unit("条").desc("pending状态平均消息数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(SOURCE_RECORD_ACTIVE_COUNT_AVG)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SOURCE_RECORD_ACTIVE_COUNT_MAX).unit("条").desc("pending状态最大消息数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(SOURCE_RECORD_ACTIVE_COUNT_MAX)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SOURCE_RECORD_POLL_RATE).unit(BYTE_PER_SEC).desc("消息读取速率").category(CATEGORY_FLOW)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(SOURCE_RECORD_POLL_RATE)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SOURCE_RECORD_POLL_TOTAL).unit("条").desc("消息读取总数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(SOURCE_RECORD_POLL_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SOURCE_RECORD_WRITE_RATE).unit(BYTE_PER_SEC).desc("消息写入速率").category(CATEGORY_FLOW)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(SOURCE_RECORD_WRITE_RATE)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SOURCE_RECORD_WRITE_TOTAL).unit("条").desc("消息写入总数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SOURCE)
                        .jmxObjectName(JMX_CONNECTOR_SOURCE_TASK_METRICS).jmxAttribute(SOURCE_RECORD_WRITE_TOTAL)));

        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_COMPLETION_RATE).unit(BYTE_PER_SEC).desc("成功的位点提交速率").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(OFFSET_COMMIT_COMPLETION_RATE)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_COMPLETION_TOTAL).unit("个").desc("成功的位点提交总数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(OFFSET_COMMIT_COMPLETION_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_SKIP_RATE).unit("").desc("被跳过的位点提交速率").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(OFFSET_COMMIT_SKIP_RATE)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_OFFSET_COMMIT_SKIP_TOTAL).unit("").desc("被跳过的位点提交总数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(OFFSET_COMMIT_SKIP_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_PARTITION_COUNT).unit("个").desc("被分配到的分区数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(PARTITION_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_PUT_BATCH_AVG_TIME_MS).unit("ms").desc("PUT平均耗时").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(PUT_BATCH_AVG_TIME_MS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_PUT_BATCH_MAX_TIME_MS).unit("ms").desc("PUT最大耗时").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_MAX, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(PUT_BATCH_MAX_TIME_MS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SINK_RECORD_ACTIVE_COUNT).unit("条").desc("pending状态消息数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(SINK_RECORD_ACTIVE_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SINK_RECORD_ACTIVE_COUNT_AVG).unit("条").desc("pending状态平均消息数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(SINK_RECORD_ACTIVE_COUNT_AVG)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SINK_RECORD_ACTIVE_COUNT_MAX).unit("条").desc("pending状态最大消息数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(SINK_RECORD_ACTIVE_COUNT_MAX)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SINK_RECORD_READ_RATE).unit(BYTE_PER_SEC).desc("消息读取速率").category(CATEGORY_FLOW)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(SINK_RECORD_READ_RATE)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SINK_RECORD_READ_TOTAL).unit("条").desc("消息读取总数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(SINK_RECORD_READ_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SINK_RECORD_SEND_RATE).unit(BYTE_PER_SEC).desc("消息写入速率").category(CATEGORY_FLOW)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(SINK_RECORD_SEND_RATE)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_SINK_RECORD_SEND_TOTAL).unit("条").desc("消息写入总数量").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM, SINK)
                        .jmxObjectName(JMX_CONNECTOR_SINK_TASK_METRICS).jmxAttribute(SINK_RECORD_SEND_TOTAL)));


        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_DEADLETTERQUEUE_PRODUCE_FAILURES).unit("次").desc("死信队列写入失败数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(DEADLETTERQUEUE_PRODUCE_FAILURES)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_DEADLETTERQUEUE_PRODUCE_REQUESTS).unit("次").desc("死信队列写入数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(DEADLETTERQUEUE_PRODUCE_REQUESTS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_LAST_ERROR_TIMESTAMP).unit("").desc("最后一次错误时间").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_MAX)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(LAST_ERROR_TIMESTAMP)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_TOTAL_ERRORS_LOGGED).unit("条").desc("记录日志的错误消息数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(TOTAL_ERRORS_LOGGED)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_TOTAL_RECORD_ERRORS).unit("次").desc("消息处理错误的次数(异常消息数量)").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(TOTAL_RECORD_ERRORS)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_TOTAL_RECORD_FAILURES).unit("次").desc("消息处理失败的次数（每次retry处理失败都会+1)").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(TOTAL_RECORD_FAILURES)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_TOTAL_RECORDS_SKIPPED).unit("条").desc("因为失败导致跳过（未处理）的消息数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(TOTAL_RECORDS_SKIPPED)));
        items.add(buildAllVersionsItem()
                .name(CONNECTOR_METRIC_TOTAL_RETRIES).unit("次").desc("失败重试的次数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM)
                        .jmxObjectName(JMX_CONNECTOR_TASK_ERROR_METRICS).jmxAttribute(TOTAL_RETRIES)));
        return items;
    }
}

