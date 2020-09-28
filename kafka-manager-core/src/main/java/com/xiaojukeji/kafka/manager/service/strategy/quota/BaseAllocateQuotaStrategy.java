package com.xiaojukeji.kafka.manager.service.strategy.quota;

import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.service.strategy.AbstractAllocateQuotaStrategy;
import org.springframework.stereotype.Service;

/**
 * 基本的Quota分配策略，即用户申请多少则分配多少
 * @author zengqiao
 * @date 20/8/20
 */
@Service("allocateQuotaStrategy")
public class BaseAllocateQuotaStrategy extends AbstractAllocateQuotaStrategy {

    @Override
    public TopicQuota getNewTopicQuota(TopicQuota originTopicQuota, Integer access, Long peakBytesIn) {
        return originTopicQuota;
    }
}