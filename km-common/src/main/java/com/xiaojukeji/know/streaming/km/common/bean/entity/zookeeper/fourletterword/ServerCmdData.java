package com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword;

import lombok.Data;

/**
 * Zookeeper version: 3.5.9-83df9301aa5c2a5d284a9940177808c01bc35cef, built on 01/06/2021 19:49 GMT
 * Latency min/avg/max: 0/0/2209
 * Received: 278202469
 * Sent: 279449055
 * Connections: 31
 * Outstanding: 0
 * Zxid: 0x20033fc12
 * Mode: leader
 * Node count: 10084
 * Proposal sizes last/min/max: 36/32/31260     leader特有
 */
@Data
public class ServerCmdData extends BaseFourLetterWordCmdData {
    private String zkVersion;
    private Float zkAvgLatency;
    private Float zkMaxLatency;
    private Float zkMinLatency;
    private Float zkPacketsReceived;
    private Float zkPacketsSent;
    private Float zkNumAliveConnections;
    private Float zkOutstandingRequests;
    private String zkServerState;
    private Float zkZnodeCount;
    private Long zkZxid;
}
