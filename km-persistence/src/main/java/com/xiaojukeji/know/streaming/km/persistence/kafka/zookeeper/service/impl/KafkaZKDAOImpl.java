package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.impl;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.topic.TopicTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.ControllerData;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers.PartitionMap;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.utils.Json;
import kafka.zk.*;
import kafka.zookeeper.AsyncResponse;
import kafka.zookeeper.CreateRequest;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import scala.Option;

import java.util.*;

/**
 * 读取Kafka-ZK数据服务
 * @author zengqiao
 * @date 22/03/08
 */
@Repository
public class KafkaZKDAOImpl implements KafkaZKDAO {
    private static final ILog logger = LogFactory.getLog(KafkaZKDAOImpl.class);

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Override
    public Broker getBrokerMetadata(String zkAddress) throws KeeperException.NoNodeException, AdminOperateException {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(zkAddress, 3000, watchedEvent -> logger.info(" receive event : " + watchedEvent.getType().name()));
            List<String> brokerIdList = this.getChildren(zooKeeper, BrokerIdsZNode.path());
            if (brokerIdList == null || brokerIdList.isEmpty()) {
                return null;
            }

            BrokerMetadata brokerMetadata = this.getData(zooKeeper, BrokerIdZNode.path(Integer.parseInt(brokerIdList.get(0))), false, BrokerMetadata.class);
            return this.convert2Broker(null, Integer.valueOf(brokerIdList.get(0)), brokerMetadata);
        }  catch (KeeperException.NoNodeException nne) {
            logger.warn("method=getBrokerMetadata||zkAddress={}||errMsg=exception", zkAddress, nne);
            throw nne;
        } catch (Exception e) {
            logger.error("method=getBrokerMetadata||zkAddress={}||errMsg=exception", zkAddress, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        } finally {
            try {
                if (zooKeeper != null) {
                    zooKeeper.close();
                }
            } catch (Exception e) {
                logger.error("method=getBrokerMetadata||zkAddress={}||msg=close failed||errMsg=exception", zkAddress, e);
            }
        }
    }

