package com.xiaojukeji.know.streaming.km.core.service.broker.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.broker.BrokerParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaBrokerConfigModifyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerConfigPO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaConfigConverter;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerConfigService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.broker.BrokerConfigDAO;
import kafka.server.ConfigType;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.jdk.javaapi.CollectionConverters;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;


@Service
public class BrokerConfigServiceImpl extends BaseKafkaVersionControlService implements BrokerConfigService {
    private static final ILog log = LogFactory.getLog(BrokerConfigServiceImpl.class);

    private static final String GET_BROKER_CONFIG      = "getBrokerConfig";
    private static final String MODIFY_BROKER_CONFIG   = "modifyBrokerConfig";

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private BrokerConfigDAO brokerConfigDAO;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.SERVICE_OP_BROKER_CONFIG;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(GET_BROKER_CONFIG,     V_0_10_1_0, V_0_11_0_0, "getBrokerConfigByZKClient",       this::getBrokerConfigByZKClient);
        registerVCHandler(GET_BROKER_CONFIG,     V_0_11_0_0, V_MAX,      "getBrokerConfigByKafkaClient",    this::getBrokerConfigByKafkaClient);

        registerVCHandler(MODIFY_BROKER_CONFIG,     V_0_10_1_0, V_2_3_0, "modifyBrokerConfigByZKClient",       this::modifyBrokerConfigByZKClient);
        registerVCHandler(MODIFY_BROKER_CONFIG,     V_2_3_0, V_MAX,      "modifyBrokerConfigByKafkaClient",    this::modifyBrokerConfigByKafkaClient);
    }

    @Override
    public Result<List<KafkaConfigDetail>> getBrokerConfigDetailFromKafka(Long clusterPhyId, Integer brokerId) {
        try {
            return (Result<List<KafkaConfigDetail>>) doVCHandler(clusterPhyId, GET_BROKER_CONFIG, new BrokerParam(clusterPhyId, brokerId));
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }
    }

    @Override
    public Result<Void> modifyBrokerConfig(KafkaBrokerConfigModifyParam kafkaBrokerConfigModifyParam, String operator) {
        Result<Void> rv = null;
        try {
            rv = (Result<Void>) versionControlService.doHandler(getVersionItemType(),
                    getMethodName(kafkaBrokerConfigModifyParam.getClusterPhyId(), MODIFY_BROKER_CONFIG), kafkaBrokerConfigModifyParam);
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
                ModuleEnum.KAFKA_BROKER_CONFIG.getDesc(),
                MsgConstant.getBrokerBizStr(kafkaBrokerConfigModifyParam.getClusterPhyId(), kafkaBrokerConfigModifyParam.getBrokerId()),
                ConvertUtil.obj2Json(kafkaBrokerConfigModifyParam)
        ));

        return rv;
    }

    @Override
    public int countBrokerConfigDiffsFromDB(Long clusterPhyId, List<String> excludeConfigs) {
        LambdaQueryWrapper<BrokerConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BrokerConfigPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.notIn(BrokerConfigPO::getConfigName, excludeConfigs);

        return brokerConfigDAO.selectCount(lambdaQueryWrapper);
    }

    @Override
    public List<BrokerConfigPO> getBrokerConfigDiffFromDB(Long clusterPhyId, Integer brokerId) {
        LambdaQueryWrapper<BrokerConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BrokerConfigPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(BrokerConfigPO::getBrokerId, brokerId);

        return brokerConfigDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public int replaceBrokerConfigDiff(BrokerConfigPO po) {
        return brokerConfigDAO.replace(po);
    }

    @Override
    public int deleteByUpdateTimeBeforeInDB(Long clusterPhyId, Date beforeTime) {
        LambdaQueryWrapper<BrokerConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BrokerConfigPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.le(BrokerConfigPO::getUpdateTime, beforeTime);

        return brokerConfigDAO.delete(lambdaQueryWrapper);
    }

    /**************************************************** private method ****************************************************/


    private DescribeConfigsOptions buildDescribeConfigsOptions() {
        return new DescribeConfigsOptions().includeDocumentation(false).includeSynonyms(false).timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS);
    }

    private Result<List<KafkaConfigDetail>> getBrokerConfigByZKClient(VersionItemParam itemParam) {
        BrokerParam param = (BrokerParam) itemParam;
        Result<Properties> propertiesResult = this.getBrokerConfigByZKClient(param.getClusterPhyId(), param.getBrokerId());
        if (propertiesResult.failed()) {
            return Result.buildFromIgnoreData(propertiesResult);
        }

        return Result.buildSuc(KafkaConfigConverter.convert2KafkaBrokerConfigDetailList(
                new ArrayList<>(),
                propertiesResult.getData()
        ));
    }

    private Result<Properties> getBrokerConfigByZKClient(Long clusterPhyId, Integer brokerId) {
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

            Properties properties = kafkaZkClient.getEntityConfigs(ConfigType.Broker(), String.valueOf(brokerId));
            for (Object key: properties.keySet()) {
                properties.getProperty((String) key);
            }

            return Result.buildSuc(properties);
        } catch (NotExistException nee) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getBrokerNotExist(clusterPhyId, brokerId));
        } catch (Exception e) {
            log.error("method=getBrokerConfigByZKClient||clusterPhyId={}||brokerId={}||errMsg=exception", clusterPhyId, brokerId, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<KafkaConfigDetail>> getBrokerConfigByKafkaClient(VersionItemParam itemParam) {
        BrokerParam param = (BrokerParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER, String.valueOf(param.getBrokerId()));
            DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(
                    Arrays.asList(configResource),
                    buildDescribeConfigsOptions()
            );

            Map<ConfigResource, Config> configMap = describeConfigsResult.all().get();

            return Result.buildSuc(KafkaConfigConverter.convert2KafkaConfigDetailList(
                    configMap.get(configResource)
            ));
        } catch (NotExistException nee) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getBrokerNotExist(param.getClusterPhyId(), param.getBrokerId()));
        } catch (Exception e) {
            log.error("method=getBrokerConfigByKafkaClient||param={}||errMsg=exception!", param, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> modifyBrokerConfigByZKClient(VersionItemParam itemParam) {
        KafkaBrokerConfigModifyParam configParam = (KafkaBrokerConfigModifyParam) itemParam;
        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(configParam.getClusterPhyId());
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(configParam.getClusterPhyId());

            // 获取ZK配置
            Properties properties = kafkaZkClient.getEntityConfigs(ConfigType.Broker(), String.valueOf(configParam.getBrokerId()));
            properties.putAll(configParam.getChangedProps());

            // 修改配置
            adminZkClient.changeBrokerConfig(CollectionConverters.asScala(Arrays.asList(configParam.getBrokerId())), properties);
        } catch (Exception e) {
            log.error("method=modifyBrokerConfigByZKClientAndNodeVersionV2||param={}||errMsg=exception.", configParam, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
        return Result.buildSuc();
    }

    private Result<Void> modifyBrokerConfigByKafkaClient(VersionItemParam itemParam) {
        KafkaBrokerConfigModifyParam configParam = (KafkaBrokerConfigModifyParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(configParam.getClusterPhyId());

            List<AlterConfigOp> configOpList = new ArrayList<>();
            for (Map.Entry<String, String> changedConfig: configParam.getChangedProps().entrySet()) {
                configOpList.add(new AlterConfigOp(new ConfigEntry(changedConfig.getKey(), changedConfig.getValue()), AlterConfigOp.OpType.SET));
            }

            Map<ConfigResource, Collection<AlterConfigOp>> configMap = new HashMap<>();
            configMap.put(new ConfigResource(ConfigResource.Type.BROKER, String.valueOf(configParam.getBrokerId())), configOpList);

            AlterConfigsResult alterConfigsResult = adminClient.incrementalAlterConfigs(configMap, new AlterConfigsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            alterConfigsResult.all().get();
        } catch (Exception e) {
            log.error("method=modifyBrokerConfigByKafkaClient||param={}||errMsg=exception.", configParam, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
        return Result.buildSuc();
    }
}
