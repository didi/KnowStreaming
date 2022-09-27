package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.ClusterMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ClusterMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


import static com.xiaojukeji.know.streaming.km.common.constant.ESIndexConstant.CLUSTER_INDEX;

@Component
public class ClusterMetricESSender extends AbstractMetricESSender implements ApplicationListener<ClusterMetricEvent> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @PostConstruct
    public void init(){
        LOGGER.info("class=ClusterMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(ClusterMetricEvent event) {
        send2es(CLUSTER_INDEX, ConvertUtil.list2List(event.getClusterMetrics(), ClusterMetricPO.class));
    }
}
