package com.xiaojukeji.know.streaming.km.persistence.es;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.PartitionMetricPO;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.PartitionMetricESDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PartitionMetricESDAOTest extends KnowStreamApplicationTest {

    @Autowired
    private PartitionMetricESDAO partitionMetricESDAO;

    @Test
    public void listPartitionLatestMetricsByTopicTest(){
        Long clusterPhyId = 2L;
        String topic = "__consumer_offsets";

        List<PartitionMetricPO> partitionMetricPOS = partitionMetricESDAO.listPartitionLatestMetricsByTopic(
                clusterPhyId, topic, new ArrayList<>());

        assert null != partitionMetricPOS;
    }
}
