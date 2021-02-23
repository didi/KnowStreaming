package com.xiaojukeji.kafka.manager.common.utils.jmx;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.naming.Context;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    private JmxConfig jmxConfig;

    public JmxConnectorWrap(String host, int port, JmxConfig jmxConfig) {
        this.host = host;
        this.port = port;
        this.jmxConfig = jmxConfig;
        if (ValidateUtils.isNull(this.jmxConfig)) {
            this.jmxConfig = new JmxConfig();
        }
        if (ValidateUtils.isNullOrLessThanZero(this.jmxConfig.getMaxConn())) {
            this.jmxConfig.setMaxConn(1);
        }
        this.atomicInteger = new AtomicInteger(this.jmxConfig.getMaxConn());
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
            Map<String, Object> environment = new HashMap<String, Object>();
            if (!ValidateUtils.isBlank(this.jmxConfig.getUsername()) && !ValidateUtils.isBlank(this.jmxConfig.getPassword())) {
                environment.put(JMXConnector.CREDENTIALS, Arrays.asList(this.jmxConfig.getUsername(), this.jmxConfig.getPassword()));
            }
            if (jmxConfig.isOpenSSL() != null && this.jmxConfig.isOpenSSL()) {
                environment.put(Context.SECURITY_PROTOCOL, "ssl");
                SslRMIClientSocketFactory clientSocketFactory = new SslRMIClientSocketFactory();
                environment.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, clientSocketFactory);
                environment.put("com.sun.jndi.rmi.factory.socket", clientSocketFactory);
            }

            jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl), environment);
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
