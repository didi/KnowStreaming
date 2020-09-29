package com.xiaojukeji.kafka.manager.bpm.common;

import com.xiaojukeji.kafka.manager.common.entity.Result;

public class OrderResult {
    private Long id;

    private Result result;

    public OrderResult() {
    }

    public OrderResult(Long id, Result result) {
        this.id = id;
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "OrderResult{" +
                "id=" + id +
                ", result=" + result +
                '}';
    }
}
