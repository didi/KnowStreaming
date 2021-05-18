package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.utils.NumberUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.config.TopicQuotaData;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
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

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AuthorityService authorityService;

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

    @Override
    public ResultStatus addTopicQuotaByAuthority(TopicQuota topicQuota) {
        // 获取物理集群id
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(topicQuota.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        // 权限判断(access 0:无权限, 1:读, 2:写, 3:读写，4:可管理)
        AuthorityDO authority = authorityService.getAuthority(physicalClusterId,
            topicQuota.getTopicName(), topicQuota.getAppId());
        if (ValidateUtils.isNull(authority) || authority.getAccess() == TopicAuthorityEnum.DENY.getCode()) {
            return ResultStatus.USER_WITHOUT_AUTHORITY;
        }
        if (authority.getAccess() == TopicAuthorityEnum.READ.getCode()) {
            // 可以消费
            topicQuota.setProduceQuota(null);
        }
        if (authority.getAccess() == TopicAuthorityEnum.WRITE.getCode()) {
            // 可以生产
            topicQuota.setConsumeQuota(null);
        }
        // 设置物理集群id
        topicQuota.setClusterId(physicalClusterId);
        // 添加配额
        if (addTopicQuota(topicQuota) > 0) {
            return ResultStatus.SUCCESS;
        }
        return ResultStatus.ZOOKEEPER_WRITE_FAILED;
    }
}