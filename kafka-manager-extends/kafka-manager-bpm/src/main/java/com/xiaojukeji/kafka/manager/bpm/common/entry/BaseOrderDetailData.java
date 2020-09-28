package com.xiaojukeji.kafka.manager.bpm.common.entry;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/6/9
 */
public class BaseOrderDetailData {
    private Long id;

    private Integer type;

    private String title;

    private Account applicant;

    private Date gmtCreate;

    private List<Account> approverList;

    private Date gmtHandle;

    private String opinion;

    private Integer status;

    private String description;

    private AbstractOrderDetailData detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Account getApplicant() {
        return applicant;
    }

    public void setApplicant(Account applicant) {
        this.applicant = applicant;
    }

    public List<Account> getApproverList() {
        return approverList;
    }

    public void setApproverList(List<Account> approverList) {
        this.approverList = approverList;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AbstractOrderDetailData getDetail() {
        return detail;
    }

    public void setDetail(AbstractOrderDetailData detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "OrderDetailBaseDTO{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", applicant=" + applicant +
                ", gmtCreate=" + gmtCreate +
                ", approverList=" + approverList +
                ", gmtHandle=" + gmtHandle +
                ", opinion='" + opinion + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", detail=" + detail +
                '}';
    }
}

