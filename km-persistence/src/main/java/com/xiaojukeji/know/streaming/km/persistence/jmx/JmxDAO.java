package com.xiaojukeji.know.streaming.km.persistence.jmx;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig;

import javax.management.ObjectName;

/**
 * 从Jmx获取相关数据的服务接口
 * @author tukun, zengqiao
 * @date 2015/11/11.
 */
public interface JmxDAO {
    Object getJmxValue(String jmxHost, Integer jmxPort, JmxConfig jmxConfig, ObjectName objectName, String attribute);

    Object getJmxValue(Long clusterPhyId, String jmxHost, Integer jmxPort, JmxConfig jmxConfig, ObjectName objectName, String attribute);

    Long getServerStartTime(Long clusterPhyId, String jmxHost, Integer jmxPort, JmxConfig jmxConfig);
}
