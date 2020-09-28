package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.TopicDiskLocation;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicOverview;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker.BrokerDiskTopicVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.RealTimeMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.TopicOverviewVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/22
 */
public class CommonModelConverter {
    public static RealTimeMetricsVO convert2RealTimeMetricsVO(List<BrokerMetrics> dataList) {
        if (ValidateUtils.isNull(dataList)) {
            return null;
        }
        BaseMetrics baseMetrics = MetricsConvertUtils.merge2BaseMetricsByAdd(dataList);
        return convert2RealTimeMetricsVO(baseMetrics);
    }

    public static RealTimeMetricsVO convert2RealTimeMetricsVO(BaseMetrics metrics) {
        if (ValidateUtils.isNull(metrics)) {
            return null;
        }

        RealTimeMetricsVO vo = new RealTimeMetricsVO();
        vo.setByteIn(Arrays.asList(
                metrics.getSpecifiedMetrics("BytesInPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("BytesInPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("BytesInPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("BytesInPerSecFifteenMinuteRate", Double.class)
        ));

        vo.setByteOut(Arrays.asList(
                metrics.getSpecifiedMetrics("BytesOutPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("BytesOutPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("BytesOutPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("BytesOutPerSecFifteenMinuteRate", Double.class)
        ));

        vo.setMessageIn(Arrays.asList(
                metrics.getSpecifiedMetrics("MessagesInPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("MessagesInPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("MessagesInPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("MessagesInPerSecFifteenMinuteRate", Double.class)
        ));

        vo.setByteRejected(Arrays.asList(
                metrics.getSpecifiedMetrics("BytesRejectedPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("BytesRejectedPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("BytesRejectedPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("BytesRejectedPerSecFifteenMinuteRate", Double.class)
        ));

        vo.setFailedProduceRequest(Arrays.asList(
                metrics.getSpecifiedMetrics("FailedProduceRequestsPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("FailedProduceRequestsPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("FailedProduceRequestsPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("FailedProduceRequestsPerSecFifteenMinuteRate", Double.class)
        ));

        vo.setFailedFetchRequest(Arrays.asList(
                metrics.getSpecifiedMetrics("FailedFetchRequestsPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("FailedFetchRequestsPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("FailedFetchRequestsPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("FailedFetchRequestsPerSecFifteenMinuteRate", Double.class)
        ));

        vo.setTotalProduceRequest(Arrays.asList(
                metrics.getSpecifiedMetrics("TotalProduceRequestsPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("TotalProduceRequestsPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("TotalProduceRequestsPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("TotalProduceRequestsPerSecFifteenMinuteRate", Double.class)
        ));

        vo.setTotalFetchRequest(Arrays.asList(
                metrics.getSpecifiedMetrics("TotalFetchRequestsPerSecMeanRate", Double.class),
                metrics.getSpecifiedMetrics("TotalFetchRequestsPerSecOneMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("TotalFetchRequestsPerSecFiveMinuteRate", Double.class),
                metrics.getSpecifiedMetrics("TotalFetchRequestsPerSecFifteenMinuteRate", Double.class)
        ));

        return vo;
    }

    public static List<TopicOverviewVO> convert2TopicOverviewVOList(Long clusterId,
                                                                    List<TopicOverview> dtoList) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return new ArrayList<>();
        }

        List<TopicOverviewVO> voList = new ArrayList<>();
        for (TopicOverview dto : dtoList) {
            TopicOverviewVO vo = new TopicOverviewVO();
            CopyUtils.copyProperties(vo, dto);
            vo.setClusterId(clusterId);
            voList.add(vo);
        }
        return voList;
    }

    public static List<BrokerDiskTopicVO> convert2BrokerDiskTopicVOList(List<TopicDiskLocation> locationList) {
        if (ValidateUtils.isEmptyList(locationList)) {
            return new ArrayList<>();
        }
        List<BrokerDiskTopicVO> voList = new ArrayList<>();
        for (TopicDiskLocation location: locationList) {
            BrokerDiskTopicVO vo = new BrokerDiskTopicVO();
            vo.setClusterId(location.getClusterId());
            vo.setTopicName(location.getTopicName());
            vo.setBrokerId(location.getBrokerId());
            vo.setDiskName(location.getDiskName());
            vo.setLeaderPartitions(location.getLeaderPartitions());
            vo.setFollowerPartitions(location.getFollowerPartitions());
            vo.setUnderReplicated(location.getUnderReplicated());
            vo.setNotUnderReplicatedPartitions(location.getUnderReplicatedPartitions());
            voList.add(vo);
        }
        return voList;
    }
}