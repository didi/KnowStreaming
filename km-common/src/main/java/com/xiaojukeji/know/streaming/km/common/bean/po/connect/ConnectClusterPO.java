package com.xiaojukeji.know.streaming.km.common.bean.po.connect;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_KC_TABLE_NAME_PREFIX + "connect_cluster")
public class ConnectClusterPO extends BasePO {
    /**
     * Kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * 集群名字
     */
    private String name;

    /**
     * 集群使用的消费组
     */
    private String groupName;

    /**
     * 集群使用的消费组状态，也表示集群状态
     */
    private Integer state;

    /**
     * 用户填写的集群地址
     */
    private String clusterUrl;

    /**
     * worker中显示的leader url信息
     */
    private String memberLeaderUrl;

    /**
     * 版本信息
     */
    private String version;

    /**
     * jmx配置
     * @see com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig
     */
    private String jmxProperties;
}
