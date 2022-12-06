package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.ZookeeperMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.ZookeeperMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperMetricService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_ZOOKEEPER;

/**
 * @author didi
 */
@Component
public class ZookeeperMetricCollector extends AbstractKafkaMetricCollector<ZookeeperMetrics> {
    protected static final ILog  LOGGER = LogFactory.getLog(ZookeeperMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ZookeeperMetricService zookeeperMetricService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private KafkaControllerService kafkaControllerService;

    @Override
    public List<ZookeeperMetrics> collectKafkaMetrics(ClusterPhy clusterPhy) {
        Long        startTime           =   System.currentTimeMillis();
        Long        clusterPhyId        =   clusterPhy.getId();
        List<VersionControlItem> items  =   versionControlService.listVersionControlItem(this.getClusterVersion(clusterPhy), collectorType().getCode());
        List<ZookeeperInfo> aliveZKList =   zookeeperService.listFromDBByCluster(clusterPhyId)
                .stream()
                .filter(elem -> Constant.ALIVE.equals(elem.getStatus()))
                .collect(Collectors.toList());
        KafkaController kafkaController =   kafkaControllerService.getKafkaControllerFromDB(clusterPhyId);

        ZookeeperMetrics metrics = ZookeeperMetrics.initWithMetric(clusterPhyId, Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);
        if (ValidateUtils.isEmptyList(aliveZKList)) {
            // 没有存活的ZK时，发布事件，然后直接返回
            publishMetric(new ZookeeperMetricEvent(this, Collections.singletonList(metrics)));
            return Collections.singletonList(metrics);
        }

        // 构造参数
        ZookeeperMetricParam param = new ZookeeperMetricParam(
                clusterPhyId,
                aliveZKList.stream().map(elem -> new Tuple<String, Integer>(elem.getHost(), elem.getPort())).collect(Collectors.toList()),
                ConvertUtil.str2ObjByJson(clusterPhy.getZkProperties(), ZKConfig.class),
                kafkaController == null? Constant.INVALID_CODE: kafkaController.getBrokerId(),
                null
        );

        for(VersionControlItem v : items) {
            try {
                if(null != metrics.getMetrics().get(v.getName())) {
                    continue;
                }

                param.setMetricName(v.getName());

                Result<ZookeeperMetrics> ret = zookeeperMetricService.collectMetricsFromZookeeper(param);
                if(null == ret || ret.failed() || null == ret.getData()){
                    continue;
                }

                metrics.putMetric(ret.getData().getMetrics());
            } catch (Exception e){
                LOGGER.error(
                        "method=collectMetrics||clusterPhyId={}||metricName={}||errMsg=exception!",
                        clusterPhyId, v.getName(), e
                );
            }
        }

        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        this.publishMetric(new ZookeeperMetricEvent(this, Collections.singletonList(metrics)));

        return Collections.singletonList(metrics);
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_ZOOKEEPER;
    }
}
