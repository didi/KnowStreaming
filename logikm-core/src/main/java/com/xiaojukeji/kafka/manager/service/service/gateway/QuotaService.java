package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;

/**
 * @author zengqiao
 * @date 20/4/28
 */
public interface QuotaService {
    /**
     * 添加quota
     * @param topicQuotaDO topicQuotaDO
     * @return int
     */
    int addTopicQuota(TopicQuota topicQuotaDO);

    /**
     * 添加quota
     * @param topicQuotaDO topicQuotaDO
     * @param access 权限
     * @return int
     */
    int addTopicQuota(TopicQuota topicQuotaDO, Integer access);

    /**
     * 从ZK读取Quota
     * @param clusterId 集群ID
     * @param topicName Topic名称
     * @param appId AppID
     * @author zengqiao
     * @date 20/5/12
     * @return TopicQuotaDO
     */
    TopicQuota getQuotaFromZk(Long clusterId, String topicName, String appId);

    Boolean modifyProduceQuota(Long clusterId, String topicName, String appId, Long produceQuota);

    /**
     * topic配额调整
     * @param topicQuota topic配额
     * @return
     */
    ResultStatus addTopicQuotaByAuthority(TopicQuota topicQuota);
}
