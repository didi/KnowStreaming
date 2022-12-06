package com.xiaojukeji.know.streaming.km.rest.api.v3.connect;

import com.xiaojukeji.know.streaming.km.biz.connect.connector.ConnectorManager;
import com.xiaojukeji.know.streaming.km.biz.connect.connector.WorkerConnectorManager;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ConnectorBasicCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.connector.ConnectorStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.task.KCTaskOverviewVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.ConnectConverter;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 22/10/17
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Connect-Connector状态-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_CONNECT_PREFIX)
public class KafkaConnectorStateController {

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectorMetricService connectorMetricService;

    @Autowired
    private WorkerConnectorManager workerConnectorManager;

    @Autowired
    private ConnectorManager connectorManager;

    @Autowired
    private ConnectClusterService connectClusterService;

    @ApiOperation(value = "Connectors基本信息", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connectors/{connectorName}/basic-combine-exist")
    @ResponseBody
    public Result<ConnectorBasicCombineExistVO> getConnectorBasicCombineExist(@PathVariable Long connectClusterId, @PathVariable String connectorName) {
        return Result.buildSuc(
                ConnectConverter.convert2BasicVO(
                        connectClusterService.getById(connectClusterId),
                        connectorService.getConnectorFromDB(connectClusterId, connectorName)
                )
        );
    }

    @ApiOperation(value = "Connector配置", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connectors/{connectorName}/config")
    @ResponseBody
    public Result<Properties> getConnectorConfig(@PathVariable Long connectClusterId, @PathVariable String connectorName) {
        Result<KSConnectorInfo> connectorResult = connectorService.getConnectorInfoFromCluster(connectClusterId, connectorName);
        if (connectorResult.failed()) {
            return Result.buildFromIgnoreData(connectorResult);
        }

        Properties props = new Properties();
        props.putAll(connectorResult.getData().getConfig());
        return Result.buildSuc(props);
    }

    @ApiOperation(value = "获取Connector的Task列表", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connectors/{connectorName}/tasks")
    @ResponseBody
    public Result<List<KCTaskOverviewVO>> getConnectorTasks(@PathVariable Long connectClusterId, @PathVariable String connectorName) {
        return workerConnectorManager.getTaskOverview(connectClusterId, connectorName);
    }

    @ApiOperation(value = "Connector近期指标")
    @PostMapping(value = "clusters/{connectClusterId}/connectors/{connectorName}/latest-metrics")
    @ResponseBody
    public Result<ConnectorMetrics> getConnectorLatestMetrics(@PathVariable Long connectClusterId,
                                                              @PathVariable String connectorName,
                                                              @RequestBody List<String> metricsNames) {
        return connectorMetricService.getLatestMetricsFromES(connectClusterId, connectorName, metricsNames);
    }

    @ApiOperation(value = "获取Connector的状态", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connectors/{connectorName}/state")
    @ResponseBody
    public Result<ConnectorStateVO> getConnectorStateVO(@PathVariable Long connectClusterId, @PathVariable String connectorName) {
        return connectorManager.getConnectorStateVO(connectClusterId, connectorName);
    }

}
