package com.xiaojukeji.kafka.manager.web.api.versionone.gateway;

import com.xiaojukeji.kafka.manager.common.annotations.ApiLevel;
import com.xiaojukeji.kafka.manager.common.constant.ApiLevelContent;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicReportDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicReportService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/7
 */
@Api(tags = "GATEWAY-开启JMX上报相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.GATEWAY_API_V1_PREFIX)
public class GatewayReportController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayReportController.class);

    @Autowired
    private TopicReportService topicReportService;

    @ApiLevel(level = ApiLevelContent.LEVEL_IMPORTANT_2)
    @ApiOperation(value = "查询开启JMX采集的Topic", notes = "")
    @RequestMapping(value = "report/jmx/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result getJmxReportTopics(@RequestParam("clusterId") Long clusterId) {
        List<TopicReportDO> doList = topicReportService.getNeedReportTopic(clusterId);
        if (ValidateUtils.isNull(doList)) {
            doList = new ArrayList<>();
        }
        List<String> topicNameList = new ArrayList<>();
        for (TopicReportDO elem: doList) {
            topicNameList.add(elem.getTopicName());
        }
        return Result.buildSuc(ListUtils.strList2String(topicNameList));
    }
}