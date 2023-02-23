package com.xiaojukeji.know.streaming.km.license.bean;

import lombok.Data;

import java.util.List;

/**
 * @author didi
 */
@Data
public class KmLicenseUsageDetail {
    /**
     * 上报的 ks 的节点
     */
    private String host;
    /**
     * 上报的 ks 的集群的所有的节点
     */
    private List<String> hosts;
    /**
     * 上报的 ks 集群中 kafka 集群数量
     */
    private int clusters;
}
