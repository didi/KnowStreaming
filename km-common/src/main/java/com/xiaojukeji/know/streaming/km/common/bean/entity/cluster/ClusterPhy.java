package com.xiaojukeji.know.streaming.km.common.bean.entity.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.entity.EntityIdInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterPhy implements Comparable<ClusterPhy>, EntityIdInterface {
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 集群名字
     */
    private String name;

    /**
     * 集群服务地址
     */
    private String bootstrapServers;

    /**
     * 版本信息
     */
    private String kafkaVersion;

    /**
     * ZK地址
     */
    private String zookeeper;

    /**
     * 集群客户端配置
     */
    private String clientProperties;

    /**
     * jmx配置
     * @see com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig
     */
    private String jmxProperties;

    /**
     * zk配置
     * @see com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig
     */
    private String zkProperties;

    /**
     * 开启ACL
     * @see com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum
     */
    private Integer authType;

    /**
     * 运行状态
     * @see com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterRunStateEnum
     */
    private Integer runState;

    /**
     * 备注
     */
    private String description;

    @Override
    public int compareTo(ClusterPhy clusterPhy) {
        return this.id.compareTo(clusterPhy.id);
    }
}
