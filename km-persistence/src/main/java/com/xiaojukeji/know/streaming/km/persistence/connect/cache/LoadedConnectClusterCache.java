package com.xiaojukeji.know.streaming.km.persistence.connect.cache;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wyb
 * @date 2022/11/7
 */
public class LoadedConnectClusterCache {
    private static final Map<Long, ConnectCluster> CONNECT_CLUSTER_MAP = new ConcurrentHashMap<>();

    public static boolean containsByPhyId(Long connectClusterId) {
        return CONNECT_CLUSTER_MAP.containsKey(connectClusterId);
    }

    public static ConnectCluster getByPhyId(Long connectClusterId) {
        return CONNECT_CLUSTER_MAP.get(connectClusterId);
    }

    public static ConnectCluster remove(Long connectClusterId) {
        return CONNECT_CLUSTER_MAP.remove(connectClusterId);
    }

    public static void replace(ConnectCluster connectCluster) {
        CONNECT_CLUSTER_MAP.put(connectCluster.getId(), connectCluster);
    }

    public static Map<Long, ConnectCluster> listAll() {
        return CONNECT_CLUSTER_MAP;
    }


}
