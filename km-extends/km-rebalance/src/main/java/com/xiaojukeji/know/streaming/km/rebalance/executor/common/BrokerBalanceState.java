package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

public class BrokerBalanceState {
    //CPU平均资源
    private Double cpuAvgResource;
    //CPU资源使用率
    private Double cpuUtilization;
    // -1,低于均衡范围
    // 0,均衡范围内
    // 1,高于均衡范围
    private Integer cpuBalanceState;
    //磁盘平均资源
    private Double diskAvgResource;
    //磁盘资源使用率
    private Double diskUtilization;
    //磁盘均衡状态
    private Integer diskBalanceState;
    //流入平均资源
    private Double bytesInAvgResource;
    //流入资源使用率
    private Double bytesInUtilization;
    //流入均衡状态
    private Integer bytesInBalanceState;
    //流出平均资源
    private Double bytesOutAvgResource;
    //流出资源使用率
    private Double bytesOutUtilization;
    //流出均衡状态
    private Integer bytesOutBalanceState;

    public Double getCpuAvgResource() {
        return cpuAvgResource;
    }

    public void setCpuAvgResource(Double cpuAvgResource) {
        this.cpuAvgResource = cpuAvgResource;
    }

    public Double getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(Double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public Integer getCpuBalanceState() {
        return cpuBalanceState;
    }

    public void setCpuBalanceState(Integer cpuBalanceState) {
        this.cpuBalanceState = cpuBalanceState;
    }

    public Double getDiskAvgResource() {
        return diskAvgResource;
    }

    public void setDiskAvgResource(Double diskAvgResource) {
        this.diskAvgResource = diskAvgResource;
    }

    public Double getDiskUtilization() {
        return diskUtilization;
    }

    public void setDiskUtilization(Double diskUtilization) {
        this.diskUtilization = diskUtilization;
    }

    public Integer getDiskBalanceState() {
        return diskBalanceState;
    }

    public void setDiskBalanceState(Integer diskBalanceState) {
        this.diskBalanceState = diskBalanceState;
    }

    public Double getBytesInAvgResource() {
        return bytesInAvgResource;
    }

    public void setBytesInAvgResource(Double bytesInAvgResource) {
        this.bytesInAvgResource = bytesInAvgResource;
    }

    public Double getBytesInUtilization() {
        return bytesInUtilization;
    }

    public void setBytesInUtilization(Double bytesInUtilization) {
        this.bytesInUtilization = bytesInUtilization;
    }

    public Integer getBytesInBalanceState() {
        return bytesInBalanceState;
    }

    public void setBytesInBalanceState(Integer bytesInBalanceState) {
        this.bytesInBalanceState = bytesInBalanceState;
    }

    public Double getBytesOutAvgResource() {
        return bytesOutAvgResource;
    }

    public void setBytesOutAvgResource(Double bytesOutAvgResource) {
        this.bytesOutAvgResource = bytesOutAvgResource;
    }

    public Double getBytesOutUtilization() {
        return bytesOutUtilization;
    }

    public void setBytesOutUtilization(Double bytesOutUtilization) {
        this.bytesOutUtilization = bytesOutUtilization;
    }

    public Integer getBytesOutBalanceState() {
        return bytesOutBalanceState;
    }

    public void setBytesOutBalanceState(Integer bytesOutBalanceState) {
        this.bytesOutBalanceState = bytesOutBalanceState;
    }

    @Override
    public String toString() {
        return "BrokerBalanceState{" +
                "cpuAvgResource=" + cpuAvgResource +
                ", cpuUtilization=" + cpuUtilization +
                ", cpuBalanceState=" + cpuBalanceState +
                ", diskAvgResource=" + diskAvgResource +
                ", diskUtilization=" + diskUtilization +
                ", diskBalanceState=" + diskBalanceState +
                ", bytesInAvgResource=" + bytesInAvgResource +
                ", bytesInUtilization=" + bytesInUtilization +
                ", bytesInBalanceState=" + bytesInBalanceState +
                ", bytesOutAvgResource=" + bytesOutAvgResource +
                ", bytesOutUtilization=" + bytesOutUtilization +
                ", bytesOutBalanceState=" + bytesOutBalanceState +
                '}';
    }
}
