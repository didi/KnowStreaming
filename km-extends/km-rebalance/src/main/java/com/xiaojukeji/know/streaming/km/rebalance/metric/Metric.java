package com.xiaojukeji.know.streaming.km.rebalance.metric;

/**
 * @author leewei
 * @date 2022/5/12
 */
public class Metric {
    private String topic;
    private int partition;
    private double cpu;
    private double bytesIn;
    private double bytesOut;
    private double disk;

    public Metric() {

    }

    public Metric(String topic, int partition, double cpu, double bytesIn, double bytesOut, double disk) {
        this.topic = topic;
        this.partition = partition;
        this.cpu = cpu;
        this.bytesIn = bytesIn;
        this.bytesOut = bytesOut;
        this.disk = disk;
    }

    public String topic() {
        return topic;
    }

    public int partition() {
        return partition;
    }

    public double cpu() {
        return cpu;
    }

    public double bytesIn() {
        return bytesIn;
    }

    public double bytesOut() {
        return bytesOut;
    }

    public double disk() {
        return disk;
    }
}
