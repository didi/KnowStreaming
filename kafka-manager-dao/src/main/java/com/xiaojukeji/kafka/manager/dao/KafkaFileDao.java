package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/7
 */
public interface KafkaFileDao {
    int insert(KafkaFileDO kafkaFileDO);

    int deleteById(Long id);

    int updateById(KafkaFileDO kafkaFileDO);

    List<KafkaFileDO> list();

    KafkaFileDO getById(Long id);

    KafkaFileDO getFileByFileName(String fileName);
}
