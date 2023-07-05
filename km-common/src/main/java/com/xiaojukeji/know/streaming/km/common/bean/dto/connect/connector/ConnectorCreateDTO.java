package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 2022-10-17
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@ApiModel(description = "创建Connector")
public class ConnectorCreateDTO extends ClusterConnectorDTO {
    @Deprecated
    @ApiModelProperty(value = "配置, 优先使用config字段，3.5.0版本将删除该字段", example = "")
    protected Properties configs;

    @ApiModelProperty(value = "配置", example = "")
    protected Properties config;

    public ConnectorCreateDTO(Long connectClusterId, String connectorName, Properties config) {
        super(connectClusterId, connectorName);
        this.config = config;
    }

    public Properties getSuitableConfig() {
        return config != null? config: configs;
    }
}
