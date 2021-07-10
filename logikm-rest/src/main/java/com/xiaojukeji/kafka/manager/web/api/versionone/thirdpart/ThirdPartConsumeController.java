package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.bizenum.ConsumeHealthEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.SystemCodeConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.openapi.common.dto.ConsumeHealthDTO;
import com.xiaojukeji.kafka.manager.openapi.common.dto.OffsetResetDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupDetailVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.openapi.ThirdPartService;
import com.xiaojukeji.kafka.manager.openapi.common.vo.ConsumeHealthVO;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.web.converters.ConsumerModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/10/12
 */
@Api(tags = "开放接口-Consumer相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_THIRD_PART_PREFIX)
public class ThirdPartConsumeController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThirdPartConsumeController.class);

    @Autowired
    private AppService appService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private ThirdPartService thirdPartService;

    private static final List<String> WHITE_SYS_CODES_LIST = Arrays.asList(SystemCodeConstant.KAFKA_MANAGER);

    @ApiOperation(value = "消费组健康", notes = "消费组是否健康")
    @RequestMapping(value = "clusters/consumer-health", method = RequestMethod.POST)
    @ResponseBody
    public Result<ConsumeHealthVO> checkConsumeHealth(@RequestBody ConsumeHealthDTO dto) {
        LOGGER.info("");
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        Result<ConsumeHealthEnum> subEnumResult = null;
        for (String topicName: dto.getTopicNameList()) {
            subEnumResult = thirdPartService.checkConsumeHealth(
                    dto.getClusterId(),
                    topicName,
                    dto.getConsumerGroup(),
                    dto.getMaxDelayTime()
            );
            if (!Constant.SUCCESS.equals(subEnumResult.getCode())) {
                return new Result<>(subEnumResult.getCode(), subEnumResult.getMessage());
            }
        }
        if (ValidateUtils.isNull(subEnumResult)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return new Result<>(new ConsumeHealthVO(subEnumResult.getData().getCode()));
    }

    @ApiOperation(value = "重置消费组", notes = "")
    @RequestMapping(value = "consumers/offsets", method = RequestMethod.PUT)
    @ResponseBody
    public Result<List<Result>> resetOffsets(@RequestBody OffsetResetDTO dto) {
        LOGGER.info("rest offset, req:{}.", dto);
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        ClusterDO clusterDO = clusterService.getById(dto.getClusterId());
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        if (!WHITE_SYS_CODES_LIST.contains(dto.getSystemCode())) {
            // 检查AppID权限
            if (!appService.verifyAppIdByPassword(dto.getAppId(), dto.getPassword())) {
                return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
            }
            // 检查权限
            AuthorityDO authority =
                authorityService.getAuthority(dto.getClusterId(), dto.getTopicName(), dto.getAppId());
            if (ValidateUtils.isNull(authority) || (authority.getAccess() & 1) <= 0) {
                authority = authorityService.getAuthority(dto.getClusterId(), "*", dto.getAppId());
            }
            if (authority == null || (authority.getAccess() & 1) <= 0) {
                return Result.buildFrom(ResultStatus.USER_WITHOUT_AUTHORITY);
            }
        }

        List<Result> resultList = thirdPartService.resetOffsets(clusterDO, dto);
        if (ValidateUtils.isNull(resultList)) {
            return Result.buildFrom(ResultStatus.OPERATION_FAILED);
        }
        for (Result result: resultList) {
            if (!Constant.SUCCESS.equals(result.getCode())) {
                return Result.buildFrom(ResultStatus.OPERATION_FAILED);
            }
        }
        return new Result<>(resultList);
    }

    @ApiOperation(value = "查询消费组的消费详情", notes = "")
    @RequestMapping(value = "{physicalClusterId}/consumers/{consumerGroup}/topics/{topicName}/consume-details",
            method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ConsumerGroupDetailVO>> getConsumeDetail(@PathVariable Long physicalClusterId,
                                                                @PathVariable String consumerGroup,
                                                                @PathVariable String topicName,
                                                                @RequestParam("location") String location) {
        if (ValidateUtils.isNull(location)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

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
}