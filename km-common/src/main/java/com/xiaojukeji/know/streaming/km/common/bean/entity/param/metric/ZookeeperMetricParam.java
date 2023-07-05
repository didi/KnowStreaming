package com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
public class ZookeeperMetricParam extends MetricParam {
    private Long                            clusterPhyId;

    private List<Tuple<String, Integer>>    zkAddressList;

    private ZKConfig                        zkConfig;

    private String                          metricName;

    private Integer                         kafkaControllerId;

    public ZookeeperMetricParam(Long clusterPhyId,
                                List<Tuple<String, Integer>> zkAddressList,
                                ZKConfig zkConfig,
                                String metricName) {
        this.clusterPhyId = clusterPhyId;
        this.zkAddressList = zkAddressList;
        this.zkConfig = zkConfig;
        this.metricName = metricName;
    }

    public ZookeeperMetricParam(Long clusterPhyId,
                                List<Tuple<String, Integer>> zkAddressList,
                                ZKConfig zkConfig,
                                Integer kafkaControllerId,
                                String metricName) {
        this.clusterPhyId = clusterPhyId;
        this.zkAddressList = zkAddressList;
        this.zkConfig = zkConfig;
        this.kafkaControllerId = kafkaControllerId;
        this.metricName = metricName;
    }
}
