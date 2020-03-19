package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.entity.dto.analysis.AnalysisBrokerDTO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.web.converters.BrokerModelConverter;
import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverallDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.web.converters.TopicModelConverter;
import com.xiaojukeji.kafka.manager.web.vo.broker.*;
import com.xiaojukeji.kafka.manager.web.vo.topic.TopicOverviewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * BrokerController
 * @author zengqiao
 * @date 19/4/3
 */
@Api(value = "BrokerController", description = "Broker相关接口")
@Controller
@RequestMapping("api/v1/")
public class BrokerController {
    private final static Logger logger = LoggerFactory.getLogger(BrokerController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private AnalysisService analysisService;

    @ApiOperation(value = "Broker概览", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerOverviewVO.class)
    @RequestMapping(value = "{clusterId}/brokers/overview", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<BrokerOverviewVO>> getBrokerOverview(@ApiParam(name = "clusterId", required = true, value = "集群ID") @PathVariable Long clusterId) {
        if (clusterId == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "clusterId illegal");
        }

        List<RegionDO> regionDOList = new ArrayList<>();
        List<BrokerOverviewDTO> brokerOverviewDTOList = null;
        try {
            brokerOverviewDTOList = brokerService.getBrokerOverviewList(clusterId, BrokerMetrics.getFieldNameList(MetricsType.BROKER_OVER_VIEW_METRICS), true);
            regionDOList = regionService.getByClusterId(clusterId);
        } catch (Exception e) {
            logger.error("getBrokerOverview@BrokerController, get failed, clusterId:{}.", clusterId);
        }
        return new Result<>(BrokerModelConverter.convert2BrokerOverviewList(brokerOverviewDTOList, regionDOList));
    }

    @ApiOperation(value = "Broker总揽", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerOverallVO.class)
    @RequestMapping(value = "{clusterId}/brokers/overall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<BrokerOverallVO>> getBrokersOverall(@PathVariable Long clusterId) {
        if (clusterId == null || clusterId < 0) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }

        List<BrokerOverallDTO> brokerOverallDTOList = brokerService.getBrokerOverallList(clusterId, BrokerMetrics.getFieldNameList(MetricsType.BROKER_OVER_ALL_METRICS));
        if (brokerOverallDTOList == null) {
            return new Result<>();
        }
        List<RegionDO> regionDOList = regionService.getByClusterId(clusterId);
        return new Result<>(BrokerModelConverter.convert2BrokerOverallVOList(clusterId, brokerOverallDTOList, regionDOList));
    }

    @ApiOperation(value = "集群Broker元信息列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerMetadataVO.class)
    @RequestMapping(value = "{clusterId}/brokers/broker-metadata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<BrokerMetadataVO>> getBrokerMetadataList(@ApiParam(name = "clusterId", required = true, value = "集群ID") @PathVariable Long clusterId) {
        if (clusterId == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "clusterId illegal");
        }
        List<Integer> brokerIdList = ClusterMetadataManager.getBrokerIdList(clusterId);
        List<BrokerMetadataVO> brokerMetadataVOList = new ArrayList<>();
        for (Integer brokerId: brokerIdList) {
            BrokerMetadata brokerMetadata = ClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (brokerMetadata == null) {
                continue;
            }
            brokerMetadataVOList.add(new BrokerMetadataVO(brokerMetadata.getBrokerId(), brokerMetadata.getHost()));
        }
        return new Result<>(brokerMetadataVOList);
    }

    @ApiOperation(value = "获取Broker基本信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerBasicVO.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/basic-info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<BrokerBasicVO> getBrokerBasicInfo(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId, @ApiParam(name = "brokerId", required = true, value = "BrokerId") @PathVariable Integer brokerId) {
        if (clusterId < 0 || brokerId < 0) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        BrokerBasicDTO brokerBasicDTO = brokerService.getBrokerBasicDTO(clusterId, brokerId, BrokerMetrics.getFieldNameList(5));
        if (brokerBasicDTO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "Broker不存在");
        }
        BrokerBasicVO brokerBasicVO = new BrokerBasicVO();
        CopyUtils.copyProperties(brokerBasicVO, brokerBasicDTO);
        return new Result<>(brokerBasicVO);
    }

    @ApiOperation(value = "获取Broker上的分区信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerPartitionsVO.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/partitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<BrokerPartitionsVO>> getBrokerPartitions(@PathVariable Long clusterId, @PathVariable Integer brokerId) {
        if (clusterId == null || clusterId < 0 || brokerId == null || brokerId < 0) {
            logger.error("getBrokerPartitions@BrokerController, parameter is invalid.");
            return new Result<>(StatusCode.PARAM_ERROR, "参数不合法");
        }

        Map<String, List<PartitionState>> partitionStateMap = null;
        try {
            partitionStateMap = topicService.getTopicPartitionState(clusterId, brokerId);
        } catch (Exception e) {
            logger.error("getBrokerPartitions@BrokerController, get BROKER topic partition state failed, clusterId:{} brokerId:{}.", clusterId, brokerId, e);
            return new Result<>(StatusCode.PARAM_ERROR, "get BROKER topic partition state error");
        }
        if (partitionStateMap == null) {
            logger.info("getBrokerPartitions@BrokerController, BROKER is empty none topic in this BROKER, clusterId:{} brokerId:{}.", clusterId, brokerId);
            return new Result<>();
        }
        return new Result<>(BrokerModelConverter.convert2BrokerPartitionsVOList(clusterId, brokerId, partitionStateMap));
    }

    @ApiOperation(value = "获取Broker实时流量信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerStatusVO.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/metrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<BrokerStatusVO> getBrokerMetrics(@PathVariable Long clusterId, @PathVariable Integer brokerId) {
        if (clusterId == null || brokerId == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        BrokerMetrics brokerMetrics = brokerService.getSpecifiedBrokerMetrics(clusterId, brokerId, BrokerMetrics.getFieldNameList(MetricsType.BROKER_REAL_TIME_METRICS), false);
        if (brokerMetrics == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, broker not exist");
        }
        List<BrokerMetrics> brokerMetricsList = new ArrayList<>();
        brokerMetricsList.add(brokerMetrics);
        return new Result<>(BrokerModelConverter.convertBroker2BrokerMetricsVO(brokerMetricsList));
    }

    @ApiOperation(value = "获取Broker历史流量信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerMetricsVO.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/metrics-history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<BrokerMetricsVO>> getBrokerMetricsHistory(@PathVariable Long clusterId, @PathVariable Integer brokerId, @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime) {
        if (clusterId == null || brokerId == null || startTime == null || endTime == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        List<BrokerMetrics> brokerMetricsList = brokerService.getBrokerMetricsByInterval(clusterId, brokerId, new Date(startTime), new Date(endTime));
        return new Result<>(BrokerModelConverter.convert2BrokerMetricsVOList(brokerMetricsList));
    }

    @ApiOperation(value = "获取Broker上的Topic列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicOverviewVO.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/topics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicOverviewVO>> getBrokerTopics(@PathVariable Long clusterId, @PathVariable Integer brokerId) {
        if (clusterId == null || clusterId < 0 || brokerId == null || brokerId < 0) {
            logger.error("getTopicListByBroker@BrokerController, parameter is invalid.");
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        List<TopicOverviewDTO> topicOverviewDTOList;
        try {
            topicOverviewDTOList = topicService.getTopicOverviewDTOList(cluster.getId(), brokerId, null);
        } catch (Exception e) {
            logger.error("getTopicListByBroker@BrokerController, get topics error, clusterId:{} brokerId:{}.", clusterId, brokerId, e);
            return new Result<>(StatusCode.PARAM_ERROR, "getTopicListByBroker error");
        }
        if (topicOverviewDTOList == null || topicOverviewDTOList.isEmpty()) {
            return new Result<>(StatusCode.PARAM_ERROR, "topics is null. clusterId is " + clusterId + ", brokerId is " + brokerId);
        }
        return new Result<>(TopicModelConverter.convert2TopicOverviewVOList(cluster, topicOverviewDTOList, null, null));
    }

    @ApiOperation(value = "删除指定Broker", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result deleteBroker(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId,
                               @ApiParam(name = "brokerId", required = true, value = "Broker的Id") @PathVariable Integer brokerId) {
        if (clusterId < 0 || brokerId < 0) {
            logger.error("deleteBrokerInfo@BrokerController, param illegal");
            return new Result(StatusCode.PARAM_ERROR, "clusterId or brokerId illegal");
        }
        return new Result(StatusCode.MY_SQL_DELETE_ERROR, "delete BROKER failed");
    }

    @ApiOperation(value = "Broker关键指标", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerKeyMetricsVO.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/key-metrics", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public Result<Map<String, List<BrokerKeyMetricsVO>>> getBrokerKeyMetrics(@PathVariable Long clusterId, @PathVariable Integer brokerId, @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime) {
        if (clusterId == null || brokerId == null || startTime == null || endTime == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        List<BrokerMetrics> todayBrokerMetricsList = brokerService.getBrokerMetricsByInterval(clusterId, brokerId, new Date(startTime), new Date(endTime));
        List<BrokerMetrics> yesterdayBrokerMetricsList = brokerService.getBrokerMetricsByInterval(clusterId, brokerId, DateUtils.addDays(new Date(startTime), -1), DateUtils.addDays(new Date(endTime), -1));
        List<BrokerMetrics> lastWeekBrokerMetricsList = brokerService.getBrokerMetricsByInterval(clusterId, brokerId, DateUtils.addDays(new Date(startTime), -7), DateUtils.addDays(new Date(endTime), -7));
        Map<String, List<BrokerKeyMetricsVO>> healthMap = new HashMap<>();
        healthMap.put("today", BrokerModelConverter.convert2BrokerKeyMetricsVOList(todayBrokerMetricsList));
        healthMap.put("yesterday", BrokerModelConverter.convert2BrokerKeyMetricsVOList(yesterdayBrokerMetricsList));
        healthMap.put("lastWeek", BrokerModelConverter.convert2BrokerKeyMetricsVOList(lastWeekBrokerMetricsList));
        return new Result<>(healthMap);
    }

    @ApiOperation(value = "BrokerTopic分析", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = AnalysisBrokerVO.class)
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/analysis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<AnalysisBrokerVO> getTopicAnalyzer(@PathVariable Long clusterId, @PathVariable Integer brokerId) {
        if (clusterId == null || clusterId < 0 || brokerId == null || brokerId < 0) {
            return new Result<>(StatusCode.PARAM_ERROR,"param illegal, please check clusterId and brokerId");
        }
        AnalysisBrokerDTO analysisBrokerDTO = analysisService.doAnalysisBroker(clusterId, brokerId);
        return new Result<>(BrokerModelConverter.convert2AnalysisBrokerVO(analysisBrokerDTO));
    }
}
