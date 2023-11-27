package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.biz.group.GroupManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterGroupSummaryDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricGroupPartitionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicPartitionKS;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupMetricService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群Groups-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ClusterGroupsController {
    @Autowired
    private GroupManager groupManager;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMetricService groupMetricService;

    @ApiOperation(value = "集群Groups信息列表")
    @GetMapping(value = "clusters/{clusterPhyId}/groups-basic")
    @ResponseBody
    public Result<List<GroupTopicBasicVO>> getGroupsBasic(@PathVariable Long clusterPhyId) {
        return Result.buildSuc(ConvertUtil.list2List(groupService.listGroupByCluster(clusterPhyId), GroupTopicBasicVO.class));
    }

    @ApiOperation(value = "集群Groups信息列表")
    @GetMapping(value = "clusters/{clusterPhyId}/groups-overview")
    @ResponseBody
    public PaginationResult<GroupOverviewVO> getGroupsOverview(@PathVariable Long clusterPhyId, ClusterGroupSummaryDTO dto) {
        return groupManager.pagingClusterGroupsOverview(clusterPhyId, dto);
    }

    @ApiOperation(value = "集群Groups指标信息")
    @PostMapping(value = "clusters/{clusterPhyId}/group-metrics")
    @ResponseBody
    public Result<List<MetricMultiLinesVO>> getClusterPhyGroupMetrics(@PathVariable Long clusterPhyId, @RequestBody MetricGroupPartitionDTO param) {
        return groupMetricService.listGroupMetricsFromES(clusterPhyId, param);
    }

    @ApiOperation(value = "Groups消费过的Partition", notes = "startTime和endTime表示查询的时间范围")
    @GetMapping(value = "clusters/{clusterPhyId}/groups/{groupName}/partitions")
    @ResponseBody
    public Result<Set<TopicPartitionKS>> getClusterPhyGroupPartitions(@PathVariable Long clusterPhyId,
                                                                      @PathVariable String groupName,
                                                                      @RequestParam Long startTime,
                                                                      @RequestParam Long endTime) {
        return groupManager.listClusterPhyGroupPartitions(clusterPhyId, groupName, startTime, endTime);
    }

    @ApiOperation(value = "Group的Topic列表")
    @GetMapping(value = "clusters/{clusterPhyId}/groups/{groupName}/topics-overview")
    public PaginationResult<GroupTopicOverviewVO> getGroupTopicsOverview(@PathVariable Long clusterPhyId,
                                                                         @PathVariable String groupName,
                                                                         PaginationBaseDTO dto) throws Exception {
        return groupManager.pagingGroupTopicMembers(clusterPhyId, groupName, dto);
    }

    /**************************************************** private method ****************************************************/
}
