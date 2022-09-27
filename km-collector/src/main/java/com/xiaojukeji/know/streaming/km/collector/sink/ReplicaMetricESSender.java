package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.ReplicaMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ReplicationMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.common.constant.ESIndexConstant.REPLICATION_INDEX;

@Component
public class ReplicaMetricESSender extends AbstractMetricESSender implements ApplicationListener<ReplicaMetricEvent> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @PostConstruct
    public void init(){
        LOGGER.info("class=GroupMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(ReplicaMetricEvent event) {
        send2es(REPLICATION_INDEX, ConvertUtil.list2List(event.getReplicationMetrics(), ReplicationMetricPO.class));
    }
}
