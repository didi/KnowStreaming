package com.xiaojukeji.know.streaming.km.core.service.zookeeper.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.ZookeeperMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.BaseFourLetterWordCmdData;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.ServerCmdData;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.parser.MonitorCmdDataParser;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.parser.ServerCmdDataParser;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.*;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.MonitorCmdData;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ZookeeperMetricPO;
import com.xiaojukeji.know.streaming.km.common.utils.zookeeper.FourLetterWordUtil;
import com.xiaojukeji.know.streaming.km.core.cache.ZookeeperLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseMetricService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperMetricService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.ZookeeperMetricESDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.VC_JMX_CONNECT_ERROR;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ZookeeperMetricVersionItems.*;


@Service
public class ZookeeperMetricServiceImpl extends BaseMetricService implements ZookeeperMetricService {
    private static final ILog LOGGER = LogFactory.getLog(ZookeeperMetricServiceImpl.class);

    public static final String ZOOKEEPER_METHOD_DO_NOTHING                          = "doNothing";
    public static final String ZOOKEEPER_METHOD_GET_METRIC_FROM_MONITOR_CMD         = "getMetricFromMonitorCmd";
    public static final String ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD          = "getMetricFromServerCmd";
    public static final String ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX        = "getMetricFromKafkaByJMX";
    public static final String ZOOKEEPER_METHOD_GET_METRIC_FROM_HEALTH_SERVICE      = "getMetricFromHealthService";

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private ZookeeperMetricESDAO zookeeperMetricESDAO;

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Autowired
    private HealthStateService healthStateService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_ZOOKEEPER;
    }

    @Override
    protected List<String> listMetricPOFields(){
        return BeanUtil.listBeanFields(ZookeeperMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler(){
        registerVCHandler( ZOOKEEPER_METHOD_DO_NOTHING,                           this::doNothing);
        registerVCHandler( ZOOKEEPER_METHOD_GET_METRIC_FROM_MONITOR_CMD,          this::getMetricFromMonitorCmd);
        registerVCHandler( ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD,           this::getMetricFromServerCmd);
        registerVCHandler( ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX,         this::getMetricFromKafkaByJMX);
        registerVCHandler( ZOOKEEPER_METHOD_GET_METRIC_FROM_HEALTH_SERVICE,       this::getMetricFromHealthService);
    }

    @Override
    public Result<ZookeeperMetrics> collectMetricsFromZookeeper(ZookeeperMetricParam param) {
        try {
            return (Result<ZookeeperMetrics>)doVCHandler(param.getClusterPhyId(), param.getMetricName(), param);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<ZookeeperMetrics> batchCollectMetricsFromZookeeper(Long clusterPhyId, List<String> metricNameList) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (null == clusterPhy) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        List<ZookeeperInfo> aliveZKList = zookeeperService.listFromDBByCluster(clusterPhyId).stream()
                .filter(elem -> Constant.ALIVE.equals(elem.getStatus()))
                .collect(Collectors.toList());

        if (ValidateUtils.isEmptyList(aliveZKList)) {
            // 没有指标可以获取
            return Result.buildSuc(new ZookeeperMetrics(clusterPhyId));
        }

        // 构造参数
        ZookeeperMetricParam param = new ZookeeperMetricParam(
                clusterPhyId,
                aliveZKList.stream().map(elem -> new Tuple<String, Integer>(elem.getHost(), elem.getPort())).collect(Collectors.toList()),
                ConvertUtil.str2ObjByJson(clusterPhy.getZkProperties(), ZKConfig.class),
                null
        );

        ZookeeperMetrics metrics = new ZookeeperMetrics(clusterPhyId);
        for(String metricName : metricNameList) {
            try {
                if(metrics.getMetrics().containsKey(metricName)) {
                    continue;
                }
                param.setMetricName(metricName);

                Result<ZookeeperMetrics> ret = this.collectMetricsFromZookeeper(param);
                if(null == ret || ret.failed() || null == ret.getData()){
                    continue;
                }

                metrics.putMetric(ret.getData().getMetrics());
            } catch (Exception e){
                LOGGER.error(
                        "method=collectMetricsFromZookeeper||clusterPhyId={}||metricName={}||errMsg=exception!",
                        clusterPhyId, metricName, e
                );
            }
        }

        return Result.buildSuc(metrics);
    }

    @Override
    public Result<List<MetricLineVO>> listMetricsFromES(Long clusterPhyId, MetricDTO dto) {
        Map<String/*metricName*/, List<MetricPointVO>> pointVOMap = zookeeperMetricESDAO.listMetricsByClusterPhyId(
                clusterPhyId,
                dto.getMetricsNames(),
                dto.getAggType(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        // 格式转化
        List<MetricLineVO> voList = new ArrayList<>();
        pointVOMap.entrySet().forEach(entry ->
            voList.add(new MetricLineVO(String.valueOf(clusterPhyId), entry.getKey(), entry.getValue()))
        );
        return Result.buildSuc(voList);
    }


    /**************************************************** private method ****************************************************/

    private Result<ZookeeperMetrics> getMetricFromServerCmd(VersionItemParam metricParam) {
        ZookeeperMetricParam param = (ZookeeperMetricParam)metricParam;

        Result<ZookeeperMetrics> rz = null;
        for (Tuple<String, Integer> hostPort: param.getZkAddressList()) {
            ServerCmdData cmdData = null;

            BaseFourLetterWordCmdData baseCmdData = ZookeeperLocalCache.getData(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.ServerCmd);
            if (baseCmdData != null) {
                cmdData = (ServerCmdData) baseCmdData;
            } else if (ZookeeperLocalCache.canUse(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.ServerCmd)) {
                Result<ServerCmdData> cmdDataResult = FourLetterWordUtil.executeFourLetterCmd(
                        param.getClusterPhyId(),
                        hostPort.getV1(),
                        hostPort.getV2(),
                        param.getZkConfig() != null ? param.getZkConfig().getOpenSecure(): false,
                        param.getZkConfig() != null ? param.getZkConfig().getRequestTimeoutUnitMs(): Constant.DEFAULT_REQUEST_TIMEOUT_UNIT_MS,
                        new ServerCmdDataParser()
                );

                if (cmdDataResult.failed()) {
                    ZookeeperLocalCache.setFailed(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.ServerCmd);

                    rz = Result.buildFromIgnoreData(cmdDataResult);
                    continue;
                }

                cmdData = cmdDataResult.getData();
                ZookeeperLocalCache.putData(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.ServerCmd, cmdData);
            } else {
                // baseCmdData为空 且 当前地址不可使用
                continue;
            }

            ZookeeperMetrics metrics = new ZookeeperMetrics(param.getClusterPhyId());
            metrics.putMetric(ZOOKEEPER_METRIC_AVG_REQUEST_LATENCY,         cmdData.getZkAvgLatency());
            metrics.putMetric(ZOOKEEPER_METRIC_MIN_REQUEST_LATENCY,         cmdData.getZkMinLatency());
            metrics.putMetric(ZOOKEEPER_METRIC_MAX_REQUEST_LATENCY,         cmdData.getZkMaxLatency());
            metrics.putMetric(ZOOKEEPER_METRIC_OUTSTANDING_REQUESTS,        cmdData.getZkOutstandingRequests());
            metrics.putMetric(ZOOKEEPER_METRIC_NODE_COUNT,                  cmdData.getZkZnodeCount());
            metrics.putMetric(ZOOKEEPER_METRIC_NUM_ALIVE_CONNECTIONS,       cmdData.getZkNumAliveConnections());
            metrics.putMetric(ZOOKEEPER_METRIC_PACKETS_RECEIVED,            cmdData.getZkPacketsReceived());
            metrics.putMetric(ZOOKEEPER_METRIC_PACKETS_SENT,                cmdData.getZkPacketsSent());

            return Result.buildSuc(metrics);
        }

        return rz != null? rz: Result.buildSuc(new ZookeeperMetrics(param.getClusterPhyId()));
    }

    private Result<ZookeeperMetrics> getMetricFromMonitorCmd(VersionItemParam metricParam) {
        ZookeeperMetricParam param = (ZookeeperMetricParam)metricParam;

        Result<ZookeeperMetrics> rz = null;
        for (Tuple<String, Integer> hostPort: param.getZkAddressList()) {
            MonitorCmdData cmdData = null;

            BaseFourLetterWordCmdData baseCmdData = ZookeeperLocalCache.getData(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.MonitorCmd);
            if (baseCmdData != null) {
                cmdData = (MonitorCmdData) baseCmdData;
            } else if (ZookeeperLocalCache.canUse(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.MonitorCmd)) {
                Result<MonitorCmdData> cmdDataResult = FourLetterWordUtil.executeFourLetterCmd(
                        param.getClusterPhyId(),
                        hostPort.getV1(),
                        hostPort.getV2(),
                        param.getZkConfig() != null ? param.getZkConfig().getOpenSecure(): false,
                        param.getZkConfig() != null ? param.getZkConfig().getRequestTimeoutUnitMs(): Constant.DEFAULT_REQUEST_TIMEOUT_UNIT_MS,
                        new MonitorCmdDataParser()
                );

                if (cmdDataResult.failed()) {
                    ZookeeperLocalCache.setFailed(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.MonitorCmd);

                    rz = Result.buildFromIgnoreData(cmdDataResult);
                    continue;
                }

                cmdData = cmdDataResult.getData();
                ZookeeperLocalCache.putData(hostPort.getV1(), hostPort.getV2(), FourLetterWordUtil.MonitorCmd, cmdData);
            } else {
                continue;
            }

            ZookeeperMetrics metrics = new ZookeeperMetrics(param.getClusterPhyId());
            metrics.putMetric(ZOOKEEPER_METRIC_AVG_REQUEST_LATENCY,         cmdData.getZkAvgLatency());
            metrics.putMetric(ZOOKEEPER_METRIC_MIN_REQUEST_LATENCY,         cmdData.getZkMinLatency());
            metrics.putMetric(ZOOKEEPER_METRIC_MAX_REQUEST_LATENCY,         cmdData.getZkMaxLatency());
            metrics.putMetric(ZOOKEEPER_METRIC_OUTSTANDING_REQUESTS,        cmdData.getZkOutstandingRequests());
            metrics.putMetric(ZOOKEEPER_METRIC_NODE_COUNT,                  cmdData.getZkZnodeCount());
            metrics.putMetric(ZOOKEEPER_METRIC_WATCH_COUNT,                 cmdData.getZkWatchCount());
            metrics.putMetric(ZOOKEEPER_METRIC_NUM_ALIVE_CONNECTIONS,       cmdData.getZkNumAliveConnections());
            metrics.putMetric(ZOOKEEPER_METRIC_PACKETS_RECEIVED,            cmdData.getZkPacketsReceived());
            metrics.putMetric(ZOOKEEPER_METRIC_PACKETS_SENT,                cmdData.getZkPacketsSent());
            metrics.putMetric(ZOOKEEPER_METRIC_EPHEMERALS_COUNT,            cmdData.getZkEphemeralsCount());
            metrics.putMetric(ZOOKEEPER_METRIC_APPROXIMATE_DATA_SIZE,       cmdData.getZkApproximateDataSize());
            metrics.putMetric(ZOOKEEPER_METRIC_OPEN_FILE_DESCRIPTOR_COUNT,  cmdData.getZkOpenFileDescriptorCount());
            metrics.putMetric(ZOOKEEPER_METRIC_MAX_FILE_DESCRIPTOR_COUNT,   cmdData.getZkMaxFileDescriptorCount());

            return Result.buildSuc(metrics);
        }

        return rz != null? rz: Result.buildSuc(new ZookeeperMetrics(param.getClusterPhyId()));
    }

    private Result<ZookeeperMetrics> doNothing(VersionItemParam metricParam) {
        ZookeeperMetricParam param = (ZookeeperMetricParam)metricParam;
        return Result.buildSuc(new ZookeeperMetrics(param.getClusterPhyId()));
    }

    private Result<ZookeeperMetrics> getMetricFromKafkaByJMX(VersionItemParam metricParam) {
        ZookeeperMetricParam param = (ZookeeperMetricParam)metricParam;

        String      metricName              = param.getMetricName();
        Long        clusterPhyId            = param.getClusterPhyId();
        Integer kafkaControllerId           = param.getKafkaControllerId();

        //1、获取jmx的属性信息
        VersionJmxInfo jmxInfo = getJMXInfo(clusterPhyId, metricName);
        if(null == jmxInfo) {
            return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);
        }

        //2、获取jmx连接
        JmxConnectorWrap jmxConnectorWrap = kafkaJMXClient.getClientWithCheck(clusterPhyId, kafkaControllerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)) {
            return Result.buildFailure(VC_JMX_INIT_ERROR);
        }

        try {
            //2、获取jmx指标
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxInfo.getJmxObjectName()), jmxInfo.getJmxAttribute()).toString();

            return Result.buildSuc(ZookeeperMetrics.initWithMetric(clusterPhyId, metricName, Float.valueOf(value)));
        } catch (Exception e) {
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }

    private Result<ZookeeperMetrics> getMetricFromHealthService(VersionItemParam metricParam) {
        ZookeeperMetricParam param = (ZookeeperMetricParam)metricParam;

        String      metricName              = param.getMetricName();
        Long        clusterPhyId            = param.getClusterPhyId();

        return Result.buildSuc(healthStateService.calZookeeperHealthMetrics(clusterPhyId));
    }
}
