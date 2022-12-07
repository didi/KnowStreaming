package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 2022-10-17
 */
@Data
@ApiModel(description = "创建Connector")
public class ConnectorCreateDTO extends ClusterConnectorDTO {
    @NotNull(message = "configs不允许为空")
    @ApiModelProperty(value = "配置", example = "")
    private Properties configs;
}
