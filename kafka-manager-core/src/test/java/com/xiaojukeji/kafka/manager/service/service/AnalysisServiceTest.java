package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ao.analysis.AnalysisBrokerDTO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2021/12/23
 */
public class AnalysisServiceTest extends BaseTest {

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.broker.id1}")
    private final static Integer REAL_BROKER_ID_IN_ZK = 1;

    private final static Long INVALID_CLUSTER_ID = -1L;

    @Autowired
    private AnalysisService analysisService;

    @Test
    public void doAnalysisBrokerTest() {
        // brokerMetrics is null
        doAnalysisBroker2brokerMetricsIsNullTest();
        // brokerMetrics is not null
        doAnalysisBroker2brokerMetricsIsNotNullTest();
    }

    private void doAnalysisBroker2brokerMetricsIsNullTest() {
        AnalysisBrokerDTO analysisBrokerDTO = analysisService.doAnalysisBroker(
                INVALID_CLUSTER_ID,
                REAL_BROKER_ID_IN_ZK
        );
        Assert.assertNotNull(analysisBrokerDTO);
        Assert.assertEquals(analysisBrokerDTO.getBrokerId(), REAL_BROKER_ID_IN_ZK);
        Assert.assertEquals(analysisBrokerDTO.getClusterId(), INVALID_CLUSTER_ID);
        Assert.assertNull(analysisBrokerDTO.getBytesIn());
    }

    private void doAnalysisBroker2brokerMetricsIsNotNullTest() {
        AnalysisBrokerDTO analysisBrokerDTO = analysisService.doAnalysisBroker(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK
        );
        Assert.assertNotNull(analysisBrokerDTO);
        Assert.assertEquals(analysisBrokerDTO.getBrokerId(), REAL_BROKER_ID_IN_ZK);
        Assert.assertEquals(analysisBrokerDTO.getClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertNotNull(analysisBrokerDTO.getBytesIn());
    }
}
