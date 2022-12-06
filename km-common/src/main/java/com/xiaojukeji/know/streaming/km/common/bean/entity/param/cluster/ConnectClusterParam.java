package com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wyb
 * @date 2022/11/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectClusterParam extends ClusterParam{
    protected Long connectClusterId;
}
