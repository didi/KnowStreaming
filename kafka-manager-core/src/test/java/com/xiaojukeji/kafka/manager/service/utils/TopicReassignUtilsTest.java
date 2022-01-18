package com.xiaojukeji.kafka.manager.service.utils;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;

/**
 * @author xuguang
 * @Date 2022/1/6
 */
public class TopicReassignUtilsTest extends BaseTest {

    private final static Long REAL_CLUSTER_ID_IN_MYSQL = 1L;

    private final static String TEST_CREATE_TOPIC = "createTopicTest";

    private final static String REAL_TOPIC_IN_ZK = "moduleTest";

    private final static String INVALID_TOPIC = ".,&";

    private final static Integer PARTITION_NUM = 1;

    private final static Integer REPLICA_NUM = 1;

    private final static Integer BROKER_ID = 1;

    private final static Integer PARTITION_ID = 1;

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

    @Test
    public void generateReassignmentJsonTest() {
        // null
        generateReassignmentJson2NullPointerExceptionTest();
        // not null
        generateReassignmentJson2SuccessTest();
    }

    private void generateReassignmentJson2NullPointerExceptionTest() {
        ClusterDO clusterDO = getClusterDO();
        clusterDO.setZookeeper(null);
        String result = TopicReassignUtils.generateReassignmentJson(
                clusterDO,
                REAL_TOPIC_IN_ZK,
                Arrays.asList(PARTITION_ID),
                Arrays.asList(BROKER_ID)
        );

        Assert.assertNull(result);
    }

    private void generateReassignmentJson2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        String result = TopicReassignUtils.generateReassignmentJson(
                clusterDO,
                REAL_TOPIC_IN_ZK,
                Arrays.asList(PARTITION_ID),
                Arrays.asList(BROKER_ID)
        );

        Assert.assertNotNull(result);
    }
}
