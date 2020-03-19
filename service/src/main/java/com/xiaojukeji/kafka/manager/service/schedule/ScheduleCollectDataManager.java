package com.xiaojukeji.kafka.manager.service.schedule;

import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.service.collector.*;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 负责管理定时数据采集任务
 * @author zengqiao
 * @date 19/4/17
 */
@Component
public class ScheduleCollectDataManager {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleCollectDataManager.class);

    /**
     * 存储每个Cluster的schedulerExecutor
     */
    private static Map<Long, ScheduledExecutorService> clusterScheduleServiceMap = new HashMap<>();


    /**
     * 定时采集周期，单位为s
     */
    private Integer collectTaskTimeInterval = 60;

    @Autowired
    private ConsumerService consumeService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private JmxService jmxService;


    /**
     * 1.对每个Cluster新建一个scheduler
     * 2.启动每个scheduler
     */
    @PostConstruct
    public void init() {
        List<ClusterDO> clusterDOList = clusterService.listAll();
        if(clusterDOList == null){
            return;
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4 * Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "DataCollectorManager-ScheduleTask");
            }
        });
        for (ClusterDO clusterDO : clusterDOList) {
            clusterScheduleServiceMap.put(clusterDO.getId(), scheduler);
            scheduler.scheduleAtFixedRate(new CollectBrokerMetricsTask(clusterDO.getId(), jmxService), 1, collectTaskTimeInterval, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new CollectTopicMetricsTask(clusterDO.getId(), jmxService), 2, collectTaskTimeInterval, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new CollectConsumerMetricsTask(clusterDO.getId(), consumeService), 3, collectTaskTimeInterval, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new CollectConsumerGroupZKMetadataTask(clusterDO.getId()), 4, collectTaskTimeInterval, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new CollectConsumerGroupBKMetadataTask(clusterDO.getId()), 5, collectTaskTimeInterval, TimeUnit.SECONDS);
        }
    }

    /**
     * 启动指定Cluster的定时数据采集任务
     */
    public void start(final ClusterDO cluster) {
        ScheduledExecutorService scheduler = clusterScheduleServiceMap.get(cluster.getId());
        if (scheduler == null || scheduler.isTerminated() || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(20, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "DataCollectorManager-ScheduleTask-ClusterId-" + cluster.getId());
                }
            });
            clusterScheduleServiceMap.put(cluster.getId(), scheduler);
        } else {
            return;
        }
        scheduler.scheduleAtFixedRate(new CollectBrokerMetricsTask(cluster.getId(), jmxService), 30, collectTaskTimeInterval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new CollectTopicMetricsTask(cluster.getId(), jmxService), 60, collectTaskTimeInterval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new CollectConsumerMetricsTask(cluster.getId(), consumeService), 90, collectTaskTimeInterval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new CollectConsumerGroupZKMetadataTask(cluster.getId()), 90, collectTaskTimeInterval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new CollectConsumerGroupBKMetadataTask(cluster.getId()), 90, collectTaskTimeInterval, TimeUnit.SECONDS);
    }

    /**
     * 停止Cluster的定时数据采集任务
     */
    public void stop(ClusterDO clusterDO) {
        ScheduledExecutorService scheduler = clusterScheduleServiceMap.get(clusterDO.getId());
        if (scheduler != null) {
            scheduler.shutdown();
            clusterScheduleServiceMap.remove(clusterDO.getId());
        }
    }
}
