package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

/**
 * @author zengqiao
 * @date 20/8/11
 */
public class CustomScheduledTaskVO {
    private String name;

    private Object cron;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getCron() {
        return cron;
    }

    public void setCron(Object cron) {
        this.cron = cron;
    }

    @Override
    public String toString() {
        return "CustomScheduledTaskVO{" +
                "name='" + name + '\'' +
                ", cron=" + cron +
                '}';
    }
}