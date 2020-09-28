package com.xiaojukeji.kafka.manager.common.entity.vo.normal.app;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/8/13
 */
@Deprecated
public class DeprecatedAppVO {
    private Long id;

    private Date gmtCreate;

    private Date gmtModify;

    private String appId;

    private String name;

    private String password;

    private String type = "离线应用";

    private String applicant;

    private String principal;

    private String department = "";

    private Long department_id = null;

    private String description;

    private String approveUser = "";

    private String approveTime = "";

    private String approveInfo = "";

    private Integer status = 1;

    private String bpmInstanceId = "";

    private Boolean lastUsed = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveInfo() {
        return approveInfo;
    }

    public void setApproveInfo(String approveInfo) {
        this.approveInfo = approveInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBpmInstanceId() {
        return bpmInstanceId;
    }

    public void setBpmInstanceId(String bpmInstanceId) {
        this.bpmInstanceId = bpmInstanceId;
    }

    public Boolean getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Boolean lastUsed) {
        this.lastUsed = lastUsed;
    }

    @Override
    public String toString() {
        return "DeprecatedAppVO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", applicant='" + applicant + '\'' +
                ", principal='" + principal + '\'' +
                ", department='" + department + '\'' +
                ", department_id=" + department_id +
                ", description='" + description + '\'' +
                ", approveUser='" + approveUser + '\'' +
                ", approveTime='" + approveTime + '\'' +
                ", approveInfo='" + approveInfo + '\'' +
                ", status=" + status +
                ", bpmInstanceId='" + bpmInstanceId + '\'' +
                ", lastUsed=" + lastUsed +
                '}';
    }
}