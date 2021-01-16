package com.xiaojukeji.kafka.manager.task.dispatch.op;

import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.component.EmptyEntry;
import com.xiaojukeji.kafka.manager.task.config.SyncTopic2DBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 定期将未落盘的Topic刷新到DB中, 仅存储对应的关系, 并不会增加权限等信息
 * @author zengqiao
 * @date 19/12/29
 */
@Component
@CustomScheduled(name = "syncTopic2DB", cron = "0 0/2 * * * ?", threadNum = 1)
@ConditionalOnProperty(prefix = "task.op", name = "sync-topic-enabled", havingValue = "true", matchIfMissing = false)
public class SyncTopic2DB extends AbstractScheduledTask<EmptyEntry> {
    private static final  Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    private static final String SYNC_TOPIC_2_DB_CONFIG_KEY = "SYNC_TOPIC_2_DB_CONFIG_KEY";

    @Autowired
    private AppService appService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Override
    public List<EmptyEntry> listAllTasks() {
        EmptyEntry emptyEntry = new EmptyEntry();
        emptyEntry.setId(System.currentTimeMillis() / 1000);
        return Arrays.asList(emptyEntry);
    }

    @Override
    public void processTask(EmptyEntry entryEntry) {
        Map<Long, SyncTopic2DBConfig> clusterIdConfigMap = getConfig();
        if (ValidateUtils.isEmptyMap(clusterIdConfigMap)) {
            LOGGER.warn("class=SyncTopic2DB||method=processTask||msg=without config or config illegal");
            return;
        }
        LOGGER.info("class=SyncTopic2DB||method=processTask||data={}||msg=start sync", JsonUtils.toJSONString(clusterIdConfigMap));

        List<ClusterDO> clusterDOList = clusterService.list();
        if (ValidateUtils.isEmptyList(clusterDOList)) {
            return;
        }

        for (ClusterDO clusterDO: clusterDOList) {
            if (!clusterIdConfigMap.containsKey(clusterDO.getId())) {
                continue;
            }
            try {
                syncTopic2DB(clusterDO.getId(), clusterIdConfigMap.get(clusterDO.getId()));
            } catch (Exception e) {
                LOGGER.error("class=SyncTopic2DB||method=processTask||clusterId={}||errMsg={}||msg=sync failed", clusterDO.getId(), e.getMessage());
            }
        }
    }

    private void syncTopic2DB(Long clusterId, SyncTopic2DBConfig syncTopic2DBConfig) {
        List<TopicDO> doList = topicManagerService.getByClusterId(clusterId);
        if (ValidateUtils.isNull(doList)) {
            doList = new ArrayList<>();
        }
        Set<String> existedTopicNameSet = doList.stream().map(elem -> elem.getTopicName()).collect(Collectors.toSet());

        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            if (existedTopicNameSet.contains(topicName)
                    || KafkaConstant.COORDINATOR_TOPIC_NAME.equals(topicName)
                    || KafkaConstant.TRANSACTION_TOPIC_NAME.equals(topicName)) {
                continue;
            }

            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }

            // 新创建10分钟内的Topic不进行同步, 避免KM平台上新建的, 有所属应用的Topic被错误的同步了
            if (System.currentTimeMillis() - topicMetadata.getCreateTime() < 10 * 60 * 1000) {
                continue;
            }

            TopicDO topicDO = new TopicDO();
            topicDO.setAppId(syncTopic2DBConfig.getDefaultAppId());
            topicDO.setClusterId(clusterId);
            topicDO.setTopicName(topicName);
            topicDO.setDescription("定期同步至DB中的无主Topic");
            topicDO.setPeakBytesIn(TopicCreationConstant.DEFAULT_QUOTA);
            topicManagerService.addTopic(topicDO);

            if (ValidateUtils.isNull(syncTopic2DBConfig.isAddAuthority()) || !syncTopic2DBConfig.isAddAuthority()) {
                // 不增加权限信息, 则直接忽略
                return;
            }

            // TODO 当前添加 Topic 和 添加 Authority 是非事务的, 中间出现异常之后, 会导致数据错误, 后续还需要优化一下
            AuthorityDO authorityDO = new AuthorityDO();
            authorityDO.setAppId(syncTopic2DBConfig.getDefaultAppId());
            authorityDO.setClusterId(clusterId);
            authorityDO.setTopicName(topicName);
            authorityDO.setAccess(TopicAuthorityEnum.READ_WRITE.getCode());
            authorityService.addAuthority(authorityDO);
        }
    }

    private Map<Long, SyncTopic2DBConfig> getConfig() {
        List<SyncTopic2DBConfig> configList = configService.getArrayByKey(SYNC_TOPIC_2_DB_CONFIG_KEY, SyncTopic2DBConfig.class);
        if (ValidateUtils.isEmptyList(configList)) {
            return Collections.EMPTY_MAP;
        }

        Map<Long, SyncTopic2DBConfig> clusterIdConfigMap = new HashMap<>();
        for (SyncTopic2DBConfig syncTopic2DBConfig: configList) {
            if (ValidateUtils.isNullOrLessThanZero(syncTopic2DBConfig.getClusterId())
                || ValidateUtils.isBlank(syncTopic2DBConfig.getDefaultAppId())) {
                continue;
            }

            AppDO appDO = appService.getByAppId(syncTopic2DBConfig.getDefaultAppId());
            if (ValidateUtils.isNull(appDO)) {
                continue;
            }

            clusterIdConfigMap.put(syncTopic2DBConfig.getClusterId(), syncTopic2DBConfig);
        }
        return clusterIdConfigMap;
    }
}
