package com.xiaojukeji.know.streaming.km.common.bean.po.zookeeper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "zookeeper")
public class ZookeeperInfoPO extends BasePO {
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
}
