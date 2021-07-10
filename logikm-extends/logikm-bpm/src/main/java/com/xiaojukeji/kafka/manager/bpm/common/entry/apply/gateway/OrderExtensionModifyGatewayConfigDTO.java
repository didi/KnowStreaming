package com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * 修改gateway配置
 * @author zengqiao
 * @date 2021/01/12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderExtensionModifyGatewayConfigDTO {
    @ApiModelProperty(value = "配置ID")
    private Long id;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "描述说明")
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "OrderExtensionModifyGatewayConfigDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isNull(id)
                || ValidateUtils.isBlank(name)
                || ValidateUtils.isBlank(type)
                || ValidateUtils.isBlank(value)) {
            return false;
        }
        return true;
    }
}
