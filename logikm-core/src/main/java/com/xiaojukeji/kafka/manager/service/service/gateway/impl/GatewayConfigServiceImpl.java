package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.bizenum.gateway.GatewayConfigKeyEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.*;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.gateway.GatewayConfigDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.GatewayConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Service
public class GatewayConfigServiceImpl implements GatewayConfigService {
    private final Logger LOGGER = LoggerFactory.getLogger(GatewayConfigServiceImpl.class);

    @Autowired
    private GatewayConfigDao gatewayConfigDao;

    @Override
    public KafkaBootstrapServerConfig getKafkaBootstrapServersConfig(Long requestVersion) {
        List<GatewayConfigDO> doList = null;
        try {
            doList = gatewayConfigDao.getByConfigType(GatewayConfigKeyEnum.SD_CLUSTER_ID.getConfigType());
            if (ValidateUtils.isEmptyList(doList)) {
                return new KafkaBootstrapServerConfig(Long.MIN_VALUE, new HashMap<>(0));
            }
            Long maxVersion = Long.MIN_VALUE;

            Map<String, List<String>> clusterIdBootstrapServersMap = new HashMap<>(doList.size());
            for (GatewayConfigDO configDO: doList) {
                clusterIdBootstrapServersMap.put(
                        configDO.getName().trim(),
                        ListUtils.string2StrList(configDO.getValue())
                );
                if (configDO.getVersion().compareTo(maxVersion) > 0) {
                    maxVersion = configDO.getVersion();
                }
            }
            return maxVersion > requestVersion
                    ? new KafkaBootstrapServerConfig(maxVersion, clusterIdBootstrapServersMap)
                    : new KafkaBootstrapServerConfig(requestVersion, new HashMap<>(0));
        } catch (Exception e) {
            LOGGER.error("class=GatewayConfigServiceImpl||method=getKafkaBootstrapServersConfig||data={}||errMsg={}||msg=get kafka bootstrap servers config failed",
                    JSON.toJSONString(doList), e.getMessage());
        }
        return null;
    }

    @Override
    public RequestQueueConfig getRequestQueueConfig(Long requestVersion) {
        GatewayConfigDO configDO = null;
        try {
            configDO = gatewayConfigDao.getByConfigTypeAndName(
                    GatewayConfigKeyEnum.SD_QUEUE_SIZE.getConfigType(),
                    GatewayConfigKeyEnum.SD_QUEUE_SIZE.getConfigName()
            );
            if (ValidateUtils.isNull(configDO) || configDO.getVersion() <= requestVersion) {
                return new RequestQueueConfig(Long.MIN_VALUE, null);
            }

            return new RequestQueueConfig(configDO.getVersion(), Long.valueOf(configDO.getValue()));
        } catch (Exception e) {
            LOGGER.error("class=GatewayConfigServiceImpl||method=getRequestQueueConfig||data={}||errMsg={}||msg=get request queue config failed",
                    JSON.toJSONString(configDO), e.getMessage());
        }
        return null;
    }

    @Override
    public AppRateConfig getAppRateConfig(Long requestVersion) {
        GatewayConfigDO configDO = null;
        try {
            configDO = gatewayConfigDao.getByConfigTypeAndName(
                    GatewayConfigKeyEnum.SD_APP_RATE.getConfigType(),
                    GatewayConfigKeyEnum.SD_APP_RATE.getConfigName()
            );
            if (ValidateUtils.isNull(configDO) || configDO.getVersion() <= requestVersion) {
                return new AppRateConfig(Long.MIN_VALUE, null);
            }

            return new AppRateConfig(configDO.getVersion(), Long.valueOf(configDO.getValue()));
        } catch (Exception e) {
            LOGGER.error("class=GatewayConfigServiceImpl||method=getAppRateConfig||data={}||errMsg={}||msg=get app rate config failed",
                    JSON.toJSONString(configDO), e.getMessage());
        }
        return null;
    }

    @Override
    public IpRateConfig getIpRateConfig(Long requestVersion) {
        GatewayConfigDO configDO = null;
        try {
            configDO = gatewayConfigDao.getByConfigTypeAndName(
                    GatewayConfigKeyEnum.SD_IP_RATE.getConfigType(),
                    GatewayConfigKeyEnum.SD_IP_RATE.getConfigName()
            );
            if (ValidateUtils.isNull(configDO) || configDO.getVersion() <= requestVersion) {
                return new IpRateConfig(Long.MIN_VALUE, null);
            }

            return new IpRateConfig(configDO.getVersion(), Long.valueOf(configDO.getValue()));
        } catch (Exception e) {
            LOGGER.error("get ip rate config failed, data:{}.", JSON.toJSONString(configDO), e);
        }
        return null;
    }

