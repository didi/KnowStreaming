package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ControllerPreferredCandidate;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.TopicMetadataVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ControllerPreferredCandidateVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.RdClusterMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ClusterBrokerStatusVO;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.KafkaControllerVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.RealTimeMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.TopicOverviewVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.BrokerOverviewVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ClusterDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.TopicThrottleVO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/1
 */
@Api(tags = "RD-Cluster维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdClusterController {
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private ThrottleService throttleService;

    @ApiOperation(value = "集群基本信息列表", notes = "默认不要详情")
    @RequestMapping(value = "clusters/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ClusterDetailVO>> getClusterBasicInfo(
            @RequestParam(value = "need-detail", required = false) Boolean needDetail) {
        if (ValidateUtils.isNull(needDetail)) {
            needDetail = false;
        }
        return new Result<>(
                ClusterModelConverter.convert2ClusterDetailVOList(clusterService.getClusterDetailDTOList(needDetail))
        );
    }

    @ApiOperation(value = "集群基本信息", notes = "默认不要详情")
    @RequestMapping(value = "clusters/{clusterId}/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<ClusterDetailVO> getClusterBasicInfo(
            @PathVariable Long clusterId,
            @RequestParam(value = "need-detail", required = false) Boolean needDetail) {
        if (ValidateUtils.isNull(needDetail)) {
            needDetail = false;
        }
        return new Result<>(
                ClusterModelConverter.convert2ClusterDetailVO(clusterService.getClusterDetailDTO(clusterId, needDetail))
        );
    }

    @ApiOperation(value = "集群实时流量")
    @RequestMapping(value = "clusters/{clusterId}/metrics", method = RequestMethod.GET)
    @ResponseBody
    public Result<RealTimeMetricsVO> getClusterRealTimeMetrics(@PathVariable Long clusterId) {
        return new Result<>(CommonModelConverter.convert2RealTimeMetricsVO(
                brokerService.getBrokerMetricsFromJmx(
                        clusterId,
                        new HashSet<>(PhysicalClusterMetadataManager.getBrokerIdList(clusterId)),
                        KafkaMetricsCollections.COMMON_DETAIL_METRICS
                )
        ));
    }

    @ApiOperation(value = "集群历史流量")
    @RequestMapping(value = "clusters/{clusterId}/metrics-history", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<RdClusterMetricsVO>> getClusterMetricsHistory(@PathVariable Long clusterId,
                                                                     @RequestParam("startTime") Long startTime,
                                                                     @RequestParam("endTime") Long endTime) {
        return new Result<>(ClusterModelConverter.convert2RdClusterMetricsVOList(
                clusterService.getClusterMetricsFromDB(
                        clusterId,
                        DateUtils.long2Date(startTime),
                        DateUtils.long2Date(endTime)
                )
        ));
    }

    @ApiOperation(value = "集群Broker列表", notes = "")
    @RequestMapping(value = "clusters/{clusterId}/brokers", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerOverviewVO>> getBrokerOverview(@PathVariable Long clusterId) {
        List<RegionDO> regionDOList = regionService.getByClusterId(clusterId);
        List<BrokerOverviewDTO> brokerOverviewDTOList = brokerService.getBrokerOverviewList(clusterId, null);
        return new Result<>(ClusterModelConverter.convert2BrokerOverviewList(brokerOverviewDTOList, regionDOList));
    }

    @ApiOperation(value = "集群Broker状态", notes = "饼状图")
    @RequestMapping(value = "clusters/{clusterId}/brokers-status", method = RequestMethod.GET)
    @ResponseBody
    public Result<ClusterBrokerStatusVO> getClusterBrokerStatusVO(@PathVariable Long clusterId) {
        return new Result<>(ClusterModelConverter.convert2ClusterBrokerStatusVO(
                brokerService.getClusterBrokerStatus(clusterId))
        );
    }

    @ApiOperation(value = "集群Topic列表")
    @RequestMapping(value = "clusters/{clusterId}/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicOverviewVO>> getTopicOverview(@PathVariable Long clusterId) {
        return new Result<>(CommonModelConverter.convert2TopicOverviewVOList(
                clusterId,
                topicService.getTopicOverviewList(
                        clusterId,
                        PhysicalClusterMetadataManager.getTopicNameList(clusterId)
                )
        ));
    }

    @ApiOperation(value = "集群Controller变更历史")
    @RequestMapping(value = "clusters/{clusterId}/controller-history", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<KafkaControllerVO>> getControllerHistory(@PathVariable Long clusterId) {
        return new Result<>(ClusterModelConverter.convert2KafkaControllerVOList(
                clusterService.getKafkaControllerHistory(clusterId)
        ));
    }

    @ApiOperation(value = "集群限流信息")
    @RequestMapping(value = "clusters/{clusterId}/throttles", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicThrottleVO>> getThrottles(@PathVariable Long clusterId) {
        return new Result<>(ClusterModelConverter.convert2TopicThrottleVOList(
                throttleService.getThrottledTopicsFromJmx(
                        clusterId,
                        new HashSet<>(PhysicalClusterMetadataManager.getBrokerIdList(clusterId)),
                        Arrays.asList(KafkaClientEnum.values())
                )
        ));
    }

    @ApiOperation(value = "集群Topic元信息列表", notes = "")
    @RequestMapping(value = "clusters/{clusterId}/topic-metadata", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicMetadataVO>> getTopicMetadatas(@PathVariable("clusterId") Long clusterId) {
        return new Result<>(ClusterModelConverter.convert2TopicMetadataVOList(clusterId));
    }

    @ApiOperation(value = "Controller优先候选的Broker", notes = "滴滴内部引擎特性")
    @RequestMapping(value = "clusters/{clusterId}/controller-preferred-candidates", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ControllerPreferredCandidateVO>> getControllerPreferredCandidates(@PathVariable("clusterId") Long clusterId) {
        Result<List<ControllerPreferredCandidate>> candidateResult = clusterService.getControllerPreferredCandidates(clusterId);
        if (candidateResult.failed()) {
            return new Result(candidateResult.getCode(), candidateResult.getMessage());
        }
        return Result.buildSuc(ClusterModelConverter.convert2ControllerPreferredCandidateVOList(candidateResult.getData()));
    }
}