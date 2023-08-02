package com.xiaojukeji.know.streaming.km.core.service.connect.cluster.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect.MetricsConnectClustersDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectWorkerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.connect.ConnectClusterMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BrokerMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.cache.CollectedMetricsLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseConnectMetricService;
import com.xiaojukeji.know.streaming.km.persistence.connect.ConnectJMXClient;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.cluster.ConnectClusterMetricESDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class ConnectClusterMetricServiceImpl extends BaseConnectMetricService implements ConnectClusterMetricService {
    protected static final ILog LOGGER = LogFactory.getLog(ConnectClusterMetricServiceImpl.class);

    public static final String CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_AVG = "getWorkerMetricAvg";

    public static final String CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM = "getWorkerMetricSum";

    public static final String CONNECT_CLUSTER_METHOD_DO_NOTHING            = "doNothing";

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private ConnectClusterMetricESDAO connectClusterMetricESDAO;

    @Autowired
    private ConnectJMXClient connectJMXClient;

    @Autowired
    private WorkerService workerService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_CONNECT_CLUSTER;
    }

    @Override
    protected List<String> listMetricPOFields() {
        return BeanUtil.listBeanFields(BrokerMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler() {
        registerVCHandler(CONNECT_CLUSTER_METHOD_DO_NOTHING, this::doNothing);
        registerVCHandler(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_AVG, this::getConnectWorkerMetricAvg);
        registerVCHandler(CONNECT_CLUSTER_METHOD_GET_WORKER_METRIC_SUM, this::getConnectWorkerMetricSum);
    }

    @Override
    public Result<ConnectClusterMetrics> collectConnectClusterMetricsFromKafkaWithCacheFirst(Long connectClusterPhyId, String metric) {
        String connectClusterMetricKey = CollectedMetricsLocalCache.genConnectClusterMetricCacheKey(connectClusterPhyId, metric);
        Float keyValue = CollectedMetricsLocalCache.getConnectClusterMetrics(connectClusterMetricKey);
        if (keyValue != null) {
            return Result.buildSuc(new ConnectClusterMetrics(connectClusterPhyId, metric, keyValue));
        }

        Result<ConnectClusterMetrics> ret = this.collectConnectClusterMetricsFromKafka(connectClusterPhyId, metric);
        if (ret == null || !ret.hasData()) {
            return ret;
        }

        Map<String, Float> metricsMap = ret.getData().getMetrics();
        for (Map.Entry<String, Float> entry : metricsMap.entrySet()) {
            CollectedMetricsLocalCache.putConnectClusterMetrics(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    @Override
    public Result<ConnectClusterMetrics> collectConnectClusterMetricsFromKafka( Long connectClusterPhyId, String metric) {
        try {
            ConnectClusterMetricParam metricParam = new ConnectClusterMetricParam(connectClusterPhyId, metric);
            return (Result<ConnectClusterMetrics>) doVCHandler(connectClusterPhyId, metric, metricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<List<MetricMultiLinesVO>> listConnectClusterMetricsFromES(Long clusterPhyId, MetricsConnectClustersDTO dto) {
        Long         startTime                      = dto.getStartTime();
        Long         endTime                        = dto.getEndTime();
        Integer      topN                           = dto.getTopNu();
        String       aggType                        = dto.getAggType();
        List<Long>   connectClusterIdList           = dto.getConnectClusterIdList();
        List<String> metricNameList                 = dto.getMetricsNames();

        Table<String, Long, List<MetricPointVO>> retTable;
        if (ValidateUtils.isEmptyList(connectClusterIdList)) {
            // 按照TopN的方式去获取
            List<Long> defaultConnectClusterIdList = this.listTopNConnectClusterIdList(clusterPhyId, topN);

            retTable = connectClusterMetricESDAO.listMetricsByTop(clusterPhyId, defaultConnectClusterIdList, metricNameList, aggType, topN, startTime, endTime);
        } else {
            // 制定集群ID去获取
            retTable = connectClusterMetricESDAO.listMetricsByConnectClusterIdList(clusterPhyId, metricNameList, aggType, connectClusterIdList, startTime, endTime);
        }

        return Result.buildSuc(this.metricMap2VO(clusterPhyId, retTable.rowMap()));
    }

    @Override
    public boolean isMetricName(String str) {
        return super.isMetricName(str);
    }

    /**************************************************** private method ****************************************************/
    private Result<ConnectClusterMetrics> doNothing(VersionItemParam metricParam) {
        ConnectClusterMetricParam param = (ConnectClusterMetricParam) metricParam;
        return Result.buildSuc(new ConnectClusterMetrics(null, param.getConnectClusterId()));
    }

    private Result<ConnectClusterMetrics> getConnectWorkerMetricAvg(VersionItemParam metricParam) {
        ConnectClusterMetricParam param = (ConnectClusterMetricParam) metricParam;
        Long connectClusterId = param.getConnectClusterId();
        String metric = param.getMetric();

        Result<List<ConnectWorkerMetrics>> ret = this.getConnectWorkerMetricsByJMX(connectClusterId, metric);
        if (ret == null || !ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        //求均值
        Float value = ret.getData().stream().map(elem -> elem.getMetric(metric) == null ? 0 : elem.getMetric(metric)).reduce(Float::sum).get();
        ConnectClusterMetrics connectClusterMetrics = new ConnectClusterMetrics(null, connectClusterId);
        connectClusterMetrics.putMetric(metric, value / ret.getData().size());
        return Result.buildSuc(connectClusterMetrics);
    }

    private Result<ConnectClusterMetrics> getConnectWorkerMetricSum(VersionItemParam metricParam) {
        ConnectClusterMetricParam param = (ConnectClusterMetricParam) metricParam;
        Long connectClusterId = param.getConnectClusterId();
        String metric = param.getMetric();

        Result<List<ConnectWorkerMetrics>> ret = this.getConnectWorkerMetricsByJMX(connectClusterId, metric);
        if (ret == null || !ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        //求和
        Float value = ret.getData().stream().map(elem -> elem.getMetric(metric) == null ? 0 : elem.getMetric(metric)).reduce(Float::sum).get();
        ConnectClusterMetrics connectClusterMetrics = new ConnectClusterMetrics(null, connectClusterId);
        connectClusterMetrics.putMetric(metric, value);
        return Result.buildSuc(connectClusterMetrics);
    }

    //获取workermetric列表
    private Result<List<ConnectWorkerMetrics>> getConnectWorkerMetricsByJMX(Long connectClusterId, String metric) {

        List<String> workerIdList = workerService.listFromDB(connectClusterId).stream().map(elem -> elem.getWorkerId()).collect(Collectors.toList());
        List<ConnectWorkerMetrics> workerMetricsList = new ArrayList<>();

        for (String workerId : workerIdList) {
            Result<ConnectWorkerMetrics> ret = this.getConnectWorkerMetricByJMX(connectClusterId, workerId, metric);
            if (ret == null || !ret.hasData() || ret.getData().getMetric(metric) == null) {
                continue;
            }
            workerMetricsList.add(ret.getData());
        }
        return Result.buildSuc(workerMetricsList);
    }

    private Result<ConnectWorkerMetrics> getConnectWorkerMetricByJMX(Long connectClusterId, String workerId, String metric) {
        VersionJmxInfo jmxInfo = getJMXInfo(connectClusterId, metric);
        if (null == jmxInfo) {
            return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);
        }

        JmxConnectorWrap jmxConnectorWrap = connectJMXClient.getClientWithCheck(connectClusterId, workerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)) {
            return Result.buildFailure(VC_JMX_INIT_ERROR);
        }
        try {
            //2、获取jmx指标
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName()), jmxInfo.getJmxAttribute()).toString();
            return Result.buildSuc(new ConnectWorkerMetrics(connectClusterId, workerId, metric, Float.valueOf(value)));
        } catch (Exception e) {
            LOGGER.error("method=getConnectWorkerMetricsByJMX||connectClusterId={}||workerId={}||metrics={}||jmx={}||msg={}",
                    connectClusterId, workerId, metric, jmxInfo.getJmxObjectName(), e.getClass().getName());
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }

    private List<Long> listTopNConnectClusterIdList(Long clusterPhyId, Integer topN) {
        List<ConnectCluster> connectClusters = connectClusterService.listByKafkaCluster(clusterPhyId);

        if (CollectionUtils.isEmpty(connectClusters)) {
            return new ArrayList<>();
        }

        return connectClusters.subList(0, Math.min(topN, connectClusters.size()))
                .stream()
                .map(b -> b.getId().longValue())
                .collect(Collectors.toList());
    }

    private List<MetricMultiLinesVO> metricMap2VO(Long connectClusterId,
                                                    Map<String/*metric*/, Map<Long, List<MetricPointVO>>> map){
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

                Map<Long, List<MetricPointVO>> metricPointMap = map.get(metric);
                if(null == metricPointMap || metricPointMap.isEmpty()){continue;}

                for(Map.Entry<Long, List<MetricPointVO>> entry : metricPointMap.entrySet()){
                    MetricLineVO metricLineVO = new MetricLineVO();
                    metricLineVO.setName(entry.getKey().toString());
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