    @Override
    public SpRateConfig getSpRateConfig(Long requestVersion) {
        List<GatewayConfigDO> doList = null;
        try {
            doList = gatewayConfigDao.getByConfigType(GatewayConfigKeyEnum.SD_SP_RATE.getConfigType());
            if (ValidateUtils.isEmptyList(doList)) {
                LOGGER.debug("class=GatewayConfigServiceImpl||method=getSpRateConfig||requestVersion={}||msg=doList is empty!",requestVersion);
                return new SpRateConfig(Long.MIN_VALUE, new HashMap<>(0));
            }
            Long maxVersion = Long.MIN_VALUE;

            Map<String, Long> spRateMap = new HashMap<>(doList.size());
            for (GatewayConfigDO configDO: doList) {
                spRateMap.put(
                        configDO.getName(),
                        Long.valueOf(configDO.getValue())
                );
                if (configDO.getVersion().compareTo(maxVersion) > 0) {
                    maxVersion = configDO.getVersion();
                }
            }
            return maxVersion > requestVersion
                    ? new SpRateConfig(maxVersion, spRateMap)
                    : new SpRateConfig(requestVersion, new HashMap<>(0));
        } catch (Exception e) {
            LOGGER.error("get sp rate config failed, data:{}.", JSON.toJSONString(doList), e);
        }
        return null;
    }

    @Override
    public GatewayConfigDO getByTypeAndName(String configType, String configName) {
        try {
            return gatewayConfigDao.getByConfigTypeAndName(configType, configName);
        } catch (Exception e) {
            LOGGER.error("get gateway config failed, configType:{} configName:{}.", configType, configName, e);
        }
        return null;
    }

    @Override
    public List<GatewayConfigDO> list() {
        try {
            return gatewayConfigDao.list();
        } catch (Exception e) {
            LOGGER.debug("class=GatewayConfigServiceImpl||method=list||errMsg={}||msg=list failed", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public Result insert(GatewayConfigDO gatewayConfigDO) {
        try {
            GatewayConfigKeyEnum configKeyEnum = GatewayConfigKeyEnum.getByConfigType(gatewayConfigDO.getType());
            if (ValidateUtils.isNull(configKeyEnum)
                    && ValidateUtils.isBlank(gatewayConfigDO.getName())
                    && ValidateUtils.isBlank(gatewayConfigDO.getValue())) {
                // 参数错误
                return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
            }

            // 获取当前同类配置, 插入之后需要增大这个version
            List<GatewayConfigDO> gatewayConfigDOList = gatewayConfigDao.getByConfigType(gatewayConfigDO.getType());
            Long version = 1L;
            for (GatewayConfigDO elem: gatewayConfigDOList) {
                if (elem.getVersion() > version) {
                    version = elem.getVersion() + 1L;
                }
            }

            gatewayConfigDO.setVersion(version);
            if (gatewayConfigDao.insert(gatewayConfigDO) > 0) {
                return Result.buildSuc();
            }
            return Result.buildFrom(ResultStatus.MYSQL_ERROR);
        } catch (Exception e) {
            LOGGER.debug("class=GatewayConfigServiceImpl||method=insert||data={}||errMsg={}||msg=insert failed", gatewayConfigDO, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }

    @Override
    public Result deleteById(Long id) {
        try {
            if (gatewayConfigDao.deleteById(id) > 0) {
                return Result.buildSuc();
            }
            return Result.buildFrom(ResultStatus.RESOURCE_NOT_EXIST);
        } catch (Exception e) {
            LOGGER.debug("class=GatewayConfigServiceImpl||method=deleteById||id={}||errMsg={}||msg=delete failed", id, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }

    @Override
    public Result updateById(GatewayConfigDO newGatewayConfigDO) {
        try {
            GatewayConfigDO oldGatewayConfigDO = this.getById(newGatewayConfigDO.getId());
            if (ValidateUtils.isNull(oldGatewayConfigDO)) {
                return Result.buildFrom(ResultStatus.RESOURCE_NOT_EXIST);
            }

            if (!oldGatewayConfigDO.getName().equals(newGatewayConfigDO.getName())
                    || !oldGatewayConfigDO.getType().equals(newGatewayConfigDO.getType())
                    || ValidateUtils.isBlank(newGatewayConfigDO.getValue())) {
                return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
            }

            // 获取当前同类配置, 插入之后需要增大这个version
            List<GatewayConfigDO> gatewayConfigDOList = gatewayConfigDao.getByConfigType(newGatewayConfigDO.getType());
            Long version = 1L;
            for (GatewayConfigDO elem: gatewayConfigDOList) {
                if (elem.getVersion() > version) {
                    version = elem.getVersion() + 1L;
                }
            }

            newGatewayConfigDO.setVersion(version);
            if (gatewayConfigDao.updateById(newGatewayConfigDO) > 0) {
                return Result.buildSuc();
            }
            return Result.buildFrom(ResultStatus.MYSQL_ERROR);
        } catch (Exception e) {
            LOGGER.debug("class=GatewayConfigServiceImpl||method=updateById||data={}||errMsg={}||msg=update failed", newGatewayConfigDO, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }

    @Override
    public GatewayConfigDO getById(Long id) {
        if (ValidateUtils.isNull(id)) {
            return null;
        }
        try {
            return gatewayConfigDao.getById(id);
        } catch (Exception e) {
            LOGGER.debug("class=GatewayConfigServiceImpl||method=getById||id={}||errMsg={}||msg=get failed", id, e.getMessage());
        }
        return null;
    }
}