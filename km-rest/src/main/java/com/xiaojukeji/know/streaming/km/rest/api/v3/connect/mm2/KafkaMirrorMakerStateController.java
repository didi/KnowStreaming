package com.xiaojukeji.know.streaming.km.rest.api.v3.connect.mm2;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.connect.mm2.MirrorMakerManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2.MirrorMakerCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerBaseStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.task.KCTaskOverviewVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @author zengqiao
 * @date 22/12/12
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "MM2-MM2状态-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_MM2_PREFIX)
public class KafkaMirrorMakerStateController {
    @Autowired
    private MirrorMakerManager mirrorMakerManager;

    @Autowired
    private MirrorMakerMetricService mirrorMakerMetricService;

    @ApiOperation(value = "获取mm2任务的状态", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connectors/{connectorName}/state")
    @ResponseBody
    public Result<MirrorMakerBaseStateVO> getMirrorMakerStateVO(@PathVariable Long connectClusterId, @PathVariable String connectorName) {
        return mirrorMakerManager.getMirrorMakerState(connectClusterId, connectorName);
    }

    @ApiOperation(value = "获取MM2的Task列表", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connectors/{connectorName}/tasks")
    @ResponseBody
    public Result<Map<String, List<KCTaskOverviewVO>>> getConnectorTasks(@PathVariable Long connectClusterId, @PathVariable String connectorName) {
        return mirrorMakerManager.getTaskOverview(connectClusterId, connectorName);
    }

    @ApiOperation(value = "MM2配置", notes = "")
    @GetMapping(value ="clusters/{connectClusterId}/connectors/{connectorName}/config")
    @ResponseBody
    public Result<List<Properties>> getMM2Configs(@PathVariable Long connectClusterId, @PathVariable String connectorName) {
        return mirrorMakerManager.getMM2Configs(connectClusterId, connectorName);
    }

    @ApiOperation(value = "Connector近期指标")
    @PostMapping(value = "clusters/{connectClusterId}/connectors/{mirrorMakerName}/latest-metrics")
    @ResponseBody
    public Result<MirrorMakerMetrics> getMirrorMakerLatestMetrics(@PathVariable Long connectClusterId,
                                                                  @PathVariable String mirrorMakerName,
                                                                  @RequestBody List<String> metricsNames) {
        return mirrorMakerMetricService.getLatestMetricsFromES(connectClusterId, mirrorMakerName, metricsNames);
    }

}
