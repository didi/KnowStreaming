package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.AdminTopicStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OperationEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.OperationHistoryDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.OperationHistoryDao;
import com.xiaojukeji.kafka.manager.dao.TopicDao;
import com.xiaojukeji.kafka.manager.service.service.AdminTopicService;
import com.xiaojukeji.kafka.manager.service.utils.BrokerMetadataUtil;
import kafka.admin.AdminOperationException;
import kafka.admin.AdminUtils;
import kafka.admin.BrokerMetadata;
import kafka.common.TopicAndPartition;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.kafka.common.errors.*;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.*;

/**
 * @author arthur
 * @date 2017/7/21
 */
@Service("adminTopicService")
public class AdminTopicServiceImpl implements AdminTopicService {
    private static final Logger logger = LoggerFactory.getLogger(AdminTopicServiceImpl.class);

    private static final int DEFAULT_SESSION_TIMEOUT = 30000;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private OperationHistoryDao operationHistoryDao;

    @Override
    public AdminTopicStatusEnum createTopic(ClusterDO clusterDO, TopicMetadata topicMetadata, TopicDO topicDO, Properties topicConfig, String operator) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(), DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT, JaasUtils.isZkSecurityEnabled());

            scala.collection.Seq<Object> brokerListSeq = JavaConversions.asScalaBuffer(new ArrayList<Object>(topicMetadata.getBrokerIdSet())).toSeq();
            scala.collection.Seq<kafka.admin.BrokerMetadata> brokerMetadataListSeq = BrokerMetadataUtil.convert2BrokerMetadata(brokerListSeq);
            scala.collection.Map<Object, scala.collection.Seq<Object>> replicaAssignment = AdminUtils.assignReplicasToBrokers(brokerMetadataListSeq, topicMetadata.getPartitionNum(), topicMetadata.getReplicaNum(), -1, -1);
            AdminUtils.createOrUpdateTopicPartitionAssignmentPathInZK(zkUtils, topicMetadata.getTopic(), replicaAssignment, topicConfig, false);
        } catch (NullPointerException e) {
            return AdminTopicStatusEnum.PARAM_NULL_POINTER;
        } catch (InvalidPartitionsException e) {
            return AdminTopicStatusEnum.PARTITION_NUM_ILLEGAL;
        } catch (InvalidReplicationFactorException e) {
            return AdminTopicStatusEnum.BROKER_NUM_NOT_ENOUGH;
        } catch (TopicExistsException | ZkNodeExistsException e) {
            return AdminTopicStatusEnum.TOPIC_EXISTED;
        } catch (InvalidTopicException e) {
            return AdminTopicStatusEnum.TOPIC_NAME_ILLEGAL;
        } catch (Throwable t) {
            return AdminTopicStatusEnum.UNKNOWN_ERROR;
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }

        // Topic信息及操作记录写DB
        try {
            OperationHistoryDO operationHistoryDO = OperationHistoryDO.newInstance(topicDO.getClusterId(), topicDO.getTopicName(), operator, OperationEnum.CREATE_TOPIC.message);
            operationHistoryDao.insert(operationHistoryDO);
            topicDao.replace(topicDO);

        } catch (Exception e) {
            return AdminTopicStatusEnum.REPLACE_DB_FAILED;
        }
        return AdminTopicStatusEnum.SUCCESS;
    }

    @Override
    public AdminTopicStatusEnum deleteTopic(ClusterDO clusterDO, String topicName, String operator) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(), DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT, JaasUtils.isZkSecurityEnabled());
            AdminUtils.deleteTopic(zkUtils, topicName);
        } catch (UnknownTopicOrPartitionException e) {
            return AdminTopicStatusEnum.UNKNOWN_TOPIC_PARTITION;
        } catch (ZkNodeExistsException e) {
            return AdminTopicStatusEnum.TOPIC_IN_DELETING;
        } catch (Throwable t) {
            return AdminTopicStatusEnum.UNKNOWN_ERROR;
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }

        // Topic信息及操作记录写DB
        try {
            OperationHistoryDO operationHistoryDO = OperationHistoryDO.newInstance(clusterDO.getId(), topicName, operator, OperationEnum.DELETE_TOPIC.message);
            operationHistoryDao.insert(operationHistoryDO);
            topicDao.deleteByName(clusterDO.getId(), topicName);
        } catch (Exception e) {
            return AdminTopicStatusEnum.REPLACE_DB_FAILED;
        }
        return AdminTopicStatusEnum.SUCCESS;
    }

    @Override
    public AdminTopicStatusEnum modifyTopic(ClusterDO clusterDO, TopicDO topicDO, Properties topicConfig, String operator) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(), DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT, JaasUtils.isZkSecurityEnabled());
            AdminUtils.changeTopicConfig(zkUtils, topicDO.getTopicName(), topicConfig);
        } catch (AdminOperationException e) {
            return AdminTopicStatusEnum.UNKNOWN_TOPIC_PARTITION;
        } catch (InvalidConfigurationException e) {
            return AdminTopicStatusEnum.TOPIC_CONFIG_ILLEGAL;
        } catch (Throwable t) {
            return AdminTopicStatusEnum.UNKNOWN_ERROR;
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }

        // Topic信息及操作记录写DB
        try {
            OperationHistoryDO operationHistoryDO = OperationHistoryDO.newInstance(clusterDO.getId(), topicDO.getTopicName(), operator, OperationEnum.MODIFY_TOPIC_CONFIG.message);
            operationHistoryDao.insert(operationHistoryDO);
            topicDao.replace(topicDO);
        } catch (Exception e) {
            return AdminTopicStatusEnum.REPLACE_DB_FAILED;
        }
        return AdminTopicStatusEnum.SUCCESS;
    }

    @Override
    public AdminTopicStatusEnum expandTopic(ClusterDO clusterDO, TopicMetadata topicMetadata, String operator) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(), DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT, JaasUtils.isZkSecurityEnabled());

            List<String> topicList = new ArrayList<String>();
            topicList.add(topicMetadata.getTopic());
            scala.collection.mutable.Map<TopicAndPartition, scala.collection.Seq<Object>> existingReplicaList = zkUtils.getReplicaAssignmentForTopics(JavaConversions.asScalaBuffer(topicList));
            Map<TopicAndPartition, scala.collection.Seq<Object>> javaMap = JavaConversions.asJavaMap(existingReplicaList);

            //重新构造分配策略
            Map<Object, scala.collection.Seq<Object>> targetMap = new HashMap<Object, scala.collection.Seq<Object>>();

            scala.collection.Map<Object, scala.collection.Seq<Object>> newPartitionReplicaList = null;

            Seq<Object> brokerListSeq = JavaConversions.asScalaBuffer(new ArrayList<Object>(topicMetadata.getBrokerIdSet())).toSeq();
            Seq<BrokerMetadata> brokerMetadataSeq = BrokerMetadataUtil.convert2BrokerMetadata(brokerListSeq);
            newPartitionReplicaList = AdminUtils.assignReplicasToBrokers(brokerMetadataSeq, topicMetadata.getPartitionNum(), existingReplicaList.head()._2().size(), (Integer) existingReplicaList.head()._2().head(), existingReplicaList.size());
            //转换为目标map，因为最终的map均是为[partition,该partition的replica的seq]
            for (Map.Entry<Object, scala.collection.Seq<Object>> entry : JavaConversions.asJavaMap(newPartitionReplicaList).entrySet()) {
                targetMap.put(entry.getKey(), entry.getValue());
            }

            //将已有的partition信息存入targetMap结构体内
            for (Map.Entry<TopicAndPartition, scala.collection.Seq<Object>> entry : javaMap.entrySet()) {
                targetMap.put(entry.getKey().partition(), entry.getValue());
            }

            //更新topic
            AdminUtils.createOrUpdateTopicPartitionAssignmentPathInZK(zkUtils, topicMetadata.getTopic(), JavaConversions.asScalaMap(targetMap), new Properties(), true);
        } catch (Throwable t) {
            return AdminTopicStatusEnum.UNKNOWN_ERROR;
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }

        // Topic信息及操作记录写DB
        try {
            OperationHistoryDO operationHistoryDO = OperationHistoryDO.newInstance(clusterDO.getId(), topicMetadata.getTopic(), operator, OperationEnum.EXPAND_TOPIC_PARTITION.message);
            operationHistoryDao.insert(operationHistoryDO);
        } catch (Exception e) {
            return AdminTopicStatusEnum.REPLACE_DB_FAILED;
        }
        return AdminTopicStatusEnum.SUCCESS;
    }

}
