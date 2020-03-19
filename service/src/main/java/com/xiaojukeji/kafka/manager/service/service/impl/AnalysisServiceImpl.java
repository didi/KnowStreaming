package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.entity.dto.analysis.AnalysisBrokerDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.analysis.AnalysisTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AnalysisService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Integer MIN_TOP_TOPIC_VALUE = 2;

    @Override
    public AnalysisBrokerDTO doAnalysisBroker(Long clusterId, Integer brokerId) {
        AnalysisBrokerDTO analysisBrokerDTO = new AnalysisBrokerDTO();
        analysisBrokerDTO.setClusterId(clusterId);
        analysisBrokerDTO.setBrokerId(brokerId);
        analysisBrokerDTO.setBaseTime(System.currentTimeMillis());
        analysisBrokerDTO.setTopicAnalysisVOList(new ArrayList<>());

        BrokerMetrics brokerMetrics = jmxService.getSpecifiedBrokerMetricsFromJmx(clusterId, brokerId, BrokerMetrics.getFieldNameList(MetricsType.BROKER_ANALYSIS_METRICS), true);
        if (brokerMetrics == null) {
            return analysisBrokerDTO;
        }
        analysisBrokerDTO.setBytesIn(brokerMetrics.getBytesInPerSec());
        analysisBrokerDTO.setBytesOut(brokerMetrics.getBytesOutPerSec());
        analysisBrokerDTO.setMessagesIn(brokerMetrics.getMessagesInPerSec());
        analysisBrokerDTO.setTotalProduceRequests(brokerMetrics.getTotalProduceRequestsPerSec());
        analysisBrokerDTO.setTotalFetchRequests(brokerMetrics.getTotalFetchRequestsPerSec());

        List<TopicMetrics> topicMetricsList = new ArrayList<>();
        for (String topicName: ClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
            if (topicMetadata == null || !topicMetadata.getBrokerIdSet().contains(brokerId)) {
                continue;
            }
            TopicMetrics topicMetrics = jmxService.getSpecifiedBrokerTopicMetricsFromJmx(clusterId, brokerId, topicName, TopicMetrics.getFieldNameList(MetricsType.BROKER_TOPIC_ANALYSIS_METRICS), true);
            if (topicMetrics == null) {
                continue;
            }
            if (topicMetrics.getBytesInPerSec() < MIN_TOP_TOPIC_VALUE.doubleValue()
                    || topicMetrics.getTotalProduceRequestsPerSec() < MIN_TOP_TOPIC_VALUE.doubleValue()) {
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
                        diff = t1.getBytesInPerSec() - t2.getBytesInPerSec();
                        break;
                    case "TotalProduceRequest":
                        diff = t1.getTotalProduceRequestsPerSec() - t2.getTotalProduceRequestsPerSec();
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
                return t1.getTopicName().compareTo(t2.toString());
            }
        });

        for (int i = 0; i < TOP_TOPIC_NUM && i < topicMetricsList.size(); ++i) {
            TopicMetrics topicMetrics = topicMetricsList.get(i);
            if (topicNameSet.contains(topicMetrics.getTopicName())) {
                continue;
            }
            AnalysisTopicDTO analysisTopicDTO = new AnalysisTopicDTO();
            analysisTopicDTO.setTopicName(topicMetrics.getTopicName());
            analysisTopicDTO.setBytesIn(topicMetrics.getBytesInPerSec());
            if (analysisBrokerDTO.getBytesIn() <= 0) {
                analysisTopicDTO.setBytesInRate(0.0);
            } else {
                analysisTopicDTO.setBytesInRate(topicMetrics.getBytesInPerSec() / analysisBrokerDTO.getBytesIn());
            }

            analysisTopicDTO.setBytesOut(topicMetrics.getBytesOutPerSec());
            if (analysisBrokerDTO.getBytesOut() <= 0) {
                analysisTopicDTO.setBytesOutRate(0.0);
            } else {
                analysisTopicDTO.setBytesOutRate(topicMetrics.getBytesOutPerSec() / analysisBrokerDTO.getBytesOut());
            }

            analysisTopicDTO.setMessagesIn(topicMetrics.getMessagesInPerSec());
            if (analysisBrokerDTO.getMessagesIn() <= 0) {
                analysisTopicDTO.setMessagesInRate(0.0);
            } else {
                analysisTopicDTO.setMessagesInRate(topicMetrics.getMessagesInPerSec() / analysisBrokerDTO.getMessagesIn());
            }

            analysisTopicDTO.setTotalFetchRequests(topicMetrics.getTotalFetchRequestsPerSec());
            if (analysisBrokerDTO.getTotalFetchRequests() <= 0) {
                analysisTopicDTO.setTotalFetchRequestsRate(0.0);
            } else {
                analysisTopicDTO.setTotalFetchRequestsRate(topicMetrics.getTotalFetchRequestsPerSec() / analysisBrokerDTO.getTotalFetchRequests());
            }

            analysisTopicDTO.setTotalProduceRequests(topicMetrics.getTotalProduceRequestsPerSec());
            if (analysisBrokerDTO.getTotalProduceRequests() <= 0) {
                analysisTopicDTO.setTotalProduceRequestsRate(0.0);
            } else {
                analysisTopicDTO.setTotalProduceRequestsRate(topicMetrics.getTotalProduceRequestsPerSec() / analysisBrokerDTO.getTotalProduceRequests());
            }
            topicNameSet.add(topicMetrics.getTopicName());
            analysisBrokerDTO.getTopicAnalysisVOList().add(analysisTopicDTO);
        }
    }
}
