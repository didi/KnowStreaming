package com.xiaojukeji.kafka.manager.common.events;

import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Region创建事件
 * @author zengqiao
 * @date 22/01/1
 */
@Getter
public class RegionCreatedEvent extends ApplicationEvent {
    private final RegionDO regionDO;

    public RegionCreatedEvent(Object source, RegionDO regionDO) {
        super(source);
        this.regionDO = regionDO;
    }
}
