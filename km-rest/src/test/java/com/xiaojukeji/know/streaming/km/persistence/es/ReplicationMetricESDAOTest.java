package com.xiaojukeji.know.streaming.km.persistence.es;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ReplicationMetricPO;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.ReplicationMetricESDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class ReplicationMetricESDAOTest extends KnowStreamApplicationTest {

    @Autowired
    private ReplicationMetricESDAO replicationMetricESDAO;

    @Test
    public void getReplicationLatestMetricsTest(){
        Long clusterPhyId   = 2l;
        Integer brokerId    = 1;
        String topic        = "know-streaming-test-251";
        Integer partitionId = 1;
        ReplicationMetricPO replicationMetricPO = replicationMetricESDAO.getReplicationLatestMetrics(
                clusterPhyId, brokerId, topic, partitionId, new ArrayList<>());

        assert null != replicationMetricPO;
    }
}
