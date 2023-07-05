package com.xiaojukeji.know.streaming.km.rest.api.v3.replica;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.replica.ReplicaMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "ReplicaMetrics-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ReplicaMetricsController {
    @Autowired
    private ReplicaMetricService replicationMetricService;

    @Deprecated
    @ApiOperation(value = "Replica指标-单个Replica")
    @PostMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/topics/{topicName}/partitions/{partitionId}/metric-points")
    @ResponseBody
    public Result<List<MetricPointVO>> getReplicaMetricPoints(@PathVariable Long clusterPhyId,
                                                              @PathVariable Integer brokerId,
                                                              @PathVariable String topicName,
                                                              @PathVariable Integer partitionId,
                                                              @RequestBody MetricDTO dto) {
        return Result.buildSuc();
    }

    @ApiOperation(value = "Replica指标-单个Replica")
    @PostMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/topics/{topicName}/partitions/{partitionId}/latest-metrics")
    @ResponseBody
    public Result<BaseMetrics> getReplicaMetricPoints(@PathVariable Long clusterPhyId,
                                                      @PathVariable Integer brokerId,
                                                      @PathVariable String topicName,
                                                      @PathVariable Integer partitionId,
                                                      @RequestBody List<String> metricsNames) {
        Result<ReplicationMetrics> metricsResult = replicationMetricService.collectReplicaMetricsFromKafka(clusterPhyId, topicName, partitionId, brokerId, metricsNames);
        if (metricsResult.failed()) {
            return Result.buildFromIgnoreData(metricsResult);
        }

        return Result.buildSuc(metricsResult.getData());
    }
}
