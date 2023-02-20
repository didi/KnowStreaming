package com.xiaojukeji.know.streaming.km.collector.sink.mm2;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.sink.AbstractMetricESSender;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.mm2.MirrorMakerMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.mm2.MirrorMakerMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.CONNECT_MM2_INDEX;

/**
 * @author zengqiao
 * @date 2022/12/20
 */
@Component
public class MirrorMakerMetricESSender extends AbstractMetricESSender implements ApplicationListener<MirrorMakerMetricEvent> {
    protected static final ILog LOGGER = LogFactory.getLog(MirrorMakerMetricESSender.class);

    @PostConstruct
    public void init(){
        LOGGER.info("method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(MirrorMakerMetricEvent event) {
        send2es(CONNECT_MM2_INDEX, ConvertUtil.list2List(event.getMetricsList(), MirrorMakerMetricPO.class));
    }
}
