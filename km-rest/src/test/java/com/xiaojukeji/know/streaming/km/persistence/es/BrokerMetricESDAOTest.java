package com.xiaojukeji.know.streaming.km.persistence.es;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchRange;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchSort;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.BrokerMetricESDAO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class BrokerMetricESDAOTest extends KnowStreamApplicationTest {

    Long clusterId = 1L;

    @Autowired
    private BrokerMetricESDAO brokerMetriceESDAO;

    @Test
    public void buildSortDslTest(){
        SearchSort sort = new SearchSort("age", true);
        SearchSort def  = new SearchSort("timestamp", true);
        String sortDsl = brokerMetriceESDAO.buildSortDsl(sort, def);

        log.info(sortDsl);
    }

    @Test
    public void buildRangeDslTest(){
        SearchRange sort = new SearchRange("age", 1232321f, 45345345345f);
        String sortDsl = brokerMetriceESDAO.buildRangeDsl(sort);

        log.info(sortDsl);
    }

    @Test
    public void buildMatchDslTest(){
        List<SearchTerm> matches = new ArrayList<>();
        matches.add(new SearchTerm("abc", "3"));
        matches.add(new SearchTerm("dce", "345"));

        String matchDsl = brokerMetriceESDAO.buildMatchDsl(matches);

        log.info(matchDsl);
    }

    @Test
    public void getBrokerMetricsPointTest(){
        Integer brokerId        = 1;
        List<String> metrics    = Arrays.asList("BytesIn", "BytesIn_min_5");
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        Map<String/*metric*/, MetricPointVO> metricPointVOS = brokerMetriceESDAO.getBrokerMetricsPoint(
                clusterId, brokerId, metrics, "avg", startTime, endTime);

        assert null != metricPointVOS;
    }

    @Test
    public void listBrokerMetricesByBrokerIdsTest(){
        List<String> metrics  = Arrays.asList("BytesInPerSec_min_1", "BytesInPerSec_min_15");
        List<Long>   brokerIds = Arrays.asList(1L);
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        brokerMetriceESDAO.listBrokerMetricsByBrokerIds(clusterId, metrics, "avg", brokerIds, startTime, endTime);
    }

    @Test
    public void listBrokerMetricsByTopTest(){
        List<String> metrics  = Arrays.asList("BytesInPerSec_min_1", "BytesInPerSec_min_15");
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        brokerMetriceESDAO.listBrokerMetricsByTop(clusterId, new ArrayList<>(), metrics, "avg", 5, startTime, endTime);
    }

    @Test
    public void getTopBrokerIdsTest(){
        List<String> metrics  = Arrays.asList("BytesInPerSec_min_1", "BytesInPerSec_min_15");
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 4 * 60 * 60 * 1000;

        brokerMetriceESDAO.getTopNBrokerIds(clusterId, metrics, "avg", 5, startTime, endTime);
    }
}
