package com.xiaojukeji.kafka.manager.monitor.component.n9e;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.utils.HttpUtils;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.monitor.common.entry.*;
import com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.N9eResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 夜莺
 * @author zengqiao
 * @date 20/4/30
 */
@Service("abstractMonitor")
public class N9eService extends AbstractMonitorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(N9eService.class);

    @Value("${monitor.n9e.base-url}")
    private String monitorN9eBaseUrl;

    /**
     * 告警策略
     */
    private static final String STRATEGY_ADD_URL = "/auth/v1/strategy/add";

    private static final String STRATEGY_DEL_URL = "/auth/v1/strategy/del";

    private static final String STRATEGY_MODIFY_URL = "/auth/v1/strategy/modify";

    private static final String STRATEGY_QUERY_BY_NS_URL = "/auth/v1/strategy/query/ns";

    private static final String STRATEGY_QUERY_BY_ID_URL = "/auth/v1/strategy/query/id";

    private static final String ALERT_QUERY_BY_NS_AND_PERIOD_URL = "/auth/v1/event/query/ns/period";

    private static final String ALERT_QUERY_BY_ID_URL = "/auth/v1/event/query/id";

    /**
     * 告警屏蔽
     */
    private static final String SILENCE_ADD_URL = "/auth/v1/silence/add";

    private static final String SILENCE_RELEASE_URL = "/auth/v1/silence/release";

    private static final String SILENCE_MODIFY_URL = "/auth/v1/silence/modify";

    private static final String SILENCE_QUERY_BY_NS_URL = "/auth/v1/silence/query/ns";

    /**
     * 指标数据
     */
    private static final String COLLECTOR_SINK_DATA_URL = "/api/collector/push";

    private static final String COLLECTOR_DOWNLOAD_DATA_URL = "/data/query/graph/dashboard/history";

    /**
     * 告警组
     */
    private static final String ALL_NOTIFY_GROUP_URL = "/auth/v1/usergroup/group/all";

    /**
     * 监控策略的增删改查
     */
    @Override
    public Integer createStrategy(Strategy strategy) {
        return 0;
    }

    @Override
    public Boolean deleteStrategyById(Long strategyId) {
        return true;
    }

    @Override
    public Boolean modifyStrategy(Strategy strategy) {
        return true;
    }

    @Override
    public List<Strategy> getStrategies() {
        return new ArrayList<>();
    }

    @Override
    public Strategy getStrategyById(Long strategyId) {
        return new Strategy();
    }

    @Override
    public List<Alert> getAlerts(Long strategyId, Long startTime, Long endTime) {
        return new ArrayList<>();
    }

    @Override
    public Alert getAlertById(Long alertId) {
        return new Alert();
    }

    @Override
    public Boolean createSilence(Silence silence) {
        return true;
    }

    @Override
    public Boolean releaseSilence(Long silenceId) {
        return true;
    }

    @Override
    public Boolean modifySilence(Silence silence) {
        return true;
    }

    @Override
    public List<Silence> getSilences(Long strategyId) {
        return new ArrayList<>();
    }

    @Override
    public Silence getSilenceById(Long silenceId) {
        return new Silence();
    }

    @Override
    public Boolean sinkMetrics(List<MetricSinkPoint> pointList) {
        String response = null;
        try {
            String content = JSON.toJSONString(N9eConverter.convert2N9eMetricSinkPointList(pointList));

            long startTime = System.currentTimeMillis();
            response = HttpUtils.postForString(
                    monitorN9eBaseUrl + COLLECTOR_SINK_DATA_URL,
                    content,
                    null
            );
            LOGGER.info("sinkMetrics cost-time:{}.", System.currentTimeMillis() - startTime);

            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (n9eResult.getErr() != null && !n9eResult.getErr().isEmpty()) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            LOGGER.error("sink metrics failed, pointList:{}.", pointList, e);
        }
        return null;
    }

    @Override
    public Metric getMetrics(String metric, Long startTime, Long endTime, Integer step, Properties tags) {
        return new Metric();
    }

    @Override
    public List<NotifyGroup> getNotifyGroups() {
        return new ArrayList<>();
    }
}