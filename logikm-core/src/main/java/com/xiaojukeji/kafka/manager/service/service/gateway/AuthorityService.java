package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;

import java.util.List;
import java.util.Map;

/**
 * @author zhongyuankai
 * @date 2020/4/28
 */
public interface AuthorityService {
    /**
     * 添加权限
     * @param authorityDO authorityDO
     * @return int
     */
    int addAuthority(AuthorityDO authorityDO);

    /**
     * 删除指定权限
     * @param appId 应用ID
     * @param clusterId 集群ID
     * @param topicName Topic名称
     * @param access 权限
     * @return int
     */
    ResultStatus deleteSpecifiedAccess(String appId, Long clusterId, String topicName, Integer access, String operator);

    /**
     * 获取权限
     * @param clusterId 集群id
     * @param topicName topic名称
     * @param appId 应用id
     * @return AuthorityDO
     */
    AuthorityDO getAuthority(Long clusterId, String topicName, String appId);

    /**
     * 获取权限
     * @param clusterId 集群id
     * @param topicName topic名称
     * @return List<AuthorityDO>
     */
    List<AuthorityDO> getAuthorityByTopic(Long clusterId, String topicName);

    List<AuthorityDO> getAuthority(String appId);

    /**
     * 查找所有
     * @return List<TopicDO>
     */
    List<AuthorityDO> listAll();

    /**
     * 添加权限和quota
     */
    int addAuthorityAndQuota(AuthorityDO authorityDO, TopicQuota quota);

    Map<String, Map<Long, Map<String, AuthorityDO>>> getAllAuthority();

    int deleteAuthorityByTopic(Long clusterId, String topicName);
}
