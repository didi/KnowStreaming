package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalCluster;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalClusterMetrics;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/29
 */
public interface LogicalClusterService {
    List<LogicalCluster> getAllLogicalCluster();

    /**
     * 获取逻辑集群信息
     */
    List<LogicalCluster> getLogicalClusterListByPrincipal(String principal);

    /**
     * 获取逻辑集群信息
     */
    LogicalCluster getLogicalCluster(Long logicalClusterId);

    /**
     * 逻辑集群下Topic元信息
     */
    List<TopicMetadata> getTopicMetadatas(LogicalClusterDO logicalClusterDO);

    /**
     * 逻辑集群下broker元信息
     */
    List<BrokerMetadata> getBrokerMetadatas(LogicalClusterDO logicalClusterDO);

    /**
     * 获取逻辑集群流量
     */
    List<LogicalClusterMetrics> getLogicalClusterMetricsFromDB(LogicalClusterDO logicalClusterDO,
                                                               Date startTime,
                                                               Date endTime);

    List<LogicalClusterDO> listAll();

    /**
     * 创建逻辑集群
     */
    ResultStatus createLogicalCluster(LogicalClusterDO logicalClusterDO);


    /**
     * 通过物理集群ID查找
     */
    List<LogicalClusterDO> getByPhysicalClusterId(Long physicalClusterId);

    LogicalClusterDO getById(Long id);

    /**
     * 删除逻辑集群
     */
    ResultStatus deleteById(Long logicalClusterId);

    /**
     * 修改逻辑集群
     */
    ResultStatus updateById(LogicalClusterDO logicalClusterDO);

    ResultStatus updateById(Long logicalClusterId, List<Long> regionIdList);
}