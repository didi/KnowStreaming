package com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicPartitionExpandParam extends TopicParam {
    private Map<Integer, List<Integer>> existingAssignment;

    private Map<Integer, List<Integer>> newPartitionAssignment;

    public TopicPartitionExpandParam(Long clusterPhyId,
                                     String topicName,
                                     Map<Integer, List<Integer>> existingAssignment,
                                     Map<Integer, List<Integer>> newPartitionAssignment) {
        super(clusterPhyId, topicName);
        this.existingAssignment = existingAssignment;
        this.newPartitionAssignment = newPartitionAssignment;
    }

    public Result<Void> simpleCheckFieldIsNull() {
        if (ValidateUtils.isNullOrLessThanZero(clusterPhyId)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "clusterPhyId不允许为null或者小于0");
        }

        if (ValidateUtils.isBlank(topicName)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "topicName不允许为空");
        }

        if (ValidateUtils.isEmptyMap(existingAssignment)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "existingAssignment不允许为空");
        }

        if (ValidateUtils.isEmptyMap(newPartitionAssignment)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "newPartitionAssignment不允许为空");
        }

        return Result.buildSuc();
    }

    public Integer getExistPartitionNum() {
        return existingAssignment.size();
    }

    public Integer getTotalPartitionNum() {
        return existingAssignment.size() + newPartitionAssignment.size();
    }
}
