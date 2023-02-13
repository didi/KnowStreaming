package com.xiaojukeji.kafka.manager.common.entity.ao.ha;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class HaSwitchTopic {
    /**
     * 是否完成
     */
    private boolean finished;

    /**
     * 每一个Topic的状态
     */
    private Map<String, Integer> activeTopicSwitchStatusMap;

    public HaSwitchTopic(boolean finished) {
        this.finished = finished;
        this.activeTopicSwitchStatusMap = new HashMap<>();
    }

    public void addHaSwitchTopic(HaSwitchTopic haSwitchTopic) {
        this.finished &= haSwitchTopic.finished;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void addActiveTopicStatus(String activeTopicName, Integer status) {
        activeTopicSwitchStatusMap.put(activeTopicName, status);
    }

    public boolean isActiveTopicSwitchFinished(String activeTopicName) {
        Integer status = activeTopicSwitchStatusMap.get(activeTopicName);
        if (status == null) {
            return false;
        }

        return status.equals(HaStatusEnum.STABLE.getCode());
    }

    @Override
    public String toString() {
        return "HaSwitchTopic{" +
                "finished=" + finished +
                ", activeTopicSwitchStatusMap=" + activeTopicSwitchStatusMap +
                '}';
    }
}
