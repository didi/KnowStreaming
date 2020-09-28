package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalCluster;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.BrokerOverviewVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.RealTimeMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.TopicOverviewVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.LogicClusterVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.NormalClusterMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.TopicMetadataVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.TopicThrottleVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker.BrokerMetadataVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.LogicalClusterService;
import com.xiaojukeji.kafka.manager.service.service.ThrottleService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.ClusterModelConverter;
import com.xiaojukeji.kafka.manager.web.converters.CommonModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/3/31
 */
@Api(tags = "Normal-Cluster相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalClusterController {
    @Autowired
    private ThrottleService throttleService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private LogicalClusterService logicalClusterService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @ApiOperation(value = "集群列表", notes = "")
    @RequestMapping(value = "clusters/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<LogicClusterVO>> getLogicClusterVOList(
            @RequestParam(value = "all", required = false) Boolean all) {
        if (!ValidateUtils.isNull(all) && all) {
            return new Result<>(ClusterModelConverter.convert2LogicClusterVOList(
                    logicalClusterService.getAllLogicalCluster()
            ));
        }
        return new Result<>(ClusterModelConverter.convert2LogicClusterVOList(
                logicalClusterService.getLogicalClusterListByPrincipal(SpringTool.getUserName())
        ));
    }

    @ApiOperation(value = "集群基本信息", notes = "")
    @RequestMapping(value = "clusters/{logicalClusterId}/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<LogicClusterVO> getLogicClusterVO(@PathVariable Long logicalClusterId) {
        LogicalCluster logicalCluster = logicalClusterService.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalCluster)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(ClusterModelConverter.convert2LogicClusterVO(logicalCluster));
    }

    @ApiOperation(value = "集群实时流量")
    @RequestMapping(value = "clusters/{logicalClusterId}/metrics", method = RequestMethod.GET)
    @ResponseBody
    public Result<RealTimeMetricsVO> getClusterRealTimeMetrics(@PathVariable Long logicalClusterId) {
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        return new Result<>(CommonModelConverter.convert2RealTimeMetricsVO(
                brokerService.getBrokerMetricsFromJmx(
                        logicalClusterDO.getClusterId(),
                        logicalClusterMetadataManager.getBrokerIdSet(logicalClusterId),
                        KafkaMetricsCollections.COMMON_DETAIL_METRICS
                )
        ));
    }

    @ApiOperation(value = "集群历史流量")
    @RequestMapping(value = "clusters/{logicalClusterId}/metrics-history", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<NormalClusterMetricsVO>> getClusterMetricsHistory(@PathVariable Long logicalClusterId,
                                                                         @RequestParam("startTime") Long startTime,
                                                                         @RequestParam("endTime") Long endTime) {
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        return new Result<>(ClusterModelConverter.convert2NormalClusterMetricsVOList(
                logicalClusterService.getLogicalClusterMetricsFromDB(
                        logicalClusterDO,
                        new Date(startTime),
                        new Date(endTime)
                )
        ));
    }

    @ApiOperation(value = "集群Broker列表", notes = "")
    @RequestMapping(value = "clusters/{logicalClusterId}/brokers", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerOverviewVO>> getBrokerOverview(@PathVariable Long logicalClusterId) {
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        List<BrokerOverviewDTO> brokerOverviewDTOList = brokerService.getBrokerOverviewList(
                logicalClusterDO.getClusterId(),
                logicalClusterMetadataManager.getBrokerIdSet(logicalClusterId)
        );
        return new Result<>(ClusterModelConverter.convert2BrokerOverviewList(brokerOverviewDTOList, null));
    }

    @ApiOperation(value = "集群Topic列表")
    @RequestMapping(value = "clusters/{logicalClusterId}/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicOverviewVO>> getTopicOverview(@PathVariable Long logicalClusterId) {
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        return new Result<>(CommonModelConverter.convert2TopicOverviewVOList(
                logicalClusterId,
                topicService.getTopicOverviewList(
                        logicalClusterDO.getClusterId(),
                        new ArrayList<>(logicalClusterMetadataManager.getTopicNameSet(logicalClusterId))
                )
        ));
    }

    @ApiOperation(value = "集群限流信息")
    @RequestMapping(value = "clusters/{logicalClusterId}/throttles", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicThrottleVO>> getThrottles(@PathVariable Long logicalClusterId) {
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        return new Result<>(ClusterModelConverter.convert2TopicThrottleVOList(
                throttleService.getThrottledTopicsFromJmx(
                        logicalClusterDO.getClusterId(),
                        logicalClusterMetadataManager.getBrokerIdSet(logicalClusterId),
                        Arrays.asList(KafkaClientEnum.values())
                )
        ));
    }

    @ApiOperation(value = "集群Topic元信息列表", notes = "")
    @RequestMapping(value = "clusters/{logicalClusterId}/topic-metadata", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicMetadataVO>> getTopicMetadatas(@PathVariable("logicalClusterId") Long logicalClusterId) {
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        return new Result<>(ClusterModelConverter.convert2TopicMetadataVOList(
                logicalClusterService.getTopicMetadatas(logicalClusterDO)
        ));
    }

    @ApiOperation(value = "集群Broker元信息列表", notes = "")
    @RequestMapping(value = "clusters/{logicalClusterId}/broker-metadata", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerMetadataVO>> getBrokerMetadatas(@PathVariable("logicalClusterId") Long logicalClusterId) {
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicalClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        List<BrokerMetadata> metadataList = logicalClusterService.getBrokerMetadatas(logicalClusterDO);

        List<BrokerMetadataVO> voList = new ArrayList<>();
        for (BrokerMetadata brokerMetadata: metadataList) {
            voList.add(new BrokerMetadataVO(brokerMetadata.getBrokerId(), brokerMetadata.getHost()));
        }
        return new Result<>(voList);
    }
}