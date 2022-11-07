package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.AbstractMetricCollector;
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
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ZookeeperMetricPO;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperMetricService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_ZOOKEEPER;

/**
 * @author didi
 */
@Component
public class ZookeeperMetricCollector extends AbstractMetricCollector<ZookeeperMetricPO> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ZookeeperMetricService zookeeperMetricService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private KafkaControllerService kafkaControllerService;

    @Override
    public void collectMetrics(ClusterPhy clusterPhy) {
        Long        startTime           =   System.currentTimeMillis();
        Long        clusterPhyId        =   clusterPhy.getId();
        List<VersionControlItem> items  =   versionControlService.listVersionControlItem(clusterPhyId, collectorType().getCode());
        List<ZookeeperInfo> aliveZKList =   zookeeperService.listFromDBByCluster(clusterPhyId)
                .stream()
                .filter(elem -> Constant.ALIVE.equals(elem.getStatus()))
                .collect(Collectors.toList());
        KafkaController kafkaController =   kafkaControllerService.getKafkaControllerFromDB(clusterPhyId);

        ZookeeperMetrics metrics = ZookeeperMetrics.initWithMetric(clusterPhyId, Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (float)Constant.INVALID_CODE);
        if (ValidateUtils.isEmptyList(aliveZKList)) {
            // 没有存活的ZK时，发布事件，然后直接返回
            publishMetric(new ZookeeperMetricEvent(this, Arrays.asList(metrics)));
            return;
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

                if(!EnvUtil.isOnline()){
                    LOGGER.info(
                            "class=ZookeeperMetricCollector||method=collectMetrics||clusterPhyId={}||metricName={}||metricValue={}",
                            clusterPhyId, v.getName(), ConvertUtil.obj2Json(ret.getData().getMetrics())
                    );
                }
            } catch (Exception e){
                LOGGER.error(
                        "class=ZookeeperMetricCollector||method=collectMetrics||clusterPhyId={}||metricName={}||errMsg=exception!",
                        clusterPhyId, v.getName(), e
                );
            }
        }

        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        publishMetric(new ZookeeperMetricEvent(this, Arrays.asList(metrics)));

        LOGGER.info(
                "class=ZookeeperMetricCollector||method=collectMetrics||clusterPhyId={}||startTime={}||costTime={}||msg=msg=collect finished.",
                clusterPhyId, startTime, System.currentTimeMillis() - startTime
        );
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_ZOOKEEPER;
    }
}
