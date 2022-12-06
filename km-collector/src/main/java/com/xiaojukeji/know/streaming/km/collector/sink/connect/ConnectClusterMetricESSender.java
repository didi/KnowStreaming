package com.xiaojukeji.know.streaming.km.collector.sink.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.sink.AbstractMetricESSender;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.connect.ConnectClusterMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.connect.ConnectClusterMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.CONNECT_CLUSTER_INDEX;

/**
 * @author wyb
 * @date 2022/11/7
 */
@Component
public class ConnectClusterMetricESSender extends AbstractMetricESSender implements ApplicationListener<ConnectClusterMetricEvent> {
    protected static final ILog LOGGER = LogFactory.getLog(ConnectClusterMetricESSender.class);

    @PostConstruct
    public void init(){
        LOGGER.info("class=ConnectClusterMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(ConnectClusterMetricEvent event) {
        send2es(CONNECT_CLUSTER_INDEX, ConvertUtil.list2List(event.getConnectClusterMetrics(), ConnectClusterMetricPO.class));
    }
}
