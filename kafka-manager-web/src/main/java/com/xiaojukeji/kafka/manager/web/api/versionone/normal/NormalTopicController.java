package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicPartitionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicDataSampleDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.RealTimeMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.TopicBusinessInfoVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.*;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaBillDO;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxAttributeEnum;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicStatisticMetricsVO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.CommonModelConverter;
import com.xiaojukeji.kafka.manager.web.converters.TopicModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/3/31
 */
@Api(tags = "Normal-Topic详情相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalTopicController {
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private TopicConnectionService connectionService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private KafkaBillService kafkaBillService;

    @ApiOperation(value = "Topic基本信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<TopicBasicVO> getTopicBasic(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(TopicModelConverter.convert2TopicBasicVO(
                topicService.getTopicBasicDTO(physicalClusterId, topicName),
                clusterService.getById(physicalClusterId),
                logicalClusterMetadataManager.getTopicLogicalClusterId(physicalClusterId, topicName)
        ));
    }

    @ApiOperation(value = "Topic实时流量信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/metrics", method = RequestMethod.GET)
    @ResponseBody
    public Result<RealTimeMetricsVO> getTopicMetrics(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(CommonModelConverter.convert2RealTimeMetricsVO(
                topicService.getTopicMetricsFromJMX(
                        physicalClusterId,
                        topicName,
                        KafkaMetricsCollections.COMMON_DETAIL_METRICS,
                        true
                )
        ));
    }

    @ApiOperation(value = "Topic历史流量信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/metrics-history", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicMetricVO>> getTopicMetrics(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        if (ValidateUtils.isBlank(appId)) {
            return new Result<>(TopicModelConverter.convert2TopicMetricsVOList(
                    topicService.getTopicMetricsFromDB(
                            physicalClusterId,
                            topicName,
                            new Date(startTime),
                            new Date(endTime)
                    )
            ));
        }
        return new Result<>(TopicModelConverter.convert2TopicMetricVOList(
                topicService.getTopicMetricsFromDB(
                        appId,
                        physicalClusterId,
                        topicName,
                        new Date(startTime),
                        new Date(endTime)
                )
        ));
    }

    @ApiOperation(value = "Topic实时请求耗时信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/request-time", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicRequestTimeDetailVO>> getTopicRequestMetrics(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId,
            @RequestParam(value = "percentile", required = false, defaultValue = "75thPercentile") String percentile) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        Boolean isPercentileLegal = Arrays.stream(JmxAttributeEnum.PERCENTILE_ATTRIBUTE.getAttribute())
                .anyMatch(percentile::equals);

        if (!isPercentileLegal) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        BaseMetrics metrics = topicService.getTopicMetricsFromJMX(
                physicalClusterId,
                topicName,
                KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS,
                false
        );
        return new Result<>(TopicModelConverter.convert2TopicRequestTimeDetailVOList(metrics, percentile));
    }

    @ApiOperation(value = "Topic历史请求耗时信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/request-time-history", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicRequestTimeVO>> getTopicRequestMetrics(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(TopicModelConverter.convert2TopicRequestTimeMetricsVOList(
                topicService.getTopicRequestMetricsFromDB(
                        physicalClusterId,
                        topicName,
                        new Date(startTime),
                        new Date(endTime)
                ))
        );
    }

    @ApiOperation(value = "Topic连接信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/connections", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicConnectionVO>> getTopicConnections(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        List<TopicConnection> connections;

        if (ValidateUtils.isBlank(appId)) {
            connections = connectionService.getByTopicName(
                    physicalClusterId,
                    topicName,
                    new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                    new Date()
            );
        } else {
            connections = connectionService.getByTopicName(
                    physicalClusterId,
                    topicName,
                    appId,
                    new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                    new Date()
            );
        }

        return new Result<>(TopicModelConverter.convert2TopicConnectionVOList(connections));
    }

    @ApiOperation(value = "Topic分区信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/partitions", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicPartitionVO>> getTopicPartitions(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (clusterDO == null || !PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, topicName)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        List<TopicPartitionDTO> dtoList = topicService.getTopicPartitionDTO(clusterDO, topicName, true);
        return new Result<>(TopicModelConverter.convert2TopicPartitionVOList(dtoList));
    }

    @ApiOperation(value = "Topic采样信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/sample", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<TopicDataSampleVO>> previewTopic(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestBody TopicDataSampleDTO reqObj) {
        reqObj.adjustConfig();

        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, reqObj.getIsPhysicalClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)
                || !PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, topicName)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }

        List<String> dataList = topicService.fetchTopicData(clusterDO, topicName, reqObj);
        if (ValidateUtils.isNull(dataList)) {
            return Result.buildFrom(ResultStatus.OPERATION_FAILED);
        }
        return new Result<>(TopicModelConverter.convert2TopicDataSampleVOList(dataList));
    }

    @ApiOperation(value = "Topic账单信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/bills", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicBillVO>> getTopicBills(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        List<KafkaBillDO> kafkaBillDOList =
                kafkaBillService.getByTopicName(physicalClusterId, topicName, new Date(startTime), new Date(endTime));

        List<TopicBillVO> voList = new ArrayList<>();
        for (KafkaBillDO kafkaBillDO: kafkaBillDOList) {
            TopicBillVO vo = new TopicBillVO();
            vo.setQuota(kafkaBillDO.getQuota().longValue());
            vo.setCost(kafkaBillDO.getCost());
            vo.setGmtMonth(kafkaBillDO.getGmtDay());
            voList.add(vo);
        }
        return new Result<>(voList);
    }

    @ApiOperation(value = "获取Topic业务信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/business", method = RequestMethod.GET)
    @ResponseBody
    public Result<TopicBusinessInfoVO> getTopicBusinessInfo(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(TopicModelConverter.convert2TopicBusinessInfoVO(
                topicManagerService.getTopicBusinessInfo(physicalClusterId, topicName)
        ));
    }

    @ApiOperation(value = "Topic有权限的应用信息", notes = "")
    @RequestMapping(value = {"{clusterId}/topics/{topicName}/apps"}, method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicAuthorizedAppVO>> getTopicAuthorizedApps(@PathVariable Long clusterId,
                                                                     @PathVariable String topicName,
                                                                     @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(TopicModelConverter.convert2TopicAuthorizedAppVOList(
                topicManagerService.getTopicAuthorizedApps(physicalClusterId, topicName))
        );
    }

    @ApiOperation(value = "Topic我的应用信息", notes = "")
    @RequestMapping(value = {"{clusterId}/topics/{topicName}/my-apps"}, method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicMyAppVO>> getTopicMyApps(@PathVariable Long clusterId,
                                                     @PathVariable String topicName,
                                                     @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        return new Result<>(TopicModelConverter.convert2TopicMineAppVOList(
            topicManagerService.getTopicMineApps(physicalClusterId, topicName, SpringTool.getUserName()))
        );
    }

    @ApiOperation(value = "Topic流量统计信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/statistic-metrics", method = RequestMethod.GET)
    @ResponseBody
    public Result<TopicStatisticMetricsVO> getTopicStatisticMetrics(@PathVariable Long clusterId,
                                                                    @PathVariable String topicName,
                                                                    @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId,
                                                                    @RequestParam("latest-day") Integer latestDay) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        Double maxAvgBytesIn = topicManagerService.getTopicMaxAvgBytesIn(physicalClusterId, topicName, new Date(DateUtils.getDayStarTime(-1 * latestDay)), new Date(), 1);
        if (ValidateUtils.isNull(maxAvgBytesIn)) {
            return Result.buildFrom(ResultStatus.MYSQL_ERROR);
        }
        return new Result<>(new TopicStatisticMetricsVO(maxAvgBytesIn));
    }

}