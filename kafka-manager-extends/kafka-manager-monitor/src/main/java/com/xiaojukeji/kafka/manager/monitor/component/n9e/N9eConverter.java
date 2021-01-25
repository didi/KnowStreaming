package com.xiaojukeji.kafka.manager.monitor.component.n9e;

import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.monitor.common.entry.*;
import com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.*;
import com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.bizenum.CategoryEnum;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/8/26
 */
public class N9eConverter {
    public static List<N9eMetricSinkPoint> convert2N9eMetricSinkPointList(String nid, List<MetricSinkPoint> pointList) {
        if (pointList == null || pointList.isEmpty()) {
            return new ArrayList<>();
        }
        List<N9eMetricSinkPoint> n9ePointList = new ArrayList<>();
        for (MetricSinkPoint sinkPoint: pointList) {
            n9ePointList.add(new N9eMetricSinkPoint(
                    nid,
                    sinkPoint.getName(),
                    sinkPoint.getValue(),
                    sinkPoint.getStep(),
                    sinkPoint.getTimestamp(),
                    sinkPoint.getTags()
            ));
        }
        return n9ePointList;
    }

    public static N9eStrategy convert2N9eStrategy(Strategy strategy,
                                                  Integer monitorN9eNid,
                                                  Map<String, NotifyGroup> notifyGroupMap) {
        if (strategy == null) {
            return null;
        }
        if (ValidateUtils.isNull(notifyGroupMap)) {
            notifyGroupMap = new HashMap<>();
        }

        N9eStrategy n9eStrategy = new N9eStrategy();
        if (!ValidateUtils.isNull(strategy.getId())) {
            n9eStrategy.setId(strategy.getId().intValue());
        }
        n9eStrategy.setCategory(CategoryEnum.DEVICE_INDEPENDENT.getCode());
        n9eStrategy.setName(strategy.getName());
        n9eStrategy.setNid(monitorN9eNid);
        n9eStrategy.setExcl_nid(new ArrayList<>());
        n9eStrategy.setPriority(strategy.getPriority());
        n9eStrategy.setAlert_dur(60);

        List<N9eStrategyExpression> exprs = new ArrayList<>();
        for (StrategyExpression strategyExpression: strategy.getStrategyExpressionList()) {
            N9eStrategyExpression n9eStrategyExpression = new N9eStrategyExpression();
            n9eStrategyExpression.setMetric(strategyExpression.getMetric());
            n9eStrategyExpression.setFunc(strategyExpression.getFunc());
            n9eStrategyExpression.setEopt(strategyExpression.getEopt());
            n9eStrategyExpression.setThreshold(strategyExpression.getThreshold().intValue());
            n9eStrategyExpression.setParams(ListUtils.string2IntList(strategyExpression.getParams()));
            exprs.add(n9eStrategyExpression);
        }
        n9eStrategy.setExprs(exprs);

        List<N9eStrategyFilter> tags = new ArrayList<>();
        for (StrategyFilter strategyFilter: strategy.getStrategyFilterList()) {
            N9eStrategyFilter n9eStrategyFilter = new N9eStrategyFilter();
            n9eStrategyFilter.setTkey(strategyFilter.getTkey());
            n9eStrategyFilter.setTopt(strategyFilter.getTopt());
            n9eStrategyFilter.setTval(Arrays.asList(strategyFilter.getTval()));
            tags.add(n9eStrategyFilter);
        }
        n9eStrategy.setTags(tags);

        n9eStrategy.setRecovery_dur(0);
        n9eStrategy.setRecovery_notify(0);

        StrategyAction strategyAction = strategy.getStrategyActionList().get(0);

        //  单位转换, 夜莺的单位是秒, KM前端的单位是分钟
        List<Integer> convergeList = ListUtils.string2IntList(strategyAction.getConverge());
        if (!ValidateUtils.isEmptyList(convergeList)) {
            convergeList.set(0, convergeList.get(0) *  60);
        }
        n9eStrategy.setConverge(convergeList);

        List<Integer> notifyGroups = new ArrayList<>();
        for (String name: ListUtils.string2StrList(strategyAction.getNotifyGroup())) {
            NotifyGroup notifyGroup = notifyGroupMap.get(name);
            if (ValidateUtils.isNull(notifyGroup)) {
                continue;
            }
            notifyGroups.add(notifyGroup.getId().intValue());
        }
        n9eStrategy.setNotify_group(notifyGroups);

        n9eStrategy.setNotify_user(new ArrayList<>());
        n9eStrategy.setCallback(strategyAction.getCallback());
        n9eStrategy.setEnable_stime("00:00");
        n9eStrategy.setEnable_etime("23:59");
        n9eStrategy.setEnable_days_of_week(ListUtils.string2IntList(strategy.getPeriodDaysOfWeek()));

        n9eStrategy.setNeed_upgrade(0);
        n9eStrategy.setAlert_upgrade(new N9eStrategyAlertUpgrade());
        return n9eStrategy;
    }

