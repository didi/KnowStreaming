package com.xiaojukeji.kafka.manager.common.entity.po.query;

/**
 * @author zengqiao
 * @date 19/12/4
 */
public class ClusterQueryOption extends BaseQueryOption {
    private String clusterName;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}