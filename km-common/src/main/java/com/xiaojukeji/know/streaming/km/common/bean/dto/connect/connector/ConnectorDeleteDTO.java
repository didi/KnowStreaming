package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zengqiao
 * @date 2022-10-17
 */
@Data
@ApiModel(description = "删除Connector")
public class ConnectorDeleteDTO extends ClusterConnectorDTO {
}
