package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

public class HostEnv {
    //BrokerId
    private int id;
    //机器IP
    private String host;
    //机架ID
    private String rackId;
    //CPU核数
    private int cpu;
    //磁盘总容量
    private double disk;
    //网卡容量
    private double network;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public double getDisk() {
        return disk;
    }

    public void setDisk(double disk) {
        this.disk = disk;
    }

    public double getNetwork() {
        return network;
    }

    public void setNetwork(double network) {
        this.network = network;
    }

    @Override
    public String toString() {
        return "HostEnv{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", rackId='" + rackId + '\'' +
                ", cpu=" + cpu +
                ", disk=" + disk +
                ", network=" + network +
                '}';
    }
}
