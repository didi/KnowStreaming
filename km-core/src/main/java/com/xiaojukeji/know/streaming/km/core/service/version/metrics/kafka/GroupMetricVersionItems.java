package com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMethodInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.CATEGORY_HEALTH;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_GROUP;
import static com.xiaojukeji.know.streaming.km.core.service.group.impl.GroupMetricServiceImpl.*;

@Component
public class GroupMetricVersionItems extends BaseMetricVersionMetric {

    public static final String GROUP_METRIC_HEALTH_STATE                  = "HealthState";
    public static final String GROUP_METRIC_HEALTH_CHECK_PASSED           = "HealthCheckPassed";
    public static final String GROUP_METRIC_HEALTH_CHECK_TOTAL            = "HealthCheckTotal";
    public static final String GROUP_METRIC_OFFSET_CONSUMED               = "OffsetConsumed";
    public static final String GROUP_METRIC_LOG_END_OFFSET                = "LogEndOffset";
    public static final String GROUP_METRIC_LAG                           = "Lag";
    public static final String GROUP_METRIC_STATE                         = "State";


    @Override
    public int versionItemType() {
        return METRIC_GROUP.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> itemList = new ArrayList<>();

        // HealthScore 指标
        itemList.add(buildAllVersionsItem()
                .name(GROUP_METRIC_HEALTH_STATE).unit("0:好 1:中 2:差 3:宕机").desc("健康状态(0:好 1:中 2:差 3:宕机)").category(CATEGORY_HEALTH)
                .extendMethod( GROUP_METHOD_GET_HEALTH_SCORE ));

        itemList.add(buildAllVersionsItem()
                .name(GROUP_METRIC_HEALTH_CHECK_PASSED ).unit("个").desc("健康检查通过数").category(CATEGORY_HEALTH)
                .extendMethod( GROUP_METHOD_GET_HEALTH_SCORE ));

        itemList.add(buildAllVersionsItem()
                .name(GROUP_METRIC_HEALTH_CHECK_TOTAL ).unit("个").desc("健康检查总数").category(CATEGORY_HEALTH)
                .extendMethod( GROUP_METHOD_GET_HEALTH_SCORE ));

        // OffsetConsumed 指标
        itemList.add( buildAllVersionsItem()
                .name(GROUP_METRIC_OFFSET_CONSUMED).unit("条").desc("Consumer的offset")
                .extend(new VersionMethodInfo()
                        .methodName( GROUP_METHOD_GET_LAG_RELEVANT_FROM_ADMIN_CLIENT )));

        // LogEndOffset 指标
        itemList.add( buildAllVersionsItem()
                .name(GROUP_METRIC_LOG_END_OFFSET).unit("条").desc("Group的LogEndOffset")
                .extendMethod(GROUP_METHOD_GET_LAG_RELEVANT_FROM_ADMIN_CLIENT));

        // Lag 指标
        itemList.add( buildAllVersionsItem()
                .name(GROUP_METRIC_LAG).unit("条").desc("Group消费者的Lag数")
                .extendMethod( GROUP_METHOD_GET_LAG_RELEVANT_FROM_ADMIN_CLIENT));

        // State 指标
        itemList.add(buildAllVersionsItem()
                .name(GROUP_METRIC_STATE ).unit("个").desc("Group组的状态")
                .extendMethod( GROUP_METHOD_GET_STATE ));

        return itemList;
    }
}
