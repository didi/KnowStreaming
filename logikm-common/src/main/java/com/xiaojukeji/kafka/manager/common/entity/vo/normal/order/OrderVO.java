package com.xiaojukeji.kafka.manager.common.entity.vo.normal.order;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zhongyuankai
 * @date 20/4/21
 */
public class OrderVO {
    @ApiModelProperty(value = "工单ID")
    private Long id;

    @ApiModelProperty(value = "工单类型, 0:topics, 1:apps, 2:quotas, 3:authorities, 4:clusters")
    private Integer type;

    @ApiModelProperty(value = "工单标题")
    private String title;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消")
    private Integer status;

    @ApiModelProperty(value = "申请/审核时间")
    private Date gmtTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtTime() {
        return gmtTime;
    }

    public void setGmtTime(Date gmtTime) {
        this.gmtTime = gmtTime;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    @Override
    public String toString() {
        return "OrderVO{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", applicant='" + applicant + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", gmtTime=" + gmtTime +
                '}';
    }
}
