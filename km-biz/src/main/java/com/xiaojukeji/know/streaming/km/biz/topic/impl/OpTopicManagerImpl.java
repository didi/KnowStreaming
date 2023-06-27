package com.xiaojukeji.know.streaming.km.biz.topic.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.topic.OpTopicManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicExpansionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicCreateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicPartitionExpandParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicTruncateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.utils.kafka.KafkaReplicaAssignUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.OpTopicService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import kafka.admin.AdminUtils;
import kafka.admin.BrokerMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import scala.Option;
import scala.collection.Seq;
import scala.jdk.javaapi.CollectionConverters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OpTopicManagerImpl implements OpTopicManager {
    private static final ILog log = LogFactory.getLog(OpTopicManagerImpl.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private OpTopicService opTopicService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private PartitionService partitionService;

    @Override
    public Result<Void> createTopic(TopicCreateDTO dto, String operator) {
        log.info("method=createTopic||param={}||operator={}.", dto, operator);

        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(dto.getClusterId());
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(dto.getClusterId()));
        }

        // 构造assignmentMap
        scala.collection.Map<Object, Seq<Object>> rawAssignmentMap = AdminUtils.assignReplicasToBrokers(
                this.buildBrokerMetadataSeq(dto.getClusterId(), dto.getBrokerIdList()),
                dto.getPartitionNum(),
                dto.getReplicaNum(),
                -1,
                -1
        );

        // 类型转换
        Map<Integer, List<Integer>> assignmentMap = new HashMap<>();
        rawAssignmentMap.
                toStream().
                foreach(elem -> assignmentMap.put(
                        (Integer) elem._1,
                        CollectionConverters.asJava(elem._2).stream().map(item -> (Integer)item).collect(Collectors.toList()))
                );

        // 创建Topic
        Result<Void> createTopicRes = opTopicService.createTopic(
                new TopicCreateParam(
                        dto.getClusterId(),
                        dto.getTopicName(),
                        new HashMap<String, String>((Map) dto.getProperties()),
                        assignmentMap,
                        dto.getDescription()
                ),
                operator
        );
        if (createTopicRes.successful()){
            try{
                FutureUtil.quickStartupFutureUtil.submitTask(() -> {
                    BackoffUtils.backoff(3000);
                    Result<List<Partition>> partitionsResult = partitionService.listPartitionsFromKafka(clusterPhy, dto.getTopicName());
                    if (partitionsResult.successful()){
                        partitionService.updatePartitions(clusterPhy.getId(), dto.getTopicName(), partitionsResult.getData(),  new ArrayList<>());
                    }
                });
            }catch (Exception e) {
                log.error("method=createTopic||param={}||operator={}||msg=add partition to db failed||errMsg=exception", dto, operator, e);
                return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, "Topic创建成功，但记录Partition到DB中失败，等待定时任务同步partition信息");
            }
        }
        return createTopicRes;
    }

    @Override
    public Result<Void> deleteTopicCombineRes(Long clusterPhyId, String topicName, String operator) {
        // 删除Topic
        Result<Void> rv = opTopicService.deleteTopic(new TopicParam(clusterPhyId, topicName), operator);
        if (rv.failed()) {
            return rv;
        }

        // 删除Topic相关的ACL信息

        return Result.buildSuc();
    }

    @Override
    @Transactional
    public Result<Void> expandTopic(TopicExpansionDTO dto, String operator) {
        Topic topic = topicService.getTopic(dto.getClusterId(), dto.getTopicName());
        if (topic == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(dto.getClusterId(), dto.getTopicName()));
        }

        TopicPartitionExpandParam expandParam = new TopicPartitionExpandParam(
                dto.getClusterId(),
                dto.getTopicName(),
                topic.getPartitionMap(),
                this.generateNewPartitionAssignment(dto.getClusterId(), topic, dto.getBrokerIdList(), dto.getIncPartitionNum())
        );

        // 更新DB分区数信息, 其他信息交由后台任务进行更新
        Result<Void> rv = topicService.updatePartitionNum(topic.getClusterPhyId(), topic.getTopicName(), topic.getPartitionNum() + dto.getIncPartitionNum());
        if (rv.failed()){
            return rv;
        }

        rv = opTopicService.expandTopic(expandParam, operator);
        if (rv.failed()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return rv;
        }
        return rv;
    }

    @Override
    public Result<Void> truncateTopic(Long clusterPhyId, String topicName, String operator) {
        // 清空Topic
        Result<Void> rv = opTopicService.truncateTopic(new TopicTruncateParam(clusterPhyId, topicName, KafkaConstant.TOPICK_TRUNCATE_DEFAULT_OFFSET), operator);
        if (rv.failed()) {
            return rv;
        }

        return Result.buildSuc();
    }

    /**************************************************** private method ****************************************************/


    private Seq<BrokerMetadata> buildBrokerMetadataSeq(Long clusterPhyId, final List<Integer> selectedBrokerIdList) {
        // 选取Broker列表
        List<Broker> brokerList = brokerService.listAliveBrokersFromDB(clusterPhyId).stream().filter( elem ->
                selectedBrokerIdList == null || selectedBrokerIdList.contains(elem.getBrokerId())
        ).collect(Collectors.toList());

        List<BrokerMetadata> brokerMetadataList = new ArrayList<>();
        for (Broker broker: brokerList) {
            brokerMetadataList.add(new BrokerMetadata(broker.getBrokerId(), Option.apply(broker.getRack())));
        }

        return CollectionConverters.asScala(brokerMetadataList);
    }

    private Map<Integer, List<Integer>> generateNewPartitionAssignment(Long clusterPhyId, Topic topic, List<Integer> brokerIdList, Integer incPartitionNum) {
        if (ValidateUtils.isEmptyList(brokerIdList)) {
            // 如果brokerId列表为空，则获取当前集群存活的Broker列表
            brokerIdList = brokerService.listAliveBrokersFromDB(clusterPhyId).stream().map( elem -> elem.getBrokerId()).collect(Collectors.toList());
        }

        Map<Integer, String> brokerRackMap = new HashMap<>();
        for (Broker broker: brokerService.listAliveBrokersFromDB(clusterPhyId)) {
            if (brokerIdList != null && !brokerIdList.contains(broker.getBrokerId())) {
                continue;
            }

            brokerRackMap.put(broker.getBrokerId(), broker.getRack() == null? "": broker.getRack());
        }

        // 生成分配规则
        return KafkaReplicaAssignUtil.generateNewPartitionAssignment(brokerRackMap, topic.getPartitionMap(), incPartitionNum);
    }
}
