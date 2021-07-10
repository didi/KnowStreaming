package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.CreateTopicElemConfig;
import com.xiaojukeji.kafka.manager.common.entity.dto.config.ConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ConfigDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/19
 */
public interface ConfigService {
    ResultStatus insert(ConfigDTO dto);

    ResultStatus deleteByKey(String configKey);

    ResultStatus updateByKey(ConfigDTO dto);

    ResultStatus updateByKey(String configKey, String configValue);

    ConfigDO getByKey(String configKey);

    <T> T getByKey(String configKey, Class<T> clazz);

    <T> List<T> getArrayByKey(String configKey, Class<T> clazz);

    Long getLongValue(String configKey, Long defaultValue);

    List<ConfigDO> listAll();

    Integer getAutoPassedTopicApplyOrderNumPerTask();

    CreateTopicElemConfig getCreateTopicConfig(Long clusterId, String systemCode);

    ClusterDO getClusterDO(Long clusterId);
}