package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/27
 */
public interface SecurityService {
    List<KafkaUserDO> getKafkaUsers(Long startTime, Long endTime);

    List<KafkaAclDO> getKafkaAcls(Long clusterId, Long startTime, Long endTime);
}