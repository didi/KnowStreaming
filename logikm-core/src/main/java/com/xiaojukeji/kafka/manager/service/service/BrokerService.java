package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.TopicDiskLocation;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ClusterBrokerStatus;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Broker模块的service接口
 * @author tukun, zengqiao
 * @date 2015/11/9
 */
public interface BrokerService {
    ClusterBrokerStatus getClusterBrokerStatus(Long clusterId);

    /**
     * 获取Broker列表信息
     */
    List<BrokerOverviewDTO> getBrokerOverviewList(Long clusterId, Set<Integer> brokerIdSet);

    /**
     * 获取BrokerMetrics信息
     */
    List<BrokerMetrics> getBrokerMetricsFromJmx(Long clusterId, Set<Integer> brokerIdSet, Integer metricsCode);

    /**
     * 获取BrokerMetrics信息
     */
    BrokerMetrics getBrokerMetricsFromJmx(Long clusterId, Integer brokerId, Integer metricsCode);

    /**
     * 根据时间区间获取Broker监控数据
     */
    List<BrokerMetricsDO> getBrokerMetricsFromDB(Long clusterId, Integer brokerId, Date startTime, Date endTime);

    List<TopicDiskLocation> getBrokerTopicLocation(Long clusterId, Integer brokerId);

    /**
     * 计算Broker的峰值均值流量
     */
    Double calBrokerMaxAvgBytesIn(Long clusterId,
                                  Integer brokerId,
                                  Integer duration,
                                  Date startTime,
                                  Date endTime);

    /**
     * 根据Cluster和brokerId获取broker的具体信息
     */
    BrokerBasicDTO getBrokerBasicDTO(Long clusterId, Integer brokerId);

    String getBrokerVersion(Long clusterId, Integer brokerId);

    List<BrokerDO> listAll();

    int replace(BrokerDO brokerDO);

    ResultStatus delete(Long clusterId, Integer brokerId);
}
