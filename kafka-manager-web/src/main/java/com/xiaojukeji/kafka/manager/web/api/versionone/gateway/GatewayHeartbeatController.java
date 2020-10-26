package com.xiaojukeji.kafka.manager.web.api.versionone.gateway;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.annotations.ApiLevel;
import com.xiaojukeji.kafka.manager.common.constant.ApiLevelContent;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.TopicConnectionDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/6
 */
@Api(tags = "GATEWAY-WEB相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.GATEWAY_API_V1_PREFIX)
public class GatewayHeartbeatController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayHeartbeatController.class);

    @Autowired
    private TopicConnectionService topicConnectionService;

    @ApiLevel(level = ApiLevelContent.LEVEL_NORMAL_3)
    @ApiOperation(value = "连接信息上报入口", notes = "Broker主动上报信息")
    @RequestMapping(value = "heartbeat/survive-user", method = RequestMethod.POST)
    @ResponseBody
    public Result receiveTopicConnections(@RequestParam("clusterId") String clusterId,
                                          @RequestParam("brokerId") String brokerId,
                                          @RequestBody List<TopicConnectionDTO> dtoList) {
        try {
            if (ValidateUtils.isEmptyList(dtoList)) {
                return Result.buildSuc();
            }
            topicConnectionService.batchAdd(dtoList);
            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error("receive topic connections failed, clusterId:{} brokerId:{} req:{}",
                    clusterId, brokerId, JSON.toJSONString(dtoList), e);
        }
        return Result.buildFailure("fail");
    }
}