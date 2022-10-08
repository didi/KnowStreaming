package com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper;

import com.xiaojukeji.know.streaming.km.common.bean.entity.BaseEntity;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
public class ZookeeperInfo extends BaseEntity {
    /**
     * 集群Id
     */
    private Long clusterPhyId;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 角色
     */
    private String role;

    /**
     * 版本
     */
    private String version;

    /**
     * ZK状态
     */
    private Integer status;

    public boolean alive() {
        return !(Constant.DOWN.equals(status));
    }
}
