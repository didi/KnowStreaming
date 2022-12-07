package com.xiaojukeji.know.streaming.km.persistence.jmx.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.persistence.jmx.JmxDAO;
import org.springframework.stereotype.Repository;

import javax.management.*;

/**
 * @author tukun, zengqiao
 * @date 2015/11/11.
 */
@Repository
public class JmxDAOImpl implements JmxDAO {
    private static final ILog log = LogFactory.getLog(JmxDAOImpl.class);

    @Override
    public Object getJmxValue(String jmxHost, Integer jmxPort, JmxConfig jmxConfig, ObjectName objectName, String attribute) {
        return this.getJmxValue(null, jmxHost, jmxPort, jmxConfig, objectName, attribute);
    }

    @Override
    public Object getJmxValue(Long clusterPhyId, String jmxHost, Integer jmxPort, JmxConfig jmxConfig, ObjectName objectName, String attribute) {
        JmxConnectorWrap jmxConnectorWrap = null;
        try {
            jmxConnectorWrap = new JmxConnectorWrap("clusterPhyId: " + clusterPhyId, null, jmxHost, jmxPort, jmxConfig);
            if (!jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
                log.error(
                        "method=getJmxValue||clusterPhyId={}||jmxHost={}||jmxPort={}||jmxConfig={}||errMgs=create jmx client failed",
                        clusterPhyId, jmxHost, jmxPort, jmxConfig
                );
                return null;
            }

            return jmxConnectorWrap.getAttribute(objectName, attribute);
        } catch (Exception e) {
            log.error(
                    "method=getJmxValue||clusterPhyId={}||jmxHost={}||jmxPort={}||jmxConfig={}||objectName={}||attribute={}||msg=get attribute failed||errMsg=exception!",
                    clusterPhyId, jmxHost, jmxPort, jmxConfig, objectName, attribute, e
            );
        } finally {
            if (jmxConnectorWrap != null) {
                jmxConnectorWrap.close();
            }
        }

        return null;
    }

    @Override
    public Long getServerStartTime(Long clusterPhyId, String jmxHost, Integer jmxPort, JmxConfig jmxConfig) {
        try {
            Object object = this.getJmxValue(
                    clusterPhyId,
                    jmxHost,
                    jmxPort,
                    jmxConfig,
                    new ObjectName("java.lang:type=Runtime"),
                    "StartTime"
            );

            return object == null? null: (Long) object;
        } catch (Exception e) {
            log.error(
                    "method=getServerStartTime||clusterPhyId={}||jmxHost={}||jmxPort={}||jmxConfig={}||errMsg=exception!",
                    clusterPhyId, jmxHost, jmxPort, jmxConfig, e
            );
        }

        return null;
    }
}
