package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ConfigDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/19
 */
public interface ConfigDao {
    int insert(ConfigDO configDO);

    int deleteByKey(String configKey);

    int updateByKey(ConfigDO configDO);

    ConfigDO getByKey(String configKey);

    List<ConfigDO> listAll();
}