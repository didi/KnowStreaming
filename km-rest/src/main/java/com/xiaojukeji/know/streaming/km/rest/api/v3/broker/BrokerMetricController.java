package com.xiaojukeji.know.streaming.km.rest.api.v3.broker;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsBrokerDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "BrokerMetric-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class BrokerMetricController {

    @Autowired
    private BrokerMetricService brokerMetricService;

    @ApiOperation(value = "物理集群指标信息")
    @PostMapping(value = "clusters/{clusterPhyId}/broker-metrics")
    @ResponseBody
    public Result<List<MetricMultiLinesVO>> getBrokerMetricsOverview(
            @PathVariable Long clusterPhyId, @RequestBody MetricsBrokerDTO param) {
        return brokerMetricService.listBrokerMetricsFromES(clusterPhyId, param);
    }

    @ApiOperation(value = "Broker指标-单个Broker")
    @PostMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/metric-points")
    @ResponseBody
    public Result<List<MetricPointVO>> getBrokerMetricPoints(@PathVariable Long clusterPhyId,
                                                             @PathVariable Integer brokerId,
                                                             @RequestBody MetricDTO dto) {
        return brokerMetricService.getMetricPointsFromES(clusterPhyId, brokerId, dto);
    }

    @ApiOperation(value = "物理集群-最近指标", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/latest-metrics")
    @ResponseBody
    public Result<BaseMetrics> getLatestBrokerMetrics(@PathVariable Long clusterPhyId,
                                                      @PathVariable Integer brokerId,
                                                      @RequestBody List<String> metricsNames) {
        Result<BrokerMetrics> metricsResult =  brokerMetricService.getLatestMetricsFromES(clusterPhyId, brokerId);
        if (metricsResult.failed()) {
            return Result.buildFromIgnoreData(metricsResult);
        }

        return Result.buildSuc(metricsResult.getData());
    }
}
