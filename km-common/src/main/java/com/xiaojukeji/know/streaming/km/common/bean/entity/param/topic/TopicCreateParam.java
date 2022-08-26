package com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicCreateParam extends TopicParam {
    private Map<String, String> config;

    private Map<Integer, List<Integer>> assignmentMap;

    private String description;

    public TopicCreateParam(Long clusterPhyId, String topicName, Map<String, String> config, Map<Integer, List<Integer>> assignmentMap, String description) {
        super(clusterPhyId, topicName);
        this.config = config;
        this.assignmentMap = assignmentMap;
        this.description = description;
    }

    public Integer getReplicaNum() {
        if (assignmentMap == null || assignmentMap.isEmpty()) {
            return 0;
        }

        return new ArrayList<>(assignmentMap.values()).get(0).size();
    }

    public Set<Integer> getBrokerIdSet() {
        Set<Integer> brokerIdSet = new HashSet<>();
        if (assignmentMap == null || assignmentMap.isEmpty()) {
            return brokerIdSet;
        }

        assignmentMap.values().forEach(elem -> brokerIdSet.addAll(elem));

        return brokerIdSet;
    }

    public Result<Void> simpleCheckFieldIsNull() {
        if (ValidateUtils.isNullOrLessThanZero(clusterPhyId)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "clusterPhyId不允许为null或者小于0");
        }

        if (ValidateUtils.isBlank(topicName)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "topicName不允许为空");
        }

        if (ValidateUtils.isNull(config)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "config不允许为null");
        }

        if (ValidateUtils.isEmptyMap(assignmentMap)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "assignmentMap不允许为空");
        }

        if (ValidateUtils.isNull(description)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "description不允许为null");
        }

        return Result.buildSuc();
    }
}
