package com.xiaojukeji.know.streaming.km.rebalance.core.service;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobConfigPO;

@EnterpriseLoadReBalance
public interface ClusterBalanceJobConfigService {

    /**
     * 新增平衡配置
     * @param clusterBalanceJobConfigPO
     * @return
     */
    Result<Void> replaceClusterBalanceJobConfigByClusterId(ClusterBalanceJobConfigPO clusterBalanceJobConfigPO);

    /**
     *
     * @param clusterId
     * @return
     */
    Result<ClusterBalanceJobConfigPO> getByClusterId(Long clusterId);
}
