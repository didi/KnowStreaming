package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/3/19
 */
@ApiModel(value = "GatewayConfigVO", description = "Gateway配置信息")
public class GatewayConfigVO {
    @ApiModelProperty(value="ID")
    private Long id;

    @ApiModelProperty(value="配置类型")
    private String type;

    @ApiModelProperty(value="配置名称")
    private String name;

    @ApiModelProperty(value="配置值")
    private String value;

    @ApiModelProperty(value="版本")
    private Long version;

    @ApiModelProperty(value="描述说明")
    private String description;

    @ApiModelProperty(value="创建时间")
    private Date createTime;

    @ApiModelProperty(value="修改时间")
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "GatewayConfigVO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", version=" + version +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
