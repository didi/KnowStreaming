package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.po.BaseESPO;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.utils.NamedThreadFactory;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.BaseMetricESDAO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMetricESSender {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    private static final int    THRESHOLD = 100;

    private static final ThreadPoolExecutor esExecutor = new ThreadPoolExecutor(
            10,
            20,
            6000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(1000),
            new NamedThreadFactory("KM-Collect-MetricESSender-ES"),
            (r, e) -> LOGGER.warn("class=MetricESSender||msg=KM-Collect-MetricESSender-ES Deque is blocked, taskCount:{}" + e.getTaskCount())
    );

    /**
     * 根据不同监控维度来发送
     */
    protected boolean send2es(String index, List<? extends BaseESPO> statsList){
        if (CollectionUtils.isEmpty(statsList)) {
            return true;
        }

        if (!EnvUtil.isOnline()) {
            LOGGER.info("class=MetricESSender||method=send2es||ariusStats={}||size={}",
                    index, statsList.size());
        }

        BaseMetricESDAO baseMetricESDao = BaseMetricESDAO.getByStatsType(index);
        if (Objects.isNull( baseMetricESDao )) {
            LOGGER.error("class=MetricESSender||method=send2es||errMsg=fail to find {}", index);
            return false;
        }

        int size = statsList.size();
        int num  = (size) % THRESHOLD == 0 ? (size / THRESHOLD) : (size / THRESHOLD + 1);

        if (size < THRESHOLD) {
            esExecutor.execute(
                    () -> baseMetricESDao.batchInsertStats(statsList)
            );
            return true;
        }

        for (int i = 1; i < num + 1; i++) {
            int end   = (i * THRESHOLD) > size ? size : (i * THRESHOLD);
            int start = (i - 1) * THRESHOLD;

            esExecutor.execute(
                    () -> baseMetricESDao.batchInsertStats(statsList.subList(start, end))
            );
        }

        return true;
    }
}
