package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public interface KafkaBillDao {
    int replace(KafkaBillDO kafkaBillDO);

    List<KafkaBillDO> getByTopicName(Long clusterId, String topicName, Date startTime, Date endTime);

    List<KafkaBillDO> getByPrincipal(String principal, Date startTime, Date endTime);

    List<KafkaBillDO> getByTimeBetween(Date startTime, Date endTime);

    List<KafkaBillDO> getByGmtDay(String gmtDay);
}