package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsClusterPhyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiValuePointVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author zengqiao
 * @date 22/02/21
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群Metrics-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ClusterMetricsController {

    @Autowired
    private ClusterMetricService clusterMetricService;

    @ApiOperation(value = "物理集群-最近指标", notes = "")
    @PostMapping(value = "physical-clusters/{clusterPhyId}/latest-metrics")
    @ResponseBody
    public Result<BaseMetrics> getLatestClusterMetrics(@PathVariable Long clusterPhyId, @RequestBody List<String> metricsNames) {
        Result<ClusterMetrics> metricsResult =  clusterMetricService.getLatestMetricsFromES(clusterPhyId, metricsNames);
        if (metricsResult.failed()) {
            return Result.buildFromIgnoreData(metricsResult);
        }

        return Result.buildSuc(metricsResult.getData());
    }

    @ApiOperation(value = "物理集群-指标时刻信息", notes = "获取时间区间的单点值")
    @PostMapping(value = "physical-clusters/{clusterPhyId}/metric-points")
    @ResponseBody
    public Result<List<MetricPointVO>> getClusterMetricPoints(@PathVariable Long clusterPhyId, @RequestBody MetricDTO dto) {
        return clusterMetricService.getMetricPointsFromES(clusterPhyId, dto);
    }

    @ApiOperation(value = "物理集群-多指标历史信息(单点单指标)", notes = "多条指标线")
    @PostMapping(value = "physical-clusters/metrics")
    @ResponseBody
    public Result<List<MetricMultiLinesVO>> getClusterPhyMetrics(@RequestBody MetricsClusterPhyDTO param) {
        return clusterMetricService.listClusterMetricsFromES(param);
    }

    @ApiOperation(value = "物理集群-多指标历史信息(单点多指标)", notes = "一条指标线每个指标点包含多个指标值")
    @PostMapping(value = "physical-clusters/metrics-multi-value")
    @ResponseBody
    public Result<List<MetricMultiValuePointVO>> getClusterPhyMetricsMultiValues(
            @RequestBody MetricsClusterPhyDTO param) {
        return metricMultiLinesVO2MetricMultiValuePointVO(clusterMetricService.listClusterMetricsFromES(param));
    }

    private Result<List<MetricMultiValuePointVO>> metricMultiLinesVO2MetricMultiValuePointVO(
            Result<List<MetricMultiLinesVO>> ret){

        if(ret.failed()){return Result.buildFromIgnoreData(ret);}

        List<MetricMultiValuePointVO> metricMultiValuePointVOS = new ArrayList<>();
        List<MetricMultiLinesVO>      metricMultiLinesVOS      = ret.getData();

        if(CollectionUtils.isEmpty(metricMultiLinesVOS)){
            return Result.buildSuc(metricMultiValuePointVOS);
        }

        Map<Long/*timestamp*/, Map<String/*metricName*/, String/*value*/>> map = new HashMap<>();

        for(MetricMultiLinesVO multiLinesVO : metricMultiLinesVOS){
            String       metricName   = multiLinesVO.getMetricName();

            if(CollectionUtils.isEmpty(multiLinesVO.getMetricLines())){
                continue;
            }

            //集群
            MetricLineVO metricLineVO = multiLinesVO.getMetricLines().get(0);

            List<MetricPointVO> metricPoints = metricLineVO.getMetricPoints();

            for(MetricPointVO metricPointVO : metricPoints){
                Long timestamp = metricPointVO.getTimeStamp();
                String value   = metricPointVO.getValue();

                Map<String/*metricName*/, String/*value*/> metricMap
                        = (null == map.get(timestamp))
                          ? new HashMap<>()
                          : map.get(timestamp);

                metricMap.put(metricName, value);
                map.put(timestamp, metricMap);
            }
        }

        for(Long timestamp : map.keySet()){
            MetricMultiValuePointVO metricMultiValuePointVO = new MetricMultiValuePointVO();
            metricMultiValuePointVO.setTimeStamp(timestamp);
            metricMultiValuePointVO.setValues(map.get(timestamp));

            metricMultiValuePointVOS.add(metricMultiValuePointVO);
        }

        return Result.buildSuc(metricMultiValuePointVOS);
    }
}