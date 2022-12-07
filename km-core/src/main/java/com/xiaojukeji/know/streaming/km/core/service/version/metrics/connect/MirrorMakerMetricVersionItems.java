package com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_MIRROR_MAKER;

@Component
public class MirrorMakerMetricVersionItems extends BaseMetricVersionMetric {

    @Override
    public int versionItemType() {
        return METRIC_CONNECT_MIRROR_MAKER.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> items = new ArrayList<>();

        return items;
    }
}

