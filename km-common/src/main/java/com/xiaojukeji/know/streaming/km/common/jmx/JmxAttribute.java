package com.xiaojukeji.know.streaming.km.common.jmx;

/**
 * @author zhongyuankai
 * @date 20/4/13
 */
public class JmxAttribute {

    public static final String RATE_MIN_1           = "OneMinuteRate";

    public static final String RATE_MIN_5           = "FiveMinuteRate";

    public static final String RATE_MIN_15          = "FifteenMinuteRate";

    public static final String PERCENTILE_50        = "50thPercentile";

    public static final String PERCENTILE_75        = "75thPercentile";

    public static final String PERCENTILE_95        = "95thPercentile";

    public static final String PERCENTILE_98        = "98thPercentile";

    public static final String PERCENTILE_99        = "99thPercentile";

    public static final String MAX                  = "Max";

    public static final String MEAN                 = "Mean";

    public static final String MIN                  = "Min";

    public static final String VALUE                = "Value";

    public static final String CONNECTION_COUNT     = "connection-count";

    public static final String VERSION              = "Version";

    /*********************************************************** connect cluster***********************************************************/
    public static final String TASK_COUNT                                = "task-count";

    public static final String CONNECTOR_STARTUP_ATTEMPTS_TOTAL          = "connector-startup-attempts-total";

    public static final String CONNECTOR_STARTUP_FAILURE_PERCENTAGE      = "connector-startup-failure-percentage";

    public static final String CONNECTOR_STARTUP_FAILURE_TOTAL           = "connector-startup-failure-total";

    public static final String CONNECTOR_STARTUP_SUCCESS_PERCENTAGE      = "connector-startup-success-percentage";

    public static final String CONNECTOR_STARTUP_SUCCESS_TOTAL           = "connector-startup-success-total";

    public static final String TASK_STARTUP_ATTEMPTS_TOTAL               = "task-startup-attempts-total";

    public static final String TASK_STARTUP_FAILURE_PERCENTAGE           = "task-startup-failure-percentage";

    public static final String TASK_STARTUP_FAILURE_TOTAL                = "task-startup-failure-total";

    public static final String TASK_STARTUP_SUCCESS_PERCENTAGE           = "task-startup-success-percentage";

    public static final String TASK_STARTUP_SUCCESS_TOTAL                = "task-startup-success-total";

    /*********************************************************** connect ***********************************************************/
    public static final String CONNECTOR_TOTAL_TASK_COUNT                = "connector-total-task-count";

    public static final String CONNECTOR_RUNNING_TASK_COUNT              = "connector-running-task-count";

    public static final String CONNECTOR_PAUSED_TASK_COUNT               = "connector-paused-task-count";

    public static final String CONNECTOR_FAILED_TASK_COUNT               = "connector-failed-task-count";

    public static final String CONNECTOR_UNASSIGNED_TASK_COUNT           = "connector-unassigned-task-count";

    public static final String BATCH_SIZE_AVG                            = "batch-size-avg";

    public static final String BATCH_SIZE_MAX                            = "batch-size-max";

    public static final String OFFSET_COMMIT_AVG_TIME_MS                 = "offset-commit-avg-time-ms";

    public static final String OFFSET_COMMIT_MAX_TIME_MS                 = "offset-commit-max-time-ms";

    public static final String OFFSET_COMMIT_FAILURE_PERCENTAGE          = "offset-commit-failure-percentage";

    public static final String OFFSET_COMMIT_SUCCESS_PERCENTAGE          = "offset-commit-success-percentage";

    public static final String POLL_BATCH_AVG_TIME_MS                    = "poll-batch-avg-time-ms";

    public static final String POLL_BATCH_MAX_TIME_MS                    = "poll-batch-max-time-ms";

    public static final String SOURCE_RECORD_ACTIVE_COUNT                = "source-record-active-count";

    public static final String SOURCE_RECORD_ACTIVE_COUNT_AVG            = "source-record-active-count-avg";

    public static final String SOURCE_RECORD_ACTIVE_COUNT_MAX            = "source-record-active-count-max";

    public static final String SOURCE_RECORD_POLL_RATE                   = "source-record-poll-rate";

    public static final String SOURCE_RECORD_POLL_TOTAL                  = "source-record-poll-total";

    public static final String SOURCE_RECORD_WRITE_RATE                  = "source-record-write-rate";

    public static final String SOURCE_RECORD_WRITE_TOTAL                 = "source-record-write-total";

    public static final String OFFSET_COMMIT_COMPLETION_RATE             = "offset-commit-completion-rate";

    public static final String OFFSET_COMMIT_COMPLETION_TOTAL            = "offset-commit-completion-total";

    public static final String OFFSET_COMMIT_SKIP_RATE                   = "offset-commit-skip-rate";

    public static final String OFFSET_COMMIT_SKIP_TOTAL                  = "offset-commit-skip-total";

    public static final String PARTITION_COUNT                           = "partition-count";

    public static final String PUT_BATCH_AVG_TIME_MS                     = "put-batch-avg-time-ms";

    public static final String PUT_BATCH_MAX_TIME_MS                     = "put-batch-max-time-ms";

    public static final String SINK_RECORD_ACTIVE_COUNT                  = "sink-record-active-count";

    public static final String SINK_RECORD_ACTIVE_COUNT_AVG              = "sink-record-active-count-avg";

    public static final String SINK_RECORD_ACTIVE_COUNT_MAX              = "sink-record-active-count-max";

    public static final String SINK_RECORD_LAG_MAX                       = "sink-record-lag-max";

    public static final String SINK_RECORD_READ_RATE                     = "sink-record-read-rate";

    public static final String SINK_RECORD_READ_TOTAL                    = "sink-record-read-total";

    public static final String SINK_RECORD_SEND_RATE                     = "sink-record-send-rate";

    public static final String SINK_RECORD_SEND_TOTAL                    = "sink-record-send-total";

    public static final String DEADLETTERQUEUE_PRODUCE_FAILURES          = "deadletterqueue-produce-failures";

    public static final String DEADLETTERQUEUE_PRODUCE_REQUESTS          = "deadletterqueue-produce-requests";

    public static final String LAST_ERROR_TIMESTAMP                      = "last-error-timestamp";

    public static final String TOTAL_ERRORS_LOGGED                       = "total-errors-logged";

    public static final String TOTAL_RECORD_ERRORS                       = "total-record-errors";

    public static final String TOTAL_RECORD_FAILURES                     = "total-record-failures";

    public static final String TOTAL_RECORDS_SKIPPED                     = "total-records-skipped";

    public static final String TOTAL_RETRIES                             = "total-retries";

    /*********************************************************** mm2 ***********************************************************/

    public static final String BYTE_COUNT                   = "byte-count";

    public static final String BYTE_RATE                    = "byte-rate";

    public static final String RECORD_AGE_MS                = "record-age-ms";

    public static final String RECORD_AGE_MS_AVG            = "record-age-ms-avg";

    public static final String RECORD_AGE_MS_MAX            = "record-age-ms-max";

    public static final String RECORD_AGE_MS_MIN            = "record-age-ms-min";

    public static final String RECORD_COUNT                 = "record-count";

    public static final String RECORD_RATE                  = "record-rate";

    public static final String REPLICATION_LATENCY_MS       = "replication-latency-ms";

    public static final String REPLICATION_LATENCY_MS_AVG   = "replication-latency-ms-avg";

    public static final String REPLICATION_LATENCY_MS_MAX   = "replication-latency-ms-max";

    public static final String REPLICATION_LATENCY_MS_MIN   = "replication-latency-ms-min";

    private JmxAttribute() {
    }
}
