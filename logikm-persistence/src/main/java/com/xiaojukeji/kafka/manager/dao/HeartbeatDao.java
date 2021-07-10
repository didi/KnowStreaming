package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.HeartbeatDO;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/10
 */
public interface HeartbeatDao {
    int replace(HeartbeatDO heartbeatDO);

    List<HeartbeatDO> selectActiveHosts(Date afterTime);
}