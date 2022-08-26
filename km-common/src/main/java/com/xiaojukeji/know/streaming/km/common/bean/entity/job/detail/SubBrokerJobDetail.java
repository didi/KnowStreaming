package com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail;

import lombok.Data;

import java.util.List;

@Data
public abstract class SubBrokerJobDetail extends SubJobDetail{
    /**
     * 源brokerId列表
     */
    private List<Integer> sourceBrokers;

    /**
     * 目的brokerId列表
     */
    private List<Integer> desBrokers;
}
