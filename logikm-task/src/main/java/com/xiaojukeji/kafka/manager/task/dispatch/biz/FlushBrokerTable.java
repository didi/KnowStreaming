package com.xiaojukeji.kafka.manager.task.dispatch.biz;

import com.xiaojukeji.kafka.manager.common.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 刷新BrokerTable数据
 * 1. 依赖DB计算Broker的峰值均值流量
 * 2. 数据更新到DB
 *
 * @author zengqiao
 * @date 20/6/2
 */
@CustomScheduled(name = "flushBrokerTable", cron = "0 0 0/1 * * ?", threadNum = 1)
public class FlushBrokerTable extends AbstractScheduledTask<ClusterDO> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private ClusterService clusterService;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        Integer duration = 10;
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 24 * 60 * 60 * 1000);

        Map<Long, Map<Integer, BrokerDO>> allBrokerMap = getBrokerMap();
        try {
            execute(clusterDO.getId(),
                    duration,
                    startTime,
                    endTime,
                    allBrokerMap.getOrDefault(clusterDO.getId(), new HashMap<>(0))
            );
        } catch (Exception e) {
            LOGGER.error("flush broker table failed, clusterId:{}.", clusterDO.getId(), e);
        }
    }

    private void execute(Long clusterId,
                         Integer duration,
                         Date startTime,
                         Date endTime,
                         Map<Integer, BrokerDO> brokerMap) {
        for (Integer brokerId: PhysicalClusterMetadataManager.getBrokerIdList(clusterId)) {
            BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (ValidateUtils.isNull(brokerMetadata)) {
                continue;
            }

            // 获取信息
            Double maxAvgBytesIn =
                    brokerService.calBrokerMaxAvgBytesIn(clusterId, brokerId, duration, startTime, endTime);

            BrokerDO brokerDO = brokerMap.get(brokerId);
            if (ValidateUtils.isNull(brokerDO)) {
                brokerDO = new BrokerDO();
            }
            brokerDO.setClusterId(brokerMetadata.getClusterId());
            brokerDO.setBrokerId(brokerMetadata.getBrokerId());
            brokerDO.setHost(brokerMetadata.getHost());
            brokerDO.setPort(brokerMetadata.getPort());
            brokerDO.setTimestamp(brokerMetadata.getTimestamp());
            brokerDO.setStatus(DBStatusEnum.ALIVE.getStatus());
            brokerDO.setMaxAvgBytesIn(maxAvgBytesIn);
            brokerService.replace(brokerDO);
        }

        for (Map.Entry<Integer, BrokerDO> entry: brokerMap.entrySet()) {
            BrokerDO brokerDO = entry.getValue();
            if (brokerDO.getStatus().equals(DBStatusEnum.ALIVE.getStatus())) {
                continue;
            }
            brokerService.replace(brokerDO);
        }
    }

    private Map<Long, Map<Integer, BrokerDO>> getBrokerMap() {
        List<BrokerDO> brokerDOList = brokerService.listAll();
        if (ValidateUtils.isNull(brokerDOList)) {
            brokerDOList = new ArrayList<>();
        }
        Map<Long, Map<Integer, BrokerDO>> brokerMap = new HashMap<>();
        for (BrokerDO brokerDO: brokerDOList) {
            // 默认修改为DEAD先, 如果ALIVE则修改为ALIVE
            brokerDO.setStatus(DBStatusEnum.DEAD.getStatus());
            Map<Integer, BrokerDO> subBrokerMap = brokerMap.getOrDefault(brokerDO.getClusterId(), new HashMap<>());
            subBrokerMap.put(brokerDO.getBrokerId(), brokerDO);
            brokerMap.put(brokerDO.getClusterId(), subBrokerMap);
        }
        return brokerMap;
    }
}