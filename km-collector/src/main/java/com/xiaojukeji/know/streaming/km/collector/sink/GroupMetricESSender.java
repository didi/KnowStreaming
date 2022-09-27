package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.GroupMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.GroupMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


import static com.xiaojukeji.know.streaming.km.common.constant.ESIndexConstant.GROUP_INDEX;

@Component
public class GroupMetricESSender extends AbstractMetricESSender implements ApplicationListener<GroupMetricEvent> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @PostConstruct
    public void init(){
        LOGGER.info("class=GroupMetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(GroupMetricEvent event) {
        send2es(GROUP_INDEX, ConvertUtil.list2List(event.getGroupMetrics(), GroupMetricPO.class));
    }
}
