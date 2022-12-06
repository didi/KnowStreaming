package com.xiaojukeji.know.streaming.km.biz.topic.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.topic.TopicConfigManager;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaTopicDefaultConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.broker.BrokerParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaConfigConverter;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerConfigService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicConfigService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;

@Component
public class TopicConfigManagerImpl extends BaseKafkaVersionControlService implements TopicConfigManager {
    private static final ILog log = LogFactory.getLog(TopicConfigManagerImpl.class);

    private static final String GET_DEFAULT_TOPIC_CONFIG       = "getDefaultTopicConfig";

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerConfigService brokerConfigService;

    @Autowired
    private TopicConfigService topicConfigService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.SERVICE_OP_TOPIC_CONFIG;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(GET_DEFAULT_TOPIC_CONFIG,     V_0_10_0_0, V_0_11_0_0, "getDefaultTopicConfigByLocal",         this::getDefaultTopicConfigByLocal);
        registerVCHandler(GET_DEFAULT_TOPIC_CONFIG,     V_0_11_0_0, V_MAX,      "getDefaultTopicConfigByClient",        this::getDefaultTopicConfigByClient);
    }

    @Override
    public Result<List<KafkaTopicDefaultConfig>> getDefaultTopicConfig(Long clusterPhyId) {
        try {
            List<Broker> aliveBrokerList = brokerService.listAliveBrokersFromDB(clusterPhyId);
            Integer aliveBrokerId = null;
            if (!aliveBrokerList.isEmpty()) {
                aliveBrokerId = aliveBrokerList.get(0).getBrokerId();
            }

            return (Result<List<KafkaTopicDefaultConfig>>) doVCHandler(clusterPhyId, GET_DEFAULT_TOPIC_CONFIG, new BrokerParam(clusterPhyId, aliveBrokerId));
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }
    }

    private Result<List<KafkaTopicDefaultConfig>> getDefaultTopicConfigByLocal(VersionItemParam itemParam) {
        BrokerParam brokerParam = (BrokerParam) itemParam;
        return Result.buildSuc(KafkaConfigConverter.convert2KafkaTopicDefaultConfigList(
                topicConfigService.getConfigNamesAndDocs(brokerParam.getClusterPhyId()),
                new HashMap<>()
        ));
    }

    private Result<List<KafkaTopicDefaultConfig>> getDefaultTopicConfigByClient(VersionItemParam itemParam) {
        BrokerParam brokerParam = (BrokerParam) itemParam;

        Result<List<KafkaConfigDetail>> defaultConfigResult = brokerConfigService.getBrokerConfigDetailFromKafka(brokerParam.getClusterPhyId(), brokerParam.getBrokerId());
        if (defaultConfigResult.failed()) {
            // 获取配置错误，但是不直接返回
            log.error("method=getDefaultTopicConfigByClient||param={}||result={}.", brokerParam, defaultConfigResult);
        }

        return Result.buildSuc(KafkaConfigConverter.convert2KafkaTopicDefaultConfigList(
                topicConfigService.getConfigNamesAndDocs(brokerParam.getClusterPhyId()),
                !defaultConfigResult.hasData()?
                        new HashMap<>():
                        defaultConfigResult.getData().stream().filter(elem -> !ValidateUtils.isNull(elem.getValue())).collect(Collectors.toMap(KafkaConfigDetail::getName, KafkaConfigDetail::getValue))
                )
        );
    }
}
