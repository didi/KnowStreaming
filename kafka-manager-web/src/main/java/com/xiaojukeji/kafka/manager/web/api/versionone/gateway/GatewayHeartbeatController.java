package com.xiaojukeji.kafka.manager.web.api.versionone.gateway;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.annotations.ApiLevel;
import com.xiaojukeji.kafka.manager.common.constant.ApiLevelContent;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result receiveTopicConnections(@RequestParam("clusterId") Long clusterId,
                                          @RequestParam("brokerId") Integer brokerId,
                                          @RequestBody JSONObject jsonObject) {
        try {
            if (ValidateUtils.isNull(jsonObject) || jsonObject.isEmpty()) {
                return Result.buildSuc();
            }
            topicConnectionService.batchAdd(JsonUtils.parseTopicConnections(clusterId, jsonObject));
            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error("receive topic connections failed, clusterId:{} brokerId:{} req:{}", clusterId, brokerId, jsonObject, e);
        }
        return Result.buildFailure("fail");
    }
}