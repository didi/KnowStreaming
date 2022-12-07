package com.xiaojukeji.know.streaming.km.common.bean.entity.param.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
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

    public ConnectorParam(Long connectClusterId, String connectorName) {
        super(connectClusterId);
        this.connectorName = connectorName;
    }

}
