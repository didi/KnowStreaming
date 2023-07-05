package com.xiaojukeji.know.streaming.km.core.service.health.checker.group;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthDetectedInLatestMinutesConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.GroupParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupMetricService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.GroupMetricVersionItems.GROUP_METRIC_STATE;

@Service
public class HealthCheckGroupService extends AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(HealthCheckGroupService.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMetricService groupMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.GROUP_RE_BALANCE_TOO_FREQUENTLY.getConfigName(), this::checkReBalanceTooFrequently);
    }

    @Override
    public List<ClusterParam> getResList(Long clusterPhyId) {
        return groupService.getGroupsFromDB(clusterPhyId).stream().map(elem -> new GroupParam(clusterPhyId, elem)).collect(Collectors.toList());
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.GROUP;
    }

    @Override
    public Integer getDimensionCodeIfSupport(Long kafkaClusterPhyId) {
        return this.getHealthCheckDimensionEnum().getDimension();
    }

    /**
     * 检查Group re-balance太频繁
     */
    private HealthCheckResult checkReBalanceTooFrequently(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        GroupParam param = (GroupParam) paramTuple.getV1();
        HealthDetectedInLatestMinutesConfig singleConfig = (HealthDetectedInLatestMinutesConfig) paramTuple.getV2();

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.GROUP.getDimension(),
                HealthCheckNameEnum.GROUP_RE_BALANCE_TOO_FREQUENTLY.getConfigName(),
                param.getClusterPhyId(),
                param.getGroupName()
        );

        Result<Integer> countResult = groupMetricService.countMetricValueOccurrencesFromES(
                param.getClusterPhyId(),
                param.getGroupName(),
                new SearchTerm(GROUP_METRIC_STATE, GroupStateEnum.PREPARING_RE_BALANCE.getCode().toString(), true),
                System.currentTimeMillis() - singleConfig.getLatestMinutes() * 60L * 1000L,
                System.currentTimeMillis()
        );

        if (countResult.failed() || !countResult.hasData()) {
            log.error("method=checkReBalanceTooFrequently||param={}||config={}||result={}||errMsg=get metrics failed",
                    param, singleConfig, countResult);
            return null;
        }

        checkResult.setPassed(countResult.getData() >= singleConfig.getDetectedTimes() ? Constant.NO : Constant.YES);

        return checkResult;
    }
}
