package com.xiaojukeji.know.streaming.km.core.service.topic.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaTopicConfigParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.kafka.*;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaConfigConverter;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Triple;
import com.xiaojukeji.know.streaming.km.common.utils.VersionUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicConfigService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.server.ConfigType;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;


@Service
public class TopicConfigServiceImpl extends BaseKafkaVersionControlService implements TopicConfigService {
    private static final ILog log = LogFactory.getLog(TopicConfigServiceImpl.class);

    private static final String GET_TOPIC_CONFIG                    = "getTopicConfig";
    private static final String MODIFY_TOPIC_CONFIG                 = "modifyTopicConfig";

    /**
     * Topic的配置支持的版本信息
     */
    private static final List<Triple<Long/*minSupportVersion*/, Long/*maxSupportVersion*/, List<Properties>>> TOPIC_CONFIG_NAMES_AND_DOCS = Arrays.asList(
            new Triple<>(0L, V_0_10_1_0.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig0100.class)),
            new Triple<>(V_0_10_1_0.getVersionL(), V_0_10_2_0.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig0101.class)),
            new Triple<>(V_0_10_2_0.getVersionL(), V_0_11_0_0.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig0102.class)),

            new Triple<>(V_0_11_0_0.getVersionL(), V_1_0_0.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig0110.class)),

            new Triple<>(V_1_0_0.getVersionL(), V_1_1_0.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig1000.class)),
            new Triple<>(V_1_1_0.getVersionL(), V_2_0_0.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig1100.class)),

            new Triple<>(V_2_0_0.getVersionL(), V_2_5_0.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig2000.class)),
            new Triple<>(V_2_5_0.getVersionL(), V_MAX.getVersionL(), AbstractTopicConfig.getTopicConfigNamesAndDocs(TopicConfig2500.class))
    );

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.SERVICE_OP_TOPIC_CONFIG;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(GET_TOPIC_CONFIG,     V_0_10_0_0, V_0_11_0_0, "getTopicConfigByZKClient",            this::getTopicConfigByZKClient);
        registerVCHandler(GET_TOPIC_CONFIG,     V_0_11_0_0, V_MAX,      "getTopicConfigByKafkaClient",         this::getTopicConfigByKafkaClient);

        registerVCHandler(MODIFY_TOPIC_CONFIG,     V_0_10_0_0, V_0_10_2_0,  "modifyTopicConfigByZKClientAndNodeVersionV1",       this::modifyTopicConfigByZKClientAndNodeVersionV1);
        registerVCHandler(MODIFY_TOPIC_CONFIG,     V_0_10_2_0, V_2_3_0,     "modifyTopicConfigByZKClientAndNodeVersionV2",       this::modifyTopicConfigByZKClientAndNodeVersionV2);
        registerVCHandler(MODIFY_TOPIC_CONFIG,     V_2_3_0, V_MAX,          "modifyTopicConfigByKafkaClient",                    this::modifyTopicConfigByKafkaClient);
    }

    @Override
    public Result<Void> modifyTopicConfig(KafkaTopicConfigParam kafkaTopicConfigParam, String operator) {
        Result<Void> rv = null;
        try {
            rv = (Result<Void>) doVCHandler(kafkaTopicConfigParam.getClusterPhyId(), MODIFY_TOPIC_CONFIG, kafkaTopicConfigParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }

        if (rv == null || rv.failed()) {
            return rv;
        }

        // 记录操作
        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.EDIT.getDesc(),
                ModuleEnum.KAFKA_TOPIC_CONFIG.getDesc(),
                MsgConstant.getTopicBizStr(kafkaTopicConfigParam.getClusterPhyId(), kafkaTopicConfigParam.getTopicName()),
                ConvertUtil.obj2Json(kafkaTopicConfigParam)
        ));

        return rv;
    }

    @Override
    public Result<List<KafkaConfigDetail>> getTopicConfigDetailFromKafka(Long clusterPhyId, String topicName) {
        try {
            return (Result<List<KafkaConfigDetail>>) doVCHandler(clusterPhyId, GET_TOPIC_CONFIG, new TopicParam(clusterPhyId, topicName));
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }
    }

    @Override
    public Result<Map<String, String>> getTopicConfigFromKafka(Long clusterPhyId, String topicName) {
        Result<List<KafkaConfigDetail>> configResult = this.getTopicConfigDetailFromKafka(clusterPhyId, topicName);
        if (configResult.failed()) {
            return Result.buildFromIgnoreData(configResult);
        }

        return Result.buildSuc(configResult.getData().stream().filter(elem -> elem.getValue() != null).collect(Collectors.toMap(KafkaConfigDetail::getName, KafkaConfigDetail::getValue)));
    }

    @Override
    public List<Properties> getConfigNamesAndDocs(Long clusterPhyId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            // 默认
            return TOPIC_CONFIG_NAMES_AND_DOCS.get(0).v3();
        }

        Long clusterVersionL = VersionUtil.normailze(clusterPhy.getKafkaVersion());
        for (Triple<Long, Long, List<Properties>> elem: TOPIC_CONFIG_NAMES_AND_DOCS) {
            if (elem.v1() <= clusterVersionL && clusterVersionL <= elem.v2()) {
                return elem.v3();
            }
        }

        return TOPIC_CONFIG_NAMES_AND_DOCS.get(0).v3();
    }


    /**************************************************** private method ****************************************************/


    private Result<List<KafkaConfigDetail>> getTopicConfigByZKClient(VersionItemParam itemParam) {
        TopicParam param = (TopicParam) itemParam;

        Result<Properties> propertiesResult = this.getTopicConfigByZKClient(param.getClusterPhyId(), param.getTopicName());
        if (propertiesResult.failed()) {
            return Result.buildFromIgnoreData(propertiesResult);
        }

        return Result.buildSuc(KafkaConfigConverter.convert2KafkaTopicConfigDetailList(
                this.getConfigNamesAndDocs(param.getClusterPhyId()),
                propertiesResult.getData()
        ));
    }

    private Result<Properties> getTopicConfigByZKClient(Long clusterPhyId, String topicName) {
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

            Properties properties = kafkaZkClient.getEntityConfigs("topics", topicName);
            for (Object key: properties.keySet()) {
                properties.getProperty((String) key);
            }

            return Result.buildSuc(properties);
        } catch (NotExistException nee) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(clusterPhyId, topicName));
        } catch (Exception e) {
            log.error("method=getTopicConfigByZKClient||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhyId, topicName, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<KafkaConfigDetail>> getTopicConfigByKafkaClient(VersionItemParam itemParam) {
        TopicParam param = (TopicParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, param.getTopicName());
            DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(
                    Collections.singletonList(configResource),
                    buildDescribeConfigsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            Map<ConfigResource, Config> configMap = describeConfigsResult.all().get();

            return Result.buildSuc(
                    KafkaConfigConverter.convert2KafkaConfigDetailList(configMap.get(configResource))
            );
        } catch (NotExistException nee) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(param.getClusterPhyId(), param.getTopicName()));
        } catch (Exception e) {
            log.error("method=getTopicConfigByKafkaClient||param={}||errMsg=exception!", param, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private DescribeConfigsOptions buildDescribeConfigsOptions() {
        return new DescribeConfigsOptions().includeDocumentation(false).includeSynonyms(false).timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS);
    }

    private Result<Void> modifyTopicConfigByKafkaClient(VersionItemParam itemParam) {
        KafkaTopicConfigParam kafkaTopicConfigParam = (KafkaTopicConfigParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(kafkaTopicConfigParam.getClusterPhyId());

            List<AlterConfigOp> configOpList = new ArrayList<>();
            for (Map.Entry<String, String> changedConfig: kafkaTopicConfigParam.getChangedProps().entrySet()) {
                configOpList.add(new AlterConfigOp(new ConfigEntry(changedConfig.getKey(), changedConfig.getValue()), AlterConfigOp.OpType.SET));
            }

            Map<ConfigResource, Collection<AlterConfigOp>> configMap = new HashMap<>();
            configMap.put(new ConfigResource(ConfigResource.Type.TOPIC, kafkaTopicConfigParam.getTopicName()), configOpList);

            AlterConfigsResult alterConfigsResult = adminClient.incrementalAlterConfigs(configMap, new AlterConfigsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            alterConfigsResult.all().get();
        } catch (Exception e) {
            log.error("method=modifyTopicConfigByKafkaClient||param={}||errMsg={}", kafkaTopicConfigParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
        return Result.buildSuc();
    }

    private Result<Void> modifyTopicConfigByZKClientAndNodeVersionV1(VersionItemParam itemParam) {
        KafkaTopicConfigParam kafkaTopicConfigParam = (KafkaTopicConfigParam) itemParam;

        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(kafkaTopicConfigParam.getClusterPhyId());
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(kafkaTopicConfigParam.getClusterPhyId());

            Properties allProps = new Properties();

            // 历史配置 + 当前修改的配置 -》获取到最终的配置
            allProps.putAll(kafkaZkClient.getEntityConfigs(ConfigType.Topic(), kafkaTopicConfigParam.getTopicName()));
            allProps.putAll(kafkaTopicConfigParam.getChangedProps());
            allProps.putAll(kafkaZkClient.getEntityConfigs(ConfigType.Topic(), kafkaTopicConfigParam.getTopicName()));

            // 检查参数是否合法
            adminZkClient.validateTopicConfig(kafkaTopicConfigParam.getTopicName(), allProps);

            // 修改配置的数据节点
            kafkaZkClient.setOrCreateEntityConfigs(ConfigType.Topic(), kafkaTopicConfigParam.getTopicName(), allProps);

            // 修改配置的通知节点
            kafkaZKDAO.createConfigChangeNotificationVersionOne(kafkaTopicConfigParam.getClusterPhyId(), ConfigType.Topic(), kafkaTopicConfigParam.getTopicName());
        } catch (Exception e) {
            log.error("method=modifyTopicConfigByZKClientAndNodeVersionV1||param={}||errMsg=exception!", kafkaTopicConfigParam, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> modifyTopicConfigByZKClientAndNodeVersionV2(VersionItemParam itemParam) {
        KafkaTopicConfigParam kafkaTopicConfigParam = (KafkaTopicConfigParam) itemParam;
        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(kafkaTopicConfigParam.getClusterPhyId());
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(kafkaTopicConfigParam.getClusterPhyId());

            // 获取ZK配置
            Properties properties = kafkaZkClient.getEntityConfigs(ConfigType.Topic(), kafkaTopicConfigParam.getTopicName());
            properties.putAll(kafkaTopicConfigParam.getChangedProps());

            // 修改配置
            adminZkClient.changeTopicConfig(kafkaTopicConfigParam.getTopicName(), properties);
        } catch (Exception e) {
            log.error("method=modifyTopicConfigByZKClient||param={}||errMsg={}", kafkaTopicConfigParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
        return Result.buildSuc();
    }
}
