package com.xiaojukeji.kafka.manager.service.utils;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.ClusterMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/17
 */
public class MetricsConvertUtils {
    public static List<ClusterMetrics> convert2ClusterMetricsList(List<ClusterMetricsDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        List<ClusterMetrics> metricsList = new ArrayList<>();
        for (ClusterMetricsDO metricsDO: doList) {
            metricsList.add(convert2ClusterMetrics(metricsDO));
        }
        return metricsList;
    }

    public static ClusterMetrics convert2ClusterMetrics(ClusterMetricsDO metricsDO) {
        if (ValidateUtils.isNull(metricsDO)) {
            return null;
        }
        ClusterMetrics metrics = new ClusterMetrics(metricsDO.getClusterId());
        metrics.setMetricsMap(JSON.parseObject(metricsDO.getMetrics()));
        return metrics;
    }

    public static List<BrokerMetrics> convert2BrokerMetricsList(List<BrokerMetricsDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        List<BrokerMetrics> metricsList = new ArrayList<>();
        for (BrokerMetricsDO metricsDO: doList) {
            metricsList.add(convert2BrokerMetrics(metricsDO));
        }
        return metricsList;
    }

    public static BrokerMetrics convert2BrokerMetrics(BrokerMetricsDO metricsDO) {
        if (ValidateUtils.isNull(metricsDO)) {
            return null;
        }
        BrokerMetrics metrics = new BrokerMetrics(metricsDO.getClusterId(), metricsDO.getBrokerId());
        metrics.setMetricsMap(JSON.parseObject(metricsDO.getMetrics()));
        return metrics;
    }

    public static List<TopicMetrics> convert2TopicMetricsList(List<TopicMetricsDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        List<TopicMetrics> metricsList = new ArrayList<>();
        for (TopicMetricsDO metricsDO: doList) {
            metricsList.add(convert2TopicMetrics(metricsDO));
        }
        return metricsList;
    }

    public static TopicMetrics convert2TopicMetrics(TopicMetricsDO metricsDO) {
        if (ValidateUtils.isNull(metricsDO)) {
            return null;
        }
        TopicMetrics metrics = new TopicMetrics(
                metricsDO.getAppId(),
                metricsDO.getClusterId(),
                metricsDO.getTopicName()
        );
        metrics.setMetricsMap(JSON.parseObject(metricsDO.getMetrics()));
        return metrics;
    }

    public static List<TopicMetricsDO> convertAndUpdateCreateTime2TopicMetricsDOList(
            Long timestamp,
            List<TopicMetrics> metricsList) {
        if (ValidateUtils.isEmptyList(metricsList)) {
            return new ArrayList<>();
        }
        List<TopicMetricsDO> doList = new ArrayList<>();
        for (TopicMetrics elem: metricsList) {
            elem.updateCreateTime(timestamp);
            if (elem.getMetricsMap().size() == 1) {
                // 没有指标数据, 直接过滤掉
                continue;
            }
            TopicMetricsDO metricsDO = new TopicMetricsDO();
            metricsDO.setAppId(elem.getAppId());
            metricsDO.setClusterId(elem.getClusterId());
            metricsDO.setTopicName(elem.getTopicName());
            metricsDO.setMetrics(JSON.toJSONString(elem.getMetricsMap()));
            doList.add(metricsDO);
        }
        return doList;
    }

    public static List<BrokerMetricsDO> convertAndUpdateCreateTime2BrokerMetricsDOList(
            Long timestamp,
            List<BrokerMetrics> metricsList) {
        if (ValidateUtils.isEmptyList(metricsList)) {
            return new ArrayList<>();
        }
        List<BrokerMetricsDO> doList = new ArrayList<>();
        for (BrokerMetrics elem: metricsList) {
            elem.updateCreateTime(timestamp);
            if (elem.getMetricsMap().size() == 1) {
                // 没有指标数据, 直接过滤掉
                continue;
            }
            BrokerMetricsDO metricsDO = new BrokerMetricsDO();
            metricsDO.setClusterId(elem.getClusterId());
            metricsDO.setBrokerId(elem.getBrokerId());
            metricsDO.setMetrics(JSON.toJSONString(elem.getMetricsMap()));
            doList.add(metricsDO);
        }
        return doList;
    }

    public static List<ClusterMetricsDO> convertAndUpdateCreateTime2ClusterMetricsDOList(
            Long timestamp,
            List<ClusterMetrics> metricsList) {
        if (ValidateUtils.isEmptyList(metricsList)) {
            return new ArrayList<>();
        }
        List<ClusterMetricsDO> doList = new ArrayList<>();
        for (ClusterMetrics elem: metricsList) {
            elem.updateCreateTime(timestamp);
            if (elem.getMetricsMap().size() == 1) {
                // 没有指标数据, 直接过滤掉
                continue;
            }
            ClusterMetricsDO metricsDO = new ClusterMetricsDO();
            metricsDO.setClusterId(elem.getClusterId());
            metricsDO.setMetrics(JSON.toJSONString(elem.getMetricsMap()));
            doList.add(metricsDO);
        }
        return doList;
    }

    public static BaseMetrics merge2BaseMetricsByAdd(List<? extends BaseMetrics> metricsList) {
        if (ValidateUtils.isEmptyList(metricsList)) {
            return new BaseMetrics();
        }
        BaseMetrics metrics = new BaseMetrics();
        for (BaseMetrics elem: metricsList) {
            metrics.mergeByAdd(elem);
        }
        return metrics;
    }

    public static BaseMetrics merge2BaseMetricsByMax(List<? extends BaseMetrics> metricsList) {
        if (ValidateUtils.isEmptyList(metricsList)) {
            return new BaseMetrics();
        }
        BaseMetrics metrics = new BaseMetrics();
        for (BaseMetrics elem: metricsList) {
            metrics.mergeByMax(elem);
        }
        return metrics;
    }

    public static ReassignTaskDO convert2ReassignTaskDO(Long taskId,
                                                        Long clusterId,
                                                        TopicMetadata topicMetadata,
                                                        ReassignTopicDTO dto,
                                                        String reassignmentJson,
                                                        String operator) {
        ReassignTaskDO reassignTaskDO = new ReassignTaskDO();
        reassignTaskDO.setTaskId(taskId);
        reassignTaskDO.setClusterId(clusterId);
        reassignTaskDO.setTopicName(topicMetadata.getTopic());
        reassignTaskDO.setPartitions(ListUtils.intList2String(dto.getPartitionIdList()));
        reassignTaskDO.setReassignmentJson(reassignmentJson);
        reassignTaskDO.setRealThrottle(dto.getThrottle());
        reassignTaskDO.setMinThrottle(dto.getMinThrottle());
        reassignTaskDO.setMaxThrottle(dto.getMaxThrottle());
        reassignTaskDO.setBeginTime(new Date(dto.getBeginTime()));

        reassignTaskDO.setSrcBrokers(ListUtils.intList2String(new ArrayList<>(topicMetadata.getBrokerIdSet())));
        reassignTaskDO.setDestBrokers(ListUtils.intList2String(dto.getBrokerIdList()));
        reassignTaskDO.setOriginalRetentionTime(dto.getOriginalRetentionTime());
        reassignTaskDO.setReassignRetentionTime(dto.getReassignRetentionTime());
        reassignTaskDO.setDescription(dto.getDescription());
        reassignTaskDO.setOperator(operator);
        return reassignTaskDO;
    }
}