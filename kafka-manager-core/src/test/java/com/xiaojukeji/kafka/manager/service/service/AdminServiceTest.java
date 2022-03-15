package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * @author xuguang
 * @Date 2021/12/24
 */
public class AdminServiceTest extends BaseTest {

    /**
     * 集群共包括三个broker:1,2,3, 该topic 1分区 1副本因子，在broker1上
     */
    @Value("${test.topic.name1}")
    private String REAL_TOPIC1_IN_ZK;

    @Value("${test.topic.name3}")
    private String REAL_TOPIC3_IN_ZK;

    /**
     * 集群共包括三个broker:1,2,3, 该topic 2分区 3副本因子，在broker1,2,3上
     */
    @Value("${test.topic.name2}")
    private String REAL_TOPIC2_IN_ZK;

    private final static String INVALID_TOPIC = "xxxxx";

    private final static String ZK_DEFAULT_TOPIC = "_consumer_offsets";

    private final static String CREATE_TOPIC_TEST = "createTopicTest";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.broker.id1}")
    private Integer REAL_BROKER_ID_IN_ZK;

    private final static Long INVALID_CLUSTER_ID = -1L;

    private final static Integer INVALID_PARTITION_ID = -1;

    private final static Integer REAL_PARTITION_ID = 0;

    private final static Integer INVALID_BROKER_ID = -1;

    @Value("${test.app.id}")
    private String APP_ID;

    private final static Long INVALID_REGION_ID = -1L;

    private final static Long REAL_REGION_ID_IN_MYSQL = 1L;

    @Value("${test.admin}")
    private String ADMIN;

    @Value("${test.phyCluster.name}")
    private String REAL_PHYSICAL_CLUSTER_NAME;

    @Value("${test.ZK.address}")
    private String ZOOKEEPER_ADDRESS;

    @Value("${test.ZK.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;


    private final static String SECURITY_PROTOCOL = "{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }";

    // 优先副本节点在zk上的路径
    private final static String ZK_NODE_PATH_PREFERRED = "/admin/preferred_replica_election";

    // 创建的topic节点在zk上的路径;brokers节点下的
    private final static String ZK_NODE_PATH_BROKERS_TOPIC = "/brokers/topics/createTopicTest";
    // config节点下的
    private final static String ZK_NODE_PATH_CONFIG_TOPIC = "/config/topics/createTopicTest";

    @Autowired
    private AdminService adminService;

    @Autowired
    private TopicManagerService topicManagerService;

    private TopicDO getTopicDO() {
        TopicDO topicDO = new TopicDO();
        topicDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicDO.setTopicName(CREATE_TOPIC_TEST);
        topicDO.setAppId(APP_ID);
        topicDO.setDescription(CREATE_TOPIC_TEST);
        topicDO.setPeakBytesIn(100000L);
        return topicDO;
    }

    public ClusterDO getClusterDO() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterDO.setClusterName(REAL_PHYSICAL_CLUSTER_NAME);
        clusterDO.setZookeeper(ZOOKEEPER_ADDRESS);
        clusterDO.setBootstrapServers(BOOTSTRAP_SERVERS);
        clusterDO.setSecurityProperties(SECURITY_PROTOCOL);
        clusterDO.setStatus(1);
        clusterDO.setGmtCreate(new Date());
        clusterDO.setGmtModify(new Date());
        return clusterDO;
    }

    @Test(description = "测试创建topic")
    public void createTopicTest() throws ConfigException {
        // broker not exist
        createTopic2BrokerNotExistTest();
        // success to create topic
        createTopic2SuccessTest();
        // failure to create topic, topic already exists
        createTopic2FailureTest();

        // 创建成功后，数据库和zk中会存在该Topic，需要删除防止影响后面测试
        // 写入数据库的整个Test结束后回滚，因此只用删除zk上的topic节点
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.delete(ZK_NODE_PATH_BROKERS_TOPIC);
        zkConfig.delete(ZK_NODE_PATH_CONFIG_TOPIC);
        zkConfig.close();

    }

    private void createTopic2BrokerNotExistTest() {
        TopicDO topicDO = getTopicDO();
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = adminService.createTopic(
                clusterDO,
                topicDO,
                1,
                1,
                INVALID_REGION_ID,
                Arrays.asList(INVALID_BROKER_ID),
                new Properties(),
                ADMIN,
                ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.BROKER_NOT_EXIST.getCode());
    }

    private void createTopic2FailureTest() {
        TopicDO topicDO = getTopicDO();
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = adminService.createTopic(
                clusterDO,
                topicDO,
                1,
                1,
                INVALID_REGION_ID,
                Arrays.asList(REAL_BROKER_ID_IN_ZK),
                new Properties(),
                ADMIN,
                ADMIN);
        Assert.assertNotEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void createTopic2SuccessTest() {
        TopicDO topicDO = getTopicDO();
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = adminService.createTopic(
                clusterDO,
                topicDO,
                1,
                1,
                INVALID_REGION_ID,
                Arrays.asList(REAL_BROKER_ID_IN_ZK),
                new Properties(),
                ADMIN,
                ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试删除topic")
    public void deleteTopicTest() {
        // topic does not exist
        deleteTopic2FailureTest();
        // success to delete
        deleteTopic2SuccessTest();
    }

    private void deleteTopic2FailureTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.deleteTopic(
                clusterDO,
                INVALID_TOPIC,
                ADMIN
        );
        Assert.assertNotEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteTopic2SuccessTest() {
        TopicDO topicDO = getTopicDO();
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = adminService.createTopic(
                clusterDO,
                topicDO,
                1,
                1,
                INVALID_REGION_ID,
                Arrays.asList(REAL_BROKER_ID_IN_ZK),
                new Properties(),
                ADMIN,
                ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
        ResultStatus resultStatus = adminService.deleteTopic(
                clusterDO,
                CREATE_TOPIC_TEST,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试优先副本选举状态")
    public void preferredReplicaElectionStatusTest() throws ConfigException {
        // running
//        preferredReplicaElectionStatus2RunningTest();
        // not running
        preferredReplicaElectionStatus2NotRunningTest();
    }

    private void preferredReplicaElectionStatus2RunningTest() throws ConfigException{
        // zk上需要创建/admin/preferred_replica_election节点
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.setOrCreatePersistentNodeStat(ZK_NODE_PATH_PREFERRED, "");
        ClusterDO clusterDO = getClusterDO();
        TaskStatusEnum taskStatusEnum = adminService.preferredReplicaElectionStatus(clusterDO);
        Assert.assertEquals(taskStatusEnum.getCode(), TaskStatusEnum.RUNNING.getCode());

        // 删除之前创建的节点，防止影响后续测试
        zkConfig.delete(ZK_NODE_PATH_PREFERRED);
        zkConfig.close();
    }

    private void preferredReplicaElectionStatus2NotRunningTest() throws ConfigException {
        ClusterDO clusterDO = getClusterDO();
        // zk上无/admin/preferred_replica_election节点
        TaskStatusEnum taskStatusEnum = adminService.preferredReplicaElectionStatus(clusterDO);
        Assert.assertEquals(taskStatusEnum.getCode(), TaskStatusEnum.SUCCEED.getCode());

        // 删除创建的节点，防止影响后续测试
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.delete(ZK_NODE_PATH_PREFERRED);
        zkConfig.close();
    }

    @Test(description = "测试集群纬度优先副本选举")
    public void preferredReplicaElectionOfCluster2Test() throws ConfigException {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(clusterDO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // 删除创建的节点，防止影响后续测试
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.delete(ZK_NODE_PATH_PREFERRED);
        zkConfig.close();
    }

    @Test(description = "Broker纬度优先副本选举")
    public void preferredReplicaElectionOfBrokerTest() throws ConfigException {
        // 参数异常
        preferredReplicaElectionOfBroker2ParamIllegalTest();
        // success
        preferredReplicaElectionOfBroker2SuccessTest();
    }

    private void preferredReplicaElectionOfBroker2ParamIllegalTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                INVALID_BROKER_ID,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void preferredReplicaElectionOfBroker2SuccessTest() throws ConfigException {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                REAL_BROKER_ID_IN_ZK,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // 删除创建的节点，防止影响后续测试
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.delete(ZK_NODE_PATH_PREFERRED);
        zkConfig.close();
    }

    @Test(description = "Topic纬度优先副本选举")
    public void preferredReplicaElectionOfTopicTest() throws ConfigException {
        // topic not exist
        preferredReplicaElectionOfTopic2TopicNotExistTest();
        // success
        preferredReplicaElectionOfTopic2SuccessTest();
    }

    private void preferredReplicaElectionOfTopic2TopicNotExistTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                INVALID_TOPIC,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void preferredReplicaElectionOfTopic2SuccessTest() throws ConfigException {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                REAL_TOPIC1_IN_ZK,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // 删除创建的节点，防止影响后续测试
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.delete(ZK_NODE_PATH_PREFERRED);
        zkConfig.close();
    }

    @Test(description = "分区纬度优先副本选举")
    public void preferredReplicaElectionOfPartitionTest() throws ConfigException {
        // topic not exist
        preferredReplicaElectionOfPartition2TopicNotExistTest();
        // partition Not Exist
        preferredReplicaElectionOfPartition2PartitionNotExistTest();
        // success
        preferredReplicaElectionOfPartition2SuccessTest();
    }

    private void preferredReplicaElectionOfPartition2TopicNotExistTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                INVALID_TOPIC,
                INVALID_PARTITION_ID,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void preferredReplicaElectionOfPartition2PartitionNotExistTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                REAL_TOPIC2_IN_ZK,
                INVALID_PARTITION_ID,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.PARTITION_NOT_EXIST.getCode());
    }

    private void preferredReplicaElectionOfPartition2SuccessTest() throws ConfigException {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                REAL_TOPIC2_IN_ZK,
                REAL_PARTITION_ID,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // 删除创建的节点，防止影响后续测试
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.delete(ZK_NODE_PATH_PREFERRED);
        zkConfig.close();
    }

    @Test(description = "测试获取Topic配置")
    public void getTopicConfigTest() {
        // result is null
        getTopicConfig2NullTest();
        // result not null
        getTopicConfig2NotNullTest();
    }

    private void getTopicConfig2NullTest() {
        ClusterDO clusterDO = getClusterDO();
        clusterDO.setId(INVALID_CLUSTER_ID);
        Properties topicConfig = adminService.getTopicConfig(clusterDO, REAL_TOPIC1_IN_ZK);
        Assert.assertNull(topicConfig);
    }

    private void getTopicConfig2NotNullTest() {
        ClusterDO clusterDO = getClusterDO();
        Properties topicConfig = adminService.getTopicConfig(clusterDO, REAL_TOPIC1_IN_ZK);
        Assert.assertNotNull(topicConfig);
    }

    @Test(description = "测试修改Topic配置")
    public void modifyTopicConfigTest() {
        ClusterDO clusterDO = getClusterDO();
        Properties properties = new Properties();
        properties.put("retention.ms", "21600000");
        ResultStatus resultStatus = adminService.modifyTopicConfig(
                clusterDO,
                REAL_TOPIC1_IN_ZK,
                properties,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试扩分区")
    // 该测试会导致真实topic分区发生变化
    public void expandPartitionsTest() {
        // broker not exist
//        expandPartitions2BrokerNotExistTest();
        // success
//        expandPartitions2SuccessTest();
    }

    private void expandPartitions2BrokerNotExistTest() {
        // 存在两个下线broker, region中包含一个
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.expandPartitions(
                clusterDO,
                REAL_TOPIC1_IN_ZK,
                2,
                REAL_REGION_ID_IN_MYSQL,
                Arrays.asList(INVALID_BROKER_ID),
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.BROKER_NOT_EXIST.getCode());
    }

    private void expandPartitions2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.expandPartitions(
                clusterDO,
                REAL_TOPIC3_IN_ZK,
                2,
                INVALID_REGION_ID,
                Arrays.asList(REAL_BROKER_ID_IN_ZK),
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

}
