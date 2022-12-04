package com.xiaojukeji.know.streaming.km.core.service.topic.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicConfig;
import com.xiaojukeji.know.streaming.km.common.bean.po.topic.TopicPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.TopicConverter;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterRunStateEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.DateUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.topic.TopicDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.zk.TopicsZNode;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author didi
 */
@Service
public class TopicServiceImpl implements TopicService {
    private static final ILog log = LogFactory.getLog(TopicConfigServiceImpl.class);

    @Autowired
    private TopicDAO topicDAO;

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    private final Cache<Long, Map<String, Topic>> topicsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

    @Override
    public Result<List<Topic>> listTopicsFromKafka(ClusterPhy clusterPhy) {
        if (clusterPhy.getRunState().equals(ClusterRunStateEnum.RUN_ZK.getRunState())) {
            return this.getTopicsFromZKClient(clusterPhy);
        }

        return this.getTopicsFromAdminClient(clusterPhy);
    }

    @Override
    public Map<Integer, List<Integer>> getTopicPartitionMapFromKafka(Long clusterPhyId, String topicName) throws NotExistException, AdminOperateException {
        AdminClient adminClient = kafkaAdminClient.getClient(clusterPhyId);

        try {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(
                    Arrays.asList(topicName),
                    new DescribeTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            TopicDescription description = describeTopicsResult.all().get().get(topicName);

            Map<Integer, List<Integer>> partitionMap = new HashMap<>();
            for (TopicPartitionInfo partitionInfo: description.partitions()) {
                partitionMap.put(partitionInfo.partition(), partitionInfo.replicas().stream().map(elem -> elem.id()).collect(Collectors.toList()));
            }

            return partitionMap;
        } catch (ExecutionException e) {
            log.error("method=getTopicPartitionMapFromKafka||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhyId, topicName, e);
            if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                throw new AdminOperateException(String.format("Kafka does not host Topic:[%s]", topicName), e.getCause(), ResultStatus.KAFKA_OPERATE_FAILED);
            }

            throw new AdminOperateException("get topic info from kafka failed", e.getCause(), ResultStatus.KAFKA_OPERATE_FAILED);
        } catch (Exception e) {
            log.error("method=getTopicPartitionMapFromKafka||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhyId, topicName, e);
            throw new AdminOperateException("get topic info from kafka failed", e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }

    @Override
    public List<Topic> listTopicsFromDB(Long clusterPhyId) {
        return TopicConverter.convert2TopicList(this.listTopicPOsFromDB(clusterPhyId));
    }

    @Override
    public List<TopicPO> listTopicPOsFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<TopicPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicPO::getClusterPhyId, clusterPhyId);

        return topicDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<Topic> listTopicsFromCacheFirst(Long clusterPhyId) {
        Map<String, Topic> topicMap = topicsCache.getIfPresent(clusterPhyId);
        if (topicMap == null) {
            topicMap = this.getTopicsAndUpdateCache(clusterPhyId);
        }

        return topicMap == null? new ArrayList<>(): new ArrayList<>(topicMap.values());
    }

    @Override
    public List<String> listRecentUpdateTopicNamesFromDB(Long clusterPhyId, Integer time) {
        LambdaQueryWrapper<TopicPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ge(TopicPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.ge(TopicPO::getUpdateTime, DateUtils.getBeforeSeconds(new Date(), time));

        List<TopicPO> poList = topicDAO.selectList(lambdaQueryWrapper);
        if (poList.isEmpty()){
            return new ArrayList<>();
        }

        return poList.stream().map(elem -> elem.getTopicName()).collect(Collectors.toList());
    }

    @Override
    public Integer getTopicSizeFromCacheFirst(Long clusterPhyId) {
        List<Topic> topicList = this.listTopicsFromCacheFirst(clusterPhyId);
        return topicList == null? 0: topicList.size();
    }

    @Override
    public Integer getReplicaSizeFromCacheFirst(Long clusterPhyId) {
        List<Topic> topicList = this.listTopicsFromCacheFirst(clusterPhyId);
        if (ValidateUtils.isEmptyList(topicList)) {
            return 0;
        }

        return topicList.stream()
                .map(elem -> elem.getPartitionNum() * elem.getReplicaNum())
                .reduce(Integer::sum)
                .get();
    }

    @Override
    public Topic getTopic(Long clusterPhyId, String topicName) {
        TopicPO po = this.getTopicFromDB(clusterPhyId, topicName);
        if (po == null) {
            return null;
        }

        return TopicConverter.convert2Topic(po);
    }

    @Override
    public Topic getTopicFromCacheFirst(Long clusterPhyId, String topicName) {
        Map<String, Topic> topicMap = topicsCache.getIfPresent(clusterPhyId);
        if (topicMap == null) {
            topicMap = this.getTopicsAndUpdateCache(clusterPhyId);
        }

        return topicMap == null? null: topicMap.get(topicName);
    }

    @Override
    public int addNewTopic2DB(TopicPO po) {
        return topicDAO.replaceAll(po);
    }

    @Override
    public int deleteTopicInDB(Long clusterPhyId, String topicName) {
        LambdaQueryWrapper<TopicPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(TopicPO::getTopicName, topicName);
        return topicDAO.delete(lambdaQueryWrapper);
    }

    @Override
    public void batchReplaceMetadata(Long clusterPhyId, List<Topic> presentTopicList) {
        Map<String, TopicPO> inDBMap = this.listTopicPOsFromDB(clusterPhyId).stream().collect(Collectors.toMap(TopicPO::getTopicName, Function.identity()));

        // 新旧合并
        for (Topic presentTopic: presentTopicList) {
            try {
                TopicPO inDBTopicPO = inDBMap.remove(presentTopic.getTopicName());

                TopicPO newTopicPO = TopicConverter.mergeAndOnlyMetadata2NewTopicPO(presentTopic, inDBTopicPO);
                if (inDBTopicPO == null) {
                    topicDAO.insert(newTopicPO);
                } else if (!newTopicPO.equals(inDBTopicPO)) {
                    // 有变化时，则进行更新
                    if (presentTopic.getUpdateTime() == null) {
                        // 如果原数据的更新时间为null，则修改为当前时间
                        newTopicPO.setUpdateTime(new Date());
                    }
                    topicDAO.updateById(newTopicPO);
                }

                // 无变化时，直接忽略更新
            } catch (DuplicateKeyException dke) {
                // 忽略key冲突错误，多台KM可能同时做insert，所以可能出现key冲突
            }
        }

        // DB中没有的则进行删除
        inDBMap.values().forEach(elem -> topicDAO.deleteById(elem.getId()));
    }

    @Override
    public int batchReplaceChangedConfig(Long clusterPhyId, List<TopicConfig> changedConfigList) {
        int effectRow = 0;
        for (TopicConfig config: changedConfigList) {
            try {
                effectRow += topicDAO.updateConfigById(ConvertUtil.obj2Obj(config, TopicPO.class));
            } catch (Exception e) {
                log.error(
                        "method=batchReplaceConfig||clusterPhyId={}||topicName={}||retentionMs={}||errMsg=exception!",
                        config.getClusterPhyId(), config.getTopicName(), config.getRetentionMs(), e
                );
            }
        }

        return effectRow;
    }

    @Override
    public Result<Void> updatePartitionNum(Long clusterPhyId, String topicName, Integer partitionNum) {
        try {
            LambdaUpdateWrapper<TopicPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(TopicPO::getClusterPhyId, clusterPhyId);
            lambdaUpdateWrapper.eq(TopicPO::getTopicName, topicName);

            TopicPO topicPO = new TopicPO();
            topicPO.setPartitionNum(partitionNum);

            if (topicDAO.update(topicPO, lambdaUpdateWrapper) > 0){
                return Result.buildSuc();
            }
        } catch (Exception e) {
            log.error("method=updatePartitionNum||clusterPhyId={}||topicName={}||partitionNum={}||errMsg=exception!", clusterPhyId, topicName, e);
            return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
        }

        return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
    }

    /**************************************************** private method ****************************************************/

    private Result<List<Topic>> getTopicsFromAdminClient(ClusterPhy clusterPhy) {
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(clusterPhy.getId());

            ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS).listInternal(true));

            List<Topic> topicList = new ArrayList<>();

            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(listTopicsResult.names().get(), new DescribeTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            Map<String, TopicDescription> descriptionMap = describeTopicsResult.all().get();
            for (TopicDescription description: descriptionMap.values()) {
                topicList.add(TopicConverter.convert2Topic(clusterPhy.getId(), description));
            }

            return Result.buildSuc(topicList);
        } catch (Exception e) {
            log.error("method=getTopicsFromAdminClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<Topic>> getTopicsFromZKClient(ClusterPhy clusterPhy) {
        try {

            List<Topic> topicList = new ArrayList<>();

            List<String> topicNameList = kafkaZKDAO.getChildren(clusterPhy.getId(), TopicsZNode.path(), false);
            for (String topicName: topicNameList) {
                topicList.add(kafkaZKDAO.getTopicMetadata(clusterPhy.getId(), topicName));
            }

            return Result.buildSuc(topicList);
        } catch (Exception e) {
            log.error("method=getTopicsFromZKClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Map<String, Topic> getTopicsAndUpdateCache(Long clusterPhyId) {
        List<Topic> topicList = this.listTopicsFromDB(clusterPhyId);

        Map<String, Topic> topicMap = topicList.stream().collect(Collectors.toMap(Topic::getTopicName, Function.identity()));
        topicsCache.put(clusterPhyId, topicMap);
        return topicMap;
    }

    private TopicPO getTopicFromDB(Long clusterPhyId, String topicName) {
        LambdaQueryWrapper<TopicPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(TopicPO::getTopicName, topicName);

        return topicDAO.selectOne(lambdaQueryWrapper);
    }
}
