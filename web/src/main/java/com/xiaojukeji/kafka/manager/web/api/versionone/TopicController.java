package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicPartitionDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicFavoriteDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import com.xiaojukeji.kafka.manager.web.model.topic.TopicDataSampleModel;
import com.xiaojukeji.kafka.manager.web.model.topic.TopicFavorite;
import com.xiaojukeji.kafka.manager.web.model.topic.TopicFavoriteModel;
import com.xiaojukeji.kafka.manager.web.vo.broker.BrokerMetadataVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.web.converters.TopicModelConverter;
import com.xiaojukeji.kafka.manager.web.vo.topic.*;
import io.swagger.annotations.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangyiminghappy@163.com, zengqiao_cn@163.com
 * @date 19/6/2
 */
@Api(value = "TopicController", description = "Topic相关接口")
@Controller
@RequestMapping("api/v1/")
public class TopicController {
    private final static Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private RegionService regionService;

    @ApiOperation(value = "Topic元信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerMetadataVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/metadata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<TopicMetadataVO> getTopicMetadata(@PathVariable Long clusterId, @PathVariable String topicName) {
        if (clusterId == null || StringUtils.isEmpty(topicName)) {
            return new Result<>(StatusCode.PARAM_ERROR, "参数错误");
        }
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "参数错误, Topic不存在");
        }
        return new Result<>(TopicModelConverter.convert2TopicMetadataVO(clusterId, topicMetadata));
    }

    @ApiOperation(value = "收藏Topic", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "topics/favorite", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result addFavorite(@RequestBody TopicFavoriteModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }

        String username = SpringContextHolder.getUserName();
        List<TopicFavorite> topicFavoriteList = reqObj.getTopicFavoriteList();
        try {
            Boolean result = topicManagerService.addFavorite(TopicModelConverter.convert2TopicFavoriteDOList(username, topicFavoriteList));
            if (!Boolean.TRUE.equals(result)) {
                return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
            }
        } catch (Exception e) {
            logger.error("addFavorite@TopicController, add failed, username:{} req:{}.", username, reqObj, e);
            return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>();
    }

    @ApiOperation(value = "取消收藏Topic", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "topics/favorite", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result delFavorite(@RequestBody TopicFavoriteModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        String username = SpringContextHolder.getUserName();
        List<TopicFavorite> topicFavoriteList = reqObj.getTopicFavoriteList();
        try {
            Boolean result = topicManagerService.delFavorite(TopicModelConverter.convert2TopicFavoriteDOList(username, topicFavoriteList));
            if (!Boolean.TRUE.equals(result)) {
                return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
            }
        } catch (Exception e) {
            logger.error("delFavorite@TopicController, del failed, username:{} req{}.", username, reqObj, e);
            return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>();
    }

    @ApiOperation(value = "Topic列表[包括收藏列表]", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicOverviewVO.class)
    @RequestMapping(value = "topics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicOverviewVO>> getTopicInfoList(@ApiParam(name = "clusterId", value = "集群Id") @RequestParam(value = "clusterId", required = false) Long clusterId,
                                                          @ApiParam(name = "favorite", value = "收藏[True:是, False:否]") @RequestParam(value = "favorite", required = false) Boolean favorite) {
        String username = SpringContextHolder.getUserName();
        // 获取关注的Topic列表
        List<TopicFavoriteDO> topicFavoriteDOList = topicManagerService.getFavorite(username, clusterId);

        List<ClusterDO> clusterDOList = clusterService.listAll();
        List<TopicOverviewVO> topicOverviewVOList = new ArrayList<>();
        for (ClusterDO clusterDO: clusterDOList) {
            if (clusterId != null && !clusterDO.getId().equals(clusterId)) {
                continue;
            }

            // 获取Topic的负责人
            List<TopicDO> topicDOList = new ArrayList<>();
            try {
                topicDOList = topicManagerService.getByClusterId(clusterDO.getId());
            } catch (Exception e) {
                logger.error("getTopicInfoList@TopicController, get topics from db error, clusterId:{}.", clusterDO.getId(), e);
            }

            // 过滤获取到需要查询JMX信息的Topic
            List<String> filterTopicNameList = null;
            if (Boolean.TRUE.equals(favorite)) {
                filterTopicNameList = topicFavoriteDOList.stream().filter(elem -> elem.getClusterId().equals(clusterDO.getId())).map(elem -> elem.getTopicName()).collect(Collectors.toList());
            }

            // 获取Topic的元信息
            List<TopicOverviewDTO> topicOverviewDTOList = new ArrayList<>();
            try {
                topicOverviewDTOList = topicService.getTopicOverviewDTOList(clusterDO.getId(), -1, filterTopicNameList);
            } catch (Exception e) {
                logger.error("getTopicInfoList@TopicController, get topics error, clusterId:{}.", clusterDO.getId(), e);
            }

            // 合并数据
            topicOverviewVOList.addAll(TopicModelConverter.convert2TopicOverviewVOList(clusterDO, topicOverviewDTOList, topicDOList, topicFavoriteDOList));
        }
        return new Result<>(topicOverviewVOList);
    }

    @ApiOperation(value = "获取Topic名称列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = String.class)
    @RequestMapping(value = "{clusterId}/topics/topic-names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<String>> getTopicNameList(@ApiParam(name = "clusterId", required = true, value = "集群ID") @PathVariable Long clusterId) {
        if (clusterId == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        return new Result<>(ClusterMetadataManager.getTopicNameList(clusterId));
    }

    @ApiOperation(value = "获取Topic的基本信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicBasicVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/basic-info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<TopicBasicVO> getTopicBasicInfo(@ApiParam(name = "clusterId", required = true, value = "集群ID") @PathVariable Long clusterId,
                                                  @ApiParam(name = "topicName", required = true, value = "Topic名字") @PathVariable String topicName) {
        if (clusterId == null || StringUtils.isEmpty(topicName)) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }

        TopicBasicDTO topicBasicDTO = topicService.getTopicBasicDTO(clusterId, topicName);
        TopicDO topicDO = topicManagerService.getByTopicName(clusterId, topicName);
        List<RegionDO> regionList = regionService.getRegionByTopicName(clusterId, topicName);
        return new Result<>(TopicModelConverter.convert2TopicBasicVO(topicBasicDTO, topicDO, regionList));
    }

    @ApiOperation(value = "Topic采样", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicDataSampleVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/sample", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicDataSampleVO>> previewTopic(@ApiParam(name = "clusterId", required = true, value = "集群ID") @PathVariable Long clusterId,
                                                        @ApiParam(name = "topicName", required = true, value = "Topic名称") @PathVariable String topicName,
                                                        @ApiParam(name = "topicDataSampleModel", required = true, value = "请求参数") @RequestBody TopicDataSampleModel topicDataSampleModel) {
        if (clusterId == null || StringUtils.isEmpty(topicName)) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (clusterDO == null || !ClusterMetadataManager.isTopicExist(clusterId, topicName)) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster or topic not exist");
        }
        if (topicDataSampleModel == null || !topicDataSampleModel.legal()) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, request body illegal");
        }

        List<String> dataList = buildSampleDataList(clusterDO, topicName, topicDataSampleModel);
        if (dataList == null) {
            return new Result<>(StatusCode.OPERATION_ERROR, "fetch data failed");
        }

        List<TopicDataSampleVO> topicDataSampleVOList = new ArrayList<>();
        for (String data: dataList) {
            TopicDataSampleVO topicDataSampleVO = new TopicDataSampleVO();
            topicDataSampleVO.setValue(data);
            topicDataSampleVOList.add(topicDataSampleVO);
        }
        return new Result<>(topicDataSampleVOList);
    }

    private List<String> buildSampleDataList(ClusterDO clusterDO, String topicName, TopicDataSampleModel topicDataSampleModel) {
        int partitionId = topicDataSampleModel.getPartitionId();
        int maxMsgNum = topicDataSampleModel.getMaxMsgNum();
        int timeout = topicDataSampleModel.getTimeout();
        long offset = topicDataSampleModel.getOffset();
        boolean truncate = topicDataSampleModel.isTruncate();

        List<TopicPartition> topicPartitionList = new ArrayList<>();
        topicPartitionList.add(new TopicPartition(topicName, partitionId));
        return topicService.fetchTopicData(clusterDO, topicPartitionList, timeout, maxMsgNum, offset, truncate);
    }


    @ApiOperation(value = "Topic实时流量信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicRealTimeMetricsVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/metrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<TopicRealTimeMetricsVO> getTopicMetric(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId,
                                                         @ApiParam(name = "topicName", required = true, value = "topic名字") @PathVariable String topicName) {
        if (!ClusterMetadataManager.isTopicExist(clusterId, topicName)) {
            return new Result<>(Integer.valueOf(StatusCode.PARAM_ERROR), "param illegal, topic not exist");
        }
        TopicMetrics topicMetrics = topicService.getTopicMetrics(clusterId, topicName, TopicMetrics.getFieldNameList(MetricsType.TOPIC_FLOW_DETAIL));
        if (null == topicMetrics) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, topic not exist");
        }
        return new Result<>(TopicModelConverter.convert2TopicRealTimeMetricsVO(topicMetrics));
    }

    @ApiOperation(value = "获取Topic历史流量信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicMetrics.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/metrics-history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicMetricsVO>> getMetricsOfTopic(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId,
                                                          @ApiParam(name = "topicName", required = true, value = "topic名字") @PathVariable String topicName,
                                                          @ApiParam(name = "startTime", required = true, value = "开始时间") @RequestParam("startTime") Long startTime,
                                                          @ApiParam(name = "endTime", required = true, value = "结束时间") @RequestParam("endTime") Long endTime) {
        if (clusterId < 0 || StringUtils.isEmpty(topicName)) {
            logger.error("getMetricsOfTopic@TopicController, parameters are invalid.");
            return new Result<>(StatusCode.PARAM_ERROR, "集群参数和topic不能为空");
        }
        List<TopicMetrics> topicMetricsList = topicService.getTopicMetricsByInterval(clusterId, topicName, DateUtils.long2Date(startTime), DateUtils.long2Date(endTime));
        List<TopicMetricsVO> result = new ArrayList<>();
        for(TopicMetrics tm : topicMetricsList){
            TopicMetricsVO topicMetricsVO = new TopicMetricsVO();
            CopyUtils.copyProperties(topicMetricsVO, tm);
            topicMetricsVO.setGmtCreate(tm.getGmtCreate().getTime());
            result.add(topicMetricsVO);
        }
        return new Result<>(result);
    }

    @ApiOperation(value = "获取Topic分区信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicPartitionVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/partitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicPartitionVO>> getPartitionsOfTopic(@PathVariable @ApiParam(name = "clusterId", required = true, value = "集群Id") Long clusterId, @PathVariable @ApiParam(name = "TopicName", required = true, value = "Topic名称") String topicName) {
        Result result = paramCheck(clusterId, topicName);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        ClusterDO cluster = (ClusterDO) result.getData();
        List<TopicPartitionDTO> topicPartitionDTOList = topicService.getTopicPartitionDTO(cluster, topicName, true);
        if (topicPartitionDTOList == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, topic not EXIST");
        }
        return new Result<>(TopicModelConverter.convert2TopicPartitionVOList(topicPartitionDTOList));
    }

    @ApiOperation(value = "查询Topic的Broker信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicBrokerVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/brokers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicBrokerVO>> getBrokersOfTopic(@PathVariable @ApiParam(name = "clusterId", required = true, value = "集群Id") Long clusterId,
                                                         @PathVariable @ApiParam(name = "topicName", required = true, value = "Topic名称") String topicName) {
        Result result = paramCheck(clusterId, topicName);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        ClusterDO cluster = (ClusterDO) result.getData();

        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, topic not exist");
        }
        List<TopicPartitionDTO> topicPartitionDTOList = topicService.getTopicPartitionDTO(cluster, topicName, false);
        return new Result<>(TopicModelConverter.convert2TopicBrokerVOList(cluster, topicMetadata, topicPartitionDTOList));
    }

    @ApiOperation(value = "查询指定时间的offset信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicOffsetVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/offsets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicOffsetVO>> getOffsetsOfTopic(@PathVariable @ApiParam(name = "clusterId", required = true, value = "集群Id") Long clusterId,
                                                         @PathVariable @ApiParam(name = "topicName", required = true, value = "topic名字") String topicName,
                                                         @RequestParam("timestamp") @ApiParam(name = "timestamp", required = true, value = "时间戳(ms)") Long timestamp) {
        if (clusterId < 0 || StringUtils.isEmpty(topicName) || timestamp == null) {
            return new Result<>(StatusCode.PARAM_ERROR,"param illegal, please check clusterId, topicName and timestamp");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        List<PartitionOffsetDTO> partitionOffsetDTOList = topicService.getPartitionOffsetList(cluster, topicName, timestamp);
        if (partitionOffsetDTOList == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, topic not exist");

        }
        return new Result<>(TopicModelConverter.convert2TopicOffsetVOList(clusterId, topicName, partitionOffsetDTOList));
    }

    /**
     * 粗略检查参数是否合法
     */
    private Result paramCheck(Long clusterId, String topicName) {
        if (clusterId == null || clusterId < 0 || StringUtils.isEmpty(topicName)) {
            return new Result(StatusCode.PARAM_ERROR, "params illegal");
        }
        ClusterDO cluster = null;
        try {
            cluster = clusterService.getById(clusterId);
        } catch (Exception e) {
            logger.error("paramCheck@TopicController, clusterId:{}.", clusterId, e);
            return new Result(StatusCode.PARAM_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        if (cluster == null) {
            return new Result(StatusCode.PARAM_ERROR, "params illegal, cluster not EXIST");
        }
        return new Result<>(cluster);
    }
}
