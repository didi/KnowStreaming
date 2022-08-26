package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.biz.cluster.ClusterTopicsManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterTopicsOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsTopicDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res.ClusterPhyTopicsOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.TopicMetadataVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.TopicVOConverter;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/21
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群Topics-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ClusterTopicsController {
    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Autowired
    private ClusterTopicsManager clusterTopicsManager;

    @ApiOperation(value = "集群Topic元信息")
    @GetMapping(value = "clusters/{clusterPhyId}/topics-metadata")
    @ResponseBody
    public Result<List<TopicMetadataVO>> getClusterPhyTopicsMetadata(@PathVariable Long clusterPhyId, @RequestParam(required = false) String searchKeyword) {
        return Result.buildSuc(
                PaginationUtil.pageByFuzzyFilter(
                        TopicVOConverter.convert2TopicMetadataVOList(topicService.listTopicsFromDB(clusterPhyId)),
                        searchKeyword,
                        Arrays.asList("topicName")
                )
        );
    }

    @ApiOperation(value = "集群Topics信息列表")
    @PostMapping(value = "clusters/{clusterPhyId}/topics-overview")
    @ResponseBody
    public PaginationResult<ClusterPhyTopicsOverviewVO> getClusterPhyTopicsOverview(@PathVariable Long clusterPhyId,
                                                                                    @Validated @RequestBody ClusterTopicsOverviewDTO dto) {
        return clusterTopicsManager.getClusterPhyTopicsOverview(clusterPhyId, dto);
    }

    @ApiOperation(value = "集群Topics指标信息")
    @PostMapping(value = "clusters/{clusterPhyId}/topic-metrics")
    @ResponseBody
    public Result<List<MetricMultiLinesVO>> getClusterPhyMetrics(@PathVariable Long clusterPhyId, @Validated @RequestBody MetricsTopicDTO param) {
        return topicMetricService.listTopicMetricsFromES(clusterPhyId, param);
    }
}