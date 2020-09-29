package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zengqiao
 * @date 20/5/13
 */
public class OrderExtensionModifyClusterDTO {
    private Long clusterId;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderExtensionModifyClusterDTO{" +
                "clusterId=" + clusterId +
                '}';
    }
}