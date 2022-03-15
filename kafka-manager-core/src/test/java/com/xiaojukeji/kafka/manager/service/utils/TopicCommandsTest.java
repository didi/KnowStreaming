package com.xiaojukeji.kafka.manager.service.utils;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

/**
 * @author xuguang
 * @Date 2022/1/6
 */
public class TopicCommandsTest extends BaseTest {

    private final static Long REAL_CLUSTER_ID_IN_MYSQL = 1L;

    private final static String TEST_CREATE_TOPIC = "createTopicTest";

    private final static String REAL_TOPIC1_IN_ZK2 = "expandPartitionTopic";

    private final static String REAL_TOPIC_IN_ZK = "moduleTest";

    private final static String INVALID_TOPIC = ".,&";

    private final static Integer PARTITION_NUM = 1;

    private final static Integer REPLICA_NUM = 1;

    private final static Integer BROKER_ID = 1;

    private final static String REAL_PHYSICAL_CLUSTER_NAME = "LogiKM_moduleTest";

    private final static String ZOOKEEPER_ADDRESS = "10.190.12.242:2181,10.190.25.160:2181,10.190.25.41:2181/wyc";

    private final static String BOOTSTRAP_SERVERS = "10.190.12.242:9093,10.190.25.160:9093,10.190.25.41:9093";

    private final static String SECURITY_PROTOCOL = "{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }";


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
    public void createTopicTest() {
        // NullPointerException
        createTopic2NullPointerExceptionTest();
        // InvalidPartitions
        createTopic2InvalidPartitionsTest();
        // InvalidReplicationFactor
        createTopic2InvalidReplicationFactorTest();
        // topic exists
        createTopic2TopicExistsTest();
        // invalid topic
        createTopic2InvalidTopicTest();
        // success
        createTopic2SuccessTest();
    }

    private void createTopic2NullPointerExceptionTest() {
        ClusterDO clusterDO = getClusterDO();
        clusterDO.setZookeeper(null);
        clusterDO.setBootstrapServers(null);
        ResultStatus result = TopicCommands.createTopic(
                clusterDO,
                TEST_CREATE_TOPIC,
                PARTITION_NUM,
                REPLICA_NUM,
                Arrays.asList(BROKER_ID),
                new Properties()
        );
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_PARAM_NULL_POINTER.getCode());
    }

    private void createTopic2InvalidPartitionsTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.createTopic(
                clusterDO,
                TEST_CREATE_TOPIC,
                -1,
                REPLICA_NUM,
                Arrays.asList(BROKER_ID),
                new Properties()
        );
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_PARTITION_NUM_ILLEGAL.getCode());
    }

    private void createTopic2InvalidReplicationFactorTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.createTopic(
                clusterDO,
                TEST_CREATE_TOPIC,
                PARTITION_NUM,
                REPLICA_NUM,
                Collections.emptyList(),
                new Properties()
        );
        Assert.assertEquals(result.getCode(), ResultStatus.BROKER_NUM_NOT_ENOUGH.getCode());
    }

    private void createTopic2TopicExistsTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.createTopic(
                clusterDO,
                REAL_TOPIC_IN_ZK,
                PARTITION_NUM,
                REPLICA_NUM,
                Arrays.asList(BROKER_ID),
                new Properties()
        );
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_TOPIC_EXISTED.getCode());
    }

    private void createTopic2InvalidTopicTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.createTopic(
                clusterDO,
                INVALID_TOPIC,
                PARTITION_NUM,
                REPLICA_NUM,
                Arrays.asList(BROKER_ID),
                new Properties()
        );
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_TOPIC_NAME_ILLEGAL.getCode());
    }

    private void createTopic2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.createTopic(
                clusterDO,
                TEST_CREATE_TOPIC,
                PARTITION_NUM,
                REPLICA_NUM,
                Arrays.asList(BROKER_ID),
                new Properties()
        );
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());

        // 删除这个Topic
        ResultStatus result1 = TopicCommands.deleteTopic(clusterDO, TEST_CREATE_TOPIC);
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试修改topic配置")
    public void modifyTopicConfigTest() {
        // AdminOperationException
        modifyTopicConfig2AdminOperationExceptionTest();
        // InvalidConfigurationException
        modifyTopicConfig2InvalidConfigurationExceptionTest();
        // success
        modifyTopicConfig2SuccessTest();
    }

    private void modifyTopicConfig2AdminOperationExceptionTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.modifyTopicConfig(clusterDO, INVALID_TOPIC, new Properties());
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION.getCode());
    }

    private void modifyTopicConfig2InvalidConfigurationExceptionTest() {
        ClusterDO clusterDO = getClusterDO();
        Properties properties = new Properties();
        properties.setProperty("xxx", "xxx");
        ResultStatus result = TopicCommands.modifyTopicConfig(clusterDO, REAL_TOPIC_IN_ZK, properties);
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_TOPIC_CONFIG_ILLEGAL.getCode());
    }

    private void modifyTopicConfig2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        Properties properties = new Properties();
        ResultStatus result = TopicCommands.modifyTopicConfig(clusterDO, REAL_TOPIC_IN_ZK, properties);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试扩分区")
    public void expandTopicTest() {
        // failed
        expandTopic2FailureTest();
        // success
        expandTopic2SuccessTest();
    }

    private void expandTopic2FailureTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.expandTopic(
                clusterDO,
                INVALID_TOPIC,
                PARTITION_NUM + 1,
                Arrays.asList(BROKER_ID, 2)
        );
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_UNKNOWN_ERROR.getCode());
    }

    private void expandTopic2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.expandTopic(
                clusterDO,
                REAL_TOPIC1_IN_ZK2,
                PARTITION_NUM + 1,
                Arrays.asList(BROKER_ID, 2)
        );
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试删除Topic")
    public void deleteTopicTest() {
        // UnknownTopicOrPartitionException
        deleteTopic2UnknownTopicOrPartitionExceptionTest();
        // NullPointerException
        deleteTopic2NullPointerExceptionTest();
        // success
        deleteTopic2SuccessTest();
    }

    private void deleteTopic2UnknownTopicOrPartitionExceptionTest() {
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.deleteTopic(clusterDO, INVALID_TOPIC);
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION.getCode());
    }

    private void deleteTopic2NullPointerExceptionTest() {
        ClusterDO clusterDO = getClusterDO();
        clusterDO.setBootstrapServers(null);
        clusterDO.setZookeeper(null);
        ResultStatus result = TopicCommands.deleteTopic(clusterDO, INVALID_TOPIC);
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_OPERATION_UNKNOWN_ERROR.getCode());
    }

    private void deleteTopic2SuccessTest() {
        // 需要先创建这个Topic
        ClusterDO clusterDO = getClusterDO();
        ResultStatus result = TopicCommands.createTopic(
                clusterDO,
                TEST_CREATE_TOPIC,
                PARTITION_NUM,
                REPLICA_NUM,
                Arrays.asList(BROKER_ID),
                new Properties()
        );
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());

        ResultStatus result1 = TopicCommands.deleteTopic(clusterDO, TEST_CREATE_TOPIC);
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }
}
