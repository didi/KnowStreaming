package com.xiaojukeji.kafka.manager.monitor.common.entry;

/**
 * @author zengqiao
 * @date 20/7/27
 */
public class NotifyGroup {
    private Long id;

    private String name;

    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "NotifyGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}