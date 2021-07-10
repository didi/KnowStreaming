package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaBillDO;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public interface KafkaBillService {
    int replace(KafkaBillDO kafkaBillDO);

    List<KafkaBillDO> getByTopicName(Long clusterId, String topicName, Date startTime, Date endTime);

    List<KafkaBillDO> getByPrincipal(String principal, Date startTime, Date endTime);

    List<KafkaBillDO> getByTimeBetween(Date startTime, Date endTime);

    List<KafkaBillDO> getByGmtDay(String gmtDay);
}