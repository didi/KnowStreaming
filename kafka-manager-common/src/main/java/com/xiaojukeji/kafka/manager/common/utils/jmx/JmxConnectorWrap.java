package com.xiaojukeji.kafka.manager.common.utils.jmx;

import com.xiaojukeji.kafka.manager.common.utils.BackoffUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JMXConnector包装类
 * @author tukun
 * @date 2015/11/9.
 */
public class JmxConnectorWrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxConnectorWrap.class);

    private final Long physicalClusterId;

    private final Integer brokerId;

    private final String host;

    private final int port;

    private JMXConnector jmxConnector;

    private final AtomicInteger atomicInteger;

    private JmxConfig jmxConfig;

    private final ReentrantLock modifyJMXConnectorLock = new ReentrantLock();

    public JmxConnectorWrap(Long physicalClusterId, Integer brokerId, String host, int port, JmxConfig jmxConfig) {
        this.physicalClusterId = physicalClusterId;
        this.brokerId = brokerId;
        this.host = host;
        this.port = port;
        this.jmxConfig = jmxConfig;
        if (ValidateUtils.isNull(this.jmxConfig)) {
            this.jmxConfig = new JmxConfig();
        }
        if (ValidateUtils.isNullOrLessThanZero(this.jmxConfig.getMaxConn())) {
            // 默认设置20
            this.jmxConfig.setMaxConn(20);
        }
        if (ValidateUtils.isNullOrLessThanZero(this.jmxConfig.getRetryConnectBackoffTimeUnitMs())) {
            // 默认回退10分钟
            this.jmxConfig.setRetryConnectBackoffTimeUnitMs(10 * 60 * 1000L);
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
        return safeCreateJmxConnector();
    }

    public void close() {
        this.closeJmxConnect();
    }

    public void closeJmxConnect() {
        if (jmxConnector == null) {
            return;
        }

        try {
            modifyJMXConnectorLock.lock();

            // 移除设置的backoff事件
            BackoffUtils.removeNeedBackoffEvent(buildConnectJmxFailedBackoffEventKey(physicalClusterId, brokerId));

            jmxConnector.close();
        } catch (Exception e) {
            LOGGER.error("close JmxConnector exception, physicalClusterId:{} brokerId:{} host:{} port:{}.", physicalClusterId, brokerId, host, port, e);
        } finally {
            jmxConnector = null;

            modifyJMXConnectorLock.unlock();
        }
    }

    private boolean safeCreateJmxConnector() {
        try {
            modifyJMXConnectorLock.lock();
            return createJmxConnector();
        } finally {
            modifyJMXConnectorLock.unlock();
        }
    }

    private synchronized boolean createJmxConnector() {
        if (jmxConnector != null) {
            return true;
        }

        if (BackoffUtils.isNeedBackoff(buildConnectJmxFailedBackoffEventKey(physicalClusterId, brokerId))) {
            // 被设置了需要进行回退，则本次不进行创建
            return false;
        }

        String jmxUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
        try {
            Map<String, Object> environment = new HashMap<String, Object>();
            if (!ValidateUtils.isBlank(this.jmxConfig.getUsername()) && !ValidateUtils.isBlank(this.jmxConfig.getPassword())) {
                // fixed by riyuetianmu
                environment.put(JMXConnector.CREDENTIALS, new String[]{this.jmxConfig.getUsername(), this.jmxConfig.getPassword()});
            }

            if (jmxConfig.getOpenSSL() != null && this.jmxConfig.getOpenSSL()) {
                // 开启ssl
                environment.put(Context.SECURITY_PROTOCOL, "ssl");
                SslRMIClientSocketFactory clientSocketFactory = new SslRMIClientSocketFactory();
                environment.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, clientSocketFactory);
                environment.put("com.sun.jndi.rmi.factory.socket", clientSocketFactory);
            }

            jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl), environment);
            LOGGER.info("connect JMX success, physicalClusterId:{} brokerId:{} host:{} port:{}.", physicalClusterId, brokerId, host, port);
            return true;
        } catch (MalformedURLException e) {
            LOGGER.error("connect JMX failed, JMX url exception, physicalClusterId:{} brokerId:{} host:{} port:{} jmxUrl:{}.", physicalClusterId, brokerId, host, port, jmxUrl, e);
        } catch (Exception e) {
            LOGGER.error("connect JMX failed, physicalClusterId:{} brokerId:{} host:{} port:{}.", physicalClusterId, brokerId, host, port, e);
        }

        // 设置连接backoff
        BackoffUtils.putNeedBackoffEvent(buildConnectJmxFailedBackoffEventKey(physicalClusterId, brokerId), this.jmxConfig.getRetryConnectBackoffTimeUnitMs());

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
        } catch (IOException ioe) {
            // io错误，则重置连接
            this.closeJmxConnect();

            throw ioe;
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
        } catch (IOException ioe) {
            // io错误，则重置连接
            this.closeJmxConnect();

            throw ioe;
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
        } catch (IOException ioe) {
            // io错误，则重置连接
            this.closeJmxConnect();

            throw ioe;
        } finally {
            atomicInteger.incrementAndGet();
        }
    }

    private void acquire() {
        long now = System.currentTimeMillis();
        while (true) {
            try {
                int num = atomicInteger.get();
                if (num <= 0) {
                    BackoffUtils.backoff(2);
                }

                if (atomicInteger.compareAndSet(num, num - 1) || System.currentTimeMillis() - now > 6000) {
                    break;
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private static String buildConnectJmxFailedBackoffEventKey(Long physicalClusterId, Integer brokerId) {
        return "CONNECT_JMX_FAILED_BACK_OFF_EVENT_PHY_" + physicalClusterId + "_BROKER_" + brokerId;
    }
}
