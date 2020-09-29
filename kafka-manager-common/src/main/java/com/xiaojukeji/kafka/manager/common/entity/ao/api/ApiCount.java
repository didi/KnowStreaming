package com.xiaojukeji.kafka.manager.common.entity.ao.api;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zengqiao
 * @date 20/7/27
 */
public class ApiCount {
    private int apiLevel;

    private Integer maxNum;

    private AtomicInteger currentNum;

    public ApiCount(int apiLevel, Integer maxNum, AtomicInteger currentNum) {
        this.apiLevel = apiLevel;
        this.maxNum = maxNum;
        this.currentNum = currentNum;
    }

    public int getApiLevel() {
        return apiLevel;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public AtomicInteger getCurrentNum() {
        return currentNum;
    }

    public Boolean incAndCheckIsOverFlow() {
        return maxNum < currentNum.incrementAndGet();
    }

    public int decPresentNum() {
        return currentNum.decrementAndGet();
    }

    @Override
    public String toString() {
        return "ApiCount{" +
                "apiLevel=" + apiLevel +
                ", maxNum=" + maxNum +
                ", currentNum=" + currentNum +
                '}';
    }
}