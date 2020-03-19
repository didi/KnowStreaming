package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.web.converters.BrokerModelConverter;
import com.xiaojukeji.kafka.manager.web.model.ClusterModel;
import com.xiaojukeji.kafka.manager.web.vo.cluster.ClusterBasicVO;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.ControllerDO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import com.xiaojukeji.kafka.manager.web.converters.ClusterModelConverter;
import com.xiaojukeji.kafka.manager.web.vo.KafkaControllerVO;
import com.xiaojukeji.kafka.manager.web.vo.broker.BrokerStatusVO;
import com.xiaojukeji.kafka.manager.web.vo.cluster.ClusterMetricsVO;
import com.xiaojukeji.kafka.manager.web.vo.cluster.ClusterDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ClusterController
 * @author zengqiao
 * @date 19/4/3
 */
@Api(value = "ClusterController", description = "Cluster相关接口")
@Controller
@RequestMapping("api/v1/")
public class ClusterController {
    private final static Logger logger = LoggerFactory.getLogger(ClusterController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private BrokerService brokerService;

    @ApiOperation(value = "Kafka版本列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = String.class)
    @RequestMapping(value = "clusters/kafka-version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<String>> getKafkaVersionList() {
        List<String> kafkaVersionList = Arrays.asList("0.10.0", "0.10.1", "0.10.2", "0.11.0", "1.0", "1.1", "2.0", "2.1", "2.2", "2.3");
        return new Result<>(kafkaVersionList);
    }

    @ApiOperation(value = "集群列表(基本信息)", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = ClusterBasicVO.class)
    @RequestMapping(value = "clusters/basic-info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<ClusterBasicVO>> getBasicList() {
        List<ClusterDO> clusterDOList = clusterService.listAll();
        if (clusterDOList == null) {
            return new Result<>(StatusCode.MY_SQL_SELECT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(ClusterModelConverter.convert2ClusterBasicVOList(clusterDOList));
    }

    @ApiOperation(value = "集群列表(详细信息)", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = ClusterDetailVO.class)
    @RequestMapping(value = "clusters", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<ClusterDetailVO>> getClusterDetailList() {
        List<ClusterDO> clusterDOList = clusterService.listAll();
        if (clusterDOList == null) {
            return new Result<>(StatusCode.MY_SQL_SELECT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        Map<Long, Long> clusterIdRegionNumMap = regionService.getRegionNum();
        Map<Long, Integer> consumerGroupNumMap = consumerService.getConsumerGroupNumMap(clusterDOList);
        return new Result<>(ClusterModelConverter.convert2ClusterDetailVOList(clusterDOList, clusterIdRegionNumMap, consumerGroupNumMap));
    }

    @ApiOperation(value = "指定集群的信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = ClusterDetailVO.class)
    @RequestMapping(value = "clusters/{clusterId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<ClusterBasicVO> getBasicInfo(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId) {
        if (clusterId == null || clusterId < 0) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (null == cluster) {
            return new Result<>(StatusCode.PARAM_ERROR, "cluster not exist");
        }
        return new Result<>(ClusterModelConverter.convert2ClusterBasicVO(cluster));
    }

    @ApiOperation(value = "添加集群", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "clusters", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result addNewCluster(@RequestBody ClusterModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        return clusterService.addNewCluster(ClusterModelConverter.convert2ClusterDO(reqObj), SpringContextHolder.getUserName());
    }

    @ApiOperation(value = "修改集群", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "clusters", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result modifyCluster(@RequestBody ClusterModel reqModel) {
        if (reqModel == null || !reqModel.legal() || reqModel.getClusterId() == null) {
            return new Result(StatusCode.PARAM_ERROR, "参数错误");
        }
        ClusterDO oldClusterDO = clusterService.getById(reqModel.getClusterId());
        if (oldClusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        ClusterDO newClusterDO = ClusterModelConverter.convert2ClusterDO(reqModel);
        return clusterService.updateCluster(newClusterDO, !oldClusterDO.getZookeeper().equals(newClusterDO.getZookeeper()), SpringContextHolder.getUserName());
    }

    @ApiOperation(value = "查询集群实时流量信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = BrokerStatusVO.class)
    @RequestMapping(value = "clusters/{clusterId}/metrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<BrokerStatusVO> getClusterSummaryMetrics(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId) {
        if (clusterId == null || clusterId < 0) {
            return new Result<>(StatusCode.PARAM_ERROR, "clusterId illegal");
        }
        Map<Integer, BrokerMetrics> brokerMap = null;
        try {
            brokerMap = brokerService.getSpecifiedBrokerMetrics(clusterId, BrokerMetrics.getFieldNameList(MetricsType.BROKER_REAL_TIME_METRICS), false);
        } catch (Exception e) {
            logger.error("getBrokerSummaryMetrics@BrokerController, get failed, clusterId:{}.", clusterId);
        }
        if (brokerMap == null || brokerMap.isEmpty()) {
            return new Result<>();
        }
        return new Result<>(BrokerModelConverter.convertBroker2BrokerMetricsVO(new ArrayList<>(brokerMap.values())));
    }

    @ApiOperation(value = "获取集群的历史流量信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = ClusterMetricsVO.class)
    @RequestMapping(value = "clusters/{clusterId}/metrics-history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result getClusterMetricsHistory(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId,
                                           @ApiParam(name = "startTime", required = true, value = "起始时间") @RequestParam("startTime") Long startTime,
                                           @ApiParam(name = "endTime", required = true, value = "截止时间") @RequestParam("endTime") Long endTime) {
        if (clusterId == null || startTime == null || endTime == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not EXIST");
        }

        List<ClusterMetricsDO> clusterMetricsList = null;
        try {
            clusterMetricsList = clusterService.getClusterMetricsByInterval(clusterId, DateUtils.long2Date(startTime), DateUtils.long2Date(endTime));
        } catch (Exception e) {
            logger.error("getClusterMetricsHistory@ClusterController, select mysql:cluster_metrics failed, clusterId:{}.", clusterId, e);
        }
        if (clusterMetricsList == null) {
            return new Result(StatusCode.MY_SQL_SELECT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }

        List<ClusterMetricsVO> result = new ArrayList<>();
        for(ClusterMetricsDO metrics : clusterMetricsList){
            ClusterMetricsVO vo = new ClusterMetricsVO();
            CopyUtils.copyProperties(vo,metrics);
            vo.setGmtCreate(metrics.getGmtCreate().getTime());
            result.add(vo);
        }
        return new Result<>(result);
    }

    @ApiOperation(value = "集群Controller变更历史", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = KafkaControllerVO.class)
    @RequestMapping(value = "clusters/{clusterId}/controller-history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<KafkaControllerVO>> getControllerHistory(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId) {
        if (clusterId == null || clusterId <= 0) {
            return new Result<>(StatusCode.PARAM_ERROR,"param illegal");
        }
        List<ControllerDO> controllerDOList = null;
        try {
            controllerDOList = clusterService.getKafkaControllerHistory(clusterId);
        } catch (Exception e) {
            logger.error("getControllerHistory@ClusterController, get failed, clusterId:{}.", clusterId, e);
            return new Result<>(StatusCode.MY_SQL_SELECT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(ClusterModelConverter.convert2KafkaControllerVOList(controllerDOList));
    }
}
