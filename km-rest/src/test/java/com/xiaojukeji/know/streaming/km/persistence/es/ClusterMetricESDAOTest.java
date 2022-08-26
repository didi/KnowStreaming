package com.xiaojukeji.know.streaming.km.persistence.es;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchPage;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchRange;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchSort;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.ClusterMetricESDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClusterMetricESDAOTest extends KnowStreamApplicationTest {

    @Autowired
    private ClusterMetricESDAO clusterMetricESDAO;

    @Test
    public void listClusterMetricsByClusterIdsTest(){
        List<String>   metrics      = Arrays.asList("BytesIn_min_1", "BytesOut_min_1");
        List<Long>     clusterIds   = Arrays.asList(123L);
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        clusterMetricESDAO.listClusterMetricsByClusterIds(metrics, "avg", clusterIds, startTime, endTime);
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
