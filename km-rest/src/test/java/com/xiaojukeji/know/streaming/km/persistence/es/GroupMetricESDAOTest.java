package com.xiaojukeji.know.streaming.km.persistence.es;

import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.GroupTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicPartitionKS;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.GroupMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.GroupMetricESDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GroupMetricESDAOTest extends KnowStreamApplicationTest {

    Long clusterId = 1L;

    @Autowired
    private GroupMetricESDAO groupMetricESDAO;

    @Test
    public void listLatestMetricsAggByGroupTopicTest(){
        List<GroupTopic> groupTopicList = new ArrayList<>();
        groupTopicList.add(new GroupTopic("g-know-streaming-123456", "know-streaming-test-251"));
        groupTopicList.add(new GroupTopic("test_group", "know-streaming-test-251"));

        List<String> metrics = Arrays.asList("OffsetConsumed", "Lag");
        AggTypeEnum aggType = AggTypeEnum.AVG;

        List<GroupMetricPO> groupMetricPOS = groupMetricESDAO.listLatestMetricsAggByGroupTopic(clusterId, groupTopicList, metrics, aggType);

        assert !CollectionUtils.isEmpty(groupMetricPOS);
    }

    @Test
    public void listGroupTopicPartitionsTest(){
        String groupName = "g-know-streaming-123456";
        Long   endTime   = System.currentTimeMillis();
        Long   startTime = endTime - 24 * 3600 * 1000;
        Set<TopicPartitionKS> topicPartitionKS = groupMetricESDAO.listGroupTopicPartitions(clusterId,   groupName, startTime, endTime);

        assert null != topicPartitionKS;
    }

    @Test
    public void listPartitionLatestMetricsTest(){
        String groupName = "test_group_20220421";
        String topicName = "know-streaming-test-251";
        List<GroupMetricPO> groupMetricPOS = groupMetricESDAO.listPartitionLatestMetrics(clusterId, groupName, topicName, null);

        assert CollectionUtils.isEmpty(groupMetricPOS);
    }

    @Test
    public void countMetricValueTest(){
        String groupName = "test_group";

        SearchTerm searchTerm = new SearchTerm("HealthCheckTotal", "1", false);
        searchTerm.setMetric(true);

        Long   endTime   = System.currentTimeMillis();
        Long   startTime = endTime - 24 * 3600 * 1000;
        Integer i = groupMetricESDAO.countMetricValue(clusterId, groupName, searchTerm, startTime, endTime);
        assert null != i;
    }

    @Test
    public void listGroupMetricsTest(){
        String groupName = "g-know-streaming-123456";
        Long   endTime   = System.currentTimeMillis();
        Long   startTime = endTime - 24 * 3600 * 1000;
        List<TopicPartitionKS> topicPartitionKS = new ArrayList<>();
        topicPartitionKS.add(new TopicPartitionKS("know-streaming-test-251", 4));

        List<String> metrics = new ArrayList<>();
        metrics.add("OffsetConsumed");

        Table<String/*metric*/, String/*topic&partition*/, List<MetricPointVO>> multiLinesVOS = groupMetricESDAO.listGroupMetrics(
                clusterId,groupName,topicPartitionKS, metrics, "avg", startTime, endTime);
        assert null != multiLinesVOS;
    }
}
