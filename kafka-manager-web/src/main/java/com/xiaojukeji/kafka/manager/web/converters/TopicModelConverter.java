package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.topic.*;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.TopicBusinessInfoVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.*;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.TopicBrokerVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author arthur
 * @date 2017/6/1.
 */
public class TopicModelConverter {
    public static TopicBasicVO convert2TopicBasicVO(TopicBasicDTO dto, ClusterDO clusterDO, Long logicalClusterId) {
        TopicBasicVO vo = new TopicBasicVO();
        vo.setClusterId(logicalClusterId);
        vo.setAppId(dto.getAppId());
        vo.setAppName(dto.getAppName());
        vo.setPartitionNum(dto.getPartitionNum());
        vo.setReplicaNum(dto.getReplicaNum());
        vo.setPrincipals(dto.getPrincipals());
        vo.setRetentionTime(dto.getRetentionTime());
        vo.setCreateTime(dto.getCreateTime());
        vo.setModifyTime(dto.getModifyTime());
        vo.setScore(dto.getScore());
        vo.setTopicCodeC(dto.getTopicCodeC());
        vo.setDescription(dto.getDescription());
        vo.setBootstrapServers("");
        vo.setRegionNameList(dto.getRegionNameList());
        if (!ValidateUtils.isNull(clusterDO)) {
            vo.setBootstrapServers(clusterDO.getBootstrapServers());
        }
        vo.setRegionNameList(dto.getRegionNameList());
        return vo;
    }

    public static List<TopicPartitionVO> convert2TopicPartitionVOList(List<TopicPartitionDTO> dtoList) {
        List<TopicPartitionVO> voList = new ArrayList<>();
        for (TopicPartitionDTO dto : dtoList) {
            TopicPartitionVO vo = new TopicPartitionVO();
            CopyUtils.copyProperties(vo, dto);
            if (!ValidateUtils.isNull(dto.getBeginningOffset())
                    && !ValidateUtils.isNull(dto.getEndOffset())) {
                vo.setMsgNum(dto.getEndOffset() - dto.getBeginningOffset());
            }
            vo.setReplicaBrokerIdList(dto.getReplicaBrokerIdList());
            vo.setIsrBrokerIdList(dto.getIsrBrokerIdList());
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicRequestTimeVO> convert2TopicRequestTimeMetricsVOList(List<TopicMetricsDO> metricsList) {
        List<TopicRequestTimeVO> voList = new ArrayList<>();
        for (TopicMetricsDO elem : metricsList) {
            TopicRequestTimeVO vo = new TopicRequestTimeVO();
            TopicMetrics metrics = MetricsConvertUtils.convert2TopicMetrics(elem);
            vo.setProduceRequestTimeMean(metrics.getSpecifiedMetrics("ProduceTotalTimeMsMean"));
            vo.setProduceRequestTime50thPercentile(metrics.getSpecifiedMetrics("ProduceTotalTimeMs50thPercentile"));
            vo.setProduceRequestTime75thPercentile(metrics.getSpecifiedMetrics("ProduceTotalTimeMs75thPercentile"));
            vo.setProduceRequestTime95thPercentile(metrics.getSpecifiedMetrics("ProduceTotalTimeMs95thPercentile"));
            vo.setProduceRequestTime99thPercentile(metrics.getSpecifiedMetrics("ProduceTotalTimeMs99thPercentile"));

            vo.setFetchRequestTimeMean(metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMsMean"));
            vo.setFetchRequestTime50thPercentile(metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMs50thPercentile"));
            vo.setFetchRequestTime75thPercentile(metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMs75thPercentile"));
            vo.setFetchRequestTime95thPercentile(metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMs95thPercentile"));
            vo.setFetchRequestTime99thPercentile(metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMs99thPercentile"));
            vo.setGmtCreate(metrics.getSpecifiedMetrics(JmxConstant.CREATE_TIME, Long.class));
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicRequestTimeDetailVO> convert2TopicRequestTimeDetailVOList(BaseMetrics metrics) {
        if (ValidateUtils.isNull(metrics)) {
            return new ArrayList<>();
        }
        TopicRequestTimeDetailVO produceVO = new TopicRequestTimeDetailVO();
        produceVO.setRequestTimeType("RequestProduceTime");
        produceVO.setRequestQueueTimeMs(metrics.getSpecifiedMetrics("ProduceRequestQueueTimeMs99thPercentile"));
        produceVO.setResponseQueueTimeMs(metrics.getSpecifiedMetrics("ProduceResponseQueueTimeMs99thPercentile"));
        produceVO.setResponseSendTimeMs(metrics.getSpecifiedMetrics("ProduceResponseSendTimeMs99thPercentile"));
        produceVO.setLocalTimeMs(metrics.getSpecifiedMetrics("ProduceLocalTimeMs99thPercentile"));
        produceVO.setThrottleTimeMs(metrics.getSpecifiedMetrics("ProduceThrottleTimeMs99thPercentile"));
        produceVO.setRemoteTimeMs(metrics.getSpecifiedMetrics("ProduceRemoteTimeMs99thPercentile"));
        produceVO.setTotalTimeMs(metrics.getSpecifiedMetrics("ProduceTotalTimeMs99thPercentile"));

        TopicRequestTimeDetailVO fetchVO = new TopicRequestTimeDetailVO();
        fetchVO.setRequestTimeType("RequestFetchTime");
        fetchVO.setRequestQueueTimeMs(metrics.getSpecifiedMetrics("FetchConsumerRequestQueueTimeMs99thPercentile"));
        fetchVO.setResponseQueueTimeMs(metrics.getSpecifiedMetrics("FetchConsumerResponseQueueTimeMs99thPercentile"));
        fetchVO.setResponseSendTimeMs(metrics.getSpecifiedMetrics("FetchConsumerResponseSendTimeMs99thPercentile"));
        fetchVO.setLocalTimeMs(metrics.getSpecifiedMetrics("FetchConsumerLocalTimeMs99thPercentile"));
        fetchVO.setThrottleTimeMs(metrics.getSpecifiedMetrics("FetchConsumerThrottleTimeMs99thPercentile"));
        fetchVO.setRemoteTimeMs(metrics.getSpecifiedMetrics("FetchConsumerRemoteTimeMs99thPercentile"));
        fetchVO.setTotalTimeMs(metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMs99thPercentile"));
        return Arrays.asList(produceVO, fetchVO);
    }

    public static List<TopicRequestTimeDetailVO> convert2TopicRequestTimeDetailVOList(BaseMetrics metrics, String percentile) {
        if (ValidateUtils.isNull(metrics)) {
            return new ArrayList<>();
        }
        TopicRequestTimeDetailVO produceVO = new TopicRequestTimeDetailVO();
        produceVO.setRequestTimeType("RequestProduceTime");
        fillTopicProduceTime(produceVO, metrics, percentile);

        TopicRequestTimeDetailVO fetchVO = new TopicRequestTimeDetailVO();
        fetchVO.setRequestTimeType("RequestFetchTime");
        fillTopicFetchTime(fetchVO, metrics, percentile);

        TopicMetrics topicMetrics = (TopicMetrics) metrics;
        if (!ValidateUtils.isEmptyList(topicMetrics.getBrokerMetricsList())) {
            List<TopicBrokerRequestTimeVO> brokerProduceTimeList = new ArrayList<>();
            List<TopicBrokerRequestTimeVO> brokerFetchTimeList = new ArrayList<>();
            topicMetrics.getBrokerMetricsList().forEach(brokerMetrics -> {
                TopicBrokerRequestTimeVO topicBrokerProduceReq = new TopicBrokerRequestTimeVO();
                topicBrokerProduceReq.setClusterId(brokerMetrics.getClusterId());
                topicBrokerProduceReq.setBrokerId(brokerMetrics.getBrokerId());

                TopicRequestTimeDetailVO brokerProduceVO = new TopicRequestTimeDetailVO();
                brokerProduceVO.setRequestTimeType("BrokerRequestProduceTime");
                fillTopicProduceTime(brokerProduceVO, brokerMetrics, percentile);

                topicBrokerProduceReq.setBrokerRequestTime(brokerProduceVO);

                TopicBrokerRequestTimeVO topicBrokerFetchReq = new TopicBrokerRequestTimeVO();
                topicBrokerFetchReq.setClusterId(brokerMetrics.getClusterId());
                topicBrokerFetchReq.setBrokerId(brokerMetrics.getBrokerId());

                TopicRequestTimeDetailVO brokerFetchVO = new TopicRequestTimeDetailVO();
                brokerProduceVO.setRequestTimeType("BrokerRequestFetchTime");
                fillTopicFetchTime(brokerFetchVO, brokerMetrics, percentile);

                topicBrokerFetchReq.setBrokerRequestTime(brokerFetchVO);

                brokerProduceTimeList.add(topicBrokerProduceReq);
                brokerFetchTimeList.add(topicBrokerFetchReq);
            });

            produceVO.setBrokerRequestTimeList(brokerProduceTimeList);
            fetchVO.setBrokerRequestTimeList(brokerFetchTimeList);
        }

        return Arrays.asList(produceVO, fetchVO);
    }

    public static List<TopicConnectionVO> convert2TopicConnectionVOList(List<TopicConnection> connectionDTOList) {
        if (ValidateUtils.isNull(connectionDTOList)) {
            return new ArrayList<>();
        }
        List<TopicConnectionVO> voList = new ArrayList<>();
        for (TopicConnection dto : connectionDTOList) {
            TopicConnectionVO vo = new TopicConnectionVO();
            CopyUtils.copyProperties(vo, dto);
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicBrokerVO> convert2TopicBrokerVO(Long physicalClusterId,
                                                            List<TopicBrokerDTO> dtoList) {
        List<TopicBrokerVO> voList = new ArrayList<>();
        for (TopicBrokerDTO dto : dtoList) {
            TopicBrokerVO vo = new TopicBrokerVO();
            vo.setClusterId(physicalClusterId);
            CopyUtils.copyProperties(vo, dto);
            vo.setLeaderPartitionIdList(dto.getLeaderPartitionIdList());
            vo.setPartitionIdList(dto.getPartitionIdList());
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicDataSampleVO> convert2TopicDataSampleVOList(List<String> dataList) {
        if (ValidateUtils.isNull(dataList)) {
            return new ArrayList<>();
        }
        List<TopicDataSampleVO> voList = new ArrayList<>();
        for (String data : dataList) {
            TopicDataSampleVO topicDataSampleVO = new TopicDataSampleVO();
            topicDataSampleVO.setValue(data);
            voList.add(topicDataSampleVO);
        }
        return voList;
    }

    public static List<TopicMetricVO> convert2TopicMetricsVOList(List<TopicMetricsDO> dataList) {
        if (ValidateUtils.isNull(dataList)) {
            return new ArrayList<>();
        }
        List<TopicMetricVO> voList = new ArrayList<>();
        for (TopicMetricsDO data : dataList) {
            TopicMetricVO vo = new TopicMetricVO();
            BaseMetrics metrics = MetricsConvertUtils.convert2TopicMetrics(data);
            if (ValidateUtils.isNull(metrics)) {
                continue;
            }
            vo.setBytesInPerSec(metrics.getBytesInPerSecOneMinuteRate(null));
            vo.setBytesOutPerSec(metrics.getBytesOutPerSecOneMinuteRate(null));
            vo.setBytesRejectedPerSec(metrics.getBytesRejectedPerSecOneMinuteRate(null));
            vo.setMessagesInPerSec(metrics.getMessagesInPerSecOneMinuteRate(null));
            vo.setTotalProduceRequestsPerSec(metrics.getTotalProduceRequestsPerSecOneMinuteRate(null));
            vo.setGmtCreate(data.getGmtCreate().getTime());
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicMetricVO> convert2TopicMetricVOList(List<TopicMetricsDTO> dataList) {
        if (ValidateUtils.isNull(dataList)) {
            return new ArrayList<>();
        }
        List<TopicMetricVO> voList = new ArrayList<>();
        for (TopicMetricsDTO data : dataList) {
            TopicMetricVO vo = new TopicMetricVO();
            CopyUtils.copyProperties(vo, data);
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicAuthorizedAppVO> convert2TopicAuthorizedAppVOList(List<TopicAppData> dtoList) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return new ArrayList<>();
        }

        List<TopicAuthorizedAppVO> voList = new ArrayList<>();
        for (TopicAppData topicAppDTO : dtoList) {
            TopicAuthorizedAppVO vo = new TopicAuthorizedAppVO();
            CopyUtils.copyProperties(vo, topicAppDTO);
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicMyAppVO> convert2TopicMineAppVOList(List<TopicAppData> dtoList) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return new ArrayList<>();
        }

        List<TopicMyAppVO> voList = new ArrayList<>();
        for (TopicAppData elem : dtoList) {
            TopicMyAppVO vo = new TopicMyAppVO();
            vo.setAppId(elem.getAppId());
            vo.setAppName(elem.getAppName());
            vo.setAppPrincipals(elem.getAppPrincipals());
            vo.setProduceQuota(elem.getProduceQuota());
            vo.setConsumerQuota(elem.getConsumerQuota());
            vo.setAccess(elem.getAccess());
            voList.add(vo);
        }
        return voList;
    }


    public static TopicBusinessInfoVO convert2TopicBusinessInfoVO(TopicBusinessInfo topicBusinessInfo) {
        if (ValidateUtils.isNull(topicBusinessInfo)) {
            return null;
        }
        TopicBusinessInfoVO topicBusinessInfoVO = new TopicBusinessInfoVO();
        CopyUtils.copyProperties(topicBusinessInfoVO,topicBusinessInfo);
        return topicBusinessInfoVO;
    }

    private static void fillTopicProduceTime(TopicRequestTimeDetailVO produceVO, BaseMetrics metrics, String thPercentile) {
        produceVO.setRequestQueueTimeMs(metrics.getSpecifiedMetrics("ProduceRequestQueueTimeMs" + thPercentile));
        produceVO.setResponseQueueTimeMs(metrics.getSpecifiedMetrics("ProduceResponseQueueTimeMs" + thPercentile));
        produceVO.setResponseSendTimeMs(metrics.getSpecifiedMetrics("ProduceResponseSendTimeMs" + thPercentile));
        produceVO.setLocalTimeMs(metrics.getSpecifiedMetrics("ProduceLocalTimeMs" + thPercentile));
        produceVO.setThrottleTimeMs(metrics.getSpecifiedMetrics("ProduceThrottleTimeMs" + thPercentile));
        produceVO.setRemoteTimeMs(metrics.getSpecifiedMetrics("ProduceRemoteTimeMs" + thPercentile));
        produceVO.setTotalTimeMs(metrics.getSpecifiedMetrics("ProduceTotalTimeMs"  + thPercentile));
    }

    private static void fillTopicFetchTime(TopicRequestTimeDetailVO fetchVO, BaseMetrics metrics, String thPercentile) {
        fetchVO.setRequestQueueTimeMs(metrics.getSpecifiedMetrics("FetchConsumerRequestQueueTimeMs" + thPercentile));
        fetchVO.setResponseQueueTimeMs(metrics.getSpecifiedMetrics("FetchConsumerResponseQueueTimeMs" + thPercentile));
        fetchVO.setResponseSendTimeMs(metrics.getSpecifiedMetrics("FetchConsumerResponseSendTimeMs" + thPercentile));
        fetchVO.setLocalTimeMs(metrics.getSpecifiedMetrics("FetchConsumerLocalTimeMs" + thPercentile));
        fetchVO.setThrottleTimeMs(metrics.getSpecifiedMetrics("FetchConsumerThrottleTimeMs" + thPercentile));
        fetchVO.setRemoteTimeMs(metrics.getSpecifiedMetrics("FetchConsumerRemoteTimeMs" + thPercentile));
        fetchVO.setTotalTimeMs(metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMs" + thPercentile));
    }
}
