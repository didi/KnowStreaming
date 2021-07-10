package com.xiaojukeji.kafka.manager.service.strategy;

import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;

/**
 * @author zengqiao
 * @date 20/8/20
 */
public abstract class AbstractAllocateQuotaStrategy {
    public abstract TopicQuota getNewTopicQuota(TopicQuota originTopicQuota, Integer access, Long peakBytesIn);
}