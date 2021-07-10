package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/09/03
 */
@ApiModel(description = "操作记录")
public class OperateRecordVO {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("模块ID")
    private Integer moduleId;

    @ApiModelProperty("模块")
    private String  module;

    @ApiModelProperty("操作ID")
    private Integer operateId;

    @ApiModelProperty("操作")
    private String  operate;

    @ApiModelProperty("资源（app、topic）")
    private String  resource;

    @ApiModelProperty("操作内容")
    private String  content;

    @ApiModelProperty("操作人")
    private String  operator;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "OperateRecordVO{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", module='" + module + '\'' +
                ", operateId=" + operateId +
                ", operate='" + operate + '\'' +
                ", resource='" + resource + '\'' +
                ", content='" + content + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
