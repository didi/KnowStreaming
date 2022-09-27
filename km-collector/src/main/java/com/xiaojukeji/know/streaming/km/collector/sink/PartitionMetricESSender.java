package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.PartitionMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.PartitionMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.common.constant.ESIndexConstant.PARTITION_INDEX;

@Component
public class PartitionMetricESSender extends AbstractMetricESSender implements ApplicationListener<PartitionMetricEvent> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @PostConstruct
    public void init(){
        LOGGER.info("class=PartitionMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(PartitionMetricEvent event) {
        send2es(PARTITION_INDEX, ConvertUtil.list2List(event.getPartitionMetrics(), PartitionMetricPO.class));
    }
}
