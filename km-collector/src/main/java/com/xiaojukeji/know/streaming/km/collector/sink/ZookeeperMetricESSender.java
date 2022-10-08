package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.ZookeeperMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ZookeeperMetricPO;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.common.constant.ESIndexConstant.ZOOKEEPER_INDEX;

@Component
public class ZookeeperMetricESSender extends AbstractMetricESSender implements ApplicationListener<ZookeeperMetricEvent> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @PostConstruct
    public void init(){
        LOGGER.info("class=ZookeeperMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(ZookeeperMetricEvent event) {
        send2es(ZOOKEEPER_INDEX, ConvertUtil.list2List(event.getZookeeperMetrics(), ZookeeperMetricPO.class));
    }
}
