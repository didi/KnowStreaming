package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/10/19
 */
public class N9eStrategyAlertUpgrade {
    private Integer duration = 60;

    private Integer level = 1;

    private List<Integer> users = new ArrayList<>();

    private List<Integer> groups = new ArrayList<>();

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "N9eStrategyAlertUpgrade{" +
                "duration=" + duration +
                ", level=" + level +
                ", users=" + users +
                ", groups=" + groups +
                '}';
    }
}