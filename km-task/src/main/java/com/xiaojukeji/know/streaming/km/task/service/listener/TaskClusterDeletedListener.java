package com.xiaojukeji.know.streaming.km.task.service.listener;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.connect.ClusterPhyDeletedEvent;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.core.service.meta.MetaDataService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class TaskClusterDeletedListener implements ApplicationListener<ClusterPhyDeletedEvent> {
    private static final ILog LOGGER = LogFactory.getLog(TaskClusterDeletedListener.class);

    @Override
    public void onApplicationEvent(ClusterPhyDeletedEvent event) {
        LOGGER.info("method=onApplicationEvent||clusterPhyId={}||msg=listened delete cluster", event.getClusterPhyId());

        // 交由KS自定义的线程池，异步执行任务
        FutureUtil.quickStartupFutureUtil.submitTask(
                () -> {
                    // 延迟60秒，避免正在运行的任务，将数据写入DB中
                    BackoffUtils.backoff(60000);

                    for (MetaDataService metaDataService: SpringTool.getBeansOfType(MetaDataService.class).values()) {
                        LOGGER.info(
                                "method=onApplicationEvent||clusterPhyId={}||className={}||msg=delete cluster data in db starting",
                                event.getClusterPhyId(), metaDataService.getClass().getSimpleName()
                        );

                        try {
                            // 删除数据
                            metaDataService.deleteInDBByKafkaClusterId(event.getClusterPhyId());

                            LOGGER.info(
                                    "method=onApplicationEvent||clusterPhyId={}||className={}||msg=delete cluster data in db finished",
                                    event.getClusterPhyId(), metaDataService.getClass().getSimpleName()
                            );
                        } catch (Exception e) {
                            LOGGER.error(
                                    "method=onApplicationEvent||clusterPhyId={}||className={}||msg=delete cluster data in db failed||errMsg=exception",
                                    event.getClusterPhyId(), metaDataService.getClass().getSimpleName(), e
                            );
                        }
                    }
                }
        );


    }
}
