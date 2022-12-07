package com.xiaojukeji.know.streaming.km.rest.api.v3.zk;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/09/19
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "ZKMetrics-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ZookeeperMetricsController {
    @Autowired
    private ZookeeperMetricService zookeeperMetricService;

    @ApiOperation(value = "ZK-最近指标", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/zookeeper-latest-metrics")
    @ResponseBody
    public Result<BaseMetrics> getLatestMetrics(@PathVariable Long clusterPhyId, @RequestBody List<String> metricsNames) {
        Result<ZookeeperMetrics> metricsResult = zookeeperMetricService.batchCollectMetricsFromZookeeper(clusterPhyId, metricsNames);
        if (metricsResult.failed()) {
            return Result.buildFromIgnoreData(metricsResult);
        }

        return Result.buildSuc(metricsResult.getData());
    }

    @ApiOperation(value = "ZK-多指标历史信息", notes = "多条指标线")
    @PostMapping(value = "clusters/{clusterPhyId}/zookeeper-metrics")
    @ResponseBody
    public Result<List<MetricLineVO>> getMetricsLine(@PathVariable Long clusterPhyId, @RequestBody MetricDTO dto) {
        return zookeeperMetricService.listMetricsFromES(clusterPhyId, dto);
    }
}
