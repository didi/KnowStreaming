package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final static String REAL_TOPIC1_IN_ZK = "moduleTest";

    /**
     * 集群共包括三个broker:1,2,3, 该topic 2分区 3副本因子，在broker1,2,3上
     */
    private final static String REAL_TOPIC2_IN_ZK = "xgTest";

    private final static String INVALID_TOPIC = "xxxxx";

    private final static String ZK_DEFAULT_TOPIC = "_consumer_offsets";

    private final static String CREATE_TOPIC_TEST = "createTopicTest";

    private final static Long REAL_CLUSTER_ID_IN_MYSQL = 1L;

    private final static Integer REAL_BROKER_ID_IN_ZK = 1;

    private final static Long INVALID_CLUSTER_ID = -1L;

    private final static Integer INVALID_PARTITION_ID = -1;

    private final static Integer REAL_PARTITION_ID = 0;

    private final static Integer INVALID_BROKER_ID = -1;

    private final static String APP_ID = "dkm_admin";

    private final static Long INVALID_REGION_ID = -1L;

    private final static Long REAL_REGION_ID_IN_MYSQL = 1L;

    private final static String ADMIN = "admin";


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
        clusterDO.setClusterName("LogiKM_moduleTest");
        clusterDO.setZookeeper("10.190.46.198:2181,10.190.14.237:2181,10.190.50.65:2181/xg");
        clusterDO.setBootstrapServers("10.190.46.198:9093,10.190.14.237:9093,10.190.50.65:9093");
        clusterDO.setSecurityProperties("{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }");
        clusterDO.setStatus(1);
        clusterDO.setGmtCreate(new Date());
        clusterDO.setGmtModify(new Date());
        return clusterDO;
    }

    @Test(description = "测试创建topic")
    public void createTopicTest() {
        // broker not exist
        createTopic2BrokerNotExistTest();
        // success to create topic
        createTopic2SuccessTest();
        // failure to create topic, topic already exists
        createTopic2FailureTest();
    }

    private void createTopic2BrokerNotExistTest() {
        TopicDO topicDO = getTopicDO();
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = adminService.createTopic(
                clusterDO,
                topicDO,
                1,
                1,
                1L,
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
        topicManagerService.addTopic(topicDO);

        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.deleteTopic(
                clusterDO,
                CREATE_TOPIC_TEST,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试优先副本选举状态")
    public void preferredReplicaElectionStatusTest() {
        // running
        preferredReplicaElectionStatus2RunningTest();
        // not running
        preferredReplicaElectionStatus2NotRunningTest();
    }

    private void preferredReplicaElectionStatus2RunningTest() {
        // zk上需要创建/admin/preferred_replica_election节点
        ClusterDO clusterDO = getClusterDO();
        TaskStatusEnum taskStatusEnum = adminService.preferredReplicaElectionStatus(clusterDO);
        Assert.assertEquals(taskStatusEnum.getCode(), TaskStatusEnum.RUNNING.getCode());
    }

    private void preferredReplicaElectionStatus2NotRunningTest() {
        ClusterDO clusterDO = getClusterDO();
        // zk上无/admin/preferred_replica_election节点
        TaskStatusEnum taskStatusEnum = adminService.preferredReplicaElectionStatus(clusterDO);
        Assert.assertEquals(taskStatusEnum.getCode(), TaskStatusEnum.SUCCEED.getCode());
    }

    @Test(description = "测试集群纬度优先副本选举")
    public void preferredReplicaElectionOfCluster2Test() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(clusterDO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "Broker纬度优先副本选举")
    public void preferredReplicaElectionOfBrokerTest() {
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

    private void preferredReplicaElectionOfBroker2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                REAL_BROKER_ID_IN_ZK,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "Topic纬度优先副本选举")
    public void preferredReplicaElectionOfTopicTest() {
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

    private void preferredReplicaElectionOfTopic2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                REAL_TOPIC1_IN_ZK,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "Topic纬度优先副本选举")
    public void preferredReplicaElectionOfPartitionTest() {
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

    private void preferredReplicaElectionOfPartition2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus resultStatus = adminService.preferredReplicaElection(
                clusterDO,
                REAL_TOPIC2_IN_ZK,
                REAL_PARTITION_ID,
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
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
    public void expandPartitionsTest() {
        // broker not exist
        expandPartitions2BrokerNotExistTest();
        // success
        expandPartitions2SuccessTest();
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
                REAL_TOPIC1_IN_ZK,
                2,
                INVALID_REGION_ID,
                Arrays.asList(REAL_BROKER_ID_IN_ZK),
                ADMIN
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.BROKER_NOT_EXIST.getCode());
    }

}
