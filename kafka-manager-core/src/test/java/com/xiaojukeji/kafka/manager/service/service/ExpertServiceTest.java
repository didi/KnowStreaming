package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.RegionTopicHotConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicRegionHot;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author xuguang
 * Collections.EmptyList()
 * @Date 2021/12/24
 */
public class ExpertServiceTest extends BaseTest {

    private final static Long REAL_CLUSTER_ID_IN_MYSQL = 1L;

    private final static Long INVALID_CLUSTER_ID = -1L;

    @Autowired
    @InjectMocks
    private ExpertService expertService;

    @Mock
    private ConfigService configService;

    @Mock
    private ClusterService clusterService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private RegionTopicHotConfig getRegionTopicHotConfig() {
        RegionTopicHotConfig regionTopicHotConfig = new RegionTopicHotConfig();
        regionTopicHotConfig.setMaxDisPartitionNum(5);
        regionTopicHotConfig.setIgnoreClusterIdList(Arrays.asList(INVALID_CLUSTER_ID));
        return regionTopicHotConfig;
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

    @Test(description = "测试Region内热点Topic")
    public void getRegionHotTopics() {
        RegionTopicHotConfig regionTopicHotConfig = getRegionTopicHotConfig();
        Mockito.when(configService.getByKey(Mockito.anyString(), Mockito.any())).thenReturn(regionTopicHotConfig);
        ClusterDO clusterDO1 = getClusterDO();
        ClusterDO clusterDO2 = getClusterDO();
        clusterDO2.setId(INVALID_CLUSTER_ID);
        Mockito.when(clusterService.list()).thenReturn(Arrays.asList(clusterDO1, clusterDO2));

        List<TopicRegionHot> regionHotTopics = expertService.getRegionHotTopics();
        Assert.assertFalse(regionHotTopics.isEmpty());
    }

}
