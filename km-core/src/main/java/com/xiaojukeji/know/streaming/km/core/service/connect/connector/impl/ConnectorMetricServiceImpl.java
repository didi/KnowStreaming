package com.xiaojukeji.know.streaming.km.core.service.connect.connector.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect.MetricsConnectorsDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorTaskMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.connect.ConnectorMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionConnectJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BrokerMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.connect.ConnectorMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.cache.CollectedMetricsLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseConnectMetricService;
import com.xiaojukeji.know.streaming.km.persistence.connect.ConnectJMXClient;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.connector.ConnectorMetricESDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;

/**
 * @author didi
 */
@Service
public class ConnectorMetricServiceImpl extends BaseConnectMetricService implements ConnectorMetricService {
    protected static final ILog LOGGER = LogFactory.getLog(ConnectorMetricServiceImpl.class);

    public static final String CONNECTOR_METHOD_DO_NOTHING                               = "doNothing";

    public static final String CONNECTOR_METHOD_GET_CONNECT_WORKER_METRIC_SUM            = "getConnectWorkerMetricSum";

    public static final String CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG           = "getConnectorTaskMetricsAvg";

    public static final String CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_MAX           = "getConnectorTaskMetricsMax";

    public static final String CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM           = "getConnectorTaskMetricsSum";

    public static final String CONNECTOR_METHOD_GET_METRIC_HEALTH_SCORE                  = "getMetricHealthScore";

    public static final String CONNECTOR_METHOD_GET_METRIC_RUNNING_STATUS                = "getMetricRunningStatus";

    @Autowired
    private ConnectorMetricESDAO connectorMetricESDAO;

    @Autowired
    private ConnectJMXClient connectJMXClient;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private WorkerConnectorService workerConnectorService;

