package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.entity.po.*;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.web.model.order.OrderPartitionExecModel;
import com.xiaojukeji.kafka.manager.web.model.order.OrderPartitionModel;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.AdminTopicStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import com.xiaojukeji.kafka.manager.web.converters.OrderConverter;
import com.xiaojukeji.kafka.manager.web.model.order.OrderTopicExecModel;
import com.xiaojukeji.kafka.manager.web.model.order.OrderTopicModel;
import com.xiaojukeji.kafka.manager.web.vo.topic.TopicOverviewVO;
import com.xiaojukeji.kafka.manager.web.vo.order.OrderPartitionVO;
import com.xiaojukeji.kafka.manager.web.vo.order.OrderTopicVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/6/2
 */
@Api(value = "OrderController", description = "工单相关接口")
@Controller
@RequestMapping("api/v1/")
public class OrderController {
    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminTopicService adminTopicService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TopicService topicService;

    @ApiOperation(value = "Topic申请", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "orders/topic", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result createOrderTopic(@RequestBody OrderTopicModel reqObj) {
        if (reqObj == null || !reqObj.createLegal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        if (ClusterMetadataManager.isTopicExist(clusterDO.getId(), reqObj.getTopicName())) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, topic already exist");
        }
        try {
            String username = SpringContextHolder.getUserName();
            if (!orderService.createOrderTopic(OrderConverter.convert2OrderTopicDO(clusterDO, username, reqObj))) {
                return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
            }
        } catch (Exception e) {
            logger.error("createOrderTopic@OrderController, create failed, req:{}.", reqObj, e);
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>();
    }

    @ApiOperation(value = "Topic工单撤销", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicOverviewVO.class)
    @RequestMapping(value = "orders/topic", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result cancelApplyTopic(@RequestParam("orderId") Long orderId) {
        String username = SpringContextHolder.getUserName();
        if (orderId == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        try {
            return orderService.cancelOrder(orderId, username, OrderTypeEnum.APPLY_TOPIC);
        } catch (Exception e) {
            logger.error("cancelApplyTopic@OrderController, update failed, username:{} orderId:{}.", username, orderId, e);
            return new Result<>(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
    }

    @ApiOperation(value = "Topic工单查看", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = OrderTopicVO.class)
    @RequestMapping(value = "orders/topic", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result getOrderTopic() {
        String username = SpringContextHolder.getUserName();
        List<OrderTopicDO> orderTopicDOList = null;
        try {
            orderTopicDOList = orderService.getOrderTopics(username);
        } catch (Exception e) {
            logger.error("getOrderTopic@OrderController, get failed, username:{}.", username, e);
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(OrderConverter.convert2OrderTopicVOList(orderTopicDOList));
    }

    @ApiOperation(value = "Topic所有工单查看[admin]", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = OrderTopicVO.class)
    @RequestMapping(value = "admin/orders/topic", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<OrderTopicVO>> getAllApplyTopic() {
        List<OrderTopicDO> orderTopicDOList = null;
        try {
            orderTopicDOList = orderService.getOrderTopics(null);
        } catch (Exception e) {
            logger.error("getOrderTopic@OrderController, get failed.", e);
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(OrderConverter.convert2OrderTopicVOList(orderTopicDOList));
    }

    @ApiOperation(value = "Topic工单执行[通过/拒绝]", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "admin/orders/topic", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result executeApplyTopic(@RequestBody OrderTopicExecModel reqObj) {
        Result result = OrderTopicExecModel.illegal(reqObj);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        OrderTopicDO orderTopicDO = orderService.getOrderTopicById(reqObj.getOrderId());
        if (orderTopicDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, order not exist");
        } else if (!OrderStatusEnum.WAIT_DEAL.getCode().equals(orderTopicDO.getOrderStatus())) {
            return new Result(StatusCode.OPERATION_ERROR, "order already handled");
        }
        ClusterDO clusterDO = clusterService.getById(orderTopicDO.getClusterId());
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }

        String username = SpringContextHolder.getUserName();
        orderTopicDO.setBrokers((reqObj.getBrokerIdList() == null || reqObj.getBrokerIdList().isEmpty())? "": ListUtils.intList2String(reqObj.getBrokerIdList()));
        orderTopicDO.setRegions((reqObj.getRegionIdList() == null || reqObj.getRegionIdList().isEmpty())? "": ListUtils.longList2String(reqObj.getRegionIdList()));
        orderTopicDO.setApprover(username);
        orderTopicDO.setOpinion(reqObj.getApprovalOpinions());
        orderTopicDO.setOrderStatus(reqObj.getOrderStatus());
        if (OrderStatusEnum.PASSED.getCode().equals(reqObj.getOrderStatus())) {
            result = createTopic(clusterDO, reqObj, orderTopicDO);
        }
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        result = orderService.modifyOrderTopic(orderTopicDO, username, true);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return new Result(StatusCode.OPERATION_ERROR, "create topic success, but update order status failed, err:" + result.getMessage());
        }
        return new Result();
    }

    private Result createTopic(ClusterDO clusterDO, OrderTopicExecModel reqObj, OrderTopicDO orderTopicDO) {
        TopicDO topicInfoDO = OrderConverter.convert2TopicInfoDO(orderTopicDO);
        List<Integer> brokerIdList = regionService.getFullBrokerId(clusterDO.getId(), reqObj.getRegionIdList(), reqObj.getBrokerIdList());
        Properties topicConfig = new Properties();
        topicConfig.setProperty("retention.ms", String.valueOf(reqObj.getRetentionTime() * 60 * 60 * 1000));
        try {
            TopicMetadata topicMetadata = new TopicMetadata();
            topicMetadata.setTopic(orderTopicDO.getTopicName());
            topicMetadata.setReplicaNum(reqObj.getReplicaNum());
            topicMetadata.setPartitionNum(reqObj.getPartitionNum());
            topicMetadata.setBrokerIdSet(new HashSet<>(brokerIdList));
            AdminTopicStatusEnum adminTopicStatusEnum = adminTopicService.createTopic(clusterDO, topicMetadata, topicInfoDO, topicConfig, SpringContextHolder.getUserName());
            if (!AdminTopicStatusEnum.SUCCESS.equals(adminTopicStatusEnum)) {
                return new Result(StatusCode.OPERATION_ERROR, adminTopicStatusEnum.getMessage());
            }
        } catch (Exception e) {
            logger.error("executeApplyTopic@OrderController, create failed, req:{}.", reqObj);
            return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result();
    }

    @ApiOperation(value = "partition申请", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "orders/partition", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result applyPartition(@RequestBody OrderPartitionModel reqObj) {
        if (reqObj == null || !reqObj.createLegal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        if (!ClusterMetadataManager.isTopicExist(clusterDO.getId(), reqObj.getTopicName())) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, topic not exist");
        }
        try {
            if (!orderService.createOrderPartition(OrderConverter.convert2OrderPartitionDO(clusterDO, SpringContextHolder.getUserName(), reqObj))) {
                return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
            }
        } catch (Exception e) {
            logger.error("applyPartition@OrderController, create failed, req:{}.", reqObj, e);
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>();
    }

    @ApiOperation(value = "partition工单撤销", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "orders/partition", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result cancelApplyPartition(@RequestParam("orderId") Long orderId) {
        if (orderId == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        try {
            return orderService.cancelOrder(orderId, SpringContextHolder.getUserName(), OrderTypeEnum.APPLY_PARTITION);
        } catch (Exception e) {
            logger.error("cancelApplyPartition@OrderController, update failed, username:{} orderId:{}.", SpringContextHolder.getUserName(), orderId, e);
            return new Result<>(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
    }

    @ApiOperation(value = "partition工单查看", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = OrderPartitionVO.class)
    @RequestMapping(value = "orders/partition", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<OrderPartitionVO>> getApplyPartitionList(@RequestParam(value = "orderId", required = false) Long orderId) {
        List<OrderPartitionDO> orderPartitionDOList = null;
        try {
            orderPartitionDOList = orderService.getOrderPartitions(SpringContextHolder.getUserName(), orderId);
        } catch (Exception e) {
            logger.error("getApplyPartition@OrderController, get failed, username:{}.", SpringContextHolder.getUserName(), e);
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(OrderConverter.convert2OrderPartitionVOList(orderPartitionDOList));
    }

    @ApiOperation(value = "partition工单查看[Admin]", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = OrderPartitionVO.class)
    @RequestMapping(value = "admin/orders/partition", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<OrderPartitionVO>> adminGetApplyPartition(@RequestParam(value = "orderId", required = false) Long orderId) {
        List<OrderPartitionDO> orderPartitionDOList = null;
        try {
            orderPartitionDOList = orderService.getOrderPartitions(null, orderId);
        } catch (Exception e) {
            logger.error("adminGetApplyPartition@OrderController, get failed.", e);
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        if (orderId == null || orderPartitionDOList.isEmpty()) {
            return new Result<>(OrderConverter.convert2OrderPartitionVOList(orderPartitionDOList));
        }
        return new Result<>(supplyExternalInfo(orderPartitionDOList));

    }

    private List<OrderPartitionVO> supplyExternalInfo(List<OrderPartitionDO> orderPartitionDOList) {
        if (orderPartitionDOList == null || orderPartitionDOList.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderPartitionVO> orderPartitionVOList = new ArrayList<>();
        for (OrderPartitionDO orderPartitionDO: orderPartitionDOList) {
            TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(orderPartitionDO.getClusterId(), orderPartitionDO.getTopicName());
            if (topicMetadata == null) {
                // Topic不存在
                continue;
            }

            // 获取Topic的峰值均值流量
            Date startTime = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
            Date endTime = new Date();
            List<TopicMetrics> topicMetricsList = topicService.getTopicMetricsByInterval(orderPartitionDO.getClusterId(), orderPartitionDO.getTopicName(), startTime, endTime);
            Long maxAvgBytes = topicService.calTopicMaxAvgBytesIn(topicMetricsList, 10);

            // 获取Topic所处的Region信息
            List<RegionDO> regionDOList = regionService.getRegionByTopicName(orderPartitionDO.getClusterId(), orderPartitionDO.getTopicName());
            orderPartitionVOList.add(OrderConverter.convert2OrderPartitionVO(orderPartitionDO, topicMetadata, maxAvgBytes, regionDOList));
        }
        return orderPartitionVOList;
    }

    @ApiOperation(value = "partition工单执行[通过/拒绝]", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "admin/orders/partition", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result executeApplyPartition(@RequestBody OrderPartitionExecModel reqObj) {
        String username = SpringContextHolder.getUserName();
        Result result = OrderPartitionExecModel.illegal(reqObj);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        OrderPartitionDO orderPartitionDO = orderService.getOrderPartitionById(reqObj.getOrderId());
        if (orderPartitionDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, order not exist");
        } else if (!OrderStatusEnum.WAIT_DEAL.getCode().equals(orderPartitionDO.getOrderStatus())) {
            return new Result(StatusCode.OPERATION_ERROR, "order already handled");
        }
        ClusterDO clusterDO = clusterService.getById(orderPartitionDO.getClusterId());
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        if (OrderStatusEnum.PASSED.getCode().equals(reqObj.getOrderStatus())) {
            result = expandTopic(clusterDO, reqObj, orderPartitionDO);
        }
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        orderPartitionDO.setApprover(username);
        orderPartitionDO.setOpinion(reqObj.getApprovalOpinions());
        orderPartitionDO.setOrderStatus(reqObj.getOrderStatus());
        result = orderService.modifyOrderPartition(orderPartitionDO, username, true);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return new Result(StatusCode.OPERATION_ERROR, "expand topic success, but update order status failed, err:" + result.getMessage());
        }
        return new Result();
    }

    private Result expandTopic(ClusterDO clusterDO,
                               OrderPartitionExecModel reqObj,
                               OrderPartitionDO orderPartitionDO) {
        List<Integer> brokerIdList = regionService.getFullBrokerId(clusterDO.getId(), reqObj.getRegionIdList(), reqObj.getBrokerIdList());
        try {
            TopicMetadata topicMetadata = new TopicMetadata();
            topicMetadata.setTopic(orderPartitionDO.getTopicName());
            topicMetadata.setBrokerIdSet(new HashSet<>(brokerIdList));
            topicMetadata.setPartitionNum(reqObj.getPartitionNum());
            AdminTopicStatusEnum adminTopicStatusEnum = adminTopicService.expandTopic(clusterDO, topicMetadata, SpringContextHolder.getUserName());
            if (!AdminTopicStatusEnum.SUCCESS.equals(adminTopicStatusEnum)) {
                return new Result(StatusCode.OPERATION_ERROR, adminTopicStatusEnum.getMessage());
            }
            orderPartitionDO.setPartitionNum(reqObj.getPartitionNum());
            orderPartitionDO.setBrokerList(ListUtils.intList2String(brokerIdList));
        } catch (Exception e) {
            logger.error("expandTopic@OrderController, create failed, req:{}.", reqObj);
            return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result();
    }
}

