package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BrokerMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BrokerMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.common.constant.ESIndexConstant.BROKER_INDEX;

@Component
public class BrokerMetricESSender extends AbstractMetricESSender implements ApplicationListener<BrokerMetricEvent> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @PostConstruct
    public void init(){
        LOGGER.info("class=BrokerMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(BrokerMetricEvent event) {
        send2es(BROKER_INDEX, ConvertUtil.list2List(event.getBrokerMetrics(), BrokerMetricPO.class));
    }
}
