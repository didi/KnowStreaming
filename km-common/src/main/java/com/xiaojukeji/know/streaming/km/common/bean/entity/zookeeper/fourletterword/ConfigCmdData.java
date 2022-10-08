package com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword;

import lombok.Data;


/**
 * clientPort=2183
 * dataDir=/data1/data/zkData2/version-2
 * dataLogDir=/data1/data/zkLog2/version-2
 * tickTime=2000
 * maxClientCnxns=60
 * minSessionTimeout=4000
 * maxSessionTimeout=40000
 * serverId=2
 * initLimit=15
 * syncLimit=10
 * electionAlg=3
 * electionPort=4445
 * quorumPort=4444
 * peerType=0
 */
@Data
public class ConfigCmdData extends BaseFourLetterWordCmdData {
    private Long clientPort;
    private String dataDir;
    private String dataLogDir;
    private Long tickTime;
    private Long maxClientCnxns;
    private Long minSessionTimeout;
    private Long maxSessionTimeout;
    private Integer serverId;
    private String initLimit;
    private Long syncLimit;
    private Long electionAlg;
    private Long electionPort;
    private Long quorumPort;
    private Long peerType;
}
