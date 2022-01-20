package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroupSummary;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 测试消费组消费情况需要保证集群中存在消费组
 * @author xuguang
 * @Date 2021/12/23
 */
public class ConsumerServiceTest extends BaseTest {

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;
    /**
     * 集群共包括三个broker:1,2,3, 该topic 1分区 1副本因子，在broker1上
     */
    @Value("${test.topic.name1}")
    private String REAL_TOPIC1_IN_ZK;

    /**
     * 集群共包括三个broker:1,2,3, 该topic 2分区 3副本因子，在broker1,2,3上
     */
    private final static String REAL_TOPIC2_IN_ZK = "xgTest";

    private final static String INVALID_TOPIC = "xxxxxx";

    @Value("${test.consumer-group}")
    private String REAL_CONSUMER_GROUP_NAME;

    private final static String INVALID_CONSUMER_GROUP_NAME = "xxxxxxxx";

    @Value("${test.phyCluster.name}")
    private String REAL_PHYSICAL_CLUSTER_NAME;

    @Value("${test.ZK.address}")
    private String ZOOKEEPER_ADDRESS;

    @Value("${test.ZK.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    private String SECURITY_PROTOCOL = "{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }";

    @Autowired
    private ConsumerService consumerService;

    private ClusterDO getClusterDO() {
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

    private ConsumerGroup getConsumerGroup() {
        return new ConsumerGroup(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_CONSUMER_GROUP_NAME,
                OffsetLocationEnum.BROKER);
    }

    private PartitionOffsetDTO getPartitionOffsetDTO() {
        PartitionOffsetDTO partitionOffsetDTO = new PartitionOffsetDTO();
        partitionOffsetDTO.setOffset(0L);
        partitionOffsetDTO.setPartitionId(0);
        return partitionOffsetDTO;
    }

//    @Test(description = "测试获取消费组列表")
//    因定时任务暂时无法跑通
    public void getConsumerGroupListTest() {
        List<ConsumerGroup> consumerGroupList = consumerService.getConsumerGroupList(REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertFalse(consumerGroupList.isEmpty());
        Assert.assertTrue(consumerGroupList.stream().allMatch(consumerGroup ->
                consumerGroup.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL)));
    }

//    @Test(description = "测试查询消费Topic的消费组")
//    因定时任务暂时无法跑通
    public void getConsumerGroupListWithTopicTest() {
        List<ConsumerGroup> consumerGroupList = consumerService.getConsumerGroupList(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC1_IN_ZK
        );
        Assert.assertFalse(consumerGroupList.isEmpty());
        Assert.assertTrue(consumerGroupList.stream().allMatch(consumerGroup ->
                consumerGroup.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL)));
    }

//    @Test(description = "测试获取消费Topic的消费组概要信息")
//    因定时任务暂时无法跑通
    public void getConsumerGroupSummariesTest() {
        // result is empty
        getConsumerGroupSummaries2EmptyTest();
        // result is not empty
        getConsumerGroupSummaries2NotEmptyTest();
    }

    private void getConsumerGroupSummaries2EmptyTest() {
        List<ConsumerGroupSummary> consumerGroupSummaries = consumerService.getConsumerGroupSummaries(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_TOPIC
        );
        Assert.assertTrue(consumerGroupSummaries.isEmpty());
    }

    private void getConsumerGroupSummaries2NotEmptyTest() {
        List<ConsumerGroupSummary> consumerGroupSummaries = consumerService.getConsumerGroupSummaries(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC1_IN_ZK
        );
        Assert.assertFalse(consumerGroupSummaries.isEmpty());
    }

    @Test(description = "测试查询消费详情")
    public void getConsumeDetail() {
        // result is empty
        getConsumeDetail2Empty();
        // result is not empty
        getConsumeDetail2NotEmpty();
    }

    private void getConsumeDetail2Empty() {
        ClusterDO clusterDO = getClusterDO();
        List<ConsumeDetailDTO> consumeDetail1 =
                consumerService.getConsumeDetail(clusterDO, INVALID_TOPIC, null);
        Assert.assertTrue(consumeDetail1.isEmpty());

        ConsumerGroup consumerGroup = getConsumerGroup();
        consumerGroup.setOffsetStoreLocation(null);
        List<ConsumeDetailDTO> consumeDetail2 =
                consumerService.getConsumeDetail(clusterDO, REAL_TOPIC1_IN_ZK, consumerGroup);
        Assert.assertTrue(consumeDetail2.isEmpty());
    }

    private void getConsumeDetail2NotEmpty() {
        ClusterDO clusterDO = getClusterDO();
        ConsumerGroup consumerGroup = getConsumerGroup();
        List<ConsumeDetailDTO> consumeDetail1 =
                consumerService.getConsumeDetail(clusterDO, REAL_TOPIC1_IN_ZK, consumerGroup);
        Assert.assertFalse(consumeDetail1.isEmpty());
    }

    @Test(description = "测试获取消费组消费的Topic列表")
    public void getConsumerGroupConsumedTopicListTest() {
        // result is empty
        getConsumerGroupConsumedTopicList2Empty();
        // result is not empty
        // 因定时任务暂时无法跑通
//        getConsumerGroupConsumedTopicList2NotEmpty();
    }

    private void getConsumerGroupConsumedTopicList2Empty() {
        List<String> list = consumerService.getConsumerGroupConsumedTopicList(
                null,
                null,
                null);
        Assert.assertTrue(list.isEmpty());
    }

    private void getConsumerGroupConsumedTopicList2NotEmpty() {
        List<String> list = consumerService.getConsumerGroupConsumedTopicList(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_CONSUMER_GROUP_NAME,
                "broker");
        Assert.assertFalse(list.isEmpty());
    }

    @Test(description = "测试获取消费者offset")
    public void getConsumerOffsetTest() {
        // result is null
        getConsumerOffset2NullTest();
        // result is not null
        getConsumerOffset2NotNullTest();
    }

    private void getConsumerOffset2NullTest() {
        Map<Integer, Long> consumerOffset1 = consumerService.getConsumerOffset(null, null, null);
        Assert.assertNull(consumerOffset1);

        ClusterDO clusterDO = getClusterDO();
        ConsumerGroup consumerGroup = getConsumerGroup();
        consumerGroup.setOffsetStoreLocation(null);
        Map<Integer, Long> consumerOffset2 = consumerService.getConsumerOffset(
                clusterDO,
                REAL_TOPIC1_IN_ZK,
                consumerGroup
        );
        Assert.assertNull(consumerOffset2);
    }

    private void getConsumerOffset2NotNullTest() {
        ClusterDO clusterDO = getClusterDO();
        ConsumerGroup consumerGroup = getConsumerGroup();
        Map<Integer, Long> consumerOffset = consumerService.getConsumerOffset(
                clusterDO,
                REAL_TOPIC1_IN_ZK,
                consumerGroup
        );
        Assert.assertNotNull(consumerOffset);
        Assert.assertFalse(consumerOffset.isEmpty());
    }

    @Test(description = "测试获取每个集群消费组的个数")
    public void getConsumerGroupNumMapTest() {
        ClusterDO clusterDO = getClusterDO();
        Map<Long, Integer> map = consumerService.getConsumerGroupNumMap(Arrays.asList(clusterDO));
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(clusterDO.getId() >= 0);
    }

    @Test(description = "验证消费组是否存在")
    public void checkConsumerGroupExistTest() {
        // 不存在
        checkConsumerGroupExist2FalseTest();
        // 存在
        // 因定时任务暂时无法跑通
//        checkConsumerGroupExist2TrueTest();
    }

    private void checkConsumerGroupExist2FalseTest() {
        boolean result = consumerService.checkConsumerGroupExist(
                OffsetLocationEnum.BROKER,
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC1_IN_ZK,
                INVALID_CONSUMER_GROUP_NAME
        );
        Assert.assertFalse(result);
    }

    private void checkConsumerGroupExist2TrueTest() {
        boolean result = consumerService.checkConsumerGroupExist(
                OffsetLocationEnum.BROKER,
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC1_IN_ZK,
                REAL_CONSUMER_GROUP_NAME
        );
        Assert.assertTrue(result);
    }

    @Test(description = "测试重置offset")
    public void resetConsumerOffsetTest() {
        ClusterDO clusterDO = getClusterDO();
        ConsumerGroup consumerGroup = getConsumerGroup();
        PartitionOffsetDTO partitionOffsetDTO1 = getPartitionOffsetDTO();
        List<Result> results = consumerService.resetConsumerOffset(
                clusterDO,
                REAL_TOPIC1_IN_ZK,
                consumerGroup,
                Arrays.asList(partitionOffsetDTO1)
        );
        Assert.assertFalse(results.isEmpty());
        Assert.assertTrue(results.stream().allMatch(result ->
                result.getCode() == ResultStatus.SUCCESS.getCode()));
    }

}
