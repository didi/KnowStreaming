package com.xiaojukeji.know.streaming.km.persistence.schedule;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.ClusterPhyLoadChangedEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.cluster.ClusterPhyPO;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.mysql.cluster.ClusterPhyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ScheduleFlushClusterTask {
    private static final ILog log = LogFactory.getLog(ScheduleFlushClusterTask.class);

    @Autowired
    private ClusterPhyDAO clusterPhyDAO;

    private final BlockingQueue<ClusterPhyLoadChangedEvent> eventQueue = new LinkedBlockingQueue<>(2000);

    private final Thread handleEventThread = new Thread(() -> handleEvent(), "ScheduleFlushClusterTaskThread");

    @PostConstruct
    public void init() {
        // 启动线程
        handleEventThread.start();

        // 立即加载集群
        flush();
    }

    @Scheduled(cron="0/10 * * * * ?")
    public void flush() {
        // 查询DB数据
        List<ClusterPhyPO> poList = clusterPhyDAO.selectList(null);

        // 转Map
        Map<Long, ClusterPhy> inDBClusterMap = ConvertUtil.list2List(poList, ClusterPhy.class).stream().collect(Collectors.toMap(ClusterPhy::getId, Function.identity(), (key1, key2) -> key2));

        // 新增的集群
        for (ClusterPhy inDBClusterPhy: inDBClusterMap.values()) {
            if (LoadedClusterPhyCache.containsByPhyId(inDBClusterPhy.getId())) {
                // 已经存在
                continue;
            }

            LoadedClusterPhyCache.replace(inDBClusterPhy);
            this.put2Queue(new ClusterPhyLoadChangedEvent(this, inDBClusterPhy, null, OperationEnum.ADD));
        }

        // 移除的集群
        for (ClusterPhy inCacheClusterPhy: LoadedClusterPhyCache.listAll().values()) {
            if (inDBClusterMap.containsKey(inCacheClusterPhy.getId())) {
                // 已经存在
                continue;
            }

            LoadedClusterPhyCache.remove(inCacheClusterPhy.getId());
            this.put2Queue(new ClusterPhyLoadChangedEvent(this, null, inCacheClusterPhy, OperationEnum.DELETE));
        }

        // 被修改配置的集群
        for (ClusterPhy inDBClusterPhy: inDBClusterMap.values()) {
            ClusterPhy inCacheClusterPhy = LoadedClusterPhyCache.getByPhyId(inDBClusterPhy.getId());
            if (inCacheClusterPhy == null || inDBClusterPhy.equals(inCacheClusterPhy)) {
                // 不存在 || 相等
                continue;
            }

            LoadedClusterPhyCache.replace(inDBClusterPhy);
            this.put2Queue(new ClusterPhyLoadChangedEvent(this, inDBClusterPhy, inCacheClusterPhy, OperationEnum.EDIT));
        }
    }

    private void put2Queue(ClusterPhyLoadChangedEvent event) {
        try {
            eventQueue.put(event);
        } catch (Exception e) {
            log.error("method=put2Queue||event={}||errMsg=exception", event, e);
        }
    }

    private void handleEvent() {
        while (true) {
            try {
                ClusterPhyLoadChangedEvent event = eventQueue.take();

                SpringTool.publish(event);
            } catch (Exception e) {
                log.error("method=handleEvent||errMsg=exception", e);
            }
        }
    }
}
