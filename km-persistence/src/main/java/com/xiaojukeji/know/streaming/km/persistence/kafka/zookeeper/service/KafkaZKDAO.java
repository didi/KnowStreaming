package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service;

import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * 读取Kafka-ZK数据服务
 * @author zengqiao
 * @date 22/03/08
 */
public interface KafkaZKDAO {
    /**
     * 获取Broker元信息
     * @param zkAddress
     * @return
     * @throws KeeperException ZK异常
     * @throws AdminOperateException 操作异常
     */
    Broker getBrokerMetadata(String zkAddress) throws KeeperException.NoNodeException, AdminOperateException;

    /**
     * 获取Broker元信息
     * @param clusterPhyId 物理集群ID
     * @param brokerId BrokerId
     * @return
     * @throws NotExistException 不存在异常
     * @throws KeeperException ZK异常
     * @throws AdminOperateException 操作异常
     */
    Broker getBrokerMetadata(Long clusterPhyId, Integer brokerId) throws NotExistException, KeeperException, AdminOperateException;

    /**
     * 获取Topic元信息
     * @param clusterPhyId 物理集群ID
     * @param topicName Topic名称
     * @return
     * @throws NotExistException 不存在异常
     * @throws KeeperException ZK异常
     * @throws AdminOperateException 操作异常
     */
    Topic getTopicMetadata(Long clusterPhyId, String topicName) throws NotExistException, KeeperException, AdminOperateException;

    /**
     * 获取Topic元信息
     * @param clusterPhyId 物理集群ID
     * @return
     * @throws NotExistException 不存在异常
     * @throws KeeperException ZK异常
     * @throws AdminOperateException 操作异常
     */
    List<Topic> getAllTopicMetadata(Long clusterPhyId, boolean addWatch) throws NotExistException, KeeperException, AdminOperateException;

    /**
     * 获取KafkaController信息
     * @param clusterPhyId 物理集群ID
     * @return
     * @throws NotExistException 不存在异常
     * @throws KeeperException ZK异常
     * @throws AdminOperateException 操作异常
     */
    KafkaController getKafkaController(Long clusterPhyId, boolean addWatch) throws NotExistException, KeeperException, AdminOperateException;

    List<String> getChildren(Long clusterPhyId, String path, boolean addWatch) throws NotExistException, KeeperException, AdminOperateException;

    List<String> getChildren(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException;

    Tuple<byte[], Stat> getDataAndStat(Long clusterPhyId, String path) throws NotExistException, KeeperException, AdminOperateException;
    <T> T getData(Long clusterPhyId, String path, Class<T> clazz) throws NotExistException, KeeperException, AdminOperateException;

    /**
     * 创建config-change节点，使用的是v1版本的结构
     * @param clusterPhyId 集群ID
     * @param entityType 实体类型
     * @param entityName 实体名称
     * @throws NotExistException 异常信息，除了这个异常之外，ZK还会有自己的异常抛出来
     */
    void createConfigChangeNotificationVersionOne(Long clusterPhyId, String entityType, String entityName) throws NotExistException;
}
