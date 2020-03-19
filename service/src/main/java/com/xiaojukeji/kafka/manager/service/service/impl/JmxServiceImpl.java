package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConnectorWrap;
import com.xiaojukeji.kafka.manager.common.utils.jmx.Mbean;
import com.xiaojukeji.kafka.manager.common.utils.jmx.MbeanNameUtil;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.utils.ObjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.*;
import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author tukun, zengqiao
 * @date 2015/11/11.
 */
@Service("jmxService")
public class JmxServiceImpl implements JmxService {
    private final static Logger logger = LoggerFactory.getLogger(JmxServiceImpl.class);

    @Autowired
    BrokerService brokerService;

    @Autowired
    ClusterService clusterService;

    @Override
    public BrokerMetrics getSpecifiedBrokerMetricsFromJmx(Long clusterId,
                                                          Integer brokerId,
                                                          List<String> specifiedFieldList,
                                                          Boolean simple) {
        if (clusterId == null || brokerId == null || specifiedFieldList == null) {
            throw new IllegalArgumentException("param illegal");
        }

        MBeanServerConnection connection = getMBeanServerConnection(clusterId, brokerId);
        if (connection == null) {
            logger.warn("getSpecifiedBrokerMetricsFromJmx@JmxServiceImpl, get jmx connector failed, clusterId:{} brokerId:{}.", clusterId, brokerId);
            return null;
        }

        BrokerMetrics brokerMetrics = null;
        for (String fieldName : specifiedFieldList) {
            String property = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            Mbean mbean = MbeanNameUtil.getMbean(property, null);
            if (mbean == null || StringUtils.isEmpty(mbean.getObjectName())) {
                // 找不到objectName，则跳过
                continue;
            }

            // 依据不同的field, 获取不同的属性信息
            String[] properties = new String[]{mbean.getProperty()};
            if (!simple && "OneMinuteRate".equals(mbean.getProperty())) {
                properties = new String[]{"MeanRate", "OneMinuteRate", "FiveMinuteRate", "FifteenMinuteRate"};
            }

            List<Attribute> attributeValueList = null;
            try {
                attributeValueList = connection.getAttributes(new ObjectName(mbean.getObjectName()), properties).asList();
            } catch (Exception e) {
                logger.error("getSpecifiedBrokerMetricsFromJmx@JmxServiceImpl, get metrics fail, objectName:{}.", mbean.getObjectName(), e);
                continue;
            }
            if (attributeValueList == null || attributeValueList.isEmpty()) {
                continue;
            }

            // 使用反射的方法调用setProperty方法给对象赋值
            for (int i = 0; i < properties.length; i++) {
                String propertyName = property;
                if ("MeanRate".equals(properties[i]) || "FiveMinuteRate".equals(properties[i]) || "FifteenMinuteRate".equals(properties[i])) {
                    propertyName = property + properties[i];
                }

                Method method = BeanUtils.findMethod(BrokerMetrics.class, setProperty(propertyName), mbean.getPropertyClass());
                if (method == null) {
                    continue;
                }

                if (brokerMetrics == null) {
                    brokerMetrics = new BrokerMetrics();
                    brokerMetrics.setClusterId(clusterId);
                    brokerMetrics.setBrokerId(brokerId);
                }
                try {
                    method.invoke(brokerMetrics, attributeValueList.get(i).getValue());
                } catch (Exception e) {
                    logger.error("getSpecifiedBrokerMetricsFromJmx@JmxServiceImpl, call method failed, methodName:{}.", setProperty(propertyName), e);
                }
            }
        }
        return brokerMetrics;
    }

    @Override
    public TopicMetrics getSpecifiedTopicMetricsFromJmx(Long clusterId,
                                                        String topicName,
                                                        List<String> specifiedFieldList,
                                                        Boolean simple) {
        return getSpecifiedBrokerTopicMetricsFromJmx(clusterId, -1, topicName, specifiedFieldList, simple);
    }

    @Override
    public TopicMetrics getSpecifiedBrokerTopicMetricsFromJmx(Long clusterId,
                                                        Integer filteredBrokerId,
                                                        String topicName,
                                                        List<String> specifiedFieldList,
                                                        Boolean simple) {
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            throw new IllegalArgumentException("param illegal");
        }
        String[] properties = null;
        if (simple) {
            properties = new String[]{"OneMinuteRate"};
        } else {
            properties = new String[]{"MeanRate", "OneMinuteRate", "FiveMinuteRate", "FifteenMinuteRate"};
        }

        TopicMetrics topicMetrics = new TopicMetrics();
        for (Integer brokerId: topicMetadata.getBrokerIdSet()) {
            try {
                if (filteredBrokerId != null && filteredBrokerId >= 0 && !brokerId.equals(filteredBrokerId)) {
                    continue;
                }

                MBeanServerConnection connection = getMBeanServerConnection(clusterId, brokerId);
                if(connection == null){
                    continue;
                }
                TopicMetrics newTopicMetrics = new TopicMetrics();
                for (String fieldName : specifiedFieldList) {
                    if (!fieldName.endsWith("PerSec")) {
                        continue;
                    }
                    String property = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    Mbean mbean = MbeanNameUtil.getMbean(property, topicName);
                    if (mbean == null || StringUtils.isEmpty(mbean.getObjectName())) {
                        // 找不到objectName，则跳过
                        continue;
                    }

                    // 对于某些存于zk上的Topic可能没有metrics值，所以单独捕获异常
                    List<Attribute> attributeValueList = null;
                    try {
                        attributeValueList = connection.getAttributes(new ObjectName(mbean.getObjectName()), properties).asList();
                    } catch (Exception e) {
                        continue;
                    }

                    for (int i = 0; i < properties.length; i++) {
                        String propertyName = "OneMinuteRate".equals(properties[i]) ? property: property + properties[i];
                        Method method = BeanUtils.findMethod(TopicMetrics.class, setProperty(propertyName), Double.class);
                        if (method != null) {
                            method.invoke(newTopicMetrics, attributeValueList.get(i).getValue());
                            continue;
                        }
                        logger.warn("method not find, methodName = " + setProperty(propertyName));
                    }
                }
                ObjectUtil.add(topicMetrics, newTopicMetrics, "PerSec");
            } catch (Exception e) {
                logger.error("getSpecifiedTopicMetrics@JmxServiceImpl, get topic metrics from jmx error.", e);
            }
        }
        topicMetrics.setClusterId(clusterId);
        topicMetrics.setTopicName(topicName);
        return topicMetrics;
    }

    private MBeanServerConnection getMBeanServerConnection(Long clusterId , int brokerId){
        JmxConnectorWrap jmxConnectorWrap = ClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (jmxConnectorWrap == null) {
//            logger.error("get jmxConnector fail, clusterId:{} brokerId:{}.", clusterId, brokerId);
            return null;
        }

        JMXConnector jmxConnector = jmxConnectorWrap.getJmxConnector();
        if(jmxConnector == null){
            return null;
        }
        try {
            return jmxConnector.getMBeanServerConnection();
        } catch (IOException e) {
            logger.error("can not get connection to brokerId:{} which clusterId is:{}",brokerId,clusterId,e);
        }
        return null;
    }

    private String setProperty(String property) {
        return "set" + property;
    }
}
