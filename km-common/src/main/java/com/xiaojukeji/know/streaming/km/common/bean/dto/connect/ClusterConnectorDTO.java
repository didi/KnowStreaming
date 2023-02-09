package com.xiaojukeji.know.streaming.km.common.bean.dto.connect;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zengqiao
 * @date 2022-10-17
 */
@Data
@NoArgsConstructor
@ApiModel(description = "集群Connector")
public class ClusterConnectorDTO extends BaseDTO {
    @NotNull(message = "connectClusterId不允许为空")
    @ApiModelProperty(value = "Connector集群ID", example = "1")
    protected Long connectClusterId;

    @NotBlank(message = "name不允许为空串")
    @ApiModelProperty(value = "Connector名称", example = "know-streaming-connector")
    protected String connectorName;

    public ClusterConnectorDTO(Long connectClusterId, String connectorName) {
        this.connectClusterId = connectClusterId;
        this.connectorName = connectorName;
    }
}
