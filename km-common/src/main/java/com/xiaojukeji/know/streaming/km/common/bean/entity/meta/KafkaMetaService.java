package com.xiaojukeji.know.streaming.km.common.bean.entity.meta;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Kafka元信息服务接口
 */
public interface KafkaMetaService<T> {
    /**
     * 从Kafka中获取数据
     * @param connectCluster connect集群
     * @return 全部资源列表, 成功的资源列表
     */
    default Result<Tuple<Set<String>, List<T>>> getDataFromKafka(ConnectCluster connectCluster) { return Result.buildSuc(new Tuple<>(new HashSet<>(), new ArrayList<>())); }

    /**
     * 从Kafka中获取数据
     * @param clusterPhy kafka集群
     * @return 全部资源集合, 成功的资源列表
     */
    default Result<Tuple<Set<String>, List<T>>> getDataFromKafka(ClusterPhy clusterPhy) { return Result.buildSuc(new Tuple<>(new HashSet<>(), new ArrayList<>())); }

    /**
     * 元信息同步至DB中
     * @param clusterId 集群ID
     * @param fullNameSet 全部资源列表
     * @param dataList 成功的资源列表
     */
    default void writeToDB(Long clusterId, Set<String> fullNameSet, List<T> dataList) {}

    /**
     * 依据kafka集群ID删除数据
     * @param clusterPhyId kafka集群ID
     */
    default int deleteInDBByKafkaClusterId(Long clusterPhyId) { return 0; }
}
