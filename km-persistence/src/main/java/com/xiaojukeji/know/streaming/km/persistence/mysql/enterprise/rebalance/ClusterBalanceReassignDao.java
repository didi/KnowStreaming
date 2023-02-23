/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.persistence.mysql.enterprise.rebalance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po.ClusterBalanceReassignPO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 集群平衡迁移详情 Dao
 * 
 * @author fengqiongfeng
 * @date 2022-05-23
 */
@Repository
@EnterpriseLoadReBalance
public interface ClusterBalanceReassignDao extends BaseMapper<ClusterBalanceReassignPO> {

    int addBatch(List<ClusterBalanceReassignPO> reassignPOList);
}
