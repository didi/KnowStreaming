package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ExpertService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.ExpertConverter;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.expert.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/20
 */
@Api(tags = "OP-专家服务相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpExpertController {
    @Autowired
    private ExpertService expertService;

    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "热点Topic(Region内)", notes = "")
    @RequestMapping(value = "expert/regions/hot-topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<RegionHotTopicVO>> getRegionHotTopics() {
        return new Result<>(
                ExpertConverter.convert2RegionHotTopicVOList(expertService.getRegionHotTopics())
        );
    }

    @ApiOperation(value = "Topic分区不足", notes = "")
    @RequestMapping(value = "expert/topics/insufficient-partitions", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<PartitionInsufficientTopicVO>> getPartitionInsufficientTopics() {
        return new Result<>(ExpertConverter.convert2PartitionInsufficientTopicVOList(
                expertService.getPartitionInsufficientTopics()
        ));
    }

    @ApiOperation(value = "Topic流量异常诊断", notes = "")
    @RequestMapping(value = "expert/topics/anomaly-flow", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AnomalyFlowTopicVO>> getAnomalyFlowTopics(@RequestParam(value = "timestamp") Long timestamp) {
        return new Result<>(ExpertConverter.convert2AnomalyFlowTopicVOList(
                expertService.getAnomalyFlowTopics(timestamp)
        ));
    }

    @ApiOperation(value = "过期Topic", notes = "")
    @RequestMapping(value = "expert/topics/expired", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ExpiredTopicVO>> getExpiredTopics() {
        return new Result<>(ExpertConverter.convert2ExpiredTopicVOList(
                expertService.getExpiredTopics(),
                clusterService.list())
        );
    }
}