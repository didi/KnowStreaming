package com.xiaojukeji.know.streaming.km.ha.mirror.service.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.dto.ha.mirror.MirrorTopicCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.ha.mirror.MirrorTopicDeleteDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.ha.HaActiveStandbyRelation;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.ha.mirror.TopicMirrorInfoVO;
import com.xiaojukeji.know.streaming.km.common.enums.ha.HaResTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.ha.HaActiveStandbyRelationService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.ha.mirror.service.MirrorTopicService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import kafka.zk.KafkaZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems.TOPIC_METRIC_BYTES_IN;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems.TOPIC_METRIC_MIRROR_FETCH_LAG;

@Service
public class MirrorTopicServiceImpl implements MirrorTopicService {
    private static final ILog logger = LogFactory.getLog(MirrorTopicServiceImpl.class);

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private HaActiveStandbyRelationService haActiveStandbyRelationService;

    @Override
    public Result<Void> batchCreateMirrorTopic(List<MirrorTopicCreateDTO> dtoList) {
        for (MirrorTopicCreateDTO mirrorTopicCreateDTO : dtoList) {
            try {
                KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(mirrorTopicCreateDTO.getDestClusterPhyId());
                ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(mirrorTopicCreateDTO.getSourceClusterPhyId());
                Properties newHaClusters = ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class);
                newHaClusters.put("bootstrap.servers", clusterPhy.getBootstrapServers());
                if(clusterPhy.getKafkaVersion().contains("2.5.0-d-")){
                    newHaClusters.put("didi.kafka.enable", "true");
                }else{
                    newHaClusters.put("didi.kafka.enable", "false");
                }
                Properties oldHaClusters = kafkaZkClient.getEntityConfigs("ha-clusters", String.valueOf(mirrorTopicCreateDTO.getSourceClusterPhyId()));
                if (!oldHaClusters.equals(newHaClusters)) {
                    kafkaZkClient.setOrCreateEntityConfigs("ha-clusters", String.valueOf(mirrorTopicCreateDTO.getSourceClusterPhyId()), newHaClusters);
                    kafkaZkClient.createConfigChangeNotification("ha-clusters/" + mirrorTopicCreateDTO.getSourceClusterPhyId());
                }
                boolean pathExists = kafkaZkClient.pathExists("/brokers/topics/" + mirrorTopicCreateDTO.getTopicName());
                if (pathExists) {
                    return Result.buildFailure(String.format("目标集群已存在%s,保证数据一致性,请删除后再创建", mirrorTopicCreateDTO.getTopicName()));
                }
                Properties haTopics = kafkaZkClient.getEntityConfigs("ha-topics", mirrorTopicCreateDTO.getTopicName());
                haTopics.put("didi.ha.remote.cluster", String.valueOf(mirrorTopicCreateDTO.getSourceClusterPhyId()));
                haTopics.put("didi.ha.sync.topic.partitions.enabled", "true");
                if (mirrorTopicCreateDTO.getSyncConfig()) {
                    haTopics.put("didi.ha.sync.topic.configs.enabled", "true");
                }
                kafkaZkClient.setOrCreateEntityConfigs("ha-topics", mirrorTopicCreateDTO.getTopicName(), haTopics);
                kafkaZkClient.createConfigChangeNotification("ha-topics/" + mirrorTopicCreateDTO.getTopicName());
                haActiveStandbyRelationService.batchReplaceTopicHA(mirrorTopicCreateDTO.getSourceClusterPhyId(), mirrorTopicCreateDTO.getDestClusterPhyId(), Collections.singletonList(mirrorTopicCreateDTO.getTopicName()));
            } catch (Exception e) {
                logger.error("method=batchCreateMirrorTopic||topicName:{}||errMsg=exception", mirrorTopicCreateDTO.getTopicName(), e);
                return Result.buildFailure(e.getMessage());
            }
        }
        return Result.buildSuc();
    }

    @Override
    public Result<Void> batchDeleteMirrorTopic(List<MirrorTopicDeleteDTO> dtoList) {
        for (MirrorTopicDeleteDTO mirrorTopicDeleteDTO : dtoList) {
            try {
                KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(mirrorTopicDeleteDTO.getDestClusterPhyId());
                Properties haTopics = kafkaZkClient.getEntityConfigs("ha-topics", mirrorTopicDeleteDTO.getTopicName());
                if (haTopics.size() != 0) {
                    kafkaZkClient.setOrCreateEntityConfigs("ha-topics", mirrorTopicDeleteDTO.getTopicName(), new Properties());
                    kafkaZkClient.createConfigChangeNotification("ha-topics/" + mirrorTopicDeleteDTO.getTopicName());
                }
                haActiveStandbyRelationService.batchDeleteTopicHA(mirrorTopicDeleteDTO.getSourceClusterPhyId(), mirrorTopicDeleteDTO.getDestClusterPhyId(), Collections.singletonList(mirrorTopicDeleteDTO.getTopicName()));
            } catch (Exception e) {
                logger.error("method=batchDeleteMirrorTopic||topicName:{}||errMsg=exception", mirrorTopicDeleteDTO.getTopicName(), e);
                return Result.buildFailure(e.getMessage());
            }
        }
        return Result.buildSuc();
    }

    @Override
    public Result<List<TopicMirrorInfoVO>> getTopicsMirrorInfo(Long clusterPhyId, String topicName) {
        List<HaActiveStandbyRelation> haActiveStandbyRelations = haActiveStandbyRelationService.listByClusterAndType(clusterPhyId, HaResTypeEnum.MIRROR_TOPIC);
        List<TopicMirrorInfoVO> topicMirrorInfoVOList = new ArrayList<>();
        for (HaActiveStandbyRelation activeStandbyRelation : haActiveStandbyRelations) {
            if (activeStandbyRelation.getResName().equals(topicName)) {
                ClusterPhy standbyClusterPhy = clusterPhyService.getClusterByCluster(activeStandbyRelation.getStandbyClusterPhyId());
                ClusterPhy activeClusterPhy = clusterPhyService.getClusterByCluster(activeStandbyRelation.getActiveClusterPhyId());
                TopicMirrorInfoVO topicMirrorInfoVO = new TopicMirrorInfoVO();
                topicMirrorInfoVO.setSourceClusterId(activeStandbyRelation.getActiveClusterPhyId());
                topicMirrorInfoVO.setDestClusterId(activeStandbyRelation.getStandbyClusterPhyId());
                topicMirrorInfoVO.setTopicName(activeStandbyRelation.getResName());
                topicMirrorInfoVO.setSourceClusterName(activeClusterPhy.getName());
                topicMirrorInfoVO.setDestClusterName(standbyClusterPhy.getName());
                Result<List<TopicMetrics>> ret = topicMetricService.collectTopicMetricsFromKafka(activeStandbyRelation.getStandbyClusterPhyId(), activeStandbyRelation.getResName(), TOPIC_METRIC_BYTES_IN);
                if (ret.hasData()) {
                    Double value = this.getTopicAggMetric(ret.getData(), TOPIC_METRIC_BYTES_IN);
                    topicMirrorInfoVO.setReplicationBytesIn(value);
                }

                ret = topicMetricService.collectTopicMetricsFromKafka(activeStandbyRelation.getActiveClusterPhyId(), activeStandbyRelation.getResName(), TOPIC_METRIC_BYTES_IN);
                if (ret.hasData()) {
                    Double value = this.getTopicAggMetric(ret.getData(), TOPIC_METRIC_BYTES_IN);
                    topicMirrorInfoVO.setBytesIn(value);
                }

                ret = topicMetricService.collectTopicMetricsFromKafka(activeStandbyRelation.getStandbyClusterPhyId(), activeStandbyRelation.getResName(), TOPIC_METRIC_MIRROR_FETCH_LAG);
                if (ret.hasData()) {
                    Float lag = ret.getData().get(0).getMetric(TOPIC_METRIC_MIRROR_FETCH_LAG);
                    topicMirrorInfoVO.setLag(lag == null ? 0 : lag.longValue());
                }

                topicMirrorInfoVOList.add(topicMirrorInfoVO);
            }
        }
        return Result.buildSuc(topicMirrorInfoVOList);
    }

    private Double getTopicAggMetric(List<TopicMetrics> topicMetricsList, String metricName) {
        for (TopicMetrics topicMetrics : topicMetricsList) {
            if (topicMetrics.isBBrokerAgg()) {
                Float value = topicMetrics.getMetric(metricName);
                if (value != null) {
                    return value.doubleValue();
                }
            }
        }
        return Double.NaN;
    }
}