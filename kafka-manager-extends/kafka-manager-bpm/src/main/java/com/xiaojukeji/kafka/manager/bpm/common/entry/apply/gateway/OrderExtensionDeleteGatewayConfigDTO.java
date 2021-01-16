package com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * 删除gateway配置
 * @author zengqiao
 * @date 2021/01/12
 */
public class OrderExtensionDeleteGatewayConfigDTO {
    @ApiModelProperty(value = "配置ID")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OrderExtensionDeleteGatewayConfigDTO{" +
                "id=" + id +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isNull(id)) {
            return false;
        }
        return true;
    }
}
