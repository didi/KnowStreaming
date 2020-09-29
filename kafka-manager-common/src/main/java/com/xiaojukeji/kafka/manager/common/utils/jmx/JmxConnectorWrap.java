package com.xiaojukeji.kafka.manager.common.utils.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JMXConnector包装类
 * @author tukun
 * @date 2015/11/9.
 */
public class JmxConnectorWrap {
    private final static Logger LOGGER = LoggerFactory.getLogger(JmxConnectorWrap.class);

    private String host;

    private int port;

    private JMXConnector jmxConnector;

    private AtomicInteger atomicInteger;

    public JmxConnectorWrap(String host, int port) {
        this.host = host;
        this.port = port;
        this.atomicInteger = new AtomicInteger(25);
    }

    public boolean checkJmxConnectionAndInitIfNeed() {
        if (jmxConnector != null) {
            return true;
        }
        if (port == -1) {
            return false;
        }
        return createJmxConnector();
    }

    public synchronized void close() {
        if (jmxConnector == null) {
            return;
        }
        try {
            jmxConnector.close();
        } catch (IOException e) {
            LOGGER.warn("close JmxConnector exception, host:{} port:{}.", host, port, e);
        }
    }

    private synchronized boolean createJmxConnector() {
        if (jmxConnector != null) {
            return true;
        }
        String jmxUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
        try {
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            jmxConnector = JMXConnectorFactory.connect(url, null);
            LOGGER.info("JMX connect success, host:{} port:{}.", host, port);
            return true;
        } catch (MalformedURLException e) {
            LOGGER.error("JMX url exception, host:{} port:{} jmxUrl:{}", host, port, jmxUrl, e);
        } catch (Exception e) {
            LOGGER.error("JMX connect exception, host:{} port:{}.", host, port, e);
        }
        return false;
    }

    public Object getAttribute(ObjectName name, String attribute) throws
            MBeanException,
            AttributeNotFoundException,
            InstanceNotFoundException,
            ReflectionException,
            IOException {
        try {
            acquire();
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            return mBeanServerConnection.getAttribute(name, attribute);
        } finally {
            atomicInteger.incrementAndGet();
        }
    }

    public AttributeList getAttributes(ObjectName name, String[] attributes) throws
            MBeanException,
            AttributeNotFoundException,
            InstanceNotFoundException,
            ReflectionException,
            IOException {
        try {
            acquire();
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            return mBeanServerConnection.getAttributes(name, attributes);
        } finally {
            atomicInteger.incrementAndGet();
        }
    }

    public Set<ObjectName> queryNames(ObjectName name,
                               QueryExp query)
            throws IOException {
        try {
            acquire();
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            return mBeanServerConnection.queryNames(name, query);
        } finally {
            atomicInteger.incrementAndGet();
        }
    }

    private void acquire() {
        long now = System.currentTimeMillis();
        while (true) {
            try {
                if (System.currentTimeMillis() - now > 60000) {
                    break;
                }
                int num = atomicInteger.get();
                if (num <= 0) {
                    Thread.sleep(2);
                    continue;
                }
                if (atomicInteger.compareAndSet(num, num - 1)) {
                    break;
                }
            } catch (Exception e) {
            }
        }
    }
}
