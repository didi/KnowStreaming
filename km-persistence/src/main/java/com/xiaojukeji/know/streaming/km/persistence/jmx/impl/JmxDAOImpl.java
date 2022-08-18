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
        return this.getJmxValue(null, null, jmxHost, jmxPort, jmxConfig, objectName, attribute);
    }

    @Override
    public Object getJmxValue(Long clusterPhyId, Integer brokerId, String jmxHost, Integer jmxPort, JmxConfig jmxConfig, ObjectName objectName, String attribute) {
        JmxConnectorWrap jmxConnectorWrap = null;
        try {
            jmxConnectorWrap = new JmxConnectorWrap(clusterPhyId, brokerId, null, jmxHost, jmxPort, jmxConfig);
            if (!jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
                log.error("method=getJmxValue||clusterPhyId={}||brokerId={}||jmxHost={}||jmxPort={}||jmxConfig={}||errMgs=create jmx client failed",
                        clusterPhyId, brokerId, jmxHost, jmxPort, jmxConfig);
                return null;
            }

            return jmxConnectorWrap.getAttribute(objectName, attribute);
        } catch (Exception e) {
            log.error("method=getJmxValue||clusterPhyId={}||brokerId={}||jmxHost={}||jmxPort={}||jmxConfig={}||objectName={}||attribute={}||msg=get attribute failed||errMsg={}",
                    clusterPhyId, brokerId, jmxHost, jmxPort, jmxConfig, objectName, attribute, e);
        } finally {
            if (jmxConnectorWrap != null) {
                jmxConnectorWrap.close();
            }
        }

        return null;
    }
}
