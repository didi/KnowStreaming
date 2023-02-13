package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.HaClusterTopicVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.HaClusterVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.HaClusterTopicHaStatusVO;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaASRelationManager;
import com.xiaojukeji.kafka.manager.service.service.ha.HaClusterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zengqiao
 * @date 20/4/23
 */
@Api(tags = "RD-HA-Cluster维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdHaClusterController {
    @Autowired
    private HaASRelationManager haASRelationManager;

    @Autowired
    private HaClusterService haClusterService;

    @ApiOperation(value = "集群-主备Topic列表", notes = "如果传入secondClusterId，则主备关系必须是firstClusterId与secondClusterId的Topic")
    @GetMapping(value = "clusters/{firstClusterId}/ha-topics")
    @ResponseBody
    public Result<List<HaClusterTopicVO>> getHATopics(@PathVariable Long firstClusterId,
                                                      @RequestParam(required = false) Long secondClusterId,
                                                      @RequestParam(required = false, defaultValue = "true") Boolean filterSystemTopics) {
        return Result.buildSuc(haASRelationManager.getHATopics(firstClusterId, secondClusterId, filterSystemTopics != null && filterSystemTopics));
    }

    @ApiOperation(value = "集群基本信息列表", notes = "含高可用集群信息")
    @GetMapping(value = "clusters/ha/basic-info")
    @ResponseBody
    public Result<List<HaClusterVO>> getClusterBasicInfo() {
        return haClusterService.listAllHA();
    }

    @ApiOperation(value = "集群Topic高可用状态信息", notes = "")
    @GetMapping(value = "clusters/{firstClusterId}/ha-topics/status")
    @ResponseBody
    public Result<List<HaClusterTopicHaStatusVO>> listHaStatusTopics(@PathVariable Long firstClusterId,
                                                                     @RequestParam(required = false, defaultValue = "true") Boolean checkMetadata) {
        return haASRelationManager.listHaStatusTopics(firstClusterId, checkMetadata);
    }
}
