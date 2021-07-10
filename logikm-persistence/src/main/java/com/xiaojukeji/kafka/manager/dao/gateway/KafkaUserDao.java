package com.xiaojukeji.kafka.manager.dao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/21
 */
public interface KafkaUserDao {
    List<KafkaUserDO> getKafkaUsers(Date startTime, Date endTime);

    /**
     * 插入数据
     * @param kafkaUserDO kafkaUserDO
     * @return int
     */
    int insert(KafkaUserDO kafkaUserDO);

    /**
     * 获取所有的应用
     * @return List<KafkaUserDO>
     */
    List<KafkaUserDO> listAll();
}
