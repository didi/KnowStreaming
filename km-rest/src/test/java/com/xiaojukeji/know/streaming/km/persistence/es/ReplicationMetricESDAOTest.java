package com.xiaojukeji.know.streaming.km.persistence.es;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ReplicationMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.ReplicationMetricESDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ReplicationMetricESDAOTest extends KnowStreamApplicationTest {

    @Autowired
    private ReplicationMetricESDAO replicationMetricESDAO;

    @Test
    public void getReplicationLatestMetricsTest(){
        Long clusterPhyId   = 2L;
        Integer brokerId    = 1;
        String topic        = "know-streaming-test-251";
        Integer partitionId = 1;
        ReplicationMetricPO replicationMetricPO = replicationMetricESDAO.getReplicationLatestMetrics(
                clusterPhyId, brokerId, topic, partitionId, new ArrayList<>());

        assert null != replicationMetricPO;
    }

    /**
     * 测试
     * 获取集群 clusterPhyId 中每个 metric 的指定 partitionId 在指定时间[startTime、endTime]区间内聚合计算(avg、max)之后的统计值
     */
    @Test
    public void getReplicationMetricsPointTest(){
        Long clusterPhyId   = 2L;
        Integer brokerId    = 1;
        String topic        = "know-streaming-test-251";
        Integer partitionId = 1;
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;
        Map<String, MetricPointVO> metricPointVOMap = replicationMetricESDAO.getReplicationMetricsPoint(
                clusterPhyId, topic, brokerId, partitionId, Collections.emptyList(), "avg", startTime, endTime);

        assert null != metricPointVOMap;
    }
}
