package com.xiaojukeji.know.streaming.km.core.service.health.checker.broker;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.broker.BrokerParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.BrokerMetricVersionItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HealthCheckBrokerService extends AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(HealthCheckBrokerService.class);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerMetricService brokerMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.BROKER_REQUEST_QUEUE_FULL.getConfigName(), this::checkBrokerRequestQueueFull);
        functionMap.putIfAbsent(HealthCheckNameEnum.BROKER_NETWORK_PROCESSOR_AVG_IDLE_TOO_LOW.getConfigName(), this::checkBrokerNetworkProcessorAvgIdleTooLow);
    }

    @Override
    public List<ClusterParam> getResList(Long clusterPhyId) {
        List<ClusterParam> paramList = new ArrayList<>();
        for (Broker broker: brokerService.listAliveBrokersFromCacheFirst(clusterPhyId)) {
            paramList.add(new BrokerParam(clusterPhyId, broker.getBrokerId()));
        }

        return paramList;
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.BROKER;
    }

    @Override
    public Integer getDimensionCodeIfSupport(Long kafkaClusterPhyId) {
        return this.getHealthCheckDimensionEnum().getDimension();
    }

    /**
     * Broker网络处理线程平均值过低
     */
    private HealthCheckResult checkBrokerNetworkProcessorAvgIdleTooLow(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        BrokerParam param = (BrokerParam) paramTuple.getV1();
        HealthCompareValueConfig singleConfig = (HealthCompareValueConfig) paramTuple.getV2();

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.BROKER.getDimension(),
                HealthCheckNameEnum.BROKER_NETWORK_PROCESSOR_AVG_IDLE_TOO_LOW.getConfigName(),
                param.getClusterPhyId(),
                String.valueOf(param.getBrokerId())
        );

        Result<BrokerMetrics> metricsResult = brokerMetricService.collectBrokerMetricsFromKafka(
                param.getClusterPhyId(),
                param.getBrokerId(),
                BrokerMetricVersionItems.BROKER_METRIC_NETWORK_RPO_AVG_IDLE
        );

        if (metricsResult.failed()) {
            log.error("method=checkBrokerNetworkProcessorAvgIdleTooLow||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, singleConfig, metricsResult);
            return null;
        }

        Float avgIdle = metricsResult.getData().getMetrics().get(BrokerMetricVersionItems.BROKER_METRIC_NETWORK_RPO_AVG_IDLE);
        if (avgIdle == null) {
            log.error("method=checkBrokerNetworkProcessorAvgIdleTooLow||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, singleConfig, metricsResult);
            return null;
        }

        checkResult.setPassed(avgIdle >= singleConfig.getValue()? Constant.YES: Constant.NO);

        return checkResult;
    }

    /**
     * Broker请求队列满
     */
    private HealthCheckResult checkBrokerRequestQueueFull(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        BrokerParam param = (BrokerParam) paramTuple.getV1();
        HealthCompareValueConfig singleConfig = (HealthCompareValueConfig) paramTuple.getV2();

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.BROKER.getDimension(),
                HealthCheckNameEnum.BROKER_REQUEST_QUEUE_FULL.getConfigName(),
                param.getClusterPhyId(),
                String.valueOf(param.getBrokerId())
        );

        Result<BrokerMetrics> metricsResult = brokerMetricService.collectBrokerMetricsFromKafka(
                param.getClusterPhyId(),
                param.getBrokerId(),
                Arrays.asList(BrokerMetricVersionItems.BROKER_METRIC_TOTAL_REQ_QUEUE)
        );

        if (metricsResult.failed()) {
            log.error("method=checkBrokerRequestQueueFull||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, singleConfig, metricsResult);
            return null;
        }

        Float queueSize = metricsResult.getData().getMetrics().get(BrokerMetricVersionItems.BROKER_METRIC_TOTAL_REQ_QUEUE);
        if (queueSize == null) {
            log.error("method=checkBrokerRequestQueueFull||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, singleConfig, metricsResult);
            return null;
        }

        checkResult.setPassed(queueSize <= singleConfig.getValue()? Constant.YES : Constant.NO);

        return checkResult;
    }
}
