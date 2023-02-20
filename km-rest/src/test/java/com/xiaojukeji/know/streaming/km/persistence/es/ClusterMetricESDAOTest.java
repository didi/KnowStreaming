package com.xiaojukeji.know.streaming.km.persistence.es;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchPage;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchRange;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchSort;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ClusterMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.ClusterMetricESDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ClusterMetricESDAOTest extends KnowStreamApplicationTest {

    Long clusterId = 1L;

    @Autowired
    private ClusterMetricESDAO clusterMetricESDAO;

    @Test
    public void listClusterMetricsByClusterIdsTest(){
        List<String>   metrics      = Arrays.asList("MessagesIn");
        List<Long>     clusterIds   = Arrays.asList(293L);
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        clusterMetricESDAO.listClusterMetricsByClusterIds(metrics, "avg", clusterIds, startTime, endTime);
    }

    /**
     * 测试
     * 获取集群 clusterPhyId 中每个 metric 在指定时间[startTime、endTime]区间内聚合计算(avg、max)之后的统计值
     */
    @Test
    public void getClusterMetricsPointTest(){
        List<String> metrics    = Arrays.asList(
                "Connections",                  "BytesIn_min_15",           "PartitionURP",
                "HealthScore_Topics",           "EventQueueSize",           "ActiveControllerCount",
                "GroupDeads",                   "BytesIn_min_5",            "HealthCheckTotal_Topics",
                "Partitions",                   "BytesOut",                 "Groups",
                "BytesOut_min_15",              "TotalRequestQueueSize",    "HealthCheckPassed_Groups",
                "TotalProduceRequests",         "HealthCheckPassed",        "TotalLogSize",
                "GroupEmptys",                  "PartitionNoLeader",        "HealthScore_Brokers",
                "Messages",                     "Topics",                   "PartitionMinISR_E",
                "HealthCheckTotal",             "Brokers",                  "Replicas",
                "HealthCheckTotal_Groups",      "GroupRebalances",          "MessageIn",
                "HealthScore",                  "HealthCheckPassed_Topics", "HealthCheckTotal_Brokers",
                "PartitionMinISR_S",            "BytesIn",                  "BytesOut_min_5",
                "GroupActives",                 "MessagesIn",               "GroupReBalances",
                "HealthCheckPassed_Brokers",    "HealthScore_Groups",       "TotalResponseQueueSize",
                "Zookeepers",                   "LeaderMessages",           "HealthScore_Cluster",
                "HealthCheckPassed_Cluster",    "HealthCheckTotal_Cluster");
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        Map<String/*metric*/, MetricPointVO> metricPointVOS = clusterMetricESDAO.getClusterMetricsPoint(
                clusterId, metrics, "avg", startTime, endTime);

        assert null != metricPointVOS;
    }

    /**
     * 测试
     * 获取集群 clusterId 最新的统计指标
     */
    @Test
    public void getClusterLatestMetricsTest(){
        List<String> metrics    = Collections.emptyList();

        ClusterMetricPO clusterLatestMetrics = clusterMetricESDAO.getClusterLatestMetrics(clusterId, metrics);

        assert null != clusterLatestMetrics;
    }

    @Test
    public void pagingClusterWithLatestMetricsTest(){
        List<Long> clusterIds = new ArrayList<>();
        List<String> metricList = new ArrayList<>();
        List<SearchTerm> searchMatches = new ArrayList<>();
        SearchTerm match = new SearchTerm("Zookeepers", "3");
        match.setMetric(true);
        searchMatches.add(match);

        SearchSort sort = new SearchSort("Replicas", true);
        sort.setMetric(true);

        SearchRange range = new SearchRange("Brokers", 1, 100);
        range.setMetric(true);

        SearchPage page = new SearchPage();

//        clusterMetricESDAO.pagingClusterWithLatestMetrics(searchMatches, sort, range);
    }
}
