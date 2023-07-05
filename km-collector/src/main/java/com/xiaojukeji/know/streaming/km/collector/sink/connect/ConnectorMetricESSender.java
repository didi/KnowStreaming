package com.xiaojukeji.know.streaming.km.collector.sink.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.sink.AbstractMetricESSender;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.connect.ConnectorMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.connect.ConnectorMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.CONNECT_CONNECTOR_INDEX;

/**
 * @author wyb
 * @date 2022/11/7
 */
@Component
public class ConnectorMetricESSender extends AbstractMetricESSender implements ApplicationListener<ConnectorMetricEvent> {
    protected static final ILog LOGGER = LogFactory.getLog(ConnectorMetricESSender.class);

    @PostConstruct
    public void init(){
        LOGGER.info("class=ConnectorMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(ConnectorMetricEvent event) {
        send2es(CONNECT_CONNECTOR_INDEX, ConvertUtil.list2List(event.getConnectorMetricsList(), ConnectorMetricPO.class));
    }
}