    public static List<Strategy> convert2StrategyList(List<N9eStrategy> n9eStrategyList,
                                                      Map<String, NotifyGroup> notifyGroupMap) {
        if (n9eStrategyList == null || n9eStrategyList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Strategy> strategyList = new ArrayList<>();
        for (N9eStrategy n9eStrategy: n9eStrategyList) {
            strategyList.add(convert2Strategy(n9eStrategy, notifyGroupMap));
        }
        return strategyList;
    }

    public static Strategy convert2Strategy(N9eStrategy n9eStrategy, Map<String, NotifyGroup> notifyGroupMap) {
        if (n9eStrategy == null) {
            return null;
        }
        if (ValidateUtils.isNull(notifyGroupMap)) {
            notifyGroupMap = new HashMap<>();
        }

        Map<Integer, NotifyGroup> newNotifyGroupMap = new HashMap<>(notifyGroupMap.size());
        for (NotifyGroup notifyGroup: notifyGroupMap.values()) {
            newNotifyGroupMap.put(notifyGroup.getId().intValue(), notifyGroup);
        }

        Strategy strategy = new Strategy();
        strategy.setId(n9eStrategy.getId().longValue());
        strategy.setName(n9eStrategy.getName());
        strategy.setPriority(n9eStrategy.getPriority());
        strategy.setPeriodHoursOfDay("0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23");
        strategy.setPeriodDaysOfWeek(ListUtils.intList2String(n9eStrategy.getEnable_days_of_week()));

        List<StrategyExpression> strategyExpressionList = new ArrayList<>();
        for (N9eStrategyExpression n9eStrategyExpression: n9eStrategy.getExprs()) {
            StrategyExpression strategyExpression = new StrategyExpression();
            strategyExpression.setMetric(n9eStrategyExpression.getMetric());
            strategyExpression.setFunc(n9eStrategyExpression.getFunc());
            strategyExpression.setEopt(n9eStrategyExpression.getEopt());
            strategyExpression.setThreshold(n9eStrategyExpression.getThreshold().longValue());
            strategyExpression.setParams(ListUtils.intList2String(n9eStrategyExpression.getParams()));
            strategyExpressionList.add(strategyExpression);
        }
        strategy.setStrategyExpressionList(strategyExpressionList);

        List<StrategyFilter> strategyFilterList = new ArrayList<>();
        for (N9eStrategyFilter n9eStrategyFilter: n9eStrategy.getTags()) {
            StrategyFilter strategyFilter = new StrategyFilter();
            strategyFilter.setTkey(n9eStrategyFilter.getTkey());
            strategyFilter.setTopt(n9eStrategyFilter.getTopt());
            strategyFilter.setTval(ListUtils.strList2String(n9eStrategyFilter.getTval()));
            strategyFilterList.add(strategyFilter);
        }
        strategy.setStrategyFilterList(strategyFilterList);

        StrategyAction strategyAction = new StrategyAction();

        List<String> notifyGroups = new ArrayList<>();
        for (Integer id: n9eStrategy.getNotify_group()) {
            NotifyGroup notifyGroup = newNotifyGroupMap.get(id);
            if (ValidateUtils.isNull(notifyGroup)) {
                continue;
            }
            notifyGroups.add(notifyGroup.getName());
        }
        strategyAction.setNotifyGroup(ListUtils.strList2String(notifyGroups));

        //  单位转换, 夜莺的单位是秒, KM前端的单位是分钟
        List<Integer> convergeList = n9eStrategy.getConverge();
        if (!ValidateUtils.isEmptyList(convergeList)) {
            convergeList.set(0, convergeList.get(0) / 60);
        }
        strategyAction.setConverge(ListUtils.intList2String(convergeList));

        strategyAction.setCallback(n9eStrategy.getCallback());
        strategy.setStrategyActionList(Arrays.asList(strategyAction));

        return strategy;
    }

    public static List<NotifyGroup> convert2NotifyGroupList(N9eNotifyGroup n9eNotifyGroup) {
        if (n9eNotifyGroup == null || n9eNotifyGroup.getList() == null) {
            return new ArrayList<>();
        }

        List<NotifyGroup> notifyGroupList = new ArrayList<>();
        for (N9eNotifyGroupElem n9eNotifyGroupElem: n9eNotifyGroup.getList()) {
            NotifyGroup notifyGroup = new NotifyGroup();
            notifyGroup.setId(n9eNotifyGroupElem.getId().longValue());
            notifyGroup.setName(n9eNotifyGroupElem.getName());
            notifyGroup.setComment(n9eNotifyGroupElem.getNote());
            notifyGroupList.add(notifyGroup);
        }
        return notifyGroupList;
    }
}