package com.xiaojukeji.know.streaming.km.persistence.cache;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengqiao
 * @date 22/02/25
 */
public class LoadedClusterPhyCache {
    /**
     * <物理集群ID, 物理集群PO>
     */
    private static final Map<Long, ClusterPhy> PHY_CLUSTER_MAP = new ConcurrentHashMap<>();

    private LoadedClusterPhyCache() {
    }

    public static boolean containsByPhyId(Long clusterPhyId) {
        return PHY_CLUSTER_MAP.containsKey(clusterPhyId);
    }

    public static ClusterPhy getByPhyId(Long clusterPhyId) {
        return PHY_CLUSTER_MAP.get(clusterPhyId);
    }

    public static ClusterPhy remove(Long clusterPhyId) {
        return PHY_CLUSTER_MAP.remove(clusterPhyId);
    }

    public static void replace(ClusterPhy clusterPhy) {
        PHY_CLUSTER_MAP.put(clusterPhy.getId(), clusterPhy);
    }

    public static Map<Long, ClusterPhy> listAll() {
        return PHY_CLUSTER_MAP;
    }
}