package com.xiaojukeji.know.streaming.km.common.bean.entity.common;

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Getter
public class FutureTaskDelayQueueData<T> implements Delayed {
    private final String taskName;

    private final Future<T> futureTask;

    private final long timeoutTimeUnitMs;

    private final long createTimeUnitMs;

    public FutureTaskDelayQueueData(String taskName, Future<T> futureTask, long timeoutTimeUnitMs) {
        this.taskName = taskName;
        this.futureTask = futureTask;
        this.timeoutTimeUnitMs = timeoutTimeUnitMs;
        this.createTimeUnitMs = System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(timeoutTimeUnitMs - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        FutureTaskDelayQueueData<T> other = (FutureTaskDelayQueueData<T>) delayed;
        if (this.timeoutTimeUnitMs == other.timeoutTimeUnitMs) {
            return (this.timeoutTimeUnitMs + "_" + this.createTimeUnitMs).compareTo((other.timeoutTimeUnitMs + "_" + other.createTimeUnitMs));
        }

        return (this.timeoutTimeUnitMs - other.timeoutTimeUnitMs) <= 0 ? -1: 1;
    }
}
