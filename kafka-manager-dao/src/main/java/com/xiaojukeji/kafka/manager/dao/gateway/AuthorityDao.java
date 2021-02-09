package com.xiaojukeji.kafka.manager.dao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;

import java.util.List;
import java.util.Map;

/**
 * @author zhongyuankai
 * @date 2020/4/27
 */
public interface AuthorityDao {
    /**
     * 插入数据
     */
    int insert(AuthorityDO authorityDO);

    /**
     * 获取权限
     * @param clusterId 集群id
     * @param topicName topic名称
     * @param appId 应用id
     * @return AuthorityDO
     */
    List<AuthorityDO> getAuthority(Long clusterId, String topicName, String appId);

    List<AuthorityDO> getAuthorityByTopic(Long clusterId, String topicName);

    List<AuthorityDO> getByAppId(String appId);

    /**
     * 查找所有
     * @return List<AuthorityDO>
     */
    List<AuthorityDO> listAll();

    Map<String, Map<Long, Map<String, AuthorityDO>>> getAllAuthority();

    int deleteAuthorityByTopic(Long clusterId, String topicName);
}
