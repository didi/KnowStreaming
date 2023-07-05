package com.xiaojukeji.know.streaming.km.core.service.zookeeper;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.ZookeeperMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;

import java.util.List;

public interface ZookeeperMetricService {
    /**
     * ZK指标获取
     * @param param 参数，因为ZK 四字命令在使用时，是短连接，所以参数内容会复杂一些，后续可以考虑优化为长连接
     * @return
     */
    Result<ZookeeperMetrics> collectMetricsFromZookeeper(ZookeeperMetricParam param);
    Result<ZookeeperMetrics> batchCollectMetricsFromZookeeper(Long clusterPhyId, List<String> metricNameList);

    Result<List<MetricLineVO>> listMetricsFromES(Long clusterPhyId, MetricDTO dto);
}
