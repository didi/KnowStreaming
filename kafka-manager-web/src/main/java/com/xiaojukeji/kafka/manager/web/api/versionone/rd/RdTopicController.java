package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.RdTopicBasic;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.RdTopicBasicVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.TopicBrokerVO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.TopicModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/31
 */
@Api(tags = "RD-Topic相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdTopicController {
    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicManagerService topicManagerService;

    @ApiOperation(value = "TopicBroker信息", notes = "")
    @RequestMapping(value = "{clusterId}/topics/{topicName}/brokers", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicBrokerVO>> getTopicBrokers(
            @PathVariable Long clusterId,
            @PathVariable String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, topicName)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        return new Result<>(TopicModelConverter.convert2TopicBrokerVO(
                physicalClusterId,
                topicService.getTopicBrokerList(physicalClusterId, topicName))
        );
    }

    @ApiOperation(value = "查询Topic信息", notes = "")
    @RequestMapping(value = "{physicalClusterId}/topics/{topicName}/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<RdTopicBasicVO> getTopicBasic(@PathVariable Long physicalClusterId,
                                                @PathVariable String topicName) {
        Result<RdTopicBasic> result = topicManagerService.getRdTopicBasic(physicalClusterId, topicName);
        if (!Constant.SUCCESS.equals(result.getCode())) {
            return new Result<>(result.getCode(), result.getMessage());
        }
        RdTopicBasicVO vo = new RdTopicBasicVO();
        CopyUtils.copyProperties(vo, result.getData());
        vo.setProperties(result.getData().getProperties());
        vo.setRegionNameList(result.getData().getRegionNameList());
        return new Result<>(vo);
    }
}