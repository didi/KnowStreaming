package com.xiaojukeji.know.streaming.km.collector.sink;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.po.BaseESPO;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.BaseMetricESDAO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

public abstract class AbstractMetricESSender {
    private static final ILog LOGGER = LogFactory.getLog(AbstractMetricESSender.class);

    private static final int THRESHOLD = 100;

    private static final FutureUtil<Void> esExecutor = FutureUtil.init(
            "MetricsESSender",
            10,
            20,
            10000
    );

    /**
     * 根据不同监控维度来发送
     */
    protected boolean send2es(String index, List<? extends BaseESPO> statsList) {
        LOGGER.info("method=send2es||indexName={}||metricsSize={}||msg=send metrics to es", index, statsList.size());

        if (CollectionUtils.isEmpty(statsList)) {
            return true;
        }

        BaseMetricESDAO baseMetricESDao = BaseMetricESDAO.getByStatsType(index);
        if (Objects.isNull(baseMetricESDao)) {
            LOGGER.error("method=send2es||indexName={}||errMsg=find dao failed", index);
            return false;
        }

        for (int i = 0; i < statsList.size(); i += THRESHOLD) {
            final int idxStart = i;

            // 异步发送
            esExecutor.submitTask(
                    () -> baseMetricESDao.batchInsertStats(statsList.subList(idxStart, Math.min(idxStart + THRESHOLD, statsList.size())))
            );
        }

        return true;
    }
}