    @Override
    public Broker getBrokerMetadata(Long clusterPhyId, Integer brokerId) throws NotExistException, KeeperException, AdminOperateException {
        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

        try {
            BrokerMetadata metadata = this.getData(kafkaZkClient.currentZooKeeper(), BrokerIdZNode.path(brokerId), false, BrokerMetadata.class);

            return this.convert2Broker(clusterPhyId, brokerId, metadata);
        } catch (KeeperException ke) {
            logger.error("method=getBrokerMetadata||clusterPhyId={}||brokerId={}||errMsg=exception", clusterPhyId, brokerId, ke);
            throw ke;
        } catch (Exception e) {
            logger.error("method=getBrokerMetadata||clusterPhyId={}||brokerId={}||errMsg=exception", clusterPhyId, brokerId, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        }
    }

    @Override
    public Topic getTopicMetadata(Long clusterPhyId, String topicName) throws NotExistException, KeeperException, AdminOperateException {
        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

        try {
            Stat stat = new Stat();
            PartitionMap partitionMap = this.getData(kafkaZkClient.currentZooKeeper(), TopicZNode.path(topicName), false, PartitionMap.class, stat);

            Topic topic = new Topic();
            topic.setClusterPhyId(clusterPhyId);
            topic.setTopicName(topicName);
            topic.setBrokerIdSet(new HashSet<>());
            topic.setCreateTime(stat.getCtime());
            topic.setUpdateTime(stat.getMtime());

            topic.setPartitionMap(partitionMap.getPartitions());
            topic.setReplicaNum(partitionMap.getPartitions().values().iterator().next().size());
            topic.setPartitionNum(partitionMap.getPartitions().size());
            topic.setType(TopicTypeEnum.getTopicTypeCode(topicName));

            Set<Integer> brokerIdSet = new HashSet<>();
            Map<Integer, List<Integer>> topicBrokers = partitionMap.getPartitions();
            for (Map.Entry<Integer, List<Integer>> entry : topicBrokers.entrySet()) {
                brokerIdSet.addAll(entry.getValue());
            }
            topic.setBrokerIdSet(brokerIdSet);

            return topic;
        } catch (KeeperException ke) {
            logger.error("method=getTopicMetadata||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhyId, topicName, ke);
            throw ke;
        } catch (Exception e) {
            logger.error("method=getTopicMetadata||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhyId, topicName, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        }
    }

    @Override
    public List<Topic> getAllTopicMetadata(Long clusterPhyId, boolean addWatch) throws NotExistException, KeeperException, AdminOperateException {
        List<Topic> topicList = new ArrayList<>();
        try {
            List<String> topicNameList = this.getChildren(clusterPhyId, TopicsZNode.path(), addWatch);
            for (String topicName: topicNameList) {
                topicList.add(this.getTopicMetadata(clusterPhyId, topicName));
            }

            return topicList;
        } catch (KeeperException ke) {
            logger.error("method=getAllTopicMetadata||clusterPhyId={}||errMsg=exception", clusterPhyId, ke);
            throw ke;
        } catch (Exception e) {
            logger.error("method=getAllTopicMetadata||clusterPhyId={}||errMsg=exception", clusterPhyId, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        }
    }

    @Override
    public KafkaController getKafkaController(Long clusterPhyId, boolean addWatch) throws NotExistException, KeeperException, AdminOperateException {
        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

        try {
            ControllerData controllerData = this.getData(kafkaZkClient.currentZooKeeper(), ControllerZNode.path(), addWatch, ControllerData.class);
            KafkaController kafkaController = new KafkaController();
            kafkaController.setClusterPhyId(clusterPhyId);
            kafkaController.setBrokerId(controllerData.getBrokerid());
            kafkaController.setTimestamp(controllerData.getTimestamp());

            return kafkaController;
        } catch (KeeperException.NoNodeException nne) {
            // controller节点不存在，则直接返回null
            return null;
        } catch (KeeperException ke) {
            logger.error("method=getKafkaController||clusterPhyId={}||errMsg=exception", clusterPhyId, ke);
            throw ke;
        } catch (Exception e) {
            logger.error("method=getKafkaController||clusterPhyId={}||errMsg=exception", clusterPhyId, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        }
    }

    @Override
    public List<String> getChildren(Long clusterPhyId, String path, boolean addWatch) throws NotExistException, KeeperException, AdminOperateException {
        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

        try {
            List<String> children = kafkaZkClient.currentZooKeeper().getChildren(path, addWatch);

            return children;
        } catch (KeeperException ke) {
            logger.error("method=getChildren||clusterPhyId={}||path={}||errMsg=exception", clusterPhyId, path, ke);
            throw ke;
        } catch (Exception e) {
            logger.error("method=getChildren||clusterPhyId={}||path={}||errMsg=exception", clusterPhyId, path, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        }
    }

    @Override
    public List<String> getChildren(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(path, false);
    }

    @Override
    public Tuple<byte[], Stat> getDataAndStat(Long clusterPhyId, String path) throws NotExistException, KeeperException, AdminOperateException {
        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

        try {
            Stat stat = new Stat();
            byte[] data = kafkaZkClient.currentZooKeeper().getData(path, false, stat);

            return new Tuple<>(data, stat);
        } catch (KeeperException ke) {
            logger.error("method=getDataAndStat||clusterPhyId={}||path={}||errMsg=exception", clusterPhyId, path, ke);
            throw ke;
        } catch (Exception e) {
            logger.error("method=getDataAndStat||clusterPhyId={}||path={}||errMsg=exception", clusterPhyId, path, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        }
    }

    @Override
    public <T> T getData(Long clusterPhyId, String path, Class<T> clazz) throws NotExistException, KeeperException, AdminOperateException {
        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

        try {
            return this.getData(kafkaZkClient.currentZooKeeper(), path, false, clazz);
        } catch (KeeperException ke) {
            logger.error("method=getData||clusterPhyId={}||path={}||errMsg=exception", clusterPhyId, path, ke);
            throw ke;
        } catch (Exception e) {
            logger.error("method=getData||clusterPhyId={}||path={}||errMsg=exception", clusterPhyId, path, e);

            throw new AdminOperateException("read zk failed", e, ResultStatus.ZK_OPERATE_FAILED);
        }
    }

    @Override
    public void createConfigChangeNotificationVersionOne(Long clusterPhyId, String entityType, String entityName) throws NotExistException {
        // 具体实现参考：KafkaZkClient.createConfigChangeNotification() 的实现，仅在组装数据的时候进行调整

        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

        kafkaZkClient.makeSurePersistentPathExists(ConfigEntityChangeNotificationZNode.path());

        String notificationPath = ConfigEntityChangeNotificationSequenceZNode.createPath();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("version", 1);
        dataMap.put("entity_type", entityType);
        dataMap.put("entity_name", entityName);

        CreateRequest createRequest = new CreateRequest(
                notificationPath,
                Json.encodeAsBytes(dataMap),
                kafkaZkClient.defaultAcls(notificationPath),
                CreateMode.PERSISTENT_SEQUENTIAL,
                Option.apply(null)
        );

        AsyncResponse createResponse = kafkaZkClient.retryRequestUntilConnected(createRequest, ZkVersion.MatchAnyVersion());
        createResponse.maybeThrow();
    }

    /**************************************************** private method ****************************************************/

    private <T> T getData(ZooKeeper zooKeeper, String path, boolean addWatch, Class<T> clazz, Stat stat) throws KeeperException, InterruptedException {
        byte[] bytes = zooKeeper.getData(path, addWatch, stat);
        return JSON.parseObject(bytes, clazz);
    }

    private <T> T getData(ZooKeeper zooKeeper, String path, boolean addWatch, Class<T> clazz) throws KeeperException, InterruptedException {
        byte[] bytes = zooKeeper.getData(path, addWatch, null);
        return JSON.parseObject(bytes, clazz);
    }

    private Broker convert2Broker(Long clusterPhyId, Integer brokerId, BrokerMetadata brokerMetadata) {
        Broker metadata = new Broker();
        metadata.setClusterPhyId(clusterPhyId);
        metadata.setBrokerId(brokerId);
        metadata.setHost(brokerMetadata.getHost());
        metadata.setPort(brokerMetadata.getPort());
        metadata.setJmxPort(brokerMetadata.getJmxPort());
        metadata.setStartTimestamp(brokerMetadata.getTimestamp());
        metadata.setRack(brokerMetadata.getRack());
        metadata.setStatus(Constant.ALIVE);
        metadata.setEndpointMap(brokerMetadata.getEndpointMap());
        return metadata;
    }
}
