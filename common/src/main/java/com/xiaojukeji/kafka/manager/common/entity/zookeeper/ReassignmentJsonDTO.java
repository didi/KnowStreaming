package com.xiaojukeji.kafka.manager.common.entity.zookeeper;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/1/15
 */
public class ReassignmentJsonDTO {
    private Integer version;

    private List<ReassignmentElemDTO> partitions;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<ReassignmentElemDTO> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<ReassignmentElemDTO> partitions) {
        this.partitions = partitions;
    }

    @Override
    public String toString() {
        return "ReassignmentJsonDTO{" +
                "version=" + version +
                ", partitions=" + partitions +
                '}';
    }
}