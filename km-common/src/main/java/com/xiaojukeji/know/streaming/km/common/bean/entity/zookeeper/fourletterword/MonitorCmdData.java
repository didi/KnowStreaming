package com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword;

import lombok.Data;

/**
 * zk_version      3.4.6-1569965, built on 02/20/2014 09:09 GMT
 * zk_avg_latency  0
 * zk_max_latency  399
 * zk_min_latency  0
 * zk_packets_received     234857
 * zk_packets_sent 234860
 * zk_num_alive_connections        4
 * zk_outstanding_requests 0
 * zk_server_state follower
 * zk_znode_count  35566
 * zk_watch_count  39
 * zk_ephemerals_count     10
 * zk_approximate_data_size        3356708
 * zk_open_file_descriptor_count   35
 * zk_max_file_descriptor_count    819200
 */
@Data
public class MonitorCmdData extends BaseFourLetterWordCmdData {
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
    private Float zkWatchCount;
    private Float zkEphemeralsCount;
    private Float zkApproximateDataSize;
    private Float zkOpenFileDescriptorCount;
    private Float zkMaxFileDescriptorCount;
}
