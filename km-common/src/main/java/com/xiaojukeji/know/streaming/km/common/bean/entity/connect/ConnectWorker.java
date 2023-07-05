package com.xiaojukeji.know.streaming.km.common.bean.entity.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ConnectWorker implements Serializable {

    protected static final ILog LOGGER = LogFactory.getLog(ConnectWorker.class);

    /**
     * Kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * 集群ID
     */
    private Long connectClusterId;

    /**
     * 成员ID
     */
    private String memberId;

    /**
     * 主机
     */
    private String host;

    /**
     * Jmx端口
     */
    private Integer jmxPort;

    /**
     * URL
     */
    private String url;

    /**
     * leader的URL
     */
    private String leaderUrl;

    /**
     * 1：是leader，0：不是leader
     */
    private Integer leader;

    /**
     * worker地址
     */
    private String workerId;

    public ConnectWorker(Long kafkaClusterPhyId,
                         Long connectClusterId,
                         String memberId,
                         String host,
                         Integer jmxPort,
                         String url,
                         String leaderUrl,
                         Integer leader) {
        this.kafkaClusterPhyId = kafkaClusterPhyId;
        this.connectClusterId = connectClusterId;
        this.memberId = memberId;
        this.host = host;
        this.jmxPort = jmxPort;
        this.url = url;
        this.leaderUrl = leaderUrl;
        this.leader = leader;
        String workerId = CommonUtils.getWorkerId(url);
        if (workerId == null) {
            workerId = memberId;
            LOGGER.error("class=ConnectWorker||connectClusterId={}||memberId={}||url={}||msg=analysis url fail"
                    , connectClusterId, memberId, url);
        }
        this.workerId = workerId;
    }
}
