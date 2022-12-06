package com.xiaojukeji.know.streaming.km.persistence.connect.schedule;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.connect.ConnectClusterLoadChangedEvent;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectClusterDAO;
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

/**
 * @author wyb
 * @date 2022/11/7
 */
@Component
public class ScheduleFlushConnectClusterTask {
    private static final ILog log = LogFactory.getLog(ScheduleFlushConnectClusterTask.class);
    
    @Autowired
    private ConnectClusterDAO connectClusterDAO;

    private final BlockingQueue<ConnectClusterLoadChangedEvent> eventQueue = new LinkedBlockingQueue<>(2000);

    private final Thread handleEventThread = new Thread(() -> handleEvent(), "ScheduleFlushConnectClusterTask");

    @PostConstruct
    public void init() {
        // 启动线程
        handleEventThread.start();

        // 立即加载集群
        flush();
    }

    @Scheduled(cron="0/10 * * * * ?")
    public void flush() {
        List<ConnectCluster> inDBConnectClusterList = ConvertUtil.list2List(connectClusterDAO.selectList(null), ConnectCluster.class);
        Map<Long, ConnectCluster> inDBConnectClusterMap = inDBConnectClusterList.stream().collect(Collectors.toMap(ConnectCluster::getId, Function.identity()));

        //排查新增
        for (ConnectCluster inDBConnectCluster : inDBConnectClusterList) {
            ConnectCluster inCacheConnectCluster = LoadedConnectClusterCache.getByPhyId(inDBConnectCluster.getId());
            //存在，查看是否需要替换
            if (inCacheConnectCluster != null) {
                if (inCacheConnectCluster.equals(inDBConnectCluster)) {
                    continue;
                }
                LoadedConnectClusterCache.replace(inCacheConnectCluster);
                this.put2Queue(new ConnectClusterLoadChangedEvent(this, inDBConnectCluster, inCacheConnectCluster, OperationEnum.EDIT));

            } else {
                LoadedConnectClusterCache.replace(inDBConnectCluster);
                this.put2Queue(new ConnectClusterLoadChangedEvent(this, inDBConnectCluster, null, OperationEnum.ADD));
            }

        }

        //排查删除
        for (ConnectCluster inCacheConnectCluster : LoadedConnectClusterCache.listAll().values()) {
            if (inDBConnectClusterMap.containsKey(inCacheConnectCluster.getId())) {
                continue;
            }
            LoadedConnectClusterCache.remove(inCacheConnectCluster.getId());
            this.put2Queue(new ConnectClusterLoadChangedEvent(this, null, inCacheConnectCluster, OperationEnum.DELETE));
        }

    }


    private void put2Queue(ConnectClusterLoadChangedEvent event) {
        try {
            eventQueue.put(event);
        } catch (Exception e) {
            log.error("method=put2Queue||event={}||errMsg=exception", event, e);
        }
    }




    private void handleEvent() {
        while (true) {
            try {
                ConnectClusterLoadChangedEvent event = eventQueue.take();
                SpringTool.publish(event);
            } catch (Exception e) {
                log.error("method=handleEvent||errMsg=exception", e);
            }
        }
    }
}
