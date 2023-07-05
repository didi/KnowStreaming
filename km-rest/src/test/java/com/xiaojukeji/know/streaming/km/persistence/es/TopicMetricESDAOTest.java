package com.xiaojukeji.know.streaming.km.persistence.es;

import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchFuzzy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchSort;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.TopicMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.TopicMetricESDAO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TopicMetricESDAOTest extends KnowStreamApplicationTest {

    Long clusterId = 1L;

    @Autowired
    private TopicMetricESDAO topicMetricESDAO;

    @Test
    public void listTopicMaxMinMetricsTest(){
        String topic            = "know-streaming-test-251";
        String topic1           = "topic_test01";
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        List<TopicMetricPO> ret = topicMetricESDAO.listTopicMaxMinMetrics(
                clusterId, Arrays.asList(topic, topic1), "BytesIn", false, startTime, endTime);
        assert null != ret;
    }

    @Test
    public void getTopicsAggsMetricsValueTest(){
        List<String> topicList  = Arrays.asList("know-streaming-test-251", "topic_test01");
        List<String> metrics    = Arrays.asList(
                "Messages",                         "BytesIn_min_15",       "BytesRejected",
                "PartitionURP",                     "HealthCheckTotal",     "ReplicationCount",
                "CollectMetricsCostTimeUnitSec",    "FailedFetchRequests",  "BytesIn_min_5",
                "HealthScore",                      "LogSize",              "BytesOut",
                "FailedProduceRequests",            "BytesOut_min_15",      "BytesIn",
                "BytesOut_min_5",                   "MessagesIn",           "TotalProduceRequests",
                "HealthCheckPassed");
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        Table<String/*topics*/, String/*metric*/, MetricPointVO> ret = topicMetricESDAO.getTopicsAggsMetricsValue(
                clusterId, topicList, metrics, "max", startTime, endTime);
        assert null != ret;
    }

    @Test
    public void listTopicWithLatestMetricsTest(){
        SearchSort sort     = new SearchSort("LogSize", true);
        sort.setMetric(true);

        SearchFuzzy fuzzy   = new SearchFuzzy("topic", "know");
        List<SearchTerm> terms = new ArrayList<>();

        List<TopicMetricPO> topicMetricPOS = topicMetricESDAO.listTopicWithLatestMetrics(clusterId, sort, fuzzy, null, terms);

        log.info("{}", topicMetricPOS);
    }

    @Test
    public void getTopicLatestMetricByBrokerIdTest(){
        String topic        = "know-streaming-test-251";
        Integer brokerId    = 1;

        TopicMetricPO topicMetricPO = topicMetricESDAO.getTopicLatestMetricByBrokerId(clusterId, topic, brokerId, new ArrayList<>());

        assert null != topicMetricPO;
    }

    @Test
    public void getTopicLatestMetricTest(){
        String topic        = "know-streaming-test-251";

        TopicMetricPO topicMetricPO = topicMetricESDAO.getTopicLatestMetric(clusterId, topic, new ArrayList<>());

        assert null != topicMetricPO;
    }

    @Test
    public void listTopicLatestMetricTest(){
        String topic        = "know-streaming-test-251";
        String topic1       = "know-streaming-123";
        String topic2       = "1209test";
        List<String> metrics = Arrays.asList(
                "Messages",                         "BytesIn_min_15",       "BytesRejected",
                "PartitionURP",                     "HealthCheckTotal",     "ReplicationCount",
                "CollectMetricsCostTimeUnitSec",    "FailedFetchRequests",  "BytesIn_min_5",
                "HealthScore",                      "LogSize",              "BytesOut",
                "FailedProduceRequests",            "BytesOut_min_15",      "BytesIn",
                "BytesOut_min_5",                   "MessagesIn",           "TotalProduceRequests",
                "HealthCheckPassed");


        List<TopicMetricPO> topicMetricPO = topicMetricESDAO.listTopicLatestMetric(clusterId, Arrays.asList(topic,topic1,topic2), metrics);

        assert null != topicMetricPO;
    }

    @Test
    public void listBrokerMetricsByTopicsTest(){
        List<String>   metrics  = Arrays.asList(
                "Messages",                         "BytesIn_min_15",       "BytesRejected",
                "PartitionURP",                     "HealthCheckTotal",     "ReplicationCount",
                "CollectMetricsCostTimeUnitSec",    "FailedFetchRequests",  "BytesIn_min_5",
                "HealthScore",                      "LogSize",              "BytesOut",
                "FailedProduceRequests",            "BytesOut_min_15",      "BytesIn",
                "BytesOut_min_5",                   "MessagesIn",           "TotalProduceRequests",
                "HealthCheckPassed");
        List<String>   topics   = Arrays.asList("QAtest_1_13", "__consumer_offsets");
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        Table<String, String, List<MetricPointVO>> list =
                topicMetricESDAO.listTopicMetricsByTopics(clusterId, metrics, "avg", topics, startTime, endTime);
        Assertions.assertNotNull(list);
    }

    @Test
    public void countMetricValueOccurrencesTest(){
        String topic = "__consumer_offsets";
        String metricName = "HealthCheckPassed";
        Float metricValue = 2f;
        boolean equalMetricValue = true;

        SearchTerm searchMatch = new SearchTerm(metricName, metricValue.toString(), equalMetricValue);
        searchMatch.setMetric(true);

        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        Integer i = topicMetricESDAO.countMetricValue(clusterId, topic, searchMatch, startTime, endTime);

        assert null != i;
    }
}
