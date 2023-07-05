package com.xiaojukeji.know.streaming.km.common.bean.entity.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.EntityIdInterface;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConnectCluster implements Serializable, Comparable<ConnectCluster>, EntityIdInterface {
    /**
     * 集群ID
     */
    private Long id;

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
     * @see com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum
     */
    private Integer state;

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

    /**
     * Kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * 集群地址
     */
    private String clusterUrl;

    public String getSuitableRequestUrl() {
        // 优先使用用户填写的url
        String suitableRequestUrl = this.clusterUrl;
        if (ValidateUtils.isBlank(suitableRequestUrl)) {
            // 用户如果没有填写，则使用元信息中的url
            suitableRequestUrl = this.memberLeaderUrl;
        }

        //url去斜杠
        if (suitableRequestUrl.length() > 0 && suitableRequestUrl.charAt(suitableRequestUrl.length() - 1) == '/') {
            return suitableRequestUrl.substring(0, suitableRequestUrl.length() - 1);
        }

        return suitableRequestUrl;
    }

    @Override
    public int compareTo(ConnectCluster connectCluster) {
        return this.id.compareTo(connectCluster.getId());
    }
}
