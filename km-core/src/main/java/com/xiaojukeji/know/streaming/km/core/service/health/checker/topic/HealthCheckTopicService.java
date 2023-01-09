package com.xiaojukeji.know.streaming.km.core.service.health.checker.topic;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthDetectedInLatestMinutesConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems.TOPIC_METRIC_UNDER_REPLICA_PARTITIONS;

@Service
public class HealthCheckTopicService extends AbstractHealthCheckService {
    private static final ILog LOGGER = LogFactory.getLog(HealthCheckTopicService.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private TopicMetricService topicMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.TOPIC_NO_LEADER.getConfigName(), this::checkTopicNoLeader);
        functionMap.putIfAbsent(HealthCheckNameEnum.TOPIC_UNDER_REPLICA_TOO_LONG.getConfigName(), this::checkTopicUnderReplicatedPartition);
    }

    @Override
    public List<ClusterParam> getResList(Long clusterPhyId) {
        List<ClusterParam> paramList = new ArrayList<>();
        for (Topic topic: topicService.listTopicsFromCacheFirst(clusterPhyId)) {
            paramList.add(new TopicParam(clusterPhyId, topic.getTopicName()));
        }
        return paramList;
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.TOPIC;
    }

    @Override
    public Integer getDimensionCodeIfSupport(Long kafkaClusterPhyId) {
        return this.getHealthCheckDimensionEnum().getDimension();
    }

    /**
     * 检查Topic长期未同步
     */
    private HealthCheckResult checkTopicUnderReplicatedPartition(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        TopicParam param = (TopicParam) paramTuple.getV1();
        HealthDetectedInLatestMinutesConfig singleConfig = (HealthDetectedInLatestMinutesConfig) paramTuple.getV2();

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.TOPIC.getDimension(),
                HealthCheckNameEnum.TOPIC_UNDER_REPLICA_TOO_LONG.getConfigName(),
                param.getClusterPhyId(),
                param.getTopicName()
        );

        Result<Integer> countResult = topicMetricService.countMetricValueOccurrencesFromES(
                param.getClusterPhyId(),
                param.getTopicName(),
                new SearchTerm(TOPIC_METRIC_UNDER_REPLICA_PARTITIONS, "0", false),
                System.currentTimeMillis() - singleConfig.getLatestMinutes() * 60L * 1000L,
                System.currentTimeMillis()
        );

        if (countResult.failed() || !countResult.hasData()) {
            LOGGER.error("method=checkTopicUnderReplicatedPartition||param={}||config={}||result={}||errMsg=search metrics from es failed",
                    param, singleConfig, countResult);
            return null;
        }

        checkResult.setPassed(countResult.getData() >= singleConfig.getDetectedTimes()? Constant.NO: Constant.YES);
        return checkResult;
    }

    /**
     * 检查NoLeader
     */
    private HealthCheckResult checkTopicNoLeader(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        TopicParam param = (TopicParam) singleConfigSimpleTuple.getV1();
        List<Partition> partitionList = partitionService.listPartitionFromCacheFirst(param.getClusterPhyId(), param.getTopicName());

        HealthCompareValueConfig valueConfig = (HealthCompareValueConfig) singleConfigSimpleTuple.getV2();
        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.TOPIC.getDimension(),
                HealthCheckNameEnum.TOPIC_NO_LEADER.getConfigName(),
                param.getClusterPhyId(),
                param.getTopicName()
        );

        checkResult.setPassed(partitionList.stream().filter(elem -> elem.getLeaderBrokerId().equals(Constant.INVALID_CODE)).count() >= valueConfig.getValue() ? Constant.NO : Constant.YES);

        return checkResult;
    }
}
