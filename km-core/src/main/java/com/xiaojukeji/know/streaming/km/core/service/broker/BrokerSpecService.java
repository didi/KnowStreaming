package com.xiaojukeji.know.streaming.km.core.service.broker;

import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.BrokerSpec;

import java.util.Map;

public interface BrokerSpecService {

    /**
     *
     * @param clusterPhyId
     * @return
     */
    Map<Integer, BrokerSpec> getBrokerSpecMap(Long clusterPhyId);
}
