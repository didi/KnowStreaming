package com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * 增加gateway配置
 * @author zengqiao
 * @date 2021/01/12
 */
public class OrderExtensionAddGatewayConfigDTO {
    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    private String value;

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

    @Override
    public String toString() {
        return "OrderExtensionAddGatewayConfigDTO{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isBlank(type)
            || ValidateUtils.isBlank(name)
            || ValidateUtils.isBlank(value)) {
            return false;
        }
        return true;
    }
}
