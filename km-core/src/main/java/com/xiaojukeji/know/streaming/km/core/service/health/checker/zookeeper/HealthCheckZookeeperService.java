package com.xiaojukeji.know.streaming.km.core.service.health.checker.zookeeper;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthAmountRatioConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.ZookeeperMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.zookeeper.ZookeeperParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.zookeeper.ZKRoleEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.zookeeper.ZookeeperUtils;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ZookeeperMetricVersionItems;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperMetricService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HealthCheckZookeeperService extends AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(HealthCheckZookeeperService.class);

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private ZookeeperMetricService zookeeperMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.ZK_BRAIN_SPLIT.getConfigName(), this::checkBrainSplit);
        functionMap.putIfAbsent(HealthCheckNameEnum.ZK_OUTSTANDING_REQUESTS.getConfigName(), this::checkOutstandingRequests);
        functionMap.putIfAbsent(HealthCheckNameEnum.ZK_WATCH_COUNT.getConfigName(), this::checkWatchCount);
        functionMap.putIfAbsent(HealthCheckNameEnum.ZK_ALIVE_CONNECTIONS.getConfigName(), this::checkAliveConnections);
        functionMap.putIfAbsent(HealthCheckNameEnum.ZK_APPROXIMATE_DATA_SIZE.getConfigName(), this::checkApproximateDataSize);
        functionMap.putIfAbsent(HealthCheckNameEnum.ZK_SENT_RATE.getConfigName(), this::checkSentRate);
    }

    @Override
    public List<ClusterParam> getResList(Long clusterPhyId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return new ArrayList<>();
        }

        try {
            return Arrays.asList(new ZookeeperParam(
                    clusterPhyId,
                    ZookeeperUtils.connectStringParser(clusterPhy.getZookeeper()),
                    ConvertUtil.str2ObjByJson(clusterPhy.getZkProperties(), ZKConfig.class)
                    ));
        } catch (Exception e) {
            log.error("class=HealthCheckZookeeperService||method=getResList||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);
        }

        return new ArrayList<>();
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.ZOOKEEPER;
    }

    private HealthCheckResult checkBrainSplit(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        ZookeeperParam param = (ZookeeperParam) singleConfigSimpleTuple.getV1();
        HealthCompareValueConfig valueConfig = (HealthCompareValueConfig) singleConfigSimpleTuple.getV2();

        List<ZookeeperInfo> infoList = zookeeperService.listFromDBByCluster(param.getClusterPhyId());
        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.ZOOKEEPER.getDimension(),
                HealthCheckNameEnum.ZK_BRAIN_SPLIT.getConfigName(),
                param.getClusterPhyId(),
                ""
        );

        long value = infoList.stream().filter(elem -> ZKRoleEnum.LEADER.getRole().equals(elem.getRole())).count();

        checkResult.setPassed(value == valueConfig.getValue().longValue() ? Constant.YES : Constant.NO);
        return checkResult;
    }

    private HealthCheckResult checkOutstandingRequests(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        ZookeeperParam param = (ZookeeperParam) singleConfigSimpleTuple.getV1();
        HealthAmountRatioConfig valueConfig = (HealthAmountRatioConfig) singleConfigSimpleTuple.getV2();

        Result<ZookeeperMetrics> metricsResult = zookeeperMetricService.collectMetricsFromZookeeper(
                new ZookeeperMetricParam(
                        param.getClusterPhyId(),
                        param.getZkAddressList(),
                        param.getZkConfig(),
                        ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_OUTSTANDING_REQUESTS
                )
        );
        if (metricsResult.failed() || !metricsResult.hasData()) {
            log.error(
                    "class=HealthCheckZookeeperService||method=checkOutstandingRequests||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, valueConfig, metricsResult
            );
            return null;
        }

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.ZOOKEEPER.getDimension(),
                HealthCheckNameEnum.ZK_OUTSTANDING_REQUESTS.getConfigName(),
                param.getClusterPhyId(),
                ""
        );

        Float value = metricsResult.getData().getMetric(ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_OUTSTANDING_REQUESTS);


        checkResult.setPassed(value.intValue() <= valueConfig.getAmount().doubleValue() * valueConfig.getRatio().doubleValue() ? Constant.YES : Constant.NO);

        return checkResult;
    }

    private HealthCheckResult checkWatchCount(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        ZookeeperParam param = (ZookeeperParam) singleConfigSimpleTuple.getV1();
        HealthAmountRatioConfig valueConfig = (HealthAmountRatioConfig) singleConfigSimpleTuple.getV2();

        Result<ZookeeperMetrics> metricsResult = zookeeperMetricService.collectMetricsFromZookeeper(
                new ZookeeperMetricParam(
                        param.getClusterPhyId(),
                        param.getZkAddressList(),
                        param.getZkConfig(),
                        ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_WATCH_COUNT
                )
        );

        if (metricsResult.failed() || !metricsResult.hasData()) {
            log.error(
                    "class=HealthCheckZookeeperService||method=checkWatchCount||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, valueConfig, metricsResult
            );
            return null;
        }

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.ZOOKEEPER.getDimension(),
                HealthCheckNameEnum.ZK_WATCH_COUNT.getConfigName(),
                param.getClusterPhyId(),
                ""
        );

        Float value = metricsResult.getData().getMetric(ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_WATCH_COUNT);


        checkResult.setPassed(value.intValue() <= valueConfig.getAmount().doubleValue() * valueConfig.getRatio().doubleValue() ? Constant.YES : Constant.NO);

        return checkResult;
    }

    private HealthCheckResult checkAliveConnections(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        ZookeeperParam param = (ZookeeperParam) singleConfigSimpleTuple.getV1();
        HealthAmountRatioConfig valueConfig = (HealthAmountRatioConfig) singleConfigSimpleTuple.getV2();

        Result<ZookeeperMetrics> metricsResult = zookeeperMetricService.collectMetricsFromZookeeper(
                new ZookeeperMetricParam(
                        param.getClusterPhyId(),
                        param.getZkAddressList(),
                        param.getZkConfig(),
                        ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_NUM_ALIVE_CONNECTIONS
                )
        );

        if (metricsResult.failed() || !metricsResult.hasData()) {
            log.error(
                    "class=HealthCheckZookeeperService||method=checkAliveConnections||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, valueConfig, metricsResult
            );
            return null;
        }

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.ZOOKEEPER.getDimension(),
                HealthCheckNameEnum.ZK_ALIVE_CONNECTIONS.getConfigName(),
                param.getClusterPhyId(),
                ""
        );

        Float value = metricsResult.getData().getMetric(ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_NUM_ALIVE_CONNECTIONS);


        checkResult.setPassed(value.intValue() <= valueConfig.getAmount().doubleValue() * valueConfig.getRatio().doubleValue() ? Constant.YES : Constant.NO);

        return checkResult;
    }

    private HealthCheckResult checkApproximateDataSize(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        ZookeeperParam param = (ZookeeperParam) singleConfigSimpleTuple.getV1();
        HealthAmountRatioConfig valueConfig = (HealthAmountRatioConfig) singleConfigSimpleTuple.getV2();

        Result<ZookeeperMetrics> metricsResult = zookeeperMetricService.collectMetricsFromZookeeper(
                new ZookeeperMetricParam(
                        param.getClusterPhyId(),
                        param.getZkAddressList(),
                        param.getZkConfig(),
                        ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_APPROXIMATE_DATA_SIZE
                )
        );

        if (metricsResult.failed() || !metricsResult.hasData()) {
            log.error(
                    "class=HealthCheckZookeeperService||method=checkApproximateDataSize||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, valueConfig, metricsResult
            );
            return null;
        }

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.ZOOKEEPER.getDimension(),
                HealthCheckNameEnum.ZK_APPROXIMATE_DATA_SIZE.getConfigName(),
                param.getClusterPhyId(),
                ""
        );

        Float value = metricsResult.getData().getMetric(ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_APPROXIMATE_DATA_SIZE);


        checkResult.setPassed(value.intValue() <= valueConfig.getAmount().doubleValue() * valueConfig.getRatio().doubleValue() ? Constant.YES : Constant.NO);

        return checkResult;
    }

    private HealthCheckResult checkSentRate(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        ZookeeperParam param = (ZookeeperParam) singleConfigSimpleTuple.getV1();
        HealthAmountRatioConfig valueConfig = (HealthAmountRatioConfig) singleConfigSimpleTuple.getV2();

        Result<ZookeeperMetrics> metricsResult = zookeeperMetricService.collectMetricsFromZookeeper(
                new ZookeeperMetricParam(
                        param.getClusterPhyId(),
                        param.getZkAddressList(),
                        param.getZkConfig(),
                        ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_PACKETS_SENT
                )
        );

        if (metricsResult.failed() || !metricsResult.hasData()) {
            log.error(
                    "class=HealthCheckZookeeperService||method=checkSentRate||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, valueConfig, metricsResult
            );
            return null;
        }

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.ZOOKEEPER.getDimension(),
                HealthCheckNameEnum.ZK_SENT_RATE.getConfigName(),
                param.getClusterPhyId(),
                ""
        );

        Float value = metricsResult.getData().getMetric(ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_PACKETS_SENT);


        checkResult.setPassed(value.intValue() <= valueConfig.getAmount().doubleValue() * valueConfig.getRatio().doubleValue() ? Constant.YES : Constant.NO);

        return checkResult;
    }
}
