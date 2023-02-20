package com.xiaojukeji.know.streaming.km.common.bean.entity.param.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ConnectClusterParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wyb
 * @date 2022/11/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorParam extends ConnectClusterParam {

    private String connectorName;

    private String connectorType;

    public ConnectorParam(Long connectClusterId, String connectorName, String connectorType) {
        super(connectClusterId);
        this.connectorName = connectorName;
        this.connectorType = connectorType;
    }

}
