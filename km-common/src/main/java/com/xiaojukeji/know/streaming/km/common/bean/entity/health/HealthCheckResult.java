package com.xiaojukeji.know.streaming.km.common.bean.entity.health;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealthCheckResult {
    /**
     * 检查维度(0:未知，1:Cluster，2:Broker，3:Topic，4:Group)
     */
    private Integer dimension;

    /**
     * 配置ID
     */
    private String configName;

    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * 资源名称
     */
    private String resName;

    /**
     * 是否通过
     */
    private Integer passed;

    public HealthCheckResult(Integer dimension, String configName, Long clusterPhyId, String resName) {
        this.dimension = dimension;
        this.configName = configName;
        this.clusterPhyId = clusterPhyId;
        this.resName = resName;
    }
}
