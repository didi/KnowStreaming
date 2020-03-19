package com.xiaojukeji.kafka.manager.web.vo.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value = "BrokerOverviewVO", description = "Broker概括信息")
public class BrokerOverviewVO implements Comparable<BrokerOverviewVO>{
    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "主机名")
    private String host;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "jmx端口")
    private Integer jmxPort;

    @ApiModelProperty(value = "启动时间")
    private Long startTime;

    @ApiModelProperty(value = "流入流量")
    private Double byteIn;

    @ApiModelProperty(value = "流出流量")
    private Double byteOut;

    @ApiModelProperty(value = "broker状态[0:在线, -1:不在线]")
    private Integer status;

    @ApiModelProperty(value = "region名称")
    private String regionName;

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Double getByteIn() {
        return byteIn;
    }

    public void setByteIn(Double byteIn) {
        this.byteIn = byteIn;
    }

    public Double getByteOut() {
        return byteOut;
    }

    public void setByteOut(Double byteOut) {
        this.byteOut = byteOut;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public int compareTo(BrokerOverviewVO that) {
        return this.getBrokerId() - that.getBrokerId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BrokerOverviewVO that = (BrokerOverviewVO) o;
        return Objects.equals(brokerId, that.brokerId) &&
                Objects.equals(host, that.host) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brokerId, host, port);
    }

    @Override
    public String toString() {
        return "BrokerOverviewVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmxPort +
                ", startTime=" + startTime +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", status=" + status +
                ", regionName='" + regionName + '\'' +
                '}';
    }
}
