package com.xiaojukeji.know.streaming.km.biz.broker;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaBrokerConfigModifyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka.KafkaBrokerConfigVO;

import java.util.List;

public interface BrokerConfigManager {
    /**
     * 获取Broker配置详细信息
     * @param clusterPhyId 物理集群ID
     * @param brokerId brokerId
     * @return
     */
    Result<List<KafkaBrokerConfigVO>> getBrokerConfigDetail(Long clusterPhyId, Integer brokerId);

    Result<Void> modifyBrokerConfig(KafkaBrokerConfigModifyParam modifyParam, String operator);
}
