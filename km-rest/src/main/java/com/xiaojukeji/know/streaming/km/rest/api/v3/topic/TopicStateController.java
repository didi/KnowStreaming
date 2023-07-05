package com.xiaojukeji.know.streaming.km.rest.api.v3.topic;

import com.xiaojukeji.know.streaming.km.biz.topic.TopicStateManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicRecordDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.acl.AclBindingVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicBrokersPartitionsSummaryVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.broker.TopicBrokerAllVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicRecordVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.partition.TopicPartitionVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaAclConverter;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.core.service.acl.KafkaAclService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "TopicState-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class TopicStateController {
    @Autowired
    private KafkaAclService kafkaAclService;

    @Autowired
    private TopicStateManager topicStateManager;

    @Autowired
    private TopicMetricService topicMetricService;

    @Autowired
    private GroupService groupService;

    @ApiOperation(value = "Topic-State信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/state")
    @ResponseBody
    public Result<TopicStateVO> getTopicState(@PathVariable Long clusterPhyId, @PathVariable String topicName) {
        return topicStateManager.getTopicState(clusterPhyId, topicName);
    }

    @ApiOperation(value = "Topic-Broker信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/brokers")
    @ResponseBody
    public Result<TopicBrokerAllVO> getTopicBrokers(@PathVariable Long clusterPhyId,
                                                    @PathVariable String topicName,
                                                    @RequestParam(required = false) String searchKeyword) throws Exception {
        return Result.buildSuc(topicStateManager.getTopicBrokerAll(clusterPhyId, topicName, searchKeyword));
    }

    @ApiOperation(value = "Topic-Broker-Partition统计信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/brokers-partitions-summary")
    @ResponseBody
    public Result<TopicBrokersPartitionsSummaryVO> getTopicBrokersPartitionsSummary(@PathVariable Long clusterPhyId,
                                                                                    @PathVariable String topicName) {
        return topicStateManager.getTopicBrokersPartitionsSummary(clusterPhyId, topicName);
    }

    @ApiOperation(value = "Topic-Partition信息", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/partitions")
    @ResponseBody
    public Result<List<TopicPartitionVO>> getTopicPartitions(@PathVariable Long clusterPhyId,
                                                             @PathVariable String topicName,
                                                             @RequestBody List<String> metricsNames) {
        return topicStateManager.getTopicPartitions(clusterPhyId, topicName, metricsNames);
    }

    @ApiOperation(value = "Topic-Messages信息", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/records")
    @ResponseBody
    public Result<List<TopicRecordVO>> getTopicMessages(@PathVariable Long clusterPhyId,
                                                        @PathVariable String topicName,
                                                        @Validated @RequestBody TopicRecordDTO dto) throws Exception {
        return topicStateManager.getTopicMessages(clusterPhyId, topicName, dto);
    }

    @ApiOperation(value = "Topic-ACL信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/acl-Bindings")
    @ResponseBody
    public PaginationResult<AclBindingVO> getTopicAclBindings(@PathVariable Long clusterPhyId,
                                                              @PathVariable String topicName,
                                                              PaginationBaseDTO dto) {
        List<KafkaAclPO> poList = kafkaAclService.getTopicAclFromDB(clusterPhyId, topicName);
        // 分页
        return PaginationUtil.pageBySubData(
                // 搜索
                PaginationUtil.pageByFuzzyFilter(
                        KafkaAclConverter.convert2AclBindingVOList(poList),
                        dto.getSearchKeywords(),
                        Arrays.asList("kafkaUser")
                ),
                dto
        );
    }

    @ApiOperation(value = "Topic指标-单个Topic")
    @PostMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/metric-points")
    @ResponseBody
    public Result<List<MetricPointVO>> getTopicMetricPoints(@PathVariable Long clusterPhyId,
                                                            @PathVariable String topicName,
                                                            @RequestBody MetricDTO dto) {
        return topicMetricService.getMetricPointsFromES(clusterPhyId, topicName, dto);
    }

    @ApiOperation(value = "Topic近期指标-单个Topic")
    @PostMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/latest-metrics")
    @ResponseBody
    public Result<BaseMetrics> getTopicLatestMetrics(@PathVariable Long clusterPhyId,
                                                     @PathVariable String topicName,
                                                     @RequestBody List<String> metricsNames) {
        return Result.buildSuc(topicMetricService.getTopicLatestMetricsFromES(clusterPhyId, topicName, metricsNames));
    }

    @ApiOperation(value = "TopicGroups基本信息列表")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/groups-basic")
    @ResponseBody
    public Result<List<GroupTopicBasicVO>> getTopicGroupsBasic(@PathVariable Long clusterPhyId, @PathVariable String topicName) {
        return Result.buildSuc(ConvertUtil.list2List(groupService.listGroupByTopic(clusterPhyId, topicName), GroupTopicBasicVO.class));
    }

    @ApiOperation("Topic的Group列表")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/groups-overview")
    public PaginationResult<GroupTopicOverviewVO> getTopicGroupsOverview(@PathVariable Long clusterPhyId,
                                                                         @PathVariable String topicName,
                                                                         @RequestParam(required = false) String searchGroupName,
                                                                         PaginationBaseDTO dto) {
        return topicStateManager.pagingTopicGroupsOverview(clusterPhyId, topicName, searchGroupName, dto);
    }

}
