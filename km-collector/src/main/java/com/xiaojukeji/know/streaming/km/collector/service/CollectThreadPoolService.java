package com.xiaojukeji.know.streaming.km.collector.service;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CollectThreadPoolService {
    private static final ILog LOGGER = LogFactory.getLog(CollectThreadPoolService.class);

    private final AtomicLong shardIdx = new AtomicLong(0L);

    @Value(value = "${thread-pool.collector.future-util.num:1}")
    private Integer futureUtilNum;

    @Value(value = "${thread-pool.collector.future-util.thread-num:8}")
    private Integer futureUtilThreadNum;

    @Value(value = "${thread-pool.collector.future-util.queue-size:10000}")
    private Integer futureUtilQueueSize;

    @Value(value = "${thread-pool.collector.future-util.select-suitable-enable:true}")
    private Boolean futureUtilSelectSuitableEnable;

    @Value(value = "${thread-pool.collector.future-util.suitable-queue-size:5000}")
    private Integer futureUtilSuitableQueueSize;

    private static final Map<Long, FutureWaitUtil<Void>> SHARD_ID_FUTURE_UTIL_MAP = new ConcurrentHashMap<>();

    private static final Cache<Long, Long> PHYSICAL_CLUSTER_ID_SHARD_ID_CACHE = Caffeine
            .newBuilder()
            .expireAfterWrite(16, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    @PostConstruct
    private void init() {
        if (futureUtilNum <= 0) {
            futureUtilNum = 1;
        }

        // 初始化job线程池
        for (int idx = 0; idx < futureUtilNum; ++idx) {
            closeOldAndCreateNew((long)idx);
        }
    }

    public FutureWaitUtil<Void> selectSuitableFutureUtil(Long clusterPhyId) {
        // 获取集群对应的shardId
        Long shardId = this.getShardId(clusterPhyId);

        return SHARD_ID_FUTURE_UTIL_MAP.get(shardId);
    }

    /**************************************************** private method ****************************************************/

    private Long getShardId(Long clusterPhyId) {
        Long shardId = PHYSICAL_CLUSTER_ID_SHARD_ID_CACHE.getIfPresent(clusterPhyId);
        if (shardId == null) {
            shardId = shardIdx.incrementAndGet() % this.futureUtilNum;
        }

        PHYSICAL_CLUSTER_ID_SHARD_ID_CACHE.put(clusterPhyId, shardId);
        return shardId;
    }

    /**************************************************** schedule flush method ****************************************************/

    @Scheduled(cron="0 0/5 * * * ?")
    public void flush() {
        // 每个shard对应的集群ID，这里使用cache的原因是，需要将长期不使用的集群过滤掉
        Map<Long, List<Long>> shardIdPhysicalClusterIdListMap = new HashMap<>();
        for (Map.Entry<Long, Long> entry: PHYSICAL_CLUSTER_ID_SHARD_ID_CACHE.asMap().entrySet()) {
            shardIdPhysicalClusterIdListMap.putIfAbsent(entry.getValue(), new ArrayList<>());
            shardIdPhysicalClusterIdListMap.get(entry.getValue()).add(entry.getKey());
        }

        // 集群在线程池的分布信息
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Long, FutureWaitUtil<Void>> entry: SHARD_ID_FUTURE_UTIL_MAP.entrySet()) {
            // 释放被canceled的任务
            entry.getValue().purgeExecutor();

            sb.append("shardId:").append(entry.getKey());
            sb.append(" queueSize:").append(entry.getValue().getExecutorQueueSize());
            sb.append(" physicalClusterIdList:").append(
                    CommonUtils.longList2String(shardIdPhysicalClusterIdListMap.getOrDefault(entry.getKey(), new ArrayList<>()))
            );
            sb.append("\t\t\t");
            if (entry.getValue().getExecutorQueueSize() >= this.futureUtilSuitableQueueSize) {
                LOGGER.info("JobThreadPoolInfo\t\t\t shardId:{} queueSize:{} physicalClusterIdList:{}.",
                        entry.getKey(),
                        entry.getValue().getExecutorQueueSize(),
                        CommonUtils.longList2String(shardIdPhysicalClusterIdListMap.getOrDefault(entry.getKey(), new ArrayList<>()))
                );
            }
        }
        LOGGER.info("JobThreadPoolInfo\t\t\t {}...", sb);

        try {
            if (futureUtilSelectSuitableEnable != null && futureUtilSelectSuitableEnable) {
                reBalancePhysicalClusterShard(shardIdPhysicalClusterIdListMap);
            }
        } catch (Exception e) {
            LOGGER.error("rebalance job-thread-pool failed.", e);
        }
    }

    private void reBalancePhysicalClusterShard(Map<Long, List<Long>> shardIdPhysicalClusterIdListMap) {
        List<Long> withoutClusterShardIdList = new ArrayList<>(); // 无集群任务的线程池
        List<Long> idleShardIdList = new ArrayList<>(); // 空闲的线程池
        List<Long> notBusyShardIdList = new ArrayList<>(); // 不忙的线程池
        List<Long> busyShardIdList = new ArrayList<>(); // 忙的线程池
        List<Long> overflowShardIdList = new ArrayList<>(); // 已处理不过来的线程池

        // 统计各类线程池信息
        for (Map.Entry<Long, List<Long>> entry: shardIdPhysicalClusterIdListMap.entrySet()) {
            Integer queueSize = SHARD_ID_FUTURE_UTIL_MAP.get(entry.getKey()).getExecutorQueueSize();
            if (entry.getValue().isEmpty()) {
                withoutClusterShardIdList.add(entry.getKey());
            }

            if (queueSize == 0) {
                // 队列为空
                idleShardIdList.add(entry.getKey());
            } else if (queueSize <= futureUtilSuitableQueueSize) {
                // 队列较空闲
                notBusyShardIdList.add(entry.getKey());
            } else if (queueSize >= futureUtilSuitableQueueSize - 10) {
                // 队列处理不过来
                overflowShardIdList.add(entry.getKey());
            } else {
                // 队列忙
                busyShardIdList.add(entry.getKey());
            }
        }

        // 将队列满的线程池的集群拆分到不同的线程池中
        this.moveShardClusterToSuitableThreadPool(overflowShardIdList, shardIdPhysicalClusterIdListMap, withoutClusterShardIdList, idleShardIdList, notBusyShardIdList, true);

        // 将busy队列的线程池的集群拆分到不同的线程池中
        this.moveShardClusterToSuitableThreadPool(busyShardIdList, shardIdPhysicalClusterIdListMap, withoutClusterShardIdList, idleShardIdList, notBusyShardIdList, false);
    }

    private void moveShardClusterToSuitableThreadPool(List<Long> needMoveShardIdList,
                                                      Map<Long, List<Long>> shardIdPhysicalClusterIdListMap,
                                                      List<Long> withoutClusterShardIdList,
                                                      List<Long> idleShardIdList,
                                                      List<Long> notBusyShardIdList,
                                                      boolean clearTaskIfFullAndOnlyOneCluster) {
        for (Long needMoveShardId: needMoveShardIdList) {
            List<Long> physicalClusterIdList = shardIdPhysicalClusterIdListMap.get(needMoveShardId);
            if ((physicalClusterIdList == null || physicalClusterIdList.isEmpty() || physicalClusterIdList.size() == 1) && clearTaskIfFullAndOnlyOneCluster) {
                // 仅一个集群，并且满了，则清空任务，重新跑任务
                closeOldAndCreateNew(needMoveShardId);
                continue;
            }

            if (physicalClusterIdList == null) {
                // 无集群
                continue;
            }

            for (int idx = 0; idx < physicalClusterIdList.size() - 1; ++idx) {
                Long newSuitableShardId = this.selectAndEmptySuitableThreadPool(shardIdPhysicalClusterIdListMap, withoutClusterShardIdList, idleShardIdList, notBusyShardIdList);
                if (newSuitableShardId == null) {
                    LOGGER.info("without suitable job-thread-pool and return.");
                    return;
                }

                modifyPhysicalClusterIdAndShardIdCache(physicalClusterIdList.get(idx), newSuitableShardId);
            }
        }
    }

    private Long selectAndEmptySuitableThreadPool(Map<Long, List<Long>> shardIdPhysicalClusterIdListMap,
                                                  List<Long> withoutClusterShardIdList,
                                                  List<Long> idleShardIdList,
                                                  List<Long> notBusyShardIdList) {
        if (!withoutClusterShardIdList.isEmpty()) {
            // 先放入无集群任务的线程池
            return withoutClusterShardIdList.remove((int) 0);
        }

        // 上一条件不满足时，优先放入比较空闲的池子
        Long newShardId = this.selectAndEmptySuitableThreadPool(shardIdPhysicalClusterIdListMap, idleShardIdList);

        // 上一条件不满足时，最后尝试放入不忙的池子
        return newShardId == null? this.selectAndEmptySuitableThreadPool(shardIdPhysicalClusterIdListMap, notBusyShardIdList): newShardId;
    }

    private Long selectAndEmptySuitableThreadPool(Map<Long, List<Long>> shardIdPhysicalClusterIdListMap, List<Long> taskThreadPoolList) {
        if (taskThreadPoolList.size() < 2) {
            // 没有空闲的线程池队列
            return null;
        }

        // 将两个非忙的合并，空出一个新的交给需要的
        Long firstNotBusyShardId = taskThreadPoolList.remove((int) 0);
        Long secondNotBusyShardId = taskThreadPoolList.remove((int) 0);

        List<Long> physicalClusterIdList = shardIdPhysicalClusterIdListMap.get(secondNotBusyShardId);
        if (physicalClusterIdList == null || physicalClusterIdList.isEmpty()) {
            return null;
        }

        for (Long physicalClusterId: physicalClusterIdList) {
            modifyPhysicalClusterIdAndShardIdCache(physicalClusterId, firstNotBusyShardId);
        }

        return secondNotBusyShardId;
    }

    private synchronized Long modifyPhysicalClusterIdAndShardIdCache(Long physicalClusterId, Long shardId) {
        if (shardId == null) {
            shardId = shardIdx.incrementAndGet() % futureUtilNum;
        }

        PHYSICAL_CLUSTER_ID_SHARD_ID_CACHE.put(physicalClusterId, shardId);
        return shardId;
    }

    private synchronized FutureWaitUtil<Void> closeOldAndCreateNew(Long shardId) {
        // 新的
        FutureWaitUtil<Void> newFutureUtil = FutureWaitUtil.init(
                "MetricCollect-Shard-" + shardId,
                this.futureUtilThreadNum,
                this.futureUtilThreadNum,
                this.futureUtilQueueSize
        );

        // 存储新的，返回旧的
        FutureWaitUtil<Void> oldFutureUtil = SHARD_ID_FUTURE_UTIL_MAP.put(shardId, newFutureUtil);

        // 为空，则直接返回
        if (oldFutureUtil == null) {
            return newFutureUtil;
        }

        LOGGER.error("close old ThreadPoolExecutor and create new, shardId:{}.", shardId);
        try {
            oldFutureUtil.shutdownNow();
        } catch (Exception e) {
            LOGGER.error("close old ThreadPoolExecutor and create new, shutdownNow failed, shardId:{}.", shardId, e);
        }

        return newFutureUtil;
    }
}
