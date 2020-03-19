package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverallDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Broker模块的service接口
 * @author tukun, zengqiao
 * @date 2015/11/9
 */
public interface BrokerService {
    /**
     * 获取Broker列表信息
     */
    List<BrokerOverviewDTO> getBrokerOverviewList(Long clusterId, List<String> specifiedFieldList, boolean simple);

    /**
     * 获取Broker概述信息
     */
    List<BrokerOverallDTO> getBrokerOverallList(Long clusterId, List<String> specifiedFieldList);

    /**
     * 从JMX获取BrokerMetrics中指定字段的信息
     */
    Map<Integer, BrokerMetrics> getSpecifiedBrokerMetrics(Long clusterId, List<String> specifiedFieldList, boolean simple);

    /**
     * 获取Broker流量信息
     */
    BrokerMetrics getSpecifiedBrokerMetrics(Long clusterId, Integer brokerId, List<String> specifiedFieldList, Boolean simple);

    /**
     * 根据时间区间获取Broker监控数据
     */
    List<BrokerMetrics> getBrokerMetricsByInterval(Long clusterId, Integer brokerId, Date startTime, Date endTime);

    /**
     * 根据Cluster和brokerId获取broker的具体信息
     */
    BrokerBasicDTO getBrokerBasicDTO(Long clusterId, Integer brokerId, List<String> specifiedFieldList);
}
