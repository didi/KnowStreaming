package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.JmxSwitchDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.TopicJmxSwitch;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 20/8/21
 */
@Api(tags = "RD-Jmx维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalJmxController {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @ApiOperation(value = "开启TopicJMX", notes="")
    @RequestMapping(value = "jmx-switch", method = RequestMethod.POST)
    @ResponseBody
    public Result openTopicJmxSwitch(@RequestBody JmxSwitchDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                dto.getClusterId(),
                dto.getPhysicalClusterId()
        );
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return zookeeperService.openTopicJmx(physicalClusterId, dto.getTopicName(), new TopicJmxSwitch(
                dto.getOpenTopicRequestMetrics(),
                dto.getOpenAppIdTopicMetrics(),
                dto.getOpenClientRequestMetrics()
        ));
    }
}