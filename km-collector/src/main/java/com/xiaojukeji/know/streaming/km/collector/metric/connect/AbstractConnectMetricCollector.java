package com.xiaojukeji.know.streaming.km.collector.metric.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.AbstractMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.LoggerUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author didi
 */
public abstract class AbstractConnectMetricCollector<M> extends AbstractMetricCollector<M, ConnectCluster> {
    private static final ILog LOGGER                    = LogFactory.getLog(AbstractConnectMetricCollector.class);

    protected static final ILog METRIC_COLLECTED_LOGGER = LoggerUtil.getMetricCollectedLogger();

    @Autowired
    private ConnectClusterService connectClusterService;

    public abstract List<M> collectConnectMetrics(ConnectCluster connectCluster);

    @Override
    public String getClusterVersion(ConnectCluster connectCluster){
        return connectClusterService.getClusterVersion(connectCluster.getId());
    }

    @Override
    public void collectMetrics(ConnectCluster connectCluster) {
        long startTime = System.currentTimeMillis();

        // 采集指标
        List<M> metricsList = this.collectConnectMetrics(connectCluster);

        // 输出耗时信息
        LOGGER.info(
                "metricType={}||connectClusterId={}||costTimeUnitMs={}",
                this.collectorType().getMessage(), connectCluster.getId(), System.currentTimeMillis() - startTime
        );

        // 输出采集到的指标信息
        METRIC_COLLECTED_LOGGER.debug("metricType={}||connectClusterId={}||metrics={}!",
                this.collectorType().getMessage(), connectCluster.getId(), ConvertUtil.obj2Json(metricsList)
        );
    }
}