    @Autowired
    private HealthStateService healthStateService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_CONNECT_CONNECTOR;
    }

    @Override
    protected List<String> listMetricPOFields() {
        return BeanUtil.listBeanFields(BrokerMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler() {
        registerVCHandler(CONNECTOR_METHOD_DO_NOTHING, this::doNothing);
        registerVCHandler(CONNECTOR_METHOD_GET_CONNECT_WORKER_METRIC_SUM,   this::getConnectWorkerMetricSum);
        registerVCHandler(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_AVG,  this::getConnectorTaskMetricsAvg);
        registerVCHandler(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_MAX,  this::getConnectorTaskMetricsMax);
        registerVCHandler(CONNECTOR_METHOD_GET_CONNECTOR_TASK_METRICS_SUM,  this::getConnectorTaskMetricsSum);
        registerVCHandler(CONNECTOR_METHOD_GET_METRIC_HEALTH_SCORE,         this::getMetricHealthScore);
        registerVCHandler(CONNECTOR_METHOD_GET_METRIC_RUNNING_STATUS,       this::getMetricRunningStatus);
    }

    @Override
    public Result<ConnectorMetrics> collectConnectClusterMetricsFromKafkaWithCacheFirst(Long connectClusterPhyId, String connectorName, String metric) {
        String connectorMetricKey = CollectedMetricsLocalCache.genConnectorMetricCacheKey(connectClusterPhyId, connectorName, metric);
        Float  keyValue           = CollectedMetricsLocalCache.getConnectorMetrics(connectorMetricKey);

        if (null != keyValue) {
            return Result.buildSuc(new ConnectorMetrics(connectClusterPhyId, connectorName, metric, keyValue));
        }

        Result<ConnectorMetrics> ret = this.collectConnectClusterMetricsFromKafka(connectClusterPhyId, connectorName, metric);
        if (ret == null || !ret.hasData()) {
            return ret;
        }

        Map<String, Float> metricMap = ret.getData().getMetrics();
        for (Map.Entry<String, Float> entry : metricMap.entrySet()) {
            CollectedMetricsLocalCache.putConnectorMetrics(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    @Override
    public Result<ConnectorMetrics> collectConnectClusterMetricsFromKafka(Long connectClusterPhyId, String connectorName, String metricName) {
        try {
            ConnectorMetricParam metricParam = new ConnectorMetricParam(connectClusterPhyId, connectorName, metricName, null);
            return (Result<ConnectorMetrics>) doVCHandler(connectClusterPhyId, metricName, metricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<ConnectorMetrics> collectConnectClusterMetricsFromKafka(Long connectClusterPhyId, String connectorName, String metricName, ConnectorTypeEnum connectorType) {
        try {
            ConnectorMetricParam metricParam = new ConnectorMetricParam(connectClusterPhyId, connectorName, metricName, connectorType);
            return (Result<ConnectorMetrics>) doVCHandler(connectClusterPhyId, metricName, metricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<List<MetricMultiLinesVO>> listConnectClusterMetricsFromES(Long clusterPhyId, MetricsConnectorsDTO dto) {
        Long         startTime                      = dto.getStartTime();
        Long         endTime                        = dto.getEndTime();
        Integer      topN                           = dto.getTopNu();
        String       aggType                        = dto.getAggType();
        List<String> metricNameList                 = dto.getMetricsNames();

        List<Tuple<Long, String>> connectorList     = new ArrayList<>();
        if(!CollectionUtils.isEmpty(dto.getConnectorNameList())){
            connectorList = dto.getConnectorNameList().stream()
                                .map(c -> new Tuple<>(c.getConnectClusterId(), c.getConnectorName()))
                                .collect(Collectors.toList());
        }

        Table<String/*metric*/, Tuple<Long, String>, List<MetricPointVO>> retTable;
        if(ValidateUtils.isEmptyList(connectorList)) {
            // 按照TopN的方式去获取
            List<Tuple<Long, String>> defaultConnectorList = this.listTopNConnectorList(clusterPhyId, topN);

            retTable = connectorMetricESDAO.listMetricsByTopN(clusterPhyId, defaultConnectorList, metricNameList, aggType, topN, startTime, endTime);
        } else {
            // 制定集群ID去获取
            retTable = connectorMetricESDAO.listMetricsByConnectors(clusterPhyId, metricNameList, aggType, connectorList, startTime, endTime);
        }

        return Result.buildSuc(this.metricMap2VO(clusterPhyId, retTable.rowMap()));
    }

    @Override
    public Result<List<ConnectorMetrics>> getLatestMetricsFromES(Long clusterPhyId, List<ClusterConnectorDTO> connectorNameList, List<String> metricsNames) {
        List<Tuple<Long, String>> connectClusterIdAndConnectorNameList = connectorNameList
                .stream()
                .map(elem -> new Tuple<>(elem.getConnectClusterId(), elem.getConnectorName()))
                .collect(Collectors.toList());

        List<ConnectorMetricPO> poList =
                connectorMetricESDAO.getConnectorLatestMetric(clusterPhyId, connectClusterIdAndConnectorNameList, metricsNames);

        return Result.buildSuc(ConvertUtil.list2List(poList, ConnectorMetrics.class));
    }

    @Override
    public Result<ConnectorMetrics> getLatestMetricsFromES(Long connectClusterId, String connectorName, List<String> metricsNames) {
        ConnectorMetricPO connectorMetricPO = connectorMetricESDAO.getConnectorLatestMetric(
                null, connectClusterId, connectorName, metricsNames);
        return Result.buildSuc(ConvertUtil.obj2Obj(connectorMetricPO, ConnectorMetrics.class));
    }

    @Override
    public boolean isMetricName(String str) {
        return super.isMetricName(str);
    }

    /**************************************************** private method ****************************************************/
    private Result<ConnectorMetrics> doNothing(VersionItemParam metricParam){
        ConnectorMetricParam param = (ConnectorMetricParam) metricParam;
        return Result.buildSuc(new ConnectorMetrics(param.getConnectClusterId(), param.getConnectorName()));
    }

    private Result<ConnectorMetrics> getMetricHealthScore(VersionItemParam metricParam) {
        ConnectorMetricParam param          = (ConnectorMetricParam) metricParam;
        Long connectClusterId               = param.getConnectClusterId();
        String connectorName                = param.getConnectorName();

        ConnectorMetrics metrics = healthStateService.calConnectorHealthMetrics(connectClusterId, connectorName);
        return Result.buildSuc(metrics);
    }

    private Result<ConnectorMetrics> getMetricRunningStatus(VersionItemParam metricParam) {
        ConnectorMetricParam param          = (ConnectorMetricParam) metricParam;
        Long connectClusterId               = param.getConnectClusterId();
        String connectorName                = param.getConnectorName();
        String metricName                   = param.getMetricName();

        ConnectorPO connector = connectorService.getConnectorFromDB(connectClusterId, connectorName);
        if (connector == null) {
            return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName, metricName, (float)ConnectStatusEnum.UNKNOWN.getStatus()));
        }

        return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName, metricName, (float)ConnectStatusEnum.getByValue(connector.getState()).getStatus()));
    }

    private Result<ConnectorMetrics> getConnectWorkerMetricSum(VersionItemParam metricParam) {
        ConnectorMetricParam param          = (ConnectorMetricParam) metricParam;
        Long connectClusterId               = param.getConnectClusterId();
        String connectorName                = param.getConnectorName();
        String metric                       = param.getMetricName();
        ConnectorTypeEnum connectorType     = param.getConnectorType();

        float sum = 0;
        boolean isCollected = false;
        //根据connectClusterId获取connectMemberId列表
        List<String> workerIdList = workerService.listFromDB(connectClusterId).stream().map(elem -> elem.getWorkerId()).collect(Collectors.toList());
        for (String workerId : workerIdList) {
            Result<ConnectorMetrics> ret = this.getConnectorMetric(connectClusterId, workerId, connectorName, metric, connectorType);

            if (ret == null || !ret.hasData() || ret.getData().getMetric(metric) == null) {
                continue;
            }

            isCollected = true;
            sum += ret.getData().getMetric(metric);
        }
        if (!isCollected) {
            return Result.buildFailure(NOT_EXIST);
        }

        return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName, metric, sum));
    }

    //kafka.connect:type=connect-worker-metrics,connector="{connector}" 指标
    private Result<ConnectorMetrics> getConnectorMetric(Long connectClusterId, String workerId, String connectorName, String metric, ConnectorTypeEnum connectorType) {
        VersionConnectJmxInfo jmxInfo = getJMXInfo(connectClusterId, metric);
        if (null == jmxInfo) {
            return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);
        }

        if (jmxInfo.getType() != null) {
            if (connectorType == null) {
                connectorType = connectorService.getConnectorType(connectClusterId, connectorName);
            }

            if (connectorType != jmxInfo.getType()) {
                return Result.buildFailure(VC_JMX_INSTANCE_NOT_FOUND);
            }
        }

        String jmxObjectName = String.format(jmxInfo.getJmxObjectName(), connectorName);

        JmxConnectorWrap jmxConnectorWrap = connectJMXClient.getClientWithCheck(connectClusterId, workerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)) {
            return Result.buildFailure(VC_JMX_INIT_ERROR);
        }

        try {
            //2、获取jmx指标
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxObjectName), jmxInfo.getJmxAttribute()).toString();
            return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName, metric, Float.valueOf(value)));
        }  catch (InstanceNotFoundException e) {
            // 忽略该错误，该错误出现的原因是该指标在JMX中不存在
            return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName));
        } catch (Exception e) {
            LOGGER.error("method=getConnectorMetric||connectClusterId={}||workerId={}||connectorName={}||metrics={}||jmx={}||msg={}",
                    connectClusterId, workerId, connectorName, metric, jmxObjectName, e.getClass().getName());
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }


    private Result<ConnectorMetrics> getConnectorTaskMetricsAvg(VersionItemParam metricParam){
        ConnectorMetricParam param          = (ConnectorMetricParam) metricParam;
        Long connectClusterId               = param.getConnectClusterId();
        String connectorName                = param.getConnectorName();
        String metric                       = param.getMetricName();
        ConnectorTypeEnum connectorType     = param.getConnectorType();

        Result<List<ConnectorTaskMetrics>> ret = this.getConnectorTaskMetricList(connectClusterId, connectorName, metric, connectorType);
        if (ret == null || !ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        Float sum = ret.getData().stream().map(elem -> elem.getMetric(metric)).reduce(Float::sum).get();
        return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName, metric, sum / ret.getData().size()));
    }

    private Result<ConnectorMetrics> getConnectorTaskMetricsMax(VersionItemParam metricParam){
        ConnectorMetricParam param          = (ConnectorMetricParam) metricParam;
        Long connectClusterId               = param.getConnectClusterId();
        String connectorName                = param.getConnectorName();
        String metric                       = param.getMetricName();
        ConnectorTypeEnum connectorType     = param.getConnectorType();

        Result<List<ConnectorTaskMetrics>> ret = this.getConnectorTaskMetricList(connectClusterId, connectorName, metric, connectorType);
        if (ret == null || !ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        Float max = ret.getData().stream().max((a, b) -> a.getMetric(metric).compareTo(b.getMetric(metric))).get().getMetric(metric);
        return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName, metric, max));
    }

    private Result<ConnectorMetrics> getConnectorTaskMetricsSum(VersionItemParam metricParam){
        ConnectorMetricParam param          = (ConnectorMetricParam) metricParam;
        Long connectClusterId               = param.getConnectClusterId();
        String connectorName                = param.getConnectorName();
        String metric                       = param.getMetricName();
        ConnectorTypeEnum connectorType     = param.getConnectorType();

        Result<List<ConnectorTaskMetrics>> ret = this.getConnectorTaskMetricList(connectClusterId, connectorName, metric, connectorType);
        if (ret == null || !ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        Float sum = ret.getData().stream().map(elem -> elem.getMetric(metric)).reduce(Float::sum).get();
        return Result.buildSuc(new ConnectorMetrics(connectClusterId, connectorName, metric, sum));
    }


    private Result<List<ConnectorTaskMetrics>> getConnectorTaskMetricList(Long connectClusterId, String connectorName, String metricName, ConnectorTypeEnum connectorType) {
        List<ConnectorTaskMetrics> connectorTaskMetricsList = new ArrayList<>();
        List<WorkerConnector> workerConnectorList = workerConnectorService.listFromDB(connectClusterId).stream().filter(elem -> elem.getConnectorName().equals(connectorName)).collect(Collectors.toList());

        if (workerConnectorList.isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        for (WorkerConnector workerConnector : workerConnectorList) {
            Result<ConnectorTaskMetrics> ret = getConnectorTaskMetric(connectClusterId, workerConnector.getWorkerId(), connectorName, workerConnector.getTaskId(), metricName, connectorType);

            if (ret == null || !ret.hasData() || ret.getData().getMetric(metricName) == null) {
                continue;
            }

            connectorTaskMetricsList.add(ret.getData());
        }
        return Result.buildSuc(connectorTaskMetricsList);
    }


    private Result<ConnectorTaskMetrics> getConnectorTaskMetric(Long connectClusterId, String workerId, String connectorName, Integer taskId, String metric, ConnectorTypeEnum connectorType) {
        VersionConnectJmxInfo jmxInfo = getJMXInfo(connectClusterId, metric);
        if (null == jmxInfo) {
            return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);
        }

        if (jmxInfo.getType() != null) {
            if (connectorType == null) {
                connectorType = connectorService.getConnectorType(connectClusterId, connectorName);
            }

            if (connectorType != jmxInfo.getType()) {
                return Result.buildFailure(VC_JMX_INSTANCE_NOT_FOUND);
            }
        }

        String jmxObjectName=String.format(jmxInfo.getJmxObjectName(), connectorName, taskId);

        JmxConnectorWrap jmxConnectorWrap = connectJMXClient.getClientWithCheck(connectClusterId, workerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)) {
            return Result.buildFailure(VC_JMX_INIT_ERROR);
        }

        try {
            //2、获取jmx指标
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxObjectName), jmxInfo.getJmxAttribute()).toString();
            return Result.buildSuc(new ConnectorTaskMetrics(connectClusterId, connectorName, taskId, metric, Float.valueOf(value)));
        } catch (Exception e) {
            LOGGER.error("method=getConnectorTaskMetric||connectClusterId={}||workerId={}||connectorName={}||taskId={}||metrics={}||jmx={}||msg={}",
                    connectClusterId, workerId, connectorName, taskId, metric, jmxObjectName, e.getClass().getName());
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }

    private List<Tuple<Long, String>> listTopNConnectorList(Long clusterPhyId, Integer topN) {
        List<ConnectorPO> poList = connectorService.listByKafkaClusterIdFromDB(clusterPhyId);

        if (CollectionUtils.isEmpty(poList)) {
            return new ArrayList<>();
        }

        return poList.subList(0, Math.min(topN, poList.size()))
                .stream()
                .map( c -> new Tuple<>(c.getId(), c.getConnectorName()) )
                .collect(Collectors.toList());
    }

    protected List<MetricMultiLinesVO> metricMap2VO(Long connectClusterId,
                                                    Map<String/*metric*/, Map<Tuple<Long, String>, List<MetricPointVO>>> map){
        List<MetricMultiLinesVO> multiLinesVOS = new ArrayList<>();
        if (map == null || map.isEmpty()) {
            // 如果为空，则直接返回
            return multiLinesVOS;
        }

        for(String metric : map.keySet()){
            try {
                MetricMultiLinesVO multiLinesVO = new MetricMultiLinesVO();
                multiLinesVO.setMetricName(metric);

                List<MetricLineVO> metricLines = new ArrayList<>();

                Map<Tuple<Long, String>, List<MetricPointVO>> metricPointMap = map.get(metric);
                if(null == metricPointMap || metricPointMap.isEmpty()){continue;}

                for(Map.Entry<Tuple<Long, String>, List<MetricPointVO>> entry : metricPointMap.entrySet()){
                    MetricLineVO metricLineVO = new MetricLineVO();
                    metricLineVO.setName(entry.getKey().getV1() + "#" + entry.getKey().getV2());
                    metricLineVO.setMetricName(metric);
                    metricLineVO.setMetricPoints(entry.getValue());

                    metricLines.add(metricLineVO);
                }

                multiLinesVO.setMetricLines(metricLines);
                multiLinesVOS.add(multiLinesVO);
            }catch (Exception e){
                LOGGER.error("method=metricMap2VO||connectClusterId={}||msg=exception!", connectClusterId, e);
            }
        }

        return multiLinesVOS;
    }
}
