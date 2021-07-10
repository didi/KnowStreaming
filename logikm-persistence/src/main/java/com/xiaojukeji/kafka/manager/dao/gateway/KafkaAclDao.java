package com.xiaojukeji.kafka.manager.dao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/21
 */
public interface KafkaAclDao {
    List<KafkaAclDO> getKafkaAcls(Long clusterId, Date startTime, Date endTime);

    /**
     * 插入数据
     * @param kafkaAclDO kafkaAclDO
     * @return int
     */
    int insert(KafkaAclDO kafkaAclDO);

    List<KafkaAclDO> listAll();
}