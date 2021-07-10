package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.ao.analysis.AnalysisBrokerDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.analysis.AnalysisTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AnalysisService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Topic抖动分析
 * @author huangyiminghappy@163.com, zengqaio_cn@163.com
 * @date 2019-06-14
 */
@Service("analysisService")
public class AnalysisServiceImpl implements AnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);

    @Autowired
    private JmxService jmxService;

    private static final Integer TOP_TOPIC_NUM = 5;

    private static final Integer MIN_TOP_TOPIC_BYTES_IN_VALUE = 100;

    private static final Integer MIN_TOP_TOPIC_QPS_VALUE = 10;

    @Override
    public AnalysisBrokerDTO doAnalysisBroker(Long clusterId, Integer brokerId) {
        AnalysisBrokerDTO analysisBrokerDTO = new AnalysisBrokerDTO();
        analysisBrokerDTO.setClusterId(clusterId);
        analysisBrokerDTO.setBrokerId(brokerId);
        analysisBrokerDTO.setBaseTime(System.currentTimeMillis());
        analysisBrokerDTO.setTopicAnalysisVOList(new ArrayList<>());

        BrokerMetrics brokerMetrics = jmxService.getBrokerMetrics(clusterId, brokerId, KafkaMetricsCollections.BROKER_ANALYSIS_METRICS);
        if (ValidateUtils.isNull(brokerMetrics)) {
            return analysisBrokerDTO;
        }
        analysisBrokerDTO.setBytesIn(brokerMetrics.getBytesInPerSecOneMinuteRate(0.0));
        analysisBrokerDTO.setBytesOut(brokerMetrics.getBytesOutPerSecOneMinuteRate(0.0));
        analysisBrokerDTO.setMessagesIn(brokerMetrics.getMessagesInPerSecOneMinuteRate(0.0));
        analysisBrokerDTO.setTotalProduceRequests(brokerMetrics.getTotalProduceRequestsPerSecOneMinuteRate(0.0));
        analysisBrokerDTO.setTotalFetchRequests(brokerMetrics.getTotalFetchRequestsPerSecOneMinuteRate(0.0));

        List<TopicMetrics> topicMetricsList = new ArrayList<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata) || !topicMetadata.getBrokerIdSet().contains(brokerId)) {
                continue;
            }
            TopicMetrics topicMetrics = KafkaMetricsCache.getTopicMetricsFromCache(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetrics )) {
                continue;
            }

            if (topicMetrics.getBytesInPerSecOneMinuteRate(0.0) < MIN_TOP_TOPIC_BYTES_IN_VALUE.doubleValue()
                    || topicMetrics.getTotalProduceRequestsPerSecOneMinuteRate(0.0) < MIN_TOP_TOPIC_QPS_VALUE.doubleValue()) {
                continue;
            }
            topicMetrics = jmxService.getTopicMetrics(
                    clusterId,
                    brokerId,
                    topicName,
                    KafkaMetricsCollections.BROKER_TOPIC_ANALYSIS_METRICS,
                    true
            );
            if (ValidateUtils.isNull(topicMetrics )) {
                continue;
            }
            topicMetricsList.add(topicMetrics);
        }
        Set<String> topicNameSet = new HashSet<>();
        supplyAnalysisTopicDTOList(analysisBrokerDTO, topicNameSet, topicMetricsList, "BytesIn");
        supplyAnalysisTopicDTOList(analysisBrokerDTO, topicNameSet, topicMetricsList, "TotalProduceRequest");
        return analysisBrokerDTO;
    }

    private void supplyAnalysisTopicDTOList(AnalysisBrokerDTO analysisBrokerDTO,
                                            Set<String> topicNameSet,
                                            List<TopicMetrics> topicMetricsList,
                                            String fieldName) {
        Collections.sort(topicMetricsList, new Comparator<TopicMetrics>() {
            @Override
            public int compare(TopicMetrics t1, TopicMetrics t2) {
                double diff = 0;
                switch (fieldName) {
                    case "BytesIn":
                        diff = t1.getBytesInPerSecOneMinuteRate(0.0) - t2.getBytesInPerSecOneMinuteRate(0.0);
                        break;
                    case "TotalProduceRequest":
                        diff = t1.getTotalProduceRequestsPerSecOneMinuteRate(0.0) - t2.getTotalProduceRequestsPerSecOneMinuteRate(0.0);
                        break;
                    default:
                        diff = 0;
                        break;
                }
                if (diff > 0) {
                    return -1;
                } else if (diff < 0) {
                    return 1;
                }
                return t1.getTopicName().compareTo(t2.getTopicName());
            }
        });

        for (int i = 0; i < TOP_TOPIC_NUM && i < topicMetricsList.size(); ++i) {
            TopicMetrics topicMetrics = topicMetricsList.get(i);
            if (topicNameSet.contains(topicMetrics.getTopicName())) {
                continue;
            }
            AnalysisTopicDTO analysisTopicDTO = new AnalysisTopicDTO();
            analysisTopicDTO.setTopicName(topicMetrics.getTopicName());
            analysisTopicDTO.setBytesIn(topicMetrics.getBytesInPerSecOneMinuteRate(0.0));
            if (analysisBrokerDTO.getBytesIn() <= 0) {
                analysisTopicDTO.setBytesInRate(0.0);
            } else {
                analysisTopicDTO.setBytesInRate(topicMetrics.getBytesInPerSecOneMinuteRate(0.0) / analysisBrokerDTO.getBytesIn());
            }

            analysisTopicDTO.setBytesOut(topicMetrics.getBytesOutPerSecOneMinuteRate(0.0));
            if (analysisBrokerDTO.getBytesOut() <= 0) {
                analysisTopicDTO.setBytesOutRate(0.0);
            } else {
                analysisTopicDTO.setBytesOutRate(topicMetrics.getBytesOutPerSecOneMinuteRate(0.0) / analysisBrokerDTO.getBytesOut());
            }

            analysisTopicDTO.setMessagesIn(topicMetrics.getMessagesInPerSecOneMinuteRate(0.0));
            if (analysisBrokerDTO.getMessagesIn() <= 0) {
                analysisTopicDTO.setMessagesInRate(0.0);
            } else {
                analysisTopicDTO.setMessagesInRate(topicMetrics.getMessagesInPerSecOneMinuteRate(0.0) / analysisBrokerDTO.getMessagesIn());
            }

            analysisTopicDTO.setTotalFetchRequests(topicMetrics.getTotalFetchRequestsPerSecOneMinuteRate(0.0));
            if (analysisBrokerDTO.getTotalFetchRequests() <= 0) {
                analysisTopicDTO.setTotalFetchRequestsRate(0.0);
            } else {
                analysisTopicDTO.setTotalFetchRequestsRate(topicMetrics.getTotalFetchRequestsPerSecOneMinuteRate(0.0) / analysisBrokerDTO.getTotalFetchRequests());
            }

            analysisTopicDTO.setTotalProduceRequests(topicMetrics.getTotalProduceRequestsPerSecOneMinuteRate(0.0));
            if (analysisBrokerDTO.getTotalProduceRequests() <= 0) {
                analysisTopicDTO.setTotalProduceRequestsRate(0.0);
            } else {
                analysisTopicDTO.setTotalProduceRequestsRate(topicMetrics.getTotalProduceRequestsPerSecOneMinuteRate(0.0) / analysisBrokerDTO.getTotalProduceRequests());
            }
            topicNameSet.add(topicMetrics.getTopicName());
            analysisBrokerDTO.getTopicAnalysisVOList().add(analysisTopicDTO);
        }
    }
}
