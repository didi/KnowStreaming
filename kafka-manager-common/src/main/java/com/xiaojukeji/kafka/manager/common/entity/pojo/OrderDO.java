package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zhongyuankai
 * @date 20/4/23
 */
public class OrderDO {
    private Long id;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer type;

    private String title;

    private String applicant;

    private String description;

    private String approver;

    private Date gmtHandle;

    private String opinion;

    private String extensions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public Date getGmtHandle() {
        return gmtHandle;
    }

    public void setGmtHandle(Date gmtHandle) {
        this.gmtHandle = gmtHandle;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        return "OrderDO{" +
                "id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", applicant='" + applicant + '\'' +
                ", description='" + description + '\'' +
                ", approver='" + approver + '\'' +
                ", gmtHandle=" + gmtHandle +
                ", opinion='" + opinion + '\'' +
                ", extensions='" + extensions + '\'' +
                '}';
    }
}
