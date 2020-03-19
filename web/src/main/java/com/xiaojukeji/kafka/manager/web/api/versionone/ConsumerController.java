package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.web.converters.ConsumerModelConverter;
import com.xiaojukeji.kafka.manager.web.vo.consumer.ConsumerGroupDetailVO;
import com.xiaojukeji.kafka.manager.web.vo.consumer.ConsumerGroupVO;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.OffsetStoreLocation;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerGroupDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.web.model.OffsetResetModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/14
 */
@Api(value = "ConsumerController", description = "Consumer相关接口")
@Controller
@RequestMapping("/api/v1/")
public class ConsumerController {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicService topicService;

    @ApiOperation(value = "获取消费组列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = ConsumerGroupVO.class)
    @RequestMapping(value = "{clusterId}/consumers/consumer-groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<ConsumerGroupVO>> getConsumerGroupList(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId) {
        if (clusterId == null || clusterId <= 0) {
            return new Result<>(StatusCode.PARAM_ERROR, "params illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "params illegal, cluster not exist");
        }
        return new Result<>(ConsumerModelConverter.convert2ConsumerGroupVOList(consumerService.getConsumerGroupList(cluster.getId())));
    }

    @ApiOperation(value = "查询消费Topic的消费组", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = ConsumerGroupVO.class)
    @RequestMapping(value = "{clusterId}/topics/{topicName}/consumer-groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<ConsumerGroupVO>> getTopicConsumerGroup(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId,
                                                               @ApiParam(name = "topicName", required = true, value = "Topic名称") @PathVariable String topicName) {
        if (clusterId == null || clusterId < 0 || StringUtils.isEmpty(topicName)) {
            return new Result<>(StatusCode.PARAM_ERROR, "params illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "params illegal, cluster not exist");
        }
        return new Result<>(ConsumerModelConverter.convert2ConsumerGroupVOList(consumerService.getConsumerGroupList(cluster.getId(), topicName)));
    }

    @ApiOperation(value = "查询消费组的消费详情", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = ConsumerGroupDetailVO.class)
    @RequestMapping(value = "{clusterId}/consumers/{consumerGroup}/topics/{topicName}/consume-detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<ConsumerGroupDetailVO>> getConsumerGroupConsumeDetail(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId,
                                                                             @ApiParam(name = "consumerGroup", required = true, value = "消费组") @PathVariable String consumerGroup,
                                                                             @ApiParam(name = "topicName", required = true, value = "Topic名称") @PathVariable String topicName,
                                                                             @ApiParam(name = "location", required = true, value = "存储位置") @RequestParam("location") String location) {
        if (clusterId < 0 || StringUtils.isEmpty(topicName) || StringUtils.isEmpty(consumerGroup) || StringUtils.isEmpty(location)) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }

        location = location.toLowerCase();
        OffsetStoreLocation offsetStoreLocation = OffsetStoreLocation.getOffsetStoreLocation(location);
        if (offsetStoreLocation == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, offset location illegal");
        }

        ConsumerGroupDTO consumeGroupDTO = new ConsumerGroupDTO(cluster.getId(), consumerGroup, offsetStoreLocation);
        try {
            List<ConsumeDetailDTO> consumeDetailDTOList = consumerService.getConsumeDetail(cluster, topicName, consumeGroupDTO);
            return new Result<>(ConsumerModelConverter.convert2ConsumerGroupDetailVO(clusterId, topicName, consumerGroup, location, consumeDetailDTOList));
        } catch (Exception e) {
            logger.error("getConsumerGroupConsumeDetail@ConsumerControlller, get consume detail failed, consumerGroup:{}.", consumeGroupDTO, e);
        }
        return new Result<>(StatusCode.RES_UNREADY, Constant.KAFKA_MANAGER_INNER_ERROR + ", get consume detail failed");
    }

    @ApiOperation(value = "查询消费组消费的Topic", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = String.class)
    @RequestMapping(value = "{clusterId}/consumer/{consumerGroup}/topics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<String>> getConsumerGroupConsumedTopicList(@ApiParam(name = "clusterId", required = true, value = "集群Id") @PathVariable Long clusterId,
                                                                  @ApiParam(name = "consumerGroup", required = true, value = "消费组") @PathVariable String consumerGroup,
                                                                  @ApiParam(name = "location", required = true, value = "消费组存储位置") @RequestParam("location") String location) {
        if (clusterId < 0 || StringUtils.isEmpty(consumerGroup) || StringUtils.isEmpty(location)) {
            return new Result<>(StatusCode.PARAM_ERROR, "params illegal");
        }
        ClusterDO cluster = clusterService.getById(clusterId);
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "params illegal, cluster not exist");
        }

        location = location.toLowerCase();
        OffsetStoreLocation offsetStoreLocation = OffsetStoreLocation.getOffsetStoreLocation(location);
        if (offsetStoreLocation == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, offset location illegal");
        }

        ConsumerGroupDTO consumeGroupDTO = new ConsumerGroupDTO(cluster.getId(), consumerGroup, offsetStoreLocation);
        return new Result<>(consumerService.getConsumerGroupConsumedTopicList(cluster, consumeGroupDTO));
    }

    @ApiOperation(value = "重置消费偏移", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "consumers/offsets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<Result>> resetConsumeOffsets(@RequestBody OffsetResetModel offsetResetModel) {
        if (offsetResetModel == null || !offsetResetModel.legal()) {
            return new Result<>(StatusCode.PARAM_ERROR,"param illegal");
        }
        ClusterDO cluster = clusterService.getById(offsetResetModel.getClusterId());
        if (cluster == null) {
            return new Result<>(StatusCode.PARAM_ERROR,"param illegal, cluster not exist");
        }
        List<PartitionOffsetDTO> partitionOffsetDTOList = offsetResetModel.getOffsetList();
        if (offsetResetModel.getTimestamp() != null) {
            partitionOffsetDTOList = topicService.getPartitionOffsetList(cluster, offsetResetModel.getTopicName(), offsetResetModel.getTimestamp());
        }
        if (partitionOffsetDTOList == null || partitionOffsetDTOList.isEmpty()) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, ");
        }
        ConsumerGroupDTO consumerGroupDTO = new ConsumerGroupDTO(cluster.getId(), offsetResetModel.getConsumerGroup(), OffsetStoreLocation.getOffsetStoreLocation(offsetResetModel.getLocation()));

        List<Result> resultList = consumerService.resetConsumerOffset(cluster, offsetResetModel.getTopicName(), consumerGroupDTO, partitionOffsetDTOList);
        for (Result result: resultList) {
            if (!StatusCode.SUCCESS.equals(result.getCode())) {
                return new Result<>(StatusCode.OPERATION_ERROR, resultList, "operator failed");
            }
        }
        return new Result<>(resultList);
    }
}
