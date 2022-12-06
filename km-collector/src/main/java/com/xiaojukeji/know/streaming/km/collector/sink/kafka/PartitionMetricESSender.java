package com.xiaojukeji.know.streaming.km.collector.sink.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.sink.AbstractMetricESSender;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.PartitionMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.PartitionMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.PARTITION_INDEX;

@Component
public class PartitionMetricESSender extends AbstractMetricESSender implements ApplicationListener<PartitionMetricEvent> {
    private static final ILog  LOGGER = LogFactory.getLog(PartitionMetricESSender.class);

    @PostConstruct
    public void init(){
        LOGGER.info("method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(PartitionMetricEvent event) {
        send2es(PARTITION_INDEX, ConvertUtil.list2List(event.getPartitionMetrics(), PartitionMetricPO.class));
    }
}
