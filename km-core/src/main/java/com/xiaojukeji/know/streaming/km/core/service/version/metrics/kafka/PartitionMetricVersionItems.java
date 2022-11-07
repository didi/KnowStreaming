package com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_PARTITION;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.RATE_MIN_1;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.VALUE;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.*;
import static com.xiaojukeji.know.streaming.km.core.service.partition.impl.PartitionMetricServiceImpl.*;

/**
 * @author didi
 */
@Component
public class PartitionMetricVersionItems extends BaseMetricVersionMetric {

    public static final String PARTITION_METRIC_LOG_END_OFFSET      = "LogEndOffset";
    public static final String PARTITION_METRIC_LOG_START_OFFSET    = "LogStartOffset";
    public static final String PARTITION_METRIC_MESSAGES            = "Messages";

    public static final String PARTITION_METRIC_BYTES_IN            = "BytesIn";
    public static final String PARTITION_METRIC_BYTES_OUT           = "BytesOut";
    public static final String PARTITION_METRIC_LOG_SIZE            = "LogSize";

    @Override
    public int versionItemType() {
        return METRIC_PARTITION.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> itemList = new ArrayList<>();

        // LogEndOffset 指标
        itemList.add( buildAllVersionsItem()
                .name(PARTITION_METRIC_LOG_END_OFFSET).unit("").desc("Partition中Leader副本的LogEndOffset")
                .extendMethod(PARTITION_METHOD_GET_OFFSET_RELEVANT_METRICS));

        // LogStartOffset 指标
        itemList.add( buildAllVersionsItem()
                .name(PARTITION_METRIC_LOG_START_OFFSET).unit("").desc("Partition中Leader副本的LogStartOffset")
                .extendMethod(PARTITION_METHOD_GET_OFFSET_RELEVANT_METRICS));

        // Messages
        itemList.add( buildAllVersionsItem()
                .name(PARTITION_METRIC_MESSAGES).unit("条").desc("Partition中Leader副本的消息条数")
                .extendMethod(PARTITION_METHOD_GET_OFFSET_RELEVANT_METRICS));

        // BytesIn
        itemList.add( buildAllVersionsItem()
                .name(PARTITION_METRIC_BYTES_IN).unit(BYTE_PER_SEC).desc("Partition的BytesIn")
                .extend( buildJMXMethodExtend(PARTITION_METHOD_GET_TOPIC_AVG_METRIC_MESSAGES)
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_1) ));

        // BytesOut
        itemList.add( buildAllVersionsItem()
                .name(PARTITION_METRIC_BYTES_OUT).unit(BYTE_PER_SEC).desc("Partition的BytesOut")
                .extend( buildJMXMethodExtend(PARTITION_METHOD_GET_TOPIC_AVG_METRIC_MESSAGES)
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_1) ));

        // LogSize
        itemList.add( buildAllVersionsItem()
                .name(PARTITION_METRIC_LOG_SIZE).unit("byte").desc("Partition的LogSize")
                .extend( buildJMXMethodExtend(PARTITION_METHOD_GET_METRIC_FROM_JMX)
                        .jmxObjectName( JMX_LOG_LOG_SIZE ).jmxAttribute(VALUE) ));
        return itemList;
    }
}
