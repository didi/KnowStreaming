package com.xiaojukeji.know.streaming.km.common.bean.entity.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseClusterConfigValue implements Serializable {
    /**
     * 物理集群ID
     */
    protected Long clusterPhyId;
}
