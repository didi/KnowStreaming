package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/14
 */
@Api(tags = "RD-通知相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdNotifyController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RdNotifyController.class);

    @ApiOperation(value = "Topic过期通知", notes = "")
    @RequestMapping(value = "notifications/topic-expired", method = RequestMethod.POST)
    @ResponseBody
    public Result createKafkaFile(List<ClusterTopicDTO> dataList) {
        return new Result();
    }
}