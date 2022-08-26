package com.xiaojukeji.know.streaming.km.biz.broker.impl;

import com.xiaojukeji.know.streaming.km.biz.broker.BrokerConfigManager;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaBrokerConfigModifyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerConfigPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka.KafkaBrokerConfigVO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigDiffTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerConfigService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import org.apache.kafka.common.config.ConfigDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BrokerConfigManagerImpl implements BrokerConfigManager {
    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerConfigService brokerConfigService;

    @Override
    public Result<List<KafkaBrokerConfigVO>> getBrokerConfigDetail(Long clusterPhyId, Integer brokerId) {
        // 获取当前broker配置
        Result<List<KafkaConfigDetail>> configResult = brokerConfigService.getBrokerConfigDetailFromKafka(clusterPhyId, brokerId);
        if (configResult.failed()) {
            return Result.buildFromIgnoreData(configResult);
        }

        // 获取差异的配置
        List<BrokerConfigPO> diffPOList = brokerConfigService.getBrokerConfigDiffFromDB(clusterPhyId, brokerId);

        // 组装数据
        return Result.buildSuc(this.convert2KafkaBrokerConfigVOList(configResult.getData(), diffPOList));
    }

    private List<KafkaBrokerConfigVO> convert2KafkaBrokerConfigVOList(List<KafkaConfigDetail> configList, List<BrokerConfigPO> diffPOList) {
        if (ValidateUtils.isEmptyList(configList)) {
            return new ArrayList<>();
        }

        Map<String, BrokerConfigPO> poMap = diffPOList.stream().collect(Collectors.toMap(BrokerConfigPO::getConfigName, Function.identity()));

        List<KafkaBrokerConfigVO> voList = ConvertUtil.list2List(configList, KafkaBrokerConfigVO.class);
        for (KafkaBrokerConfigVO vo: voList) {
            BrokerConfigPO po = poMap.get(vo.getName());
            if (po != null) {
                vo.setExclusive(po.getDiffType().equals(ConfigDiffTypeEnum.ALONE_POSSESS.getCode()));
                vo.setDifferentiated(po.getDiffType().equals(ConfigDiffTypeEnum.UN_EQUAL.getCode()));
            } else {
                vo.setExclusive(false);
                vo.setDifferentiated(false);
            }

            ConfigDef.ConfigKey configKey = KafkaConstant.KAFKA_ALL_CONFIG_DEF_MAP.get(vo.getName());
            if (configKey == null) {
                continue;
            }

            try {
                vo.setDocumentation(configKey.documentation);
                vo.setDefaultValue(configKey.defaultValue.toString());
            } catch (Exception e) {
                // ignore
            }
        }
        return voList;
    }

    @Override
    public Result<Void> modifyBrokerConfig(KafkaBrokerConfigModifyParam modifyParam, String operator) {
        if (modifyParam.getApplyAll() == null || !modifyParam.getApplyAll()) {
            return brokerConfigService.modifyBrokerConfig(modifyParam, operator);
        }

        List<Broker> brokerList = brokerService.listAliveBrokersFromDB(modifyParam.getClusterPhyId());
        for (Broker broker: brokerList) {
            modifyParam.setBrokerId(broker.getBrokerId());
            Result<Void> rv = brokerConfigService.modifyBrokerConfig(modifyParam, operator);
            if (rv.failed()) {
                return rv;
            }
        }

        return Result.buildSuc();
    }
}
