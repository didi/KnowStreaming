package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperationStatusEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.gateway.AuthorityDao;
import com.xiaojukeji.kafka.manager.dao.gateway.KafkaAclDao;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhongyuankai
 * @date 20/4/28
 */
@Service("authorityService")
public class AuthorityServiceImpl implements AuthorityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityServiceImpl.class);

    @Autowired
    private AuthorityDao authorityDao;

    @Autowired
    private KafkaAclDao kafkaAclDao;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Override
    public int addAuthority(AuthorityDO authorityDO) {
        int result = 0;
        Integer newAccess = authorityDO.getAccess();
        try {
            // 权限只会增加, 不会减少, 这里做了新旧权限的merge
            AuthorityDO originAuthority = getAuthority(
                    authorityDO.getClusterId(),
                    authorityDO.getTopicName(),
                    authorityDO.getAppId()
            );
            if (!ValidateUtils.isNull(originAuthority)) {
                newAccess |= originAuthority.getAccess();
                authorityDO.setAccess(newAccess);
                if (newAccess.equals(originAuthority.getAccess())) {
                    // 新旧权限一致, 不需要做任何调整
                    return result;
                }
            }
            if (authorityDao.insert(authorityDO) < 1) {
                return result;
            }
            KafkaAclDO kafkaAclDO = new KafkaAclDO();
            kafkaAclDO.setTopicName(authorityDO.getTopicName());
            kafkaAclDO.setClusterId(authorityDO.getClusterId());
            kafkaAclDO.setAppId(authorityDO.getAppId());
            kafkaAclDO.setAccess(authorityDO.getAccess());
            kafkaAclDO.setOperation(OperationStatusEnum.CREATE.getCode());
            return kafkaAclDao.insert(kafkaAclDO);
        } catch (Exception e) {
            LOGGER.error("add authority failed, authorityDO:{}.", authorityDO, e);
        }
        return result;
    }

    @Override
    public ResultStatus deleteSpecifiedAccess(String appId, Long clusterId, String topicName, Integer access, String operator) {
        AuthorityDO authorityDO = getAuthority(clusterId, topicName, appId);
        if (ValidateUtils.isNull(authorityDO)) {
            return ResultStatus.AUTHORITY_NOT_EXIST;
        }

        if ((authorityDO.getAccess() & access) != access) {
            // 并不具备所要删除的权限, 返回错误
            return ResultStatus.PARAM_ILLEGAL;
        }

        int newAccess = authorityDO.getAccess() ^ access;
        authorityDO.setAccess(newAccess);
        try {
            if (authorityDao.insert(authorityDO) < 1) {
                return ResultStatus.OPERATION_FAILED;
            }

            // kafka_acl表, 删除权限时, 只需要存储所要删除的权限, 不需要存储权限的终态或者什么的
            KafkaAclDO kafkaAclDO = new KafkaAclDO();
            kafkaAclDO.setOperation(OperationStatusEnum.DELETE.getCode());
            kafkaAclDO.setAccess(access);
            kafkaAclDO.setAppId(appId);
            kafkaAclDO.setClusterId(clusterId);
            kafkaAclDO.setTopicName(topicName);
            if (kafkaAclDao.insert(kafkaAclDO) < 1) {
                return ResultStatus.OPERATION_FAILED;
            }

            // 记录操作
            Map<String, Object> content = new HashMap<>(4);
            content.put("clusterId", clusterId);
            content.put("topicName", topicName);
            content.put("access", access);
            content.put("appId", appId);
            OperateRecordDO operateRecordDO = new OperateRecordDO();
            operateRecordDO.setModuleId(ModuleEnum.AUTHORITY.getCode());
            operateRecordDO.setOperateId(OperateEnum.DELETE.getCode());
            operateRecordDO.setResource(topicName);
            operateRecordDO.setContent(JsonUtils.toJSONString(content));
            operateRecordDO.setOperator(operator);
            operateRecordService.insert(operateRecordDO);
        } catch (Exception e) {
            LOGGER.error("delete authority failed, authorityDO:{}.", authorityDO, e);
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public AuthorityDO getAuthority(Long clusterId, String topicName, String appId) {
        List<AuthorityDO> authorityDOList = null;
        try {
            authorityDOList = authorityDao.getAuthority(clusterId, topicName, appId);
        } catch (Exception e) {
            LOGGER.error("get authority failed, clusterId:{}, topicName:{}, appId:{}.", clusterId, topicName, appId, e);
        }
        if (ValidateUtils.isEmptyList(authorityDOList)) {
            return null;
        }
        return authorityDOList.get(0);
    }

    @Override
    public List<AuthorityDO> getAuthorityByTopic(Long clusterId, String topicName) {
        try {
            return authorityDao.getAuthorityByTopic(clusterId, topicName);
        } catch (Exception e) {
            LOGGER.error("get authority failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<AuthorityDO> getAuthority(String appId) {
        List<AuthorityDO> doList = null;
        try {
            doList = authorityDao.getByAppId(appId);
        } catch (Exception e) {
            LOGGER.error("get authority failed, appId:{}.", appId, e);
        }
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }

        // 去除掉权限列表中无权限的数据
        return doList.stream()
                .filter(authorityDO -> !TopicAuthorityEnum.DENY.getCode().equals(authorityDO.getAccess()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthorityDO> listAll() {
        return authorityDao.listAll();
    }

    @Override
    public int addAuthorityAndQuota(AuthorityDO authorityDO, TopicQuota topicQuotaDO) {
        int result = 0;
        try {
            result = addAuthority(authorityDO);
            if (result < 1) {
                return result;
            }
            return quotaService.addTopicQuota(topicQuotaDO, authorityDO.getAccess());
        } catch (Exception e) {
            LOGGER.error("add authority and quota failed, authorityDO:{} topicQuotaDO:{}.",
                    authorityDO, topicQuotaDO, e);
            return result;
        }
    }

    @Override
    public Map<String, Map<Long, Map<String, AuthorityDO>>> getAllAuthority() {
        return authorityDao.getAllAuthority();
    }

    @Override
    public int deleteAuthorityByTopic(Long clusterId, String topicName) {
        return authorityDao.deleteAuthorityByTopic(clusterId, topicName);
    }

}