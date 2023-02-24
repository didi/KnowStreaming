package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.biz.connect.mm2.MirrorMakerManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterMirrorMakersOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.mm2.MetricsMirrorMakersDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.ClusterMirrorMakerOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant;
import com.xiaojukeji.know.streaming.km.common.converter.ConnectConverter;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 22/12/12
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群MM2s-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX) // 这里使用 API_V3_PREFIX 没有使用 API_V3_CONNECT_PREFIX 的原因是这个接口在Kafka集群页面下
public class ClusterMirrorMakersController {
    @Autowired
    private MirrorMakerMetricService mirrorMakerMetricService;

    @Autowired
    private MirrorMakerManager mirrorMakerManager;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private ConnectorService connectorService;

    @ApiOperation(value = "集群MM2状态", notes = "")
    @GetMapping(value = "kafka-clusters/{clusterPhyId}/mirror-makers-state")
    @ResponseBody
    public Result<MirrorMakerStateVO> getClusterMM2State(@PathVariable Long clusterPhyId) {
        return mirrorMakerManager.getMirrorMakerStateVO(clusterPhyId);
    }

    @ApiOperation(value = "集群MM2基本信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/mirror-makers-basic")
    @ResponseBody
    public Result<List<MirrorMakerBasicVO>> getClusterMirrorMakersBasic(@PathVariable Long clusterPhyId) {
        return Result.buildSuc(
                ConnectConverter.convert2MirrorMakerBasicVOList(
                        connectClusterService.listByKafkaCluster(clusterPhyId),
                        connectorService.listByKafkaClusterIdFromDB(clusterPhyId).stream().filter(elem -> elem.getConnectorClassName().equals(KafkaConnectConstant.MIRROR_MAKER_SOURCE_CONNECTOR_TYPE)).collect(Collectors.toList())
                )
        );
    }

    @ApiOperation(value = "集群MM2概览列表", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/mirror-makers-overview")
    @ResponseBody
    public PaginationResult<ClusterMirrorMakerOverviewVO> getClusterMirrorMakersOverview(@PathVariable Long clusterPhyId,
                                                                                         @Validated @RequestBody ClusterMirrorMakersOverviewDTO dto) {
        return mirrorMakerManager.getClusterMirrorMakersOverview(clusterPhyId,dto);
    }

    @ApiOperation(value = "集群MM2指标信息")
    @PostMapping(value = "clusters/{clusterPhyId}/mirror-makers-metrics")
    @ResponseBody
    public Result<List<MetricMultiLinesVO>> getClusterMirrorMakersMetrics(@PathVariable Long clusterPhyId,
                                                                          @Validated @RequestBody MetricsMirrorMakersDTO dto) {
        return mirrorMakerMetricService.listMirrorMakerClusterMetricsFromES(clusterPhyId, dto);
    }
}
