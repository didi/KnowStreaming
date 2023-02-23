package com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.BrokerSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po.ClusterBalanceJobConfigPO;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerSpecService;
import com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service.ClusterBalanceJobConfigService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.enterprise.rebalance.ClusterBalanceJobConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@EnterpriseLoadReBalance
public class ClusterBalanceJobConfigServiceImpl implements ClusterBalanceJobConfigService {
    private static final ILog logger = LogFactory.getLog(ClusterBalanceJobConfigServiceImpl.class);

    @Autowired
    private ClusterBalanceJobConfigDao clusterBalanceJobConfigDao;

    @Autowired
    private BrokerSpecService brokerSpecService;

    @Autowired
    private BrokerService brokerService;

    @Override
    public Result<Void> replaceClusterBalanceJobConfigByClusterId(ClusterBalanceJobConfigPO clusterBalanceJobConfigPO) {
        List<Broker> brokers = brokerService.listAllBrokersFromDB(clusterBalanceJobConfigPO.getClusterId());
        Map<Integer, BrokerSpec> brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterBalanceJobConfigPO.getClusterId());
        for(Broker broker: brokers){
            if (brokerSpecMap.get(broker.getBrokerId())==null){
                return Result.buildFrom(ResultStatus.CLUSTER_SPEC_INCOMPLETE);
            }
        }

        try {
            LambdaQueryWrapper<ClusterBalanceJobConfigPO> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(ClusterBalanceJobConfigPO::getClusterId, clusterBalanceJobConfigPO.getClusterId());
            ClusterBalanceJobConfigPO oldConfig = clusterBalanceJobConfigDao.selectOne(queryWrapper);

            int count;
            if (oldConfig == null){
                count = clusterBalanceJobConfigDao.insert(clusterBalanceJobConfigPO);

            }else{
                clusterBalanceJobConfigPO.setId(oldConfig.getId());
                count = clusterBalanceJobConfigDao.updateById(clusterBalanceJobConfigPO);
            }
            if (count < 1){
                logger.error("replace cluster balance job config detail failed! clusterBalanceJobConfigPO:{}", clusterBalanceJobConfigPO);
                return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
            }

        }catch (Exception e){
            logger.error("replace cluster balance job config failed! clusterBalanceJobConfigPO:{}", clusterBalanceJobConfigPO, e);
            return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);

        }
        return Result.buildSuc();
    }

    @Override
    public Result<ClusterBalanceJobConfigPO> getByClusterId(Long clusterId) {
        ClusterBalanceJobConfigPO queryParam = new ClusterBalanceJobConfigPO();
        queryParam.setClusterId(clusterId);
        return Result.buildSuc(clusterBalanceJobConfigDao.selectOne(new QueryWrapper<>(queryParam)));
    }
}
