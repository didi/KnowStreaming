package com.xiaojukeji.kafka.manager.openapi;

import com.xiaojukeji.kafka.manager.common.bizenum.ConsumeHealthEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.openapi.common.dto.OffsetResetDTO;
import com.xiaojukeji.kafka.manager.openapi.config.BaseTest;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyc
 * @date 2022/1/6
 */
public class ThirdPartServiceTest extends BaseTest {

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.topic.name1}")
    private String REAL_TOPIC_IN_ZK;

    @Value("${test.phyCluster.name}")
    private String REAL_PHYSICAL_CLUSTER_NAME;

    @Value("${test.ZK.address}")
    private String ZOOKEEPER;

    @Value("${test.ZK.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    private final static String SECURITY_PROPERTIES = "{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }";

    private final static String JMX_PROPERTIES = "{\n" + "\t\"maxConn\": 100000\n" + "}";
    private final static Integer STATUS = 1;

    @Value("${test.app.id}")
    private String REAL_APP_ID;

    // 要求消费moduleTest这个topic的消费者所属的消费者组是moduleTestGroup
    @Value("${test.consumer-group}")
    private String REAL_CONSUMER_GROUP_ID;

    @Autowired
    @InjectMocks
    private ThirdPartService thirdPartService;

    @Mock
    private ConsumerService consumerService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    private ClusterDO getClusterDO() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterDO.setClusterName(REAL_PHYSICAL_CLUSTER_NAME);
        clusterDO.setBootstrapServers(BOOTSTRAP_SERVERS);
        clusterDO.setJmxProperties(JMX_PROPERTIES);
        clusterDO.setSecurityProperties(SECURITY_PROPERTIES);
        clusterDO.setStatus(STATUS);
        clusterDO.setZookeeper(ZOOKEEPER);
        return clusterDO;
    }

    private PartitionOffsetDTO getPartitionOffsetDTO() {
        PartitionOffsetDTO dto = new PartitionOffsetDTO();
        dto.setPartitionId(0);
        dto.setTimestamp(0L);
        dto.setOffset(0L);
        return dto;
    }

    private OffsetResetDTO getOffsetResetDTO() {
        OffsetResetDTO dto = new OffsetResetDTO();
        dto.setAppId(REAL_APP_ID);
        dto.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        dto.setConsumerGroup(REAL_CONSUMER_GROUP_ID);
        dto.setTopicName(REAL_TOPIC_IN_ZK);
        dto.setTimestamp(0L);
        dto.setPartitionOffsetDTOList(new ArrayList<>(Arrays.asList(getPartitionOffsetDTO())));
        dto.setLocation("broker");
        return dto;
    }

    @Test(description = "CLUSTER_NOT_EXIST")
    public void checkConsumeHealth2ClusterNotExistTest() {
        // maxDelayTime = 24h,也就是如果消费组当前的offset介于24小时前这一时间戳对应的offset之后，则认为消费者是健康的
        Assert.assertEquals(thirdPartService.checkConsumeHealth(-1L, REAL_TOPIC_IN_ZK, REAL_CONSUMER_GROUP_ID, 60 * 60 * 24 * 1000L).toString(), Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST).toString());
    }

    @Test(description = "TOPIC_NOT_EXIST")
    public void checkConsumeHealth2TopicNotExistTest() {
        Assert.assertEquals(thirdPartService.checkConsumeHealth(1L, "topic_not_exist", REAL_CONSUMER_GROUP_ID, 60 * 60 * 24 * 1000L).toString(), Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST).toString());
    }

    @Test(description = "CONSUMER_GROUP_NOT_EXIST")
    public void checkConsumeHealth2ConsumerGroupNotExistTest() {
        Assert.assertEquals(thirdPartService.checkConsumeHealth(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, "group_not_exist", 60 * 60 * 24 * 1000L).toString(), Result.buildFrom(ResultStatus.CONSUMER_GROUP_NOT_EXIST).toString());
    }

    @Test(description = "HEALTH")
    public void checkConsumeHealth2HealthTest() {
        // 要求生产者向topic_a发送消息，消费者的group.id=group.demo,生产者生产消息，消费者消费，之后让消费者停止，下面测试才能通过
        Assert.assertEquals(thirdPartService.checkConsumeHealth(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, REAL_CONSUMER_GROUP_ID, 60 * 60 * 24 * 1000L).toString(), new Result<>(ConsumeHealthEnum.HEALTH).toString());
    }


    @Test
    public void resetOffsetsTest() {
        ClusterDO clusterDO = getClusterDO();
        OffsetResetDTO offsetResetDTO = getOffsetResetDTO();
        Mockito.when(consumerService.checkConsumerGroupExist(Mockito.any(), Mockito.any(), Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        List<Result> results = thirdPartService.resetOffsets(clusterDO, offsetResetDTO);
        System.out.println(results);
    }

    @Test
    public void resetOffset2NullTest1() {
        ClusterDO clusterDO = getClusterDO();
        Assert.assertNull(thirdPartService.resetOffsets(clusterDO, null));
    }

    @Test
    public void resetOffsetSuccessTest() {
        // 要求有消费组moduleTestGroup
        Result expectedResult = Result.buildSuc();
        ClusterDO clusterDO = getClusterDO();
        OffsetResetDTO offsetResetDTO = getOffsetResetDTO();
        Mockito.when(consumerService.checkConsumerGroupExist(Mockito.any(), Mockito.any(), Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        Mockito.when(consumerService.resetConsumerOffset(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>(Arrays.asList(Result.buildSuc())));

        List<Result> results = thirdPartService.resetOffsets(clusterDO, offsetResetDTO);
        Assert.assertTrue(!results.isEmpty() && results.stream().allMatch(result -> result.toString().equals(expectedResult.toString())));
    }


}
