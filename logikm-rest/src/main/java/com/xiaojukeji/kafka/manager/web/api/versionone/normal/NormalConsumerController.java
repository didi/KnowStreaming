package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OffsetPosEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicOffsetResetDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupSummaryVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.ConsumerModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/4/8
 */
@Api(tags = "Normal-Consumer相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalConsumerController {
    private final static Logger LOGGER = LoggerFactory.getLogger(NormalConsumerController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @ApiOperation(value = "查询消费Topic的消费组", notes = "")
    @RequestMapping(value = "{clusterId}/consumers/{topicName}/consumer-groups", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ConsumerGroupSummaryVO>> getConsumeGroups(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(ConsumerModelConverter.convert2ConsumerGroupSummaryVOList(
                consumerService.getConsumerGroupSummaries(physicalClusterId, topicName)
        ));
    }

    @ApiOperation(value = "查询消费组的消费详情", notes = "")
    @RequestMapping(value = "{clusterId}/consumers/{consumerGroup}/topics/{topicName}/consume-details",
            method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ConsumerGroupDetailVO>> getConsumeDetail(
            @PathVariable Long clusterId,
            @PathVariable String consumerGroup,
            @PathVariable String topicName,
            @RequestParam("location") String location,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        if (ValidateUtils.isNull(location)) {

            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);

        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        location = location.toLowerCase();
        OffsetLocationEnum offsetStoreLocation = OffsetLocationEnum.getOffsetStoreLocation(location);
        if (ValidateUtils.isNull(offsetStoreLocation)) {
            return Result.buildFrom(ResultStatus.CG_LOCATION_ILLEGAL);
        }

        ConsumerGroup consumeGroup = new ConsumerGroup(clusterDO.getId(), consumerGroup, offsetStoreLocation);
        try {
            List<ConsumeDetailDTO> consumeDetailDTOList =
                    consumerService.getConsumeDetail(clusterDO, topicName, consumeGroup);
            return new Result<>(
                    ConsumerModelConverter.convert2ConsumerGroupDetailVO(
                            topicName,
                            consumerGroup,
                            location,
                            consumeDetailDTOList
                    )
            );
        } catch (Exception e) {
            LOGGER.error("get consume detail failed, consumerGroup:{}.", consumeGroup, e);
        }
        return Result.buildFrom(ResultStatus.OPERATION_FAILED);

    }

    @ApiOperation(value = "重置Topic消费偏移", notes = "")
    @RequestMapping(value = "consumers/offsets", method = RequestMethod.PUT)
    @ResponseBody
    public Result<List<Result>> resetConsumeOffsets(@RequestBody TopicOffsetResetDTO dto) {
        if (!dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                dto.getClusterId(),
                dto.getIsPhysicalClusterId()
        );
        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        List<PartitionOffsetDTO> offsetDTOList = getPartitionOffset(clusterDO, dto);
        if (ValidateUtils.isEmptyList(offsetDTOList)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        ConsumerGroup consumerGroup = new ConsumerGroup(physicalClusterId, dto.getConsumerGroup(), OffsetLocationEnum.getOffsetStoreLocation(dto.getLocation()));
        List<Result> resultList = consumerService.resetConsumerOffset(
                clusterDO,
                dto.getTopicName(),
                consumerGroup,
                offsetDTOList
        );
        for (Result result: resultList) {
            if (ResultStatus.SUCCESS.getCode() != result.getCode()) {
                return new Result<>(ResultStatus.OPERATION_FAILED.getCode(), resultList, "operator failed");
            }
        }
        return new Result<>(resultList);
    }

    private List<PartitionOffsetDTO> getPartitionOffset(ClusterDO clusterDO, TopicOffsetResetDTO dto) {
        OffsetPosEnum offsetPosEnum = OffsetPosEnum.getOffsetPosEnum(dto.getOffsetPos());
        if (!ValidateUtils.isNull(dto.getOffsetPos()) && !OffsetPosEnum.NONE.equals(offsetPosEnum)) {
            // 按照指定位置进行重置, 此时获取指定位置的offset
            List<PartitionOffsetDTO> offsetDTOList = new ArrayList<>();
            Map<TopicPartition, Long> offsetMap = topicService.getPartitionOffset(clusterDO, dto.getTopicName(), offsetPosEnum);
            for (Map.Entry<TopicPartition, Long> entry : offsetMap.entrySet()) {
                offsetDTOList.add(new PartitionOffsetDTO(entry.getKey().partition(), entry.getValue()));
            }
            return offsetDTOList;
        }

        // 指定offset
        if (!ValidateUtils.isEmptyList(dto.getOffsetList())) {
            return dto.getOffsetList();
        }

        // 获取指定时间点的offset
        if (!ValidateUtils.isNullOrLessThanZero(dto.getTimestamp())) {
            return topicService.getPartitionOffsetList(clusterDO, dto.getTopicName(), dto.getTimestamp());
        }
        return new ArrayList<>();
    }
}
