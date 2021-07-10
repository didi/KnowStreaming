package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperationStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.AppTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.dao.gateway.AppDao;
import com.xiaojukeji.kafka.manager.dao.gateway.KafkaUserDao;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhongyuankai
 * @date 20/4/28
 */
@Service("appService")
public class AppServiceImpl implements AppService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServiceImpl.class);

    @Autowired
    private AppDao appDao;

    @Autowired
    private KafkaUserDao kafkaUserDao;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private LogicalClusterMetadataManager logicClusterMetadataManager;

    @Autowired
    private OperateRecordService operateRecordService;

    @Override
    public ResultStatus addApp(AppDO appDO, String operator) {
        try {
            if (appDao.insert(appDO) < 1) {
                LOGGER.warn("class=AppServiceImpl||method=addApp||AppDO={}||msg=add fail,{}",appDO,ResultStatus.MYSQL_ERROR.getMessage());
                return ResultStatus.MYSQL_ERROR;
            }
            KafkaUserDO kafkaUserDO = new KafkaUserDO();
            kafkaUserDO.setAppId(appDO.getAppId());
            kafkaUserDO.setPassword(appDO.getPassword());
            kafkaUserDO.setOperation(OperationStatusEnum.CREATE.getCode());
            kafkaUserDO.setUserType(0);
            kafkaUserDao.insert(kafkaUserDO);

            Map<String, String> content = new HashMap<>();
            content.put("appId", appDO.getAppId());
            content.put("name", appDO.getName());
            content.put("applicant", appDO.getApplicant());
            content.put("password", appDO.getPassword());
            content.put("principals", appDO.getPrincipals());
            content.put("description", appDO.getDescription());
            operateRecordService.insert(operator, ModuleEnum.APP, appDO.getName(), OperateEnum.ADD, content);
        } catch (DuplicateKeyException e) {
            LOGGER.error("class=AppServiceImpl||method=addApp||errMsg={}||appDO={}|", e.getMessage(), appDO, e);
            return ResultStatus.RESOURCE_ALREADY_EXISTED;
        } catch (Exception e) {
            LOGGER.error("add app failed, appDO:{}.", appDO, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public int deleteApp(AppDO appDO, String operator) {
        int result = 0;
        try {
            result = appDao.deleteByName(appDO.getName());
            if (result < 1) {
                return result;
            }
            KafkaUserDO kafkaUserDO = new KafkaUserDO();
            kafkaUserDO.setAppId(appDO.getAppId());
            kafkaUserDO.setOperation(OperationStatusEnum.DELETE.getCode());
            kafkaUserDO.setUserType(0);
            kafkaUserDO.setPassword(appDO.getPassword());
            kafkaUserDao.insert(kafkaUserDO);

            // 记录操作
            Map<String, Object> content = new HashMap<>(1);
            content.put("appId", appDO.getAppId());
            OperateRecordDO operateRecordDO = new OperateRecordDO();
            operateRecordDO.setModuleId(ModuleEnum.APP.getCode());
            operateRecordDO.setOperateId(OperateEnum.DELETE.getCode());
            operateRecordDO.setResource(appDO.getAppId());
            operateRecordDO.setContent(JSONObject.toJSONString(content));
            operateRecordDO.setOperator(operator);
            operateRecordService.insert(operateRecordDO);
        } catch (Exception e) {
            LOGGER.error("delete app failed, appDO:{}.", appDO, e);
        }
        return result;
    }

    @Override
    public AppDO getByName(String name) {
        try {
            return appDao.getByName(name);
        } catch (Exception e) {
            LOGGER.error("get app failed, name:{}.", name, e);
        }
        return null;
    }

    @Override
    public ResultStatus updateByAppId(AppDTO dto, String operator, Boolean adminApi) {
        try {
            AppDO appDO = appDao.getByAppId(dto.getAppId());
            if (ValidateUtils.isNull(appDO)) {
                return ResultStatus.APP_NOT_EXIST;
            }
            if (!adminApi && !ListUtils.string2StrList(appDO.getPrincipals()).contains(operator)) {
                return ResultStatus.USER_WITHOUT_AUTHORITY;
            }
            appDO.setName(dto.getName());
            appDO.setPrincipals(dto.getPrincipals());
            appDO.setDescription(dto.getDescription());

            if (appDao.updateById(appDO) > 0) {
                Map<String, String> content = new HashMap<>();
                content.put("appId", appDO.getAppId());
                content.put("name", appDO.getName());
                content.put("principals", appDO.getPrincipals());
                content.put("description", appDO.getDescription());
                operateRecordService.insert(operator, ModuleEnum.APP, appDO.getName(), OperateEnum.EDIT, content);
                return ResultStatus.SUCCESS;
            }
        } catch (DuplicateKeyException e) {
            LOGGER.error("class=AppServiceImpl||method=updateByAppId||errMsg={}||AppDTO={}||operator={}||adminApi={}", e.getMessage(), dto, operator, adminApi, e);
            return ResultStatus.RESOURCE_NAME_DUPLICATED;
        } catch (Exception e) {
            LOGGER.error("update app failed, dto:{}, operator:{}, adminApi:{}.", dto, operator, adminApi, e);
        }
        LOGGER.warn("class=AppServiceImpl||method=updateByAppId||dto={}||operator={}||adminApi={}||msg=update app fail,{}!", dto,operator,adminApi,ResultStatus.MYSQL_ERROR.getMessage());
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public List<AppDO> getByPrincipal(String principal) {
        try {
            List<AppDO> appDOs = appDao.getByPrincipal(principal);
            if (!ValidateUtils.isEmptyList(appDOs)) {
                return appDOs.stream()
                        .filter(appDO -> ListUtils.string2StrList(appDO.getPrincipals()).contains(principal))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("get app list failed, principals:{}.", principal);
        }
        return new ArrayList<>();
    }

    @Override
    public AppDO getAppByUserAndId(String appId, String curUser) {
        AppDO appDO = this.getByAppId(appId);
        if (appDO != null) {
            if (ListUtils.string2StrList(appDO.getPrincipals()).contains(curUser)) {
                return appDO;
            }
        }
        LOGGER.debug("class=AppServiceImpl||method=getAppByUserAndId||appId={}||curUser={}||msg=appDO is null!", appId, curUser);
        return null;
    }

    @Override
    public AppDO getByAppId(String appId) {
        try {
            return appDao.getByAppId(appId);
        } catch (Exception e) {
            LOGGER.error("get app failed, appId:{}.", appId, e);
        }
        return null;
    }

    @Override
    public List<AppDO> listAll() {
        return appDao.listAll();
    }

    @Override
    public List<AppTopicDTO> getAppTopicDTOList(String appId, Boolean mine) {
        // 查询AppID
        AppDO appDO = appDao.getByAppId(appId);
        if (ValidateUtils.isNull(appDO)) {
            LOGGER.debug("class=AppServiceImpl||method=getAppTopicDTOList||appId={}||msg=appDO is null!", appId);
            return new ArrayList<>();
        }

        List<TopicDO> topicDOList = topicManagerService.listAll();
        Map<Long, Map<String, TopicDO>> topicMap = new HashMap<>();
        for (TopicDO topicDO: topicDOList) {
            Map<String, TopicDO> subTopicMap = topicMap.getOrDefault(topicDO.getClusterId(), new HashMap<>());
            subTopicMap.put(topicDO.getTopicName(), topicDO);
            topicMap.put(topicDO.getClusterId(), subTopicMap);
        }

        // 查询AppID有权限的Topic
        List<AuthorityDO> authorityDOList = authorityService.getAuthority(appId);
        if (ValidateUtils.isEmptyList(authorityDOList)) {
            return new ArrayList<>();
        }
        List<AppTopicDTO> dtoList = new ArrayList<>();
        for (AuthorityDO authorityDO: authorityDOList) {
            TopicDO topicDO = topicMap
                    .getOrDefault(authorityDO.getClusterId(), new HashMap<>())
                    .get(authorityDO.getTopicName());

            if (ValidateUtils.isNull(topicDO)) {
                continue;
            }

            if (Boolean.TRUE.equals(mine)
                    && !topicDO.getAppId().equals(appId)) {
                continue;
            }

            if (Boolean.FALSE.equals(mine)
                    && topicDO.getAppId().equals(appId)) {
                continue;
            }

            LogicalClusterDO logicalClusterDO = logicClusterMetadataManager.getTopicLogicalCluster(
                    authorityDO.getClusterId(),
                    authorityDO.getTopicName()
            );

            AppTopicDTO appTopicDTO = new AppTopicDTO();
            if (!ValidateUtils.isNull(logicalClusterDO)) {
                appTopicDTO.setPhysicalClusterId(logicalClusterDO.getClusterId());
                appTopicDTO.setLogicalClusterId(logicalClusterDO.getId());
                appTopicDTO.setLogicalClusterName(logicalClusterDO.getName());
            } else {
                LOGGER.warn("class=AppServiceImpl||method=getAppTopicDTOList||clusterId={}||topicName={}||msg=logicalClusterDO is null!", authorityDO.getClusterId(), authorityDO.getTopicName());
                continue;
            }
            appTopicDTO.setOperator("");
            appTopicDTO.setTopicName(authorityDO.getTopicName());
            appTopicDTO.setAccess(authorityDO.getAccess());
            appTopicDTO.setGmtCreate(authorityDO.getCreateTime().getTime());
            dtoList.add(appTopicDTO);
        }
        return dtoList;
    }

    @Override
    public boolean verifyAppIdByPassword(String appId, String password) {
        if (ValidateUtils.isBlank(appId) || ValidateUtils.isBlank(password)) {
            return false;
        }
        AppDO appDO = getByAppId(appId);
        if (ValidateUtils.isNull(appDO) || !password.equals(appDO.getPassword())) {
            return false;
        }
        return true;
    }
}