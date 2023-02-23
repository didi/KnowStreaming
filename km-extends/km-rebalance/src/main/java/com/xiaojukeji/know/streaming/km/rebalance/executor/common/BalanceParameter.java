package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

import java.util.List;
import java.util.Properties;

public class BalanceParameter {
    //集群名称
    private String cluster;
    //集群访问配置
    private Properties kafkaConfig;
    //ES访问地址
    private String esRestURL;
    //ES存储索引前缀
    private String esIndexPrefix;
    //均衡目标
    private List<String> goals;
    //Topic黑名单，（参与模型计算）
    private String excludedTopics = "";
    //忽略的Topic列表，（不参与模型计算）
    private String ignoredTopics = "";
    //下线的Broker
    private String offlineBrokers = "";
    //需要均衡的Broker
    private String balanceBrokers = "";
    //默认Topic副本分布阈值
    private double topicReplicaThreshold = 0.1;
    //磁盘浮动阈值
    private double diskThreshold = 0.1;
    //CPU浮动阈值
    private double cpuThreshold = 0.1;
    //流入浮动阈值
    private double networkInThreshold = 0.1;
    //流出浮动阈值
    private double networkOutThreshold = 0.1;
    //均衡时间范围
    private int beforeSeconds = 300;
    //集群中所有Broker的硬件环境:cpu、disk、bytesIn、bytesOut
    private List<HostEnv> hardwareEnv;
    //最小Leader浮动阈值,不追求绝对平均，避免集群流量抖动
    private double topicLeaderThreshold = 0.1;

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getEsRestURL() {
        return esRestURL;
    }

    public void setEsRestURL(String esRestURL) {
        this.esRestURL = esRestURL;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public String getExcludedTopics() {
        return excludedTopics;
    }

    public void setExcludedTopics(String excludedTopics) {
        this.excludedTopics = excludedTopics;
    }

    public String getIgnoredTopics() {
        return ignoredTopics;
    }

    public void setIgnoredTopics(String ignoredTopics) {
        this.ignoredTopics = ignoredTopics;
    }

    public double getTopicReplicaThreshold() {
        return topicReplicaThreshold;
    }

    public void setTopicReplicaThreshold(double topicReplicaThreshold) {
        this.topicReplicaThreshold = topicReplicaThreshold;
    }

    public double getDiskThreshold() {
        return diskThreshold;
    }

    public void setDiskThreshold(double diskThreshold) {
        this.diskThreshold = diskThreshold;
    }

    public double getCpuThreshold() {
        return cpuThreshold;
    }

    public void setCpuThreshold(double cpuThreshold) {
        this.cpuThreshold = cpuThreshold;
    }

    public double getNetworkInThreshold() {
        return networkInThreshold;
    }

    public void setNetworkInThreshold(double networkInThreshold) {
        this.networkInThreshold = networkInThreshold;
    }

    public double getNetworkOutThreshold() {
        return networkOutThreshold;
    }

    public void setNetworkOutThreshold(double networkOutThreshold) {
        this.networkOutThreshold = networkOutThreshold;
    }

    public List<HostEnv> getHardwareEnv() {
        return hardwareEnv;
    }

    public void setHardwareEnv(List<HostEnv> hardwareEnv) {
        this.hardwareEnv = hardwareEnv;
    }

    public String getBalanceBrokers() {
        return balanceBrokers;
    }

    public void setBalanceBrokers(String balanceBrokers) {
        this.balanceBrokers = balanceBrokers;
    }

    public Properties getKafkaConfig() {
        return kafkaConfig;
    }

    public void setKafkaConfig(Properties kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    public String getEsIndexPrefix() {
        return esIndexPrefix;
    }

    public void setEsIndexPrefix(String esIndexPrefix) {
        this.esIndexPrefix = esIndexPrefix;
    }

    public String getOfflineBrokers() {
        return offlineBrokers;
    }

    public void setOfflineBrokers(String offlineBrokers) {
        this.offlineBrokers = offlineBrokers;
    }

    public int getBeforeSeconds() {
        return beforeSeconds;
    }

    public void setBeforeSeconds(int beforeSeconds) {
        this.beforeSeconds = beforeSeconds;
    }

    public double getTopicLeaderThreshold() {
        return topicLeaderThreshold;
    }

    public void setTopicLeaderThreshold(double topicLeaderThreshold) {
        this.topicLeaderThreshold = topicLeaderThreshold;
    }

    @Override
    public String toString() {
        return "BalanceParameter{" +
                "cluster='" + cluster + '\'' +
                ", kafkaConfig=" + kafkaConfig +
                ", esRestURL='" + esRestURL + '\'' +
                ", esIndexPrefix='" + esIndexPrefix + '\'' +
                ", goals=" + goals +
                ", excludedTopics='" + excludedTopics + '\'' +
                ", offlineBrokers='" + offlineBrokers + '\'' +
                ", balanceBrokers='" + balanceBrokers + '\'' +
                ", topicReplicaThreshold=" + topicReplicaThreshold +
                ", diskThreshold=" + diskThreshold +
                ", cpuThreshold=" + cpuThreshold +
                ", networkInThreshold=" + networkInThreshold +
                ", networkOutThreshold=" + networkOutThreshold +
                ", beforeSeconds=" + beforeSeconds +
                ", hardwareEnv=" + hardwareEnv +
                ", topicLeaderThreshold=" + topicLeaderThreshold +
                '}';
    }
}
