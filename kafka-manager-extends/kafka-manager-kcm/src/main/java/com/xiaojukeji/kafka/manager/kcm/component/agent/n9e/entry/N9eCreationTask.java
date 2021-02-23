package com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry;

import java.util.List;

public class N9eCreationTask {
    /**
     * 任务标题
     */
    private String title;

    /**
     * 并发度, =2则表示两台并发执行
     */
    private Integer batch;

    /**
     * 错误容忍度, 达到容忍度之上时, 任务会被暂停并不可以继续执行
     */
    private Integer tolerance;

    /**
     * 单台任务的超时时间(秒)
     */
    private Integer timeout;

    /**
     * 暂停点, 格式: host1,host2,host3
     */
    private String pause;

    /**
     * 任务执行对应的脚本
     */
    private String script;

    /**
     * 任务参数
     */
    private String args;

    /**
     * 使用的账号
     */
    private String account;

    /**
     * 动作
     */
    private String action;

    /**
     * 操作的主机列表
     */
    private List<String> hosts;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public Integer getTolerance() {
        return tolerance;
    }

    public void setTolerance(Integer tolerance) {
        this.tolerance = tolerance;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getPause() {
        return pause;
    }

    public void setPause(String pause) {
        this.pause = pause;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    @Override
    public String toString() {
        return "N9eCreationTask{" +
                "title='" + title + '\'' +
                ", batch=" + batch +
                ", tolerance=" + tolerance +
                ", timeout=" + timeout +
                ", pause='" + pause + '\'' +
                ", script='" + script + '\'' +
                ", args='" + args + '\'' +
                ", account='" + account + '\'' +
                ", action='" + action + '\'' +
                ", hosts=" + hosts +
                '}';
    }
}
