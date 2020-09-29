package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/8/28
 */
public class ClusterTaskDetailDO {
    private Long id;

    private Long taskId;

    private String hostname;

    private Integer groupNum;

    private Date execTime;

    private Date rollbackTime;

    private Date createTime;

    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(Integer groupNum) {
        this.groupNum = groupNum;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public Date getRollbackTime() {
        return rollbackTime;
    }

    public void setRollbackTime(Date rollbackTime) {
        this.rollbackTime = rollbackTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "ClusterTaskDetailDO{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", hostname='" + hostname + '\'' +
                ", groupNum=" + groupNum +
                ", execTime=" + execTime +
                ", rollbackTime=" + rollbackTime +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}