package com.xiaojukeji.kafka.manager.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.*;
import com.xiaojukeji.kafka.manager.common.entity.dto.config.ConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ConfigDO;
import com.xiaojukeji.kafka.manager.dao.ConfigDao;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/19
 */
@Service("configService")
public class ConfigServiceImpl implements ConfigService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Autowired
    private ConfigDao configDao;

    @Override
    public ResultStatus insert(ConfigDTO dto) {
        try {
            if (configDao.insert(convert2ConfigDO(dto)) >= 1) {
                return ResultStatus.SUCCESS;
            }
        } catch (DuplicateKeyException e) {
            return ResultStatus.RESOURCE_ALREADY_EXISTED;
        } catch (Exception e) {
            LOGGER.error("insert config failed, config:{}.", dto, e);
        }
        LOGGER.warn("class=ConfigServiceImpl||method=insert||dto={}||msg=insert config fail,{}!", dto,ResultStatus.MYSQL_ERROR.getMessage());
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus deleteByKey(String configKey) {
        if (ValidateUtils.isNull(configKey)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            if (configDao.deleteByKey(configKey) >= 1) {
                return ResultStatus.SUCCESS;
            }
            LOGGER.warn("class=ConfigServiceImpl||method=deleteByKey||configKey={}||msg=delete config fail,{}!", configKey,ResultStatus.CONFIG_NOT_EXIST.getMessage());
            return ResultStatus.CONFIG_NOT_EXIST;
        } catch (Exception e) {
            LOGGER.error("delete config failed, configKey:{}.", configKey, e);
        }
        LOGGER.warn("class=ConfigServiceImpl||method=deleteByKey||configKey={}||msg=delete config fail,{}!", configKey,ResultStatus.MYSQL_ERROR.getMessage());
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus updateByKey(ConfigDTO dto) {
        try {
            if (configDao.updateByKey(convert2ConfigDO(dto)) >= 1) {
                return ResultStatus.SUCCESS;
            }
            LOGGER.warn("class=ConfigServiceImpl||method=updateByKey||dto={}||msg=update config fail,{}!", dto,ResultStatus.CONFIG_NOT_EXIST.getMessage());
            return ResultStatus.CONFIG_NOT_EXIST;
        } catch (Exception e) {
            LOGGER.error("update config failed, config:{}.", dto, e);
        }
        LOGGER.warn("class=ConfigServiceImpl||method=deleteByKey||dto={}||msg=delete config fail,{}!", dto,ResultStatus.MYSQL_ERROR.getMessage());
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus updateByKey(String configKey, String configValue) {
        try {
            ConfigDO configDO = new ConfigDO();
            configDO.setConfigKey(configKey);
            configDO.setConfigValue(configValue);
            configDO.setConfigDescription("");
            if (configDao.updateByKey(configDO) >= 1) {
                return ResultStatus.SUCCESS;
            }
            LOGGER.warn("class=ConfigServiceImpl||method=updateByKey||configKey={}||configValue={}||msg=update config fail,{}!"
                    , configKey,configValue,ResultStatus.CONFIG_NOT_EXIST.getMessage());
            return ResultStatus.CONFIG_NOT_EXIST;
        } catch (Exception e) {
            LOGGER.error("update config failed, configValue:{}.", configValue, e);
        }
        LOGGER.warn("class=ConfigServiceImpl||method=deleteByKey||configKey={}||configValue={}||msg=delete config fail,{}!"
                , configKey,configValue,ResultStatus.MYSQL_ERROR.getMessage());

        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ConfigDO getByKey(String configKey) {
        try {
            return configDao.getByKey(configKey);
        } catch (Exception e) {
            LOGGER.error("get config failed, configKey:{}.", configKey, e);
        }
        return null;
    }

    @Override
    public <T> T getByKey(String configKey, Class<T> clazz) {
        ConfigDO configDO = this.getByKey(configKey);
        if (ValidateUtils.isNull(configDO)) {
            return null;
        }
        try {
            return JSON.parseObject(configDO.getConfigValue(), clazz);
        } catch (Exception e) {
            LOGGER.error("get config failed, configKey:{}.", configKey, e);
        }
        return null;
    }

    @Override
    public <T> List<T> getArrayByKey(String configKey, Class<T> clazz) {
        ConfigDO configDO = this.getByKey(configKey);
        if (ValidateUtils.isNull(configDO)) {
            return null;
        }
        try {
            return JSON.parseArray(configDO.getConfigValue(), clazz);
        } catch (Exception e) {
            LOGGER.error("get config failed, configKey:{}.", configKey, e);
        }
        return null;
    }

    @Override
    public Long getLongValue(String configKey, Long defaultValue) {
        ConfigDO configDO = this.getByKey(configKey);
        if (ValidateUtils.isNull(configDO)) {
            return defaultValue;
        }
        try {
            return Long.valueOf(configDO.getConfigValue());
        } catch (Exception e) {
            LOGGER.error("get and convert config value failed, configKey:{}.", configKey, e);
        }
        return defaultValue;
    }

    @Override
    public List<ConfigDO> listAll() {
        try {
            return configDao.listAll();
        } catch (Exception e) {
            LOGGER.error("get configs failed.", e);
        }
        return null;
    }

    private ConfigDO convert2ConfigDO(ConfigDTO dto) {
        ConfigDO configDO = new ConfigDO();
        configDO.setConfigKey(dto.getConfigKey());
        configDO.setConfigValue(dto.getConfigValue());
        configDO.setConfigDescription(dto.getConfigDescription());
        return configDO;
    }

    @Override
    public Integer getAutoPassedTopicApplyOrderNumPerTask() {
        String configKey = TopicCreationConstant.INNER_CREATE_TOPIC_CONFIG_KEY;
        CreateTopicConfig configValue = this.getByKey(configKey, CreateTopicConfig.class);
        if (ValidateUtils.isNull(configValue)) {
            return TopicCreationConstant.DEFAULT_MAX_PASSED_ORDER_NUM_PER_TASK;
        }
        return configValue.getMaxPassedOrderNumPerTask();
    }

    @Override
    public CreateTopicElemConfig getCreateTopicConfig(Long clusterId, String systemCode) {
        String configKey = TopicCreationConstant.INNER_CREATE_TOPIC_CONFIG_KEY;
        CreateTopicConfig configValue = this.getByKey(
                configKey,
                CreateTopicConfig.class
        );

        CreateTopicElemConfig config = new CreateTopicElemConfig();
        config.setClusterId(clusterId);
        config.setBrokerIdList(new ArrayList<>());
        config.setRegionIdList(new ArrayList<>());
        config.setPartitionNum(TopicCreationConstant.DEFAULT_PARTITION_NUM);
        config.setReplicaNum(TopicCreationConstant.DEFAULT_REPLICA);
        config.setRetentionTimeUnitHour(TopicCreationConstant.DEFAULT_RETENTION_TIME_UNIT_HOUR);
        config.setAutoExecMaxPeakBytesInUnitB(TopicCreationConstant.AUTO_EXEC_MAX_BYTES_IN_UNIT_B);
        if (ValidateUtils.isNull(configValue) || ValidateUtils.isEmptyList(configValue.getConfigList())) {
            return config;
        }
        for (CreateTopicElemConfig elem: configValue.getConfigList()) {
            if (!clusterId.equals(elem.getClusterId())) {
                continue;
            }
            if (!ValidateUtils.isEmptyList(elem.getBrokerIdList())) {
                config.setBrokerIdList(elem.getBrokerIdList());
            }
            if (!ValidateUtils.isEmptyList(elem.getRegionIdList())) {
                config.setRegionIdList(elem.getRegionIdList());
            }
            if (!ValidateUtils.isNull(elem.getReplicaNum())) {
                config.setReplicaNum(elem.getReplicaNum());
            }
            if (!ValidateUtils.isNull(elem.getPartitionNum())) {
                config.setPartitionNum(elem.getPartitionNum());
            }
            if (!ValidateUtils.isNull(elem.getRetentionTimeUnitHour())) {
                config.setRetentionTimeUnitHour(elem.getRetentionTimeUnitHour());
            }
            if (!ValidateUtils.isNull(elem.getAutoExecMaxPeakBytesInUnitB())) {
                config.setAutoExecMaxPeakBytesInUnitB(elem.getAutoExecMaxPeakBytesInUnitB());
            }
            return config;
        }
        return config;
    }

    @Override
    public ClusterDO getClusterDO(Long clusterId) {
        ConfigDO configDO = this.getByKey(ConfigConstant.KAFKA_CLUSTER_DO_CONFIG_KEY);
        if (ValidateUtils.isNull(configDO)) {
            return null;
        }
        try {
            List<ClusterDO> clusterDOList = JSON.parseArray(configDO.getConfigValue(), ClusterDO.class);
            if (ValidateUtils.isEmptyList(clusterDOList)) {
                return null;
            }
            for (ClusterDO clusterDO: clusterDOList) {
                if (clusterId.equals(clusterDO.getId())) {
                    return clusterDO;
                }
            }
        } catch (Exception e) {
            LOGGER.error("get cluster do failed, clusterId:{}.", clusterId, e);
        }
        return null;
    }
}