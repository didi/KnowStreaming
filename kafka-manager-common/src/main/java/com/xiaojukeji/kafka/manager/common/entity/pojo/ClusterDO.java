package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;
import java.util.Objects;

/**
 * @author zengqiao
 * @date 20/4/23
 */
public class ClusterDO implements Comparable<ClusterDO> {
    private Long id;

    private String clusterName;

    private String zookeeper;

    private String bootstrapServers;

    private String securityProperties;

    private String jmxProperties;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(String securityProperties) {
        this.securityProperties = securityProperties;
    }

    public String getJmxProperties() {
        return jmxProperties;
    }

    public void setJmxProperties(String jmxProperties) {
        this.jmxProperties = jmxProperties;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "ClusterDO{" +
                "id=" + id +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", securityProperties='" + securityProperties + '\'' +
                ", jmxProperties='" + jmxProperties + '\'' +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }

    @Override
    public int compareTo(ClusterDO clusterDO) {
        return this.id.compareTo(clusterDO.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterDO clusterDO = (ClusterDO) o;
        return Objects.equals(id, clusterDO.id)
                && Objects.equals(clusterName, clusterDO.clusterName)
                && Objects.equals(zookeeper, clusterDO.zookeeper)
                && Objects.equals(bootstrapServers, clusterDO.bootstrapServers)
                && Objects.equals(securityProperties, clusterDO.securityProperties)
                && Objects.equals(jmxProperties, clusterDO.jmxProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clusterName, zookeeper, bootstrapServers, securityProperties, jmxProperties);
    }
}