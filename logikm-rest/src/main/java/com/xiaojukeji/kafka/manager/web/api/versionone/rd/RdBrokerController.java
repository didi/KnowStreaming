package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.analysis.AnalysisBrokerDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.RealTimeMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.TopicOverviewVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker.*;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AnalysisService;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.BrokerModelConverter;
import com.xiaojukeji.kafka.manager.web.converters.CommonModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/4/20
 */
@Api(tags = "RD-Broker相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdBrokerController {
    @Autowired
    private BrokerService brokerService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private RegionService regionService;

    @ApiOperation(value = "Broker元信息")
    @RequestMapping(value = "{clusterId}/brokers/broker-metadata", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerMetadataVO>> getBrokerMetadataList(@PathVariable Long clusterId) {
        List<Integer> brokerIdList = PhysicalClusterMetadataManager.getBrokerIdList(clusterId);
        List<BrokerMetadataVO> brokerMetadataVOList = new ArrayList<>();
        for (Integer brokerId : brokerIdList) {
            BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (brokerMetadata == null) {
                continue;
            }
            brokerMetadataVOList.add(new BrokerMetadataVO(brokerMetadata.getBrokerId(), brokerMetadata.getHost()));
        }
        return new Result<>(brokerMetadataVOList);
    }

    @ApiOperation(value = "Broker基本信息")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<BrokerBasicVO> getBrokerBasic(@PathVariable Long clusterId,
                                                @PathVariable Integer brokerId) {
        BrokerBasicDTO brokerBasicDTO = brokerService.getBrokerBasicDTO(clusterId, brokerId);
        if (brokerBasicDTO == null) {
            return Result.buildFrom(ResultStatus.BROKER_NOT_EXIST);
        }
        BrokerBasicVO brokerBasicVO = new BrokerBasicVO();
        CopyUtils.copyProperties(brokerBasicVO, brokerBasicDTO);
        return new Result<>(brokerBasicVO);
    }


    @ApiOperation(value = "Broker基本信息列表", notes = "")
    @RequestMapping(value = "clusters/{clusterId}/brokers/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<RdBrokerBasicVO>> getBrokerBasics(@PathVariable("clusterId") Long clusterId) {
        List<Integer> brokerIdList = PhysicalClusterMetadataManager.getBrokerIdList(clusterId);
        if (ValidateUtils.isEmptyList(brokerIdList)) {
            return new Result<>(new ArrayList<>());
        }
        List<RegionDO> regionDOList = regionService.listAll()
                .stream()
                .filter(regionDO -> clusterId.equals(regionDO.getClusterId()))
                .collect(Collectors.toList());
        Map<Integer, RegionDO> regionMap = new HashMap<>();
        for (RegionDO regionDO : regionDOList) {
            if (ValidateUtils.isNull(regionDO)) {
                continue;
            }
            for (Integer brokerId : ListUtils.string2IntList(regionDO.getBrokerList())) {
                regionMap.put(brokerId, regionDO);
            }
        }
        return new Result<>(BrokerModelConverter.convert2RdBrokerBasicVO(clusterId, brokerIdList, regionMap));
    }

    @ApiOperation(value = "BrokerTopic信息")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicOverviewVO>> getBrokerTopics(@PathVariable Long clusterId,
                                                         @PathVariable Integer brokerId) {
        return new Result<>(CommonModelConverter.convert2TopicOverviewVOList(
                clusterId,
                topicService.getTopicOverviewList(clusterId, brokerId)
        ));
    }

    @ApiOperation(value = "Broker分区信息")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/partitions", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerPartitionVO>> getBrokerPartitions(@PathVariable Long clusterId,
                                                               @PathVariable Integer brokerId) {
        Map<String, List<PartitionState>> partitionStateMap = topicService.getTopicPartitionState(clusterId, brokerId);
        if (ValidateUtils.isNull(partitionStateMap)) {
            return Result.buildFrom(ResultStatus.BROKER_NOT_EXIST);
        }
        return new Result<>(BrokerModelConverter.convert2BrokerPartitionVOList(clusterId, brokerId, partitionStateMap));
    }

    @ApiOperation(value = "Broker实时流量")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/metrics", method = RequestMethod.GET)
    @ResponseBody
    public Result<RealTimeMetricsVO> getBrokerMetrics(@PathVariable Long clusterId,
                                                      @PathVariable Integer brokerId) {
        BrokerMetrics brokerMetrics = brokerService.getBrokerMetricsFromJmx(
                clusterId,
                brokerId,
                KafkaMetricsCollections.COMMON_DETAIL_METRICS
        );
        if (ValidateUtils.isNull(brokerMetrics)) {
            return Result.buildFrom(ResultStatus.BROKER_NOT_EXIST);
        }
        return new Result<>(CommonModelConverter.convert2RealTimeMetricsVO(brokerMetrics));
    }

    @ApiOperation(value = "Broker磁盘分区", notes = "")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/partitions-location", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerDiskTopicVO>> getBrokerPartitionsLocation(@PathVariable Long clusterId,
                                                                       @PathVariable Integer brokerId) {
        return new Result<>(CommonModelConverter.convert2BrokerDiskTopicVOList(
                brokerService.getBrokerTopicLocation(clusterId, brokerId)
        ));
    }

    @ApiOperation(value = "Broker历史指标")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/metrics-history", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerMetricsVO>> getBrokerMetricsHistory(@PathVariable Long clusterId,
                                                                 @PathVariable Integer brokerId,
                                                                 @RequestParam("startTime") Long startTime,
                                                                 @RequestParam("endTime") Long endTime) {
        List<BrokerMetricsDO> metricsList =
                brokerService.getBrokerMetricsFromDB(clusterId, brokerId, new Date(startTime), new Date(endTime));
        return new Result<>(BrokerModelConverter.convert2BrokerMetricsVOList(metricsList));
    }

    @ApiOperation(value = "BrokerTopic分析")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/analysis", method = RequestMethod.GET)
    @ResponseBody
    public Result<AnalysisBrokerVO> getTopicAnalyzer(@PathVariable Long clusterId, @PathVariable Integer brokerId) {
        AnalysisBrokerDTO analysisBrokerDTO = analysisService.doAnalysisBroker(clusterId, brokerId);
        return new Result<>(BrokerModelConverter.convert2AnalysisBrokerVO(analysisBrokerDTO));
    }

    @ApiOperation(value = "Broker删除", notes = "删除DB中的Broker信息")
    @RequestMapping(value = "{clusterId}/brokers", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteBrokerId(@PathVariable Long clusterId, @RequestParam("brokerId") Integer brokerId) {
        return Result.buildFrom(brokerService.delete(clusterId, brokerId));
    }
}