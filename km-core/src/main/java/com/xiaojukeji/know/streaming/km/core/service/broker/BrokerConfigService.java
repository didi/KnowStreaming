package com.xiaojukeji.know.streaming.km.core.service.broker;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaBrokerConfigModifyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerConfigPO;

import java.util.Date;
import java.util.List;

/**
 * Kafka相关配置接口
 * @author zengqiao
 * @date 22/03/03
 */
public interface BrokerConfigService {
    /**
     * 获取Broker配置
     */
    Result<List<KafkaConfigDetail>> getBrokerConfigDetailFromKafka(Long clusterPhyId, Integer brokerId);

    Result<Void> modifyBrokerConfig(KafkaBrokerConfigModifyParam kafkaBrokerConfigModifyParam, String operator);

    int countBrokerConfigDiffsFromDB(Long clusterPhyId, List<String> excludeConfigs);

    List<BrokerConfigPO> getBrokerConfigDiffFromDB(Long clusterPhyId, Integer brokerId);

    int replaceBrokerConfigDiff(BrokerConfigPO po);

    int deleteByUpdateTimeBeforeInDB(Long clusterPhyId, Date beforeTime);
}
