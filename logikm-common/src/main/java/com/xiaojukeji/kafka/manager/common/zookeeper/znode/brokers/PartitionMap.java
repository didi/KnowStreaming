package com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 根据/brokers/topics/topic的节点内容定义
 * @author tukun
 * @date 2015/11/10.
 */
public class PartitionMap implements Serializable {

    /**
     * 版本号
     */
    private int                         version;

    /**
     * Map<PartitionId，副本所在的brokerId列表>
     */
    private Map<Integer, List<Integer>> partitions;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Integer, List<Integer>> getPartitions() {
        return partitions;
    }

    public void setPartitions(Map<Integer, List<Integer>> partitions) {
        this.partitions = partitions;
    }

    @Override
    public String toString() {
        return "PartitionMap{" +
                "version=" + version +
                ", partitions=" + partitions +
                '}';
    }
}
