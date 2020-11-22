package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/10/18
 */
public class N9eStrategy {
    private Integer id;

    private Integer category = 1;

    /**
     * 策略名称
     */
    private String name;

    /**
     * 策略关联的对象树节点id
     */
    private Integer nid;

    private List<Integer> excl_nid = new ArrayList<>();

    private Integer priority;

    private Integer alert_dur = 60;

    private List<N9eStrategyExpression> exprs;

    private List<N9eStrategyFilter> tags;

    private Integer recovery_dur;

    private Integer recovery_notify;

    private N9eStrategyAlertUpgrade alert_upgrade;

    private List<Integer> converge;

    private List<Integer> notify_group;

    private List<Integer> notify_user;

    private String callback;

    private String enable_stime;

    private String enable_etime;

    private List<Integer> enable_days_of_week;

    private Integer need_upgrade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNid() {
        return nid;
    }

    public void setNid(Integer nid) {
        this.nid = nid;
    }

    public List<Integer> getExcl_nid() {
        return excl_nid;
    }

    public void setExcl_nid(List<Integer> excl_nid) {
        this.excl_nid = excl_nid;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getAlert_dur() {
        return alert_dur;
    }

    public void setAlert_dur(Integer alert_dur) {
        this.alert_dur = alert_dur;
    }

    public List<N9eStrategyExpression> getExprs() {
        return exprs;
    }

    public void setExprs(List<N9eStrategyExpression> exprs) {
        this.exprs = exprs;
    }

    public List<N9eStrategyFilter> getTags() {
        return tags;
    }

    public void setTags(List<N9eStrategyFilter> tags) {
        this.tags = tags;
    }

    public Integer getRecovery_dur() {
        return recovery_dur;
    }

    public void setRecovery_dur(Integer recovery_dur) {
        this.recovery_dur = recovery_dur;
    }

    public Integer getRecovery_notify() {
        return recovery_notify;
    }

    public void setRecovery_notify(Integer recovery_notify) {
        this.recovery_notify = recovery_notify;
    }

    public N9eStrategyAlertUpgrade getAlert_upgrade() {
        return alert_upgrade;
    }

    public void setAlert_upgrade(N9eStrategyAlertUpgrade alert_upgrade) {
        this.alert_upgrade = alert_upgrade;
    }

    public List<Integer> getConverge() {
        return converge;
    }

    public void setConverge(List<Integer> converge) {
        this.converge = converge;
    }

    public List<Integer> getNotify_group() {
        return notify_group;
    }

    public void setNotify_group(List<Integer> notify_group) {
        this.notify_group = notify_group;
    }

    public List<Integer> getNotify_user() {
        return notify_user;
    }

    public void setNotify_user(List<Integer> notify_user) {
        this.notify_user = notify_user;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getEnable_stime() {
        return enable_stime;
    }

    public void setEnable_stime(String enable_stime) {
        this.enable_stime = enable_stime;
    }

    public String getEnable_etime() {
        return enable_etime;
    }

    public void setEnable_etime(String enable_etime) {
        this.enable_etime = enable_etime;
    }

    public List<Integer> getEnable_days_of_week() {
        return enable_days_of_week;
    }

    public void setEnable_days_of_week(List<Integer> enable_days_of_week) {
        this.enable_days_of_week = enable_days_of_week;
    }

    public Integer getNeed_upgrade() {
        return need_upgrade;
    }

    public void setNeed_upgrade(Integer need_upgrade) {
        this.need_upgrade = need_upgrade;
    }

    @Override
    public String toString() {
        return "N9eStrategy{" +
                "id=" + id +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", nid=" + nid +
                ", excl_nid=" + excl_nid +
                ", priority=" + priority +
                ", alert_dur=" + alert_dur +
                ", exprs=" + exprs +
                ", tags=" + tags +
                ", recovery_dur=" + recovery_dur +
                ", recovery_notify=" + recovery_notify +
                ", alert_upgrade=" + alert_upgrade +
                ", converge=" + converge +
                ", notify_group=" + notify_group +
                ", notify_user=" + notify_user +
                ", callback='" + callback + '\'' +
                ", enable_stime='" + enable_stime + '\'' +
                ", enable_etime='" + enable_etime + '\'' +
                ", enable_days_of_week=" + enable_days_of_week +
                ", need_upgrade=" + need_upgrade +
                '}';
    }
}