package com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_MIRROR_MAKER;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.JMX_MIRROR_MAKER_SOURCE;
import static com.xiaojukeji.know.streaming.km.core.service.connect.mm2.impl.MirrorMakerMetricServiceImpl.*;

@Component
public class MirrorMakerMetricVersionItems extends BaseMetricVersionMetric {

    public static final String MIRROR_MAKER_METRIC_COLLECT_COST_TIME            = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;

    public static final String MIRROR_MAKER_METRIC_HEALTH_STATE                 = "HealthState";

    public static final String MIRROR_MAKER_METRIC_HEALTH_CHECK_PASSED          = "HealthCheckPassed";

    public static final String MIRROR_MAKER_METRIC_HEALTH_CHECK_TOTAL           = "HealthCheckTotal";

    public static final String MIRROR_MAKER_METRIC_BYTE_COUNT                   = "ByteCount";

    public static final String MIRROR_MAKER_METRIC_BYTE_RATE                    = "ByteRate";

    public static final String MIRROR_MAKER_METRIC_RECORD_AGE_MS                = "RecordAgeMs";

    public static final String MIRROR_MAKER_METRIC_RECORD_AGE_MS_AVG            = "RecordAgeMsAvg";

    public static final String MIRROR_MAKER_METRIC_RECORD_AGE_MS_MAX            = "RecordAgeMsMax";

    public static final String MIRROR_MAKER_METRIC_RECORD_AGE_MS_MIN            = "RecordAgeMsMin";

    public static final String MIRROR_MAKER_METRIC_RECORD_COUNT                 = "RecordCount";

    public static final String MIRROR_MAKER_METRIC_RECORD_RATE                  = "RecordRate";

    public static final String MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS       = "ReplicationLatencyMs";

    public static final String MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_AVG   = "ReplicationLatencyMsAvg";

    public static final String MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_MAX   = "ReplicationLatencyMsMax";

    public static final String MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_MIN   = "ReplicationLatencyMsMin";

    @Override
    public int versionItemType() {
        return METRIC_CONNECT_MIRROR_MAKER.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init() {
        List<VersionMetricControlItem> items = new ArrayList<>();
        // HealthScore 指标
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_HEALTH_STATE).unit("0:好 1:中 2:差 3:宕机").desc("健康状态(0:好 1:中 2:差 3:宕机)").category(CATEGORY_HEALTH)
                .extendMethod(MIRROR_MAKER_METHOD_GET_HEALTH_SCORE));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_HEALTH_CHECK_PASSED).unit("个").desc("健康项检查通过数").category(CATEGORY_HEALTH)
                .extendMethod(MIRROR_MAKER_METHOD_GET_HEALTH_SCORE));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_HEALTH_CHECK_TOTAL).unit("个").desc("健康项检查总数").category(CATEGORY_HEALTH)
                .extendMethod(MIRROR_MAKER_METHOD_GET_HEALTH_SCORE));

        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_COLLECT_COST_TIME).unit("秒").desc("采集mirrorMaker指标的耗时").category(CATEGORY_PERFORMANCE)
                .extendMethod(MIRROR_MAKER_METHOD_DO_NOTHING));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_BYTE_COUNT).unit("byte").desc("消息复制流量大小").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_SUM)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(BYTE_COUNT)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_BYTE_RATE).unit(BYTE_PER_SEC).desc("复制流量速率").category(CATEGORY_FLOW)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_SUM)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(BYTE_RATE)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_RECORD_AGE_MS).unit("ms").desc("消息获取时年龄").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_AVG)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(RECORD_AGE_MS)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_RECORD_AGE_MS_AVG).unit("ms").desc("消息获取时平均年龄").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_AVG)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(RECORD_AGE_MS_AVG)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_RECORD_AGE_MS_MAX).unit("ms").desc("消息获取时最大年龄").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MAX)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(RECORD_AGE_MS_MAX)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_RECORD_AGE_MS_MIN).unit("ms").desc("消息获取时最小年龄").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MIN)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(RECORD_AGE_MS_MIN)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_RECORD_COUNT).unit("条").desc("消息复制条数").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_SUM)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(RECORD_COUNT)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_RECORD_RATE).unit("条/s").desc("消息复制速率").category(CATEGORY_FLOW)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_SUM)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(RECORD_RATE)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS).unit("ms").desc("消息复制延迟时间").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_AVG)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(REPLICATION_LATENCY_MS)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_AVG).unit("ms").desc("消息复制平均延迟时间").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_AVG)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(REPLICATION_LATENCY_MS_AVG)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_MAX).unit("ms").desc("消息复制最大延迟时间").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MAX)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(REPLICATION_LATENCY_MS_MAX)));
        items.add(buildAllVersionsItem()
                .name(MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_MIN).unit("ms").desc("消息复制最小延迟时间").category(CATEGORY_PERFORMANCE)
                .extend(buildConnectJMXMethodExtend(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MIN)
                        .jmxObjectName(JMX_MIRROR_MAKER_SOURCE).jmxAttribute(REPLICATION_LATENCY_MS_MIN)));
        return items;
    }
}

