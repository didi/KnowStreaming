package com.xiaojukeji.kafka.manager.common.entity.dto.rd;

/**
 * @author zengqiao
 * @date 20/8/11
 */
public class CustomScheduledTaskDTO {
    private String name;

    private String cron;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Override
    public String toString() {
        return "CustomScheduledTaskDTO{" +
                "name='" + name + '\'' +
                ", cron='" + cron + '\'' +
                '}';
    }
}