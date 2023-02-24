package com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common;

public class BalanceDetailed {
    private int brokerId;
    private String host;
    //当前CPU使用率
    private double currentCPUUtilization;
    //最新CPU使用率
    private double lastCPUUtilization;
    //当前磁盘使用率
    private double currentDiskUtilization;
    //最新磁盘使用量
    private double lastDiskUtilization;
    //当前网卡入流量
    private double currentNetworkInUtilization;
    //最新网卡入流量
    private double lastNetworkInUtilization;
    //当前网卡出流量
    private double currentNetworkOutUtilization;
    //最新网卡出流量
    private double lastNetworkOutUtilization;
    //均衡状态
    private int balanceState = 0;
    //迁入磁盘容量
    private double moveInDiskSize;
    //迁出磁盘容量
    private double moveOutDiskSize;
    //迁入副本数
    private double moveInReplicas;
    //迁出副本数
    private double moveOutReplicas;

    public int getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(int brokerId) {
        this.brokerId = brokerId;
    }

    public double getCurrentCPUUtilization() {
        return currentCPUUtilization;
    }

    public void setCurrentCPUUtilization(double currentCPUUtilization) {
        this.currentCPUUtilization = currentCPUUtilization;
    }

    public double getLastCPUUtilization() {
        return lastCPUUtilization;
    }

    public void setLastCPUUtilization(double lastCPUUtilization) {
        this.lastCPUUtilization = lastCPUUtilization;
    }

    public double getCurrentDiskUtilization() {
        return currentDiskUtilization;
    }

    public void setCurrentDiskUtilization(double currentDiskUtilization) {
        this.currentDiskUtilization = currentDiskUtilization;
    }

    public double getLastDiskUtilization() {
        return lastDiskUtilization;
    }

    public void setLastDiskUtilization(double lastDiskUtilization) {
        this.lastDiskUtilization = lastDiskUtilization;
    }

    public double getCurrentNetworkInUtilization() {
        return currentNetworkInUtilization;
    }

    public void setCurrentNetworkInUtilization(double currentNetworkInUtilization) {
        this.currentNetworkInUtilization = currentNetworkInUtilization;
    }

    public double getLastNetworkInUtilization() {
        return lastNetworkInUtilization;
    }

    public void setLastNetworkInUtilization(double lastNetworkInUtilization) {
        this.lastNetworkInUtilization = lastNetworkInUtilization;
    }

    public double getCurrentNetworkOutUtilization() {
        return currentNetworkOutUtilization;
    }

    public void setCurrentNetworkOutUtilization(double currentNetworkOutUtilization) {
        this.currentNetworkOutUtilization = currentNetworkOutUtilization;
    }

    public double getLastNetworkOutUtilization() {
        return lastNetworkOutUtilization;
    }

    public void setLastNetworkOutUtilization(double lastNetworkOutUtilization) {
        this.lastNetworkOutUtilization = lastNetworkOutUtilization;
    }

    public int getBalanceState() {
        return balanceState;
    }

    public void setBalanceState(int balanceState) {
        this.balanceState = balanceState;
    }

    public double getMoveInDiskSize() {
        return moveInDiskSize;
    }

    public void setMoveInDiskSize(double moveInDiskSize) {
        this.moveInDiskSize = moveInDiskSize;
    }

    public double getMoveOutDiskSize() {
        return moveOutDiskSize;
    }

    public void setMoveOutDiskSize(double moveOutDiskSize) {
        this.moveOutDiskSize = moveOutDiskSize;
    }

    public double getMoveInReplicas() {
        return moveInReplicas;
    }

    public void setMoveInReplicas(double moveInReplicas) {
        this.moveInReplicas = moveInReplicas;
    }

    public double getMoveOutReplicas() {
        return moveOutReplicas;
    }

    public void setMoveOutReplicas(double moveOutReplicas) {
        this.moveOutReplicas = moveOutReplicas;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "BalanceDetailed{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", currentCPUUtilization=" + currentCPUUtilization +
                ", lastCPUUtilization=" + lastCPUUtilization +
                ", currentDiskUtilization=" + currentDiskUtilization +
                ", lastDiskUtilization=" + lastDiskUtilization +
                ", currentNetworkInUtilization=" + currentNetworkInUtilization +
                ", lastNetworkInUtilization=" + lastNetworkInUtilization +
                ", currentNetworkOutUtilization=" + currentNetworkOutUtilization +
                ", lastNetworkOutUtilization=" + lastNetworkOutUtilization +
                ", balanceState=" + balanceState +
                ", moveInDiskSize=" + moveInDiskSize +
                ", moveOutDiskSize=" + moveOutDiskSize +
                ", moveInReplicas=" + moveInReplicas +
                ", moveOutReplicas=" + moveOutReplicas +
                '}';
    }
}
