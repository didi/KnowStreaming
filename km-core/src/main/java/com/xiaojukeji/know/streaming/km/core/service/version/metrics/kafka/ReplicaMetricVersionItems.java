package com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.CATEGORY_PERFORMANCE;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_REPLICATION;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.*;
import static com.xiaojukeji.know.streaming.km.core.service.replica.impl.ReplicaMetricServiceImpl.*;

@Component
public class ReplicaMetricVersionItems extends BaseMetricVersionMetric {

    public static final String REPLICATION_METRIC_LOG_END_OFFSET                = "LogEndOffset";
    public static final String REPLICATION_METRIC_LOG_START_OFFSET              = "LogStartOffset";
    public static final String REPLICATION_METRIC_MESSAGES                      = "Messages";
    public static final String REPLICATION_METRIC_LOG_SIZE                      = "LogSize";
    public static final String REPLICATION_METRIC_IN_SYNC                       = "InSync";
    public static final String REPLICATION_METRIC_COLLECT_COST_TIME             = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;

    public ReplicaMetricVersionItems(){}

    @Override
    public int versionItemType() {
        return METRIC_REPLICATION.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> itemList = new ArrayList<>();

        // LogEndOffset 指标
        itemList.add(buildAllVersionsItem()
                .name(REPLICATION_METRIC_LOG_END_OFFSET).unit("").desc("副本的LogEndOffset")
                .extend(buildJMXMethodExtend(REPLICATION_METHOD_GET_METRIC_FROM_JMX )
                        .jmxObjectName( JMX_LOG_LOG_END_OFFSET ).jmxAttribute(VALUE)));

        // LogStartOffset 指标
        itemList.add(buildAllVersionsItem()
                .name( REPLICATION_METRIC_LOG_START_OFFSET ).unit("").desc("副本的LogStartOffset")
                .extend(buildJMXMethodExtend(REPLICATION_METHOD_GET_METRIC_FROM_JMX )
                        .jmxObjectName( JMX_LOG_LOG_START_OFFSET ).jmxAttribute(VALUE)));

        // Messages 指标
        itemList.add(buildAllVersionsItem()
                .name( REPLICATION_METRIC_MESSAGES ).unit("条").desc("副本的总消息条数")
                .extendMethod( REPLICATION_METHOD_GET_METRIC_MESSAGES ));

        // LogSize 指标
        itemList.add( buildAllVersionsItem()
                .name(REPLICATION_METRIC_LOG_SIZE).unit("byte").desc("副本的容量大小")
                .extend(buildJMXMethodExtend(REPLICATION_METHOD_GET_METRIC_FROM_JMX )
                        .jmxObjectName( JMX_LOG_LOG_SIZE ).jmxAttribute(VALUE)));

        // InSync 指标
        itemList.add( buildAllVersionsItem()
                .name(REPLICATION_METRIC_IN_SYNC).unit("是/否").desc("副本处于ISR中")
                .extendMethod( REPLICATION_METHOD_GET_IN_SYNC ));

        itemList.add(buildAllVersionsItem()
                .name(REPLICATION_METRIC_COLLECT_COST_TIME).unit("秒").desc("采集Replica指标的耗时").category(CATEGORY_PERFORMANCE)
                .extendMethod(REPLICATION_METHOD_DO_NOTHING));

        return itemList;
    }
}
