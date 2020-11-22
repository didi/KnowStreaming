package com.xiaojukeji.kafka.manager.monitor.component.n9e;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.kafka.manager.common.utils.HttpUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.monitor.common.entry.*;
import com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.N9eNotifyGroup;
import com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.N9eResult;
import com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.N9eStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 夜莺
 * @author zengqiao
 * @date 20/4/30
 */
@Service("abstractMonitor")
public class N9eService extends AbstractMonitorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(N9eService.class);

    @Value("${monitor.n9e.nid}")
    private Integer monitorN9eNid;

    @Value("${monitor.n9e.user-token}")
    private String monitorN9eUserToken;

    @Value("${monitor.n9e.mon.base-url}")
    private String monitorN9eMonBaseUrl;

    @Value("${monitor.n9e.sink.base-url}")
    private String monitorN9eSinkBaseUrl;

    @Value("${monitor.n9e.rdb.base-url}")
    private String monitorN9eRdbBaseUrl;

    private static final Cache<String, NotifyGroup> NOTIFY_GROUP_CACHE = Caffeine.newBuilder()
            .maximumSize(100000)
            .expireAfterWrite(60, TimeUnit.MINUTES).build();

    /**
     * 告警策略
     */
    private static final String STRATEGY_ADD_URL = "/api/mon/stra";

    private static final String STRATEGY_DEL_URL = "/api/mon/stra";

    private static final String STRATEGY_MODIFY_URL = "/api/mon/stra";

    private static final String STRATEGY_QUERY_BY_NS_URL = "/api/mon/stra";

    private static final String STRATEGY_QUERY_BY_ID_URL = "/api/mon/stra";


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
    private static final String COLLECTOR_SINK_DATA_URL = "/api/transfer/push";

    private static final String COLLECTOR_DOWNLOAD_DATA_URL = "/data/query/graph/dashboard/history";

    /**
     * 告警组
     */
    private static final String ALL_NOTIFY_GROUP_URL = "/api/rdb/teams/all?limit=10000";

    /**
     * 监控策略的增删改查
     */
    @Override
    public Integer createStrategy(Strategy strategy) {
        String response = null;
        try {
            response = HttpUtils.postForString(
                    monitorN9eMonBaseUrl + STRATEGY_ADD_URL,
                    JSON.toJSONString(N9eConverter.convert2N9eStrategy(strategy, monitorN9eNid, getNotifyGroupsFromCacheFirst())),
                    buildHeader()
            );
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("create strategy failed, strategy:{} response:{}.", strategy, response);
                return null;
            }
            return JSON.parseObject(JSON.toJSONString(n9eResult.getDat())).getInteger("id");
        } catch (Exception e) {
            LOGGER.error("create strategy failed, strategy:{} response:{}.", strategy, response, e);
        }
        return null;
    }

    @Override
    public Boolean deleteStrategyById(Long strategyId) {
        Map<String, List<Long>> params = new HashMap<>(1);
        params.put("ids", Arrays.asList(strategyId));

        String response = null;
        try {
            response = HttpUtils.deleteForString(
                    monitorN9eMonBaseUrl + STRATEGY_DEL_URL,
                    JSON.toJSONString(params),
                    buildHeader()
            );
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("delete strategy failed, strategyId:{} response:{}.", strategyId, response);
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            LOGGER.error("delete strategy failed, strategyId:{} response:{}.", strategyId, response, e);
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean modifyStrategy(Strategy strategy) {
        String response = null;
        try {
            response = HttpUtils.putForString(
                    monitorN9eMonBaseUrl + STRATEGY_MODIFY_URL,
                    JSON.toJSONString(N9eConverter.convert2N9eStrategy(strategy, monitorN9eNid, getNotifyGroupsFromCacheFirst())),
                    buildHeader()
            );
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("modify strategy failed, strategy:{} response:{}.", strategy, response);
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            LOGGER.error("modify strategy failed, strategy:{} response:{}.", strategy, response, e);
        }
        return Boolean.FALSE;
    }

    @Override
    public List<Strategy> getStrategies() {
        Map<String, String> params = new HashMap<>();
        params.put("nid", String.valueOf(monitorN9eNid));

        String response = null;
        try {
            response = HttpUtils.get(monitorN9eMonBaseUrl + STRATEGY_QUERY_BY_NS_URL, params, buildHeader());
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("get monitor strategies failed, response:{}.", response);
                return new ArrayList<>();
            }
            return N9eConverter.convert2StrategyList(JSON.parseArray(JSON.toJSONString(n9eResult.getDat()), N9eStrategy.class), getNotifyGroupsFromCacheFirst());
        } catch (Exception e) {
            LOGGER.error("get monitor strategies failed, response:{}.", response, e);
        }
        return new ArrayList<>();
    }

    @Override
    public Strategy getStrategyById(Long strategyId) {
        String uri = STRATEGY_QUERY_BY_ID_URL + "/" + String.valueOf(strategyId);

        String response = null;
        try {
            response = HttpUtils.get(monitorN9eMonBaseUrl + uri, new HashMap<>(0), buildHeader());
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("get monitor strategy failed, response:{}.", response);
                return null;
            }
            return N9eConverter.convert2Strategy(JSON.parseObject(JSON.toJSONString(n9eResult.getDat()), N9eStrategy.class), getNotifyGroupsFromCacheFirst());
        } catch (Exception e) {
            LOGGER.error("get monitor strategy failed, response:{}.", response, e);
        }
        return null;
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
            String content = JSON.toJSONString(N9eConverter.convert2N9eMetricSinkPointList(String.valueOf(this.monitorN9eNid), pointList));

            long startTime = System.currentTimeMillis();
            response = HttpUtils.postForString(
                    monitorN9eSinkBaseUrl + COLLECTOR_SINK_DATA_URL,
                    content,
                    buildHeader()
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
        String response = null;
        try {
            response = HttpUtils.get(monitorN9eRdbBaseUrl + ALL_NOTIFY_GROUP_URL, new HashMap<>(0), buildHeader());
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("get notify group failed, response:{}.", response);
                return new ArrayList<>();
            }
            List<NotifyGroup> notifyGroupList = N9eConverter.convert2NotifyGroupList(JSON.parseObject(JSON.toJSONString(n9eResult.getDat()), N9eNotifyGroup.class));
            if (ValidateUtils.isEmptyList(notifyGroupList)) {
                return new ArrayList<>();
            }
            for (NotifyGroup notifyGroup: notifyGroupList) {
                NOTIFY_GROUP_CACHE.put(notifyGroup.getName(), notifyGroup);
            }
            return notifyGroupList;
        } catch (Exception e) {
            LOGGER.error("get notify group failed, response:{}.", response, e);
        }
        return new ArrayList<>();
    }

    private Map<String, NotifyGroup> getNotifyGroupsFromCacheFirst() {
        Map<String, NotifyGroup> notifyGroupMap = NOTIFY_GROUP_CACHE.asMap();
        if (ValidateUtils.isEmptyMap(notifyGroupMap)) {
            this.getNotifyGroups();
        }
        return NOTIFY_GROUP_CACHE.asMap();
    }

    private Map<String, String> buildHeader() {
        Map<String, String> header = new HashMap<>(2);
        header.put("Content-Type", "application/json");
        header.put("X-User-Token", monitorN9eUserToken);
        return header;
    }
}
