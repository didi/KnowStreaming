package com.xiaojukeji.kafka.manager.task.listener.biz;

import com.xiaojukeji.kafka.manager.common.events.RegionCreatedEvent;
import com.xiaojukeji.kafka.manager.task.dispatch.biz.CalRegionCapacity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Region创建监听器,
 * TODO 后续需要将其移动到core模块
 * @author zengqiao
 * @date 22/01/11
 */
@Component
public class RegionCreatedListener implements ApplicationListener<RegionCreatedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(RegionCreatedListener.class);

    @Autowired
    private CalRegionCapacity calRegionCapacity;

    @Async
    @Override
    public void onApplicationEvent(RegionCreatedEvent event) {
        try {
            logger.info("cal region capacity started when region created, regionDO:{}.", event.getRegionDO());

            calRegionCapacity.processTask(event.getRegionDO());

            logger.info("cal region capacity finished when region created, regionDO:{}.", event.getRegionDO());
        } catch (Exception e) {
            logger.error("cal region capacity failed when region created, regionDO:{}.", event.getRegionDO(), e);
        }
    }
}
