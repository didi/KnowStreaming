package com.xiaojukeji.kafka.manager.common.entity.ao.config;

import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/24
 */
public class CreateTopicConfig {
    /**
     * 单次自动化审批, 允许的通过单子
     */
    private Integer maxPassedOrderNumPerTask;

    private List<CreateTopicElemConfig> configList;

    public Integer getMaxPassedOrderNumPerTask() {
        if (ValidateUtils.isNull(maxPassedOrderNumPerTask)) {
            return TopicCreationConstant.DEFAULT_MAX_PASSED_ORDER_NUM_PER_TASK;
        }
        if (maxPassedOrderNumPerTask > TopicCreationConstant.MAX_PASSED_ORDER_NUM_PER_TASK) {
            return TopicCreationConstant.MAX_PASSED_ORDER_NUM_PER_TASK;
        }
        return maxPassedOrderNumPerTask;
    }

    public void setMaxPassedOrderNumPerTask(Integer maxPassedOrderNumPerTask) {
        this.maxPassedOrderNumPerTask = maxPassedOrderNumPerTask;
    }

    public List<CreateTopicElemConfig> getConfigList() {
        return configList;
    }

    public void setConfigList(List<CreateTopicElemConfig> configList) {
        this.configList = configList;
    }

    @Override
    public String toString() {
        return "CreateTopicConfig{" +
                "maxPassedOrderNumPerTask=" + maxPassedOrderNumPerTask +
                ", configList=" + configList +
                '}';
    }
}