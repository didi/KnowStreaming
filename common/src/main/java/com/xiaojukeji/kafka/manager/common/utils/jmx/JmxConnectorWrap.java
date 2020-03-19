package com.xiaojukeji.kafka.manager.common.utils.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * JMXConnector包装类
 * @author tukun
 * @date 2015/11/9.
 */
public class JmxConnectorWrap {
    private final static Logger logger = LoggerFactory.getLogger(JmxConnectorWrap.class);

    private JMXConnector jmxConnector;

    /**
     * JMX连接的主机名
     */
    private String host;

    /**
     * JMX连接端口
     */
    private int port;

    public JmxConnectorWrap(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public JMXConnector getJmxConnector() {
        // 如果JMX连接断开，则进行重新连接
        if (jmxConnector == null && port != -1) {
            createJMXConnector();
        }
        return jmxConnector;
    }

    private synchronized void createJMXConnector() {
        if (jmxConnector != null) {
            return;
        }

        String jmxUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
        try {
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            jmxConnector = JMXConnectorFactory.connect(url, null);
        } catch (MalformedURLException e) {
            logger.error("JMX url exception, host:{} port:{} jmxUrl:{}", host, port, jmxUrl, e);
        } catch (IOException e) {
            logger.error("JMX connect exception, host:{} port:{}.", host, port, e);
        }
        logger.info("JMX connect success, host:{} port:{}.", host, port);
    }

    public void close() {
        if (jmxConnector == null) {
            return;
        }
        try {
            jmxConnector.close();
        } catch (IOException e) {
            logger.warn("close JmxConnector exception, host:{} port:{}.", host, port, e);
        }
    }
}
