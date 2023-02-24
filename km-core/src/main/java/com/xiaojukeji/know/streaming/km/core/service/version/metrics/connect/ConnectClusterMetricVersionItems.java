package com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.CATEGORY_CLUSTER;
import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.CATEGORY_PERFORMANCE;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_CLUSTER;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.JMX_CONNECT_WORKER_METRIC;
import static com.xiaojukeji.know.streaming.km.core.service.connect.cluster.impl.ConnectClusterMetricServiceImpl.*;


@Component
public class ConnectClusterMetricVersionItems extends BaseMetricVersionMetric {
    public static final String CONNECT_CLUSTER_METRIC_CONNECTOR_COUNT                            = "ConnectorCount";
    public static final String CONNECT_CLUSTER_METRIC_TASK_COUNT                                 = "TaskCount";

    public static final String CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_ATTEMPTS_TOTAL           = "ConnectorStartupAttemptsTotal";

    public static final String CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_PERCENTAGE       = "ConnectorStartupFailurePercentage";

    public static final String CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_TOTAL            = "ConnectorStartupFailureTotal";

    public static final String CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_SUCCESS_PERCENTAGE       = "ConnectorStartupSuccessPercentage";

    public static final String CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_SUCCESS_TOTAL            = "ConnectorStartupSuccessTotal";

    public static final String CONNECT_CLUSTER_METRIC_TASK_STARTUP_ATTEMPTS_TOTAL                = "TaskStartupAttemptsTotal";

    public static final String CONNECT_CLUSTER_METRIC_TASK_STARTUP_FAILURE_PERCENTAGE            = "TaskStartupFailurePercentage";

    public static final String CONNECT_CLUSTER_METRIC_TASK_STARTUP_FAILURE_TOTAL                 = "TaskStartupFailureTotal";

    public static final String CONNECT_CLUSTER_METRIC_TASK_STARTUP_SUCCESS_PERCENTAGE            = "TaskStartupSuccessPercentage";

    public static final String CONNECT_CLUSTER_METRIC_TASK_STARTUP_SUCCESS_TOTAL                 = "TaskStartupSuccessTotal";

    public static final String CONNECT_CLUSTER_METRIC_COLLECT_COST_TIME                          = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;


    @Override
    public int versionItemType() {
        return METRIC_CONNECT_CLUSTER.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init() {
        List<VersionMetricControlItem> items = new ArrayList<>();
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_CONNECTOR_COUNT).unit("个").desc("连接器数量").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(TASK_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_TASK_COUNT).unit("个").desc("任务数量").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(TASK_COUNT)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_ATTEMPTS_TOTAL).unit("次").desc("连接器启动次数").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(CONNECTOR_STARTUP_ATTEMPTS_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_PERCENTAGE).unit("%").desc("连接器启动失败概率").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_AVG)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(CONNECTOR_STARTUP_FAILURE_PERCENTAGE)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_TOTAL).unit("次").desc("连接器启动失败次数").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(CONNECTOR_STARTUP_FAILURE_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_SUCCESS_PERCENTAGE).unit("%").desc("连接器启动成功概率").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_AVG)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(CONNECTOR_STARTUP_SUCCESS_PERCENTAGE)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_SUCCESS_TOTAL).unit("次").desc("连接器启动成功次数").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(CONNECTOR_STARTUP_SUCCESS_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_TASK_STARTUP_ATTEMPTS_TOTAL).unit("次").desc("任务启动次数").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(TASK_STARTUP_ATTEMPTS_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_TASK_STARTUP_FAILURE_PERCENTAGE).unit("%").desc("任务启动失败概率").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_AVG)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(TASK_STARTUP_FAILURE_PERCENTAGE)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_TASK_STARTUP_FAILURE_TOTAL).unit("次").desc("任务启动失败次数").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(TASK_STARTUP_FAILURE_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_TASK_STARTUP_SUCCESS_PERCENTAGE).unit("%").desc("任务启动成功概率").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_AVG)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(TASK_STARTUP_SUCCESS_PERCENTAGE)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_TASK_STARTUP_SUCCESS_TOTAL).unit("次").desc("任务启动成功次数").category(CATEGORY_CLUSTER)
                .extend(buildConnectJMXMethodExtend(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM)
                        .jmxObjectName(JMX_CONNECT_WORKER_METRIC).jmxAttribute(TASK_STARTUP_SUCCESS_TOTAL)));
        items.add(buildAllVersionsItem()
                .name(CONNECT_CLUSTER_METRIC_COLLECT_COST_TIME).unit("秒").desc("采集connect集群指标耗时").category(CATEGORY_PERFORMANCE)
                .extendMethod(CONNECT_CLUSTER_METHOD_DO_NOTHING));
        return items;
    }

}

