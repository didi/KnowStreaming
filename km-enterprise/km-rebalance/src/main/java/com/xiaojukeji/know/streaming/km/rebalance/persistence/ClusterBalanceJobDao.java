/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.rebalance.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobPO;
import org.springframework.stereotype.Repository;

/**
 * 集群均衡任务 Dao
 * 
 * @author fengqiongfeng
 * @date 2022-05-23
 */
@Repository
@EnterpriseLoadReBalance
public interface ClusterBalanceJobDao  extends BaseMapper<ClusterBalanceJobPO> {

    void addClusterBalanceJob(ClusterBalanceJobPO clusterBalanceJobPO);

}
