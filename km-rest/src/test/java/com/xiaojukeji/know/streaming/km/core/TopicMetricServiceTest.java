package com.xiaojukeji.know.streaming.km.core;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsTopicDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchFuzzy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchPage;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchSort;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TopicMetricServiceTest extends KnowStreamApplicationTest {

    Long clusterId = 1L;

    @Autowired
    private TopicMetricService topicMetricService;

    @Test
    public void listTopicMetricsFromESTest(){
        Long endTime   = System.currentTimeMillis();
        Long startTime = endTime - 3600 * 1000;

        MetricsTopicDTO dto = new MetricsTopicDTO();
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setTopNu(0);

        List<String> metricName = new ArrayList<>();
        metricName.add("LogSize");
        dto.setMetricsNames(metricName);

        List<String> topicName = new ArrayList<>();
        topicName.add("__consumer_offsets");
        dto.setTopics(topicName);

        Result<List<MetricMultiLinesVO>> ret = topicMetricService.listTopicMetricsFromES(clusterId, dto);

        assert ret.successful();
    }

    @Test
    public void pagingTopicWithLatestMetricsFromESTest(){
        List<String> metricNameList = new ArrayList<>();
        SearchSort sort = new SearchSort();
        sort.setQueryName("LogSize");

        SearchFuzzy fuzzy = new SearchFuzzy();
        SearchPage page = new SearchPage();

        PaginationResult<TopicMetrics> result = topicMetricService.pagingTopicWithLatestMetricsFromES(
                clusterId, metricNameList, sort, fuzzy, null, null, page);
        assert result.successful();
    }
}
