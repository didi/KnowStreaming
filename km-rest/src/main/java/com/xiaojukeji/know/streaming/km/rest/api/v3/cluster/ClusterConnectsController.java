package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.biz.cluster.ClusterConnectorsManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterConnectorsOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect.MetricsConnectClustersDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect.MetricsConnectorsDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect.ConnectClusterBasicCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect.ConnectClusterBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ClusterWorkerOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ConnectorBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ClusterConnectorOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect.ConnectStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.ConnectConverter;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/10/27
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群Connects-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX) // 这里使用 API_V3_PREFIX 没有使用 API_V3_CONNECT_PREFIX 的原因是这个接口在Kafka集群页面下
public class ClusterConnectsController {
    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectorMetricService connectorMetricService;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private ConnectClusterMetricService connectClusterMetricService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private ClusterConnectorsManager clusterConnectorsManager;


    /**************************************************** connect method ****************************************************/

    @ApiOperation(value = "Connect集群基本信息", notes = "")
    @GetMapping(value = "kafka-clusters/{clusterPhyId}/connect-clusters/{connectClusterName}/basic-combine-exist")
    @ResponseBody
    public Result<ConnectClusterBasicCombineExistVO> getBasicCombineExist(@PathVariable Long clusterPhyId,
                                                                          @PathVariable String connectClusterName) {
        return Result.buildSuc(ConnectConverter.convert2ConnectClusterBasicCombineExistVO(
                connectClusterService.getByName(clusterPhyId, connectClusterName))
        );
    }

    @ApiOperation(value = "Connect集群基本信息列表", notes = "")
    @GetMapping(value = "kafka-clusters/{clusterPhyId}/connect-clusters-basic")
    @ResponseBody
    public Result<List<ConnectClusterBasicVO>> getClusterConnectClustersBasic(@PathVariable Long clusterPhyId) {
        return Result.buildSuc(ConvertUtil.list2List(connectClusterService.listByKafkaCluster(clusterPhyId), ConnectClusterBasicVO.class));
    }

    @ApiOperation(value = "Connect集群指标信息")
    @PostMapping(value = "kafka-clusters/{clusterPhyId}/connect-cluster-metrics")
    @ResponseBody
    public Result<List<MetricMultiLinesVO>> getConnectClusterMetrics(@PathVariable Long clusterPhyId,
                                                                     @Validated @RequestBody MetricsConnectClustersDTO dto) {
        return connectClusterMetricService.listConnectClusterMetricsFromES(clusterPhyId, dto);
    }

    @ApiOperation(value = "集群Connectors状态", notes = "")
    @GetMapping(value = "kafka-clusters/{clusterPhyId}/connect-state")
    @ResponseBody
    public Result<ConnectStateVO> getClusterConnectorsState(@PathVariable Long clusterPhyId) {
        return Result.buildSuc(clusterConnectorsManager.getClusterConnectorsState(clusterPhyId));
    }


    /**************************************************** connector method ****************************************************/

    @ApiOperation(value = "Connectors基本信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/connectors-basic")
    @ResponseBody
    public Result<List<ConnectorBasicVO>> getClusterConnectorsBasic(@PathVariable Long clusterPhyId) {
        return Result.buildSuc(
                ConnectConverter.convert2BasicVOList(
                        connectClusterService.listByKafkaCluster(clusterPhyId),
                        connectorService.listByKafkaClusterIdFromDB(clusterPhyId)
                )
        );
    }

    @ApiOperation(value = "Connectors概览列表", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/connectors-overview")
    @ResponseBody
    public PaginationResult<ClusterConnectorOverviewVO> getClusterConnectorsOverview(@PathVariable Long clusterPhyId,
                                                                                     @Validated @RequestBody ClusterConnectorsOverviewDTO dto) {
        return clusterConnectorsManager.getClusterConnectorsOverview(clusterPhyId, dto);
    }

    @ApiOperation(value = "集群Connectors指标信息")
    @PostMapping(value = "clusters/{clusterPhyId}/connectors-metrics")
    @ResponseBody
    public Result<List<MetricMultiLinesVO>> getClusterPhyMetrics(@PathVariable Long clusterPhyId,
                                                                 @Validated @RequestBody MetricsConnectorsDTO dto) {
        return connectorMetricService.listConnectClusterMetricsFromES(clusterPhyId, dto);
    }

    /**************************************************** connector method ****************************************************/
    @ApiOperation(value = "worker概览列表", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/workers-overview")
    @ResponseBody
    public PaginationResult<ClusterWorkerOverviewVO> getClusterWorkersOverview(@PathVariable Long clusterPhyId, PaginationBaseDTO dto) {
        return workerService.pageWorkByKafkaClusterPhy(clusterPhyId, dto);
    }
}
