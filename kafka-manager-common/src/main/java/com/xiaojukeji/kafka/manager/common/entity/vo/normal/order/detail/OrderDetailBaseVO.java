package com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.detail;

import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/21
 */
@ApiModel(description = "工单详情类")
public class OrderDetailBaseVO<T> {
    @ApiModelProperty(value = "工单ID")
    private Long id;

    @ApiModelProperty(value = "工单类型")
    private Integer type;

    @ApiModelProperty(value = "工单标题")
    private String title;

    @ApiModelProperty(value = "申请人")
    private AccountVO applicant;

    @ApiModelProperty(value = "申请时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "审批人列表, 状态为未处理时返回的是审批人, 状态为处理完成时返回的是审批的人")
    private List<AccountVO> approverList;

    @ApiModelProperty(value = "审批时间")
    private Date gmtHandle;

    @ApiModelProperty(value = "审批审批意见")
    private String opinion;

    @ApiModelProperty(value = "工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "工单明细")
    private T detail;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountVO getApplicant() {
        return applicant;
    }

    public void setApplicant(AccountVO applicant) {
        this.applicant = applicant;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public List<AccountVO> getApproverList() {
        return approverList;
    }

    public void setApproverList(List<AccountVO> approverList) {
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

    public T getDetail() {
        return detail;
    }

    public void setDetail(T detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "OrderDetailBaseVO{" +
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