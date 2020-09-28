package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.utils.NumberUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.config.TopicQuotaData;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.strategy.AbstractAllocateQuotaStrategy;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zengqiao
 * @date 20/4/28
 */
@Service("quotaService")
public class QuotaServiceImpl implements QuotaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuotaServiceImpl.class);
    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private AbstractAllocateQuotaStrategy allocateQuotaStrategy;

    @Override
    public int addTopicQuota(TopicQuota topicQuotaDO) {
        return KafkaZookeeperUtils.setTopicQuota(
                PhysicalClusterMetadataManager.getZKConfig(topicQuotaDO.getClusterId()),
                topicQuotaDO
        ) ? 1: 0;
    }

    @Override
    public int addTopicQuota(TopicQuota topicQuota, Integer access) {
        TopicDO topicDO = topicManagerService.getByTopicName(topicQuota.getClusterId(), topicQuota.getTopicName());
        if (ValidateUtils.isNull(topicDO)) {
            topicDO = new TopicDO();
        }
        TopicQuota newTopicQuota = allocateQuotaStrategy.getNewTopicQuota(topicQuota, access, topicDO.getPeakBytesIn());
        return addTopicQuota(newTopicQuota);
    }

    @Override
    public TopicQuota getQuotaFromZk(Long clusterId, String topicName, String appId) {
        TopicQuotaData quotaData = KafkaZookeeperUtils.getTopicQuota(
                PhysicalClusterMetadataManager.getZKConfig(clusterId),
                appId,
                topicName
        );
        if (ValidateUtils.isNull(quotaData)) {
            return null;
        }
        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setClusterId(clusterId);
        topicQuotaDO.setTopicName(topicName);
        topicQuotaDO.setAppId(appId);
        topicQuotaDO.setConsumeQuota(NumberUtils.string2Long(quotaData.getConsumer_byte_rate()));
        topicQuotaDO.setProduceQuota(NumberUtils.string2Long(quotaData.getProducer_byte_rate()));
        return topicQuotaDO;
    }

    @Override
    public Boolean modifyProduceQuota(Long clusterId, String topicName, String appId, Long produceQuota) {
        TopicQuota topicQuotaDO = this.getQuotaFromZk(clusterId, topicName, appId);
        if (ValidateUtils.isNull(topicQuotaDO)) {
            return Boolean.FALSE;
        }

        topicQuotaDO.setProduceQuota(produceQuota);
        if (this.addTopicQuota(topicQuotaDO) < 1) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}