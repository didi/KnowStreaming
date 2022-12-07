package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zengqiao
 * @date 2022-10-17
 */
@Data
@ApiModel(description = "操作Connector")
public class ConnectorActionDTO extends ClusterConnectorDTO {
    @NotBlank(message = "action不允许为空串")
    @ApiModelProperty(value = "Connector名称", example = "stop|restart|resume")
    private String action;
}
