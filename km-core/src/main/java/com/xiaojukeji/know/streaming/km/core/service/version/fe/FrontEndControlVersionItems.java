package com.xiaojukeji.know.streaming.km.core.service.version.fe;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.WEB_OP;

/**
 * 多版本前端控件差异列表
 */
@Component
public class FrontEndControlVersionItems extends BaseMetricVersionMetric {
    /**
     * 字段内容不可随意更改，因为前端会使用该字段进行页面控制
     */

    private static final String FE_TESTING_PRODUCER_HEADER                              = "FETestingProducerHeader";
    private static final String FE_TESTING_PRODUCER_COMPRESSION_TYPE_Z_STD              = "FETestingProducerCompressionTypeZSTD";

    private static final String FE_TESTING_CONSUMER_HEADER                              = "FETestingConsumerHeader";

    private static final String FE_BROKERS_SPECIFIED_BROKER_CONFIG_LIST                 = "FEBrokersSpecifiedBrokerConfigList";
    private static final String FE_BROKERS_SPECIFIED_BROKER_DATA_LOGS_LIST              = "FEBrokersSpecifiedBrokerDataLogsList";

    private static final String FE_SECURITY_ACL_CREATE_PATTERN_TYPE_PREFIXED            = "FESecurityAclCreatePatternTypePrefixed";
    private static final String FE_SECURITY_ACL_CREATE_RESOURCE_TYPE_TRANSACTIONAL_ID   = "FESecurityAclCreateResourceTypeTransactionalId";
    private static final String FE_SECURITY_ACL_CREATE_RESOURCE_TYPE_DELEGATION_TOKEN   = "FESecurityAclCreateResourceTypeDelegationToken";

    private static final String FE_CREATE_TOPIC_CLEANUP_POLICY                          = "FECreateTopicCleanupPolicy";

    private static final String FE_HA_CREATE_MIRROR_TOPIC                               = "FEHaCreateMirrorTopic";
    private static final String FE_HA_DELETE_MIRROR_TOPIC                               = "FEHaDeleteMirrorTopic";

    private static final String FE_TRUNCATE_TOPIC                                       = "FETruncateTopic";

    private static final String FE_DELETE_GROUP_OFFSET                                  = "FEDeleteGroupOffset";
    private static final String FE_DELETE_GROUP_TOPIC_OFFSET                            = "FEDeleteGroupTopicOffset";
    private static final String FE_DELETE_GROUP_TOPIC_PARTITION_OFFSET                  = "FEDeleteGroupTopicPartitionOffset";

    public FrontEndControlVersionItems() {
        // ignore
    }

    @Override
    public int versionItemType() {
        return WEB_OP.getCode();
    }

    @Override
    public List<VersionControlItem> init(){
        List<VersionControlItem> itemList = new ArrayList<>();

        // 测试-生产者-Header字段
        itemList.add(buildItem().minVersion(VersionEnum.V_0_11_0_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_TESTING_PRODUCER_HEADER).desc("测试-生产者-Header字段支持的版本"));

        // 测试-生产者-ZStd字段
        itemList.add(buildItem().minVersion(VersionEnum.V_2_1_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_TESTING_PRODUCER_COMPRESSION_TYPE_Z_STD).desc("CompressionType中的zstd压缩格式"));

        // 测试-消费者-Header字段
        itemList.add(buildItem().minVersion(VersionEnum.V_0_11_0_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_TESTING_CONSUMER_HEADER).desc("测试-消费者-Header字段支持的版本"));

        // Brokers-具体Broker-Config列表
        itemList.add(buildItem().minVersion(VersionEnum.V_0_10_1_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_BROKERS_SPECIFIED_BROKER_CONFIG_LIST).desc("Brokers-具体Broker-Config列表"));

        // Brokers-具体Broker-DataLogs列表
        itemList.add(buildItem().minVersion(VersionEnum.V_1_0_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_BROKERS_SPECIFIED_BROKER_DATA_LOGS_LIST).desc("Brokers-具体Broker-DataLogs列表"));

        // Security-创建ACL-PatternType-Prefixed
        itemList.add(buildItem().minVersion(VersionEnum.V_2_0_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_SECURITY_ACL_CREATE_PATTERN_TYPE_PREFIXED).desc("Security-创建ACL-PatternType-Prefixed"));

        // Security-创建ACL-ResourceType-TransactionalId
        itemList.add(buildItem().minVersion(VersionEnum.V_0_11_0_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_SECURITY_ACL_CREATE_RESOURCE_TYPE_TRANSACTIONAL_ID).desc("Security-创建ACL-ResourceType-TransactionalId"));

        // Security-创建ACL-ResourceType-DelegationToken
        itemList.add(buildItem().minVersion(VersionEnum.V_1_1_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_SECURITY_ACL_CREATE_RESOURCE_TYPE_DELEGATION_TOKEN).desc("Security-创建ACL-ResourceType-DelegationToken"));

        // topic-创建-清理策略(delete和compact)V_0_10_1_0都可以选择
        itemList.add(buildItem().minVersion(VersionEnum.V_0_10_1_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_CREATE_TOPIC_CLEANUP_POLICY).desc("Topic-创建Topic-Cleanup-Policy"));

        // HA-Topic复制
        itemList.add(buildItem().minVersion(VersionEnum.V_2_5_0_D_300).maxVersion(VersionEnum.V_2_5_0_D_MAX)
                .name(FE_HA_CREATE_MIRROR_TOPIC).desc("HA-创建Topic复制"));
        itemList.add(buildItem().minVersion(VersionEnum.V_2_5_0_D_300).maxVersion(VersionEnum.V_2_5_0_D_MAX)
                .name(FE_HA_DELETE_MIRROR_TOPIC).desc("HA-取消Topic复制"));

        // truncate topic
        itemList.add(buildItem().minVersion(VersionEnum.V_0_11_0_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_TRUNCATE_TOPIC).desc("清空Topic"));

        // 删除Offset
        itemList.add(buildItem().minVersion(VersionEnum.V_2_0_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_DELETE_GROUP_OFFSET).desc("删除GroupOffset"));
        itemList.add(buildItem().minVersion(VersionEnum.V_2_4_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_DELETE_GROUP_TOPIC_OFFSET).desc("删除GroupTopicOffset"));
        itemList.add(buildItem().minVersion(VersionEnum.V_2_4_0).maxVersion(VersionEnum.V_MAX)
                .name(FE_DELETE_GROUP_TOPIC_PARTITION_OFFSET).desc("删除GroupTopicPartitionOffset"));
        return itemList;
    }
}
