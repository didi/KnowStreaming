package com.xiaojukeji.kafka.manager.service.service.impl;

import com.google.common.base.Joiner;
import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.KafkaVersion;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionAttributeDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.*;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.ThreadPool;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.*;
import javax.management.Attribute;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author tukun, zengqiao
 * @date 2015/11/11.
 */
@Service("jmxService")
public class JmxServiceImpl implements JmxService {
    private final static Logger LOGGER = LoggerFactory.getLogger(JmxServiceImpl.class);

    @Autowired
    private PhysicalClusterMetadataManager physicalClusterMetadataManager;

    @Override
    public BrokerMetrics getBrokerMetrics(Long clusterId, Integer brokerId, Integer metricsCode) {
        if (clusterId == null || brokerId == null || metricsCode == null) {
            // 参数非法
            return null;
        }
        if (!PhysicalClusterMetadataManager.isBrokerAlive(clusterId, brokerId)) {
            // Broker不存在
            return null;
        }

        KafkaVersion kafkaVersion = physicalClusterMetadataManager.getKafkaVersion(clusterId, brokerId);

        List<MbeanV2> mbeanV2List = MbeanNameUtilV2.getMbeanList(metricsCode);
        if (ValidateUtils.isEmptyList(mbeanV2List)) {
            return new BrokerMetrics(clusterId, brokerId);
        }

        JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap) || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            LOGGER.warn("get jmx connector failed, clusterId:{} brokerId:{}.", clusterId, brokerId);
            return new BrokerMetrics(clusterId, brokerId);
        }

        BrokerMetrics metrics = new BrokerMetrics(clusterId, brokerId);
        for (MbeanV2 mbeanV2 : mbeanV2List) {
            try {
                getAndSupplyAttributes2BaseMetrics(
                        metrics,
                        jmxConnectorWrap,
                        mbeanV2,
                        new ObjectName(mbeanV2.getObjectName(kafkaVersion.getVersionNum()))
                );
            } catch (Exception e) {
                LOGGER.error("get broker metrics fail, clusterId:{} brokerId:{} mbean:{}.",
                        clusterId, brokerId, mbeanV2, e
                );
            }
        }
        return metrics;
    }

    @Override
    public List<TopicMetrics> getTopicMetrics(Long clusterId, Integer metricsCode, Boolean byAdd) {
        List<String> topicNameList = PhysicalClusterMetadataManager.getTopicNameList(clusterId);
        FutureTask<TopicMetrics>[] taskList = new FutureTask[topicNameList.size()];
        for (int i = 0; i < topicNameList.size(); i++) {
            final String topicName = topicNameList.get(i);
            taskList[i] = new FutureTask<TopicMetrics>(new Callable<TopicMetrics>() {
                @Override
                public TopicMetrics call() throws Exception {
                    return getTopicMetrics(
                            clusterId,
                            topicName,
                            metricsCode,
                            byAdd
                    );
                }
            });
            ThreadPool.submitCollectMetricsTask(taskList[i]);
        }

        List<TopicMetrics> metricsList = new ArrayList<>();
        for (int i = 0; i < taskList.length; ++i) {
            try {
                TopicMetrics topicMetrics = taskList[i].get();
                if (ValidateUtils.isNull(topicMetrics)) {
                    continue;
                }
                metricsList.add(topicMetrics);
            } catch (Exception e) {
                LOGGER.error("get topic-metrics failed, clusterId:{} topicName:{}.",
                        clusterId, topicNameList.get(i), e
                );
            }
        }
        return metricsList;
    }

    @Override
    public TopicMetrics getTopicMetrics(Long clusterId, String topicName, Integer metricsCode, Boolean byAdd) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (topicMetadata == null) {
            return null;
        }
        TopicMetrics metrics = null;
        List<BrokerMetrics> brokerMetricsList = new ArrayList<>();
        for (Integer brokerId : topicMetadata.getBrokerIdSet()) {
            TopicMetrics subMetrics = getTopicMetrics(clusterId, brokerId, topicName, metricsCode, byAdd);

            if (ValidateUtils.isNull(subMetrics)) {
                continue;
            }

            BrokerMetrics brokerMetrics = new BrokerMetrics(clusterId, brokerId);
            brokerMetrics.setMetricsMap(subMetrics.getMetricsMap());

            brokerMetricsList.add(brokerMetrics);

            if (ValidateUtils.isNull(metrics)) {
                metrics = new TopicMetrics(clusterId, topicName);
            }
            if (byAdd) {
                metrics.mergeByAdd(subMetrics);
            } else {
                metrics.mergeByMax(subMetrics);
            }
        }
        if (!ValidateUtils.isNull(metrics)) {
            metrics.setBrokerMetricsList(brokerMetricsList);
        }

        return metrics;
    }

    @Override
    public TopicMetrics getTopicMetrics(Long clusterId, Integer brokerId, String topicName, Integer metricsCode, Boolean byAdd) {
        List<MbeanV2> mbeanV2List = MbeanNameUtilV2.getMbeanList(metricsCode);
        if (ValidateUtils.isEmptyList(mbeanV2List)) {
            return null;
        }
        JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)|| !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            return null;
        }
        TopicMetrics metrics = new TopicMetrics(clusterId, topicName);
        for (MbeanV2 mbeanV2: mbeanV2List) {
            KafkaVersion kafkaVersion = physicalClusterMetadataManager.getKafkaVersion(clusterId, brokerId);
            try {
                getAndSupplyAttributes2BaseMetrics(
                        metrics,
                        jmxConnectorWrap,
                        mbeanV2,
                        new ObjectName(mbeanV2.getObjectName(kafkaVersion.getVersionNum()) + ",topic=" + topicName)
                );
            } catch (Exception e) {
                LOGGER.error("get topic metrics failed, clusterId:{} topicName:{} mbean:{}.",
                        clusterId, topicName, mbeanV2, e
                );
            }
        }
        return metrics;
    }

    @Override
    public String getTopicCodeCValue(Long clusterId, String topicName) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (topicMetadata == null) {
            return null;
        }

        MbeanV2 topicCodeCMBean = null;
        List<MbeanV2> mbeanV2List = MbeanNameUtilV2.getMbeanList(KafkaMetricsCollections.TOPIC_BASIC_PAGE_METRICS);
        if (!ValidateUtils.isEmptyList(mbeanV2List)) {
            topicCodeCMBean = mbeanV2List.stream()
                    .filter(mbeanV2 -> "TopicCodeC".equals(mbeanV2.getFieldName()))
                    .findFirst()
                    .orElse(null);
        }

        if (topicCodeCMBean == null) {
            return null;
        }

        KafkaVersion kafkaVersion;
        Set<String> codeCValues = new HashSet<>();
        TopicMetrics metrics = new TopicMetrics(clusterId, topicName);
        for (Integer brokerId : topicMetadata.getBrokerIdSet()) {
            JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
            if (ValidateUtils.isNull(jmxConnectorWrap)|| !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
                continue;
            }
            kafkaVersion = physicalClusterMetadataManager.getKafkaVersion(clusterId, brokerId);
            // 如果是高版本,需要获取指标{kafka.server:type=AppIdTopicMetrics,name=RecordCompression,appId=*,topic=xxx}
            if (kafkaVersion.getVersionNum() > KafkaVersion.VERSION_0_10_3.longValue()) {
                try {
                    ObjectName objectNameRegX = new ObjectName(topicCodeCMBean.getObjectName(kafkaVersion.getVersionNum())
                            + "*,topic=" + topicName);
                    QueryExp exp = Query.match(Query.attr("Value"), Query.value("*"));
                    Set<ObjectName> objectNames = jmxConnectorWrap.queryNames(objectNameRegX, exp);
                    for (ObjectName objectName : objectNames) {
                        if (objectName.toString().indexOf(",appId=admin,") == -1) {
                            String value = (String) jmxConnectorWrap.getAttribute(objectName, "Value");
                            if (!codeCValues.contains(value)) {
                                codeCValues.add(value);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("get topic codec metrics failed, clusterId:{} brokerId:{} topicName:{} mbean:{}.",
                            clusterId, brokerId, topicName, topicCodeCMBean, e
                    );
                }
            } else {
                // 低版本沿用老逻辑...
                try {
                    getAndSupplyAttributes2BaseMetrics(
                            metrics,
                            jmxConnectorWrap,
                            topicCodeCMBean,
                            new ObjectName(topicCodeCMBean.getObjectName(kafkaVersion.getVersionNum()) + ",topic=" + topicName)
                    );
                } catch (Exception e) {
                    LOGGER.error("get topic codec metrics failed, clusterId:{} topicName:{} mbean:{}.",
                            clusterId, topicName, topicCodeCMBean, e
                    );
                }
            }
        }

        codeCValues.addAll(ListUtils.string2StrList(metrics.getSpecifiedMetrics("TopicCodeCValue", String.class)));

        return Joiner.on(",").join(codeCValues);
    }

    private void getAndSupplyAttributes2BaseMetrics(BaseMetrics metrics,
                                                    JmxConnectorWrap jmxConnectorWrap,
                                                    MbeanV2 mbeanV2,
                                                    ObjectName objectName) {
        List<Attribute> attributeList = null;
        try {
            attributeList = jmxConnectorWrap.getAttributes(objectName, mbeanV2.getAttributeEnum().getAttribute()).asList();
        } catch (InstanceNotFoundException e) {
            return;
        } catch (Exception e) {
            LOGGER.error("get attributes failed, metrics:{} objectName:{}.", metrics, objectName, e);
        }
        if (ValidateUtils.isEmptyList(attributeList)) {
            return;
        }
        for (Attribute attribute: attributeList) {
            if (JmxAttributeEnum.PERCENTILE_ATTRIBUTE.equals(mbeanV2.getAttributeEnum())) {
                metrics.mergeByMax(mbeanV2.getFieldName() + attribute.getName(), attribute.getValue());
            } else {
                metrics.mergeByAdd(mbeanV2.getFieldName() + attribute.getName(), attribute.getValue());
            }
        }
    }

    @Override
    public List<TopicMetrics> getTopicAppMetrics(Long clusterId, Integer metricsCode) {
        List<MbeanV2> mbeanV2List = MbeanNameUtilV2.getMbeanList(metricsCode);
        if (ValidateUtils.isEmptyList(mbeanV2List)) {
            return new ArrayList<>();
        }
        List<Integer> brokerIdList = PhysicalClusterMetadataManager.getBrokerIdList(clusterId);

        FutureTask<List<TopicMetrics>>[] taskList = new FutureTask[brokerIdList.size()];
        for (int i = 0; i < brokerIdList.size(); ++i) {
            final Integer brokerId = brokerIdList.get(i);
            taskList[i] = new FutureTask<List<TopicMetrics>>(new Callable<List<TopicMetrics>>() {
                @Override
                public List<TopicMetrics> call() throws Exception {
                    List<TopicMetrics> metricsList = new ArrayList<>();
                    for (MbeanV2 mbeanV2: mbeanV2List) {
                        List<TopicMetrics> subMetricsList = getTopicAppMetrics(clusterId, brokerId, mbeanV2);
                        if (ValidateUtils.isEmptyList(subMetricsList)) {
                            continue;
                        }
                        metricsList.addAll(subMetricsList);
                    }
                    return metricsList;
                }
            });
            ThreadPool.submitCollectMetricsTask(taskList[i]);
        }

        Map<String, TopicMetrics> metricsMap = new HashMap<>();
        for (int i = 0; i < taskList.length; ++i) {
            try {
                List<TopicMetrics> metricsList = taskList[i].get();
                if (ValidateUtils.isEmptyList(metricsList)) {
                    continue;
                }
                for (TopicMetrics elem: metricsList) {
                    String key = elem.getAppId() + "$" +elem.getTopicName();
                    TopicMetrics metrics = metricsMap.getOrDefault(
                            key, new TopicMetrics(elem.getAppId(), elem.getClusterId(), elem.getTopicName())
                    );
                    metrics.mergeByAdd(elem);
                    metricsMap.put(key, metrics);
                }
            } catch (Exception e) {
                LOGGER.error("get app-topic-metrics failed, clusterId:{} brokerId:{}.",
                        clusterId, brokerIdList.get(i), e
                );
            }
        }
        return new ArrayList<>(metricsMap.values());
    }

    private List<TopicMetrics> getTopicAppMetrics(Long clusterId, Integer brokerId, MbeanV2 mbeanV2) {
        JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)|| !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            return new ArrayList<>();
        }
        List<TopicMetrics> metricsList = new ArrayList<>();

        Set<ObjectName> objectNameSet = queryObjectNameSet(clusterId,  brokerId, mbeanV2, jmxConnectorWrap);
        for (ObjectName objectName: objectNameSet) {
            String topicName = objectName.getKeyProperty(JmxConstant.TOPIC);
            String appId = objectName.getKeyProperty(JmxConstant.APP_ID);
            if (ValidateUtils.isNull(topicName) || ValidateUtils.isNull(appId)) {
                continue;
            }
            TopicMetrics metrics = new TopicMetrics(appId, clusterId, topicName);
            getAndSupplyAttributes2BaseMetrics(metrics, jmxConnectorWrap, mbeanV2, objectName);
            metricsList.add(metrics);
        }
        return metricsList;
    }

    private Set<ObjectName> queryObjectNameSet(Long clusterId,
                                               Integer brokerId,
                                               MbeanV2 mbeanV2,
                                               JmxConnectorWrap jmxConnectorWrap) {
        KafkaVersion kafkaVersion = physicalClusterMetadataManager.getKafkaVersion(clusterId, brokerId);
        Set<ObjectName> objectNameSet = new HashSet<>();
        for (String attribute: mbeanV2.getAttributeEnum().getAttribute()) {
            try {
                QueryExp exp = Query.gt(Query.attr(attribute), Query.value(0.0));
                objectNameSet.addAll(
                        jmxConnectorWrap.queryNames(new ObjectName(mbeanV2.getObjectName(kafkaVersion.getVersionNum())), exp)
                );
                break;
            } catch (Exception e) {
                LOGGER.error("query objectNames failed, clusterId:{} brokerId:{} mbean:{} attribute:{}.",
                        clusterId, brokerId, mbeanV2, attribute, e
                );
            }
        }
        return objectNameSet;
    }


    @Override
    public Map<TopicPartition, String> getBrokerTopicLocation(Long clusterId, Integer brokerId) {
        JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)|| !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            return new HashMap<>(0);
        }

        String diskName = "*";
        Map<TopicPartition, String> diskNameMap = new HashMap<>();

        Set<ObjectName> allObjectNameSet = null;
        Long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now <= 5000L) {
            // 在超时时间内处理完
            try {
                ObjectName preObjectName = new ObjectName(
                        "kafka.log:type=TopicPartitionMetrics,name=TopicPartitionDir,topic=*,partition=*"
                );
                QueryExp exp = Query.match(Query.attr("Value"), Query.value(diskName));

                Set<ObjectName> subObjectNameSet = jmxConnectorWrap.queryNames(preObjectName, exp);
                if (!diskName.equals("*") && subObjectNameSet != null) {
                    allObjectNameSet.removeAll(subObjectNameSet);
                    for (ObjectName objectName: subObjectNameSet) {
                        String subObjectName = objectName.toString()
                                .replaceFirst("kafka.log:type=TopicPartitionMetrics,name=TopicPartitionDir,topic=", "")
                                .replaceFirst(",partition=", "\t");
                        String[] topicNamePartitionId = subObjectName.split("\t");
                        diskNameMap.put(
                                new TopicPartition(topicNamePartitionId[0], Integer.valueOf(topicNamePartitionId[1])),
                                diskName
                        );
                    }
                } else {
                    allObjectNameSet = subObjectNameSet;
                }
                if (ValidateUtils.isEmptySet(allObjectNameSet)) {
                    break;
                }
                ObjectName objectName = allObjectNameSet.iterator().next();
                subObjectNameSet.remove(objectName);

                diskName = (String) jmxConnectorWrap.getAttribute(objectName, "Value");
            } catch (Exception e) {
                LOGGER.error("get broker topic locations failed, clusterId:{} brokerId:{}.", clusterId, brokerId, e);
            }
        }
        return diskNameMap;
    }











    @Override
    public Map<Integer, PartitionAttributeDTO> getPartitionAttribute(Long clusterId,
                                                                     String topicName,
                                                                     List<PartitionState> partitionStateList) {
        Map<Integer, PartitionAttributeDTO> partitionMap = new HashMap<>();
        if (ValidateUtils.isEmptyList(partitionStateList)) {
            return partitionMap;
        }
        Mbean logSizeMbean = MbeanNameUtil.getMbean(JmxConstant.TOPIC_PARTITION_LOG_SIZE);
        for (PartitionState partitionState : partitionStateList) {
            if (partitionMap.containsKey(partitionState.getPartitionId())) {
                continue;
            }

            JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, partitionState.getLeader());
            if (ValidateUtils.isNull(jmxConnectorWrap) || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
                continue;
            }
            try {
                PartitionAttributeDTO dto = new PartitionAttributeDTO();
                dto.setLogSize((Long) jmxConnectorWrap.getAttribute(
                        new ObjectName(
                                String.format(
                                        logSizeMbean.getObjectName(),
                                        topicName,
                                        partitionState.getPartitionId()
                                )),
                        logSizeMbean.getProperty()
                ));
                partitionMap.put(partitionState.getPartitionId(), dto);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return partitionMap;
    }



    @Override
    public Set<String> getBrokerThrottleClients(Long clusterId,
                                                Integer brokerId,
                                                KafkaClientEnum kafkaClientEnum) {
        Mbean mbean = MbeanNameUtil.getMbean(kafkaClientEnum.getName() + "ThrottleTime");
        if (ValidateUtils.isNull(mbean)) {
            return new HashSet<>();
        }

        JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap) || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            return new HashSet<>();
        }
        QueryExp exp = Query.gt(Query.attr(mbean.getProperty()), Query.value(0.0));
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(mbean.getObjectName());
        } catch (Exception e) {
            LOGGER.error("getBrokerThrottleClients@JmxServiceImpl, get failed, clusterId:{} brokerId:{} mbean:{}.",
                    clusterId,
                    brokerId,
                    mbean,
                    e);
        }
        if (ValidateUtils.isNull(objectName)) {
            return new HashSet<>();
        }

        Set<String> clientSet = new HashSet<>();
        try {
            Set<ObjectName> objectNameSet = jmxConnectorWrap.queryNames(objectName, exp);
            if (objectNameSet == null || objectNameSet.isEmpty()) {
                return clientSet;
            }
            for (ObjectName name : objectNameSet) {
                clientSet.add(name.toString().substring(mbean.getObjectName().length() - 1));
            }
        } catch (Exception e) {
            LOGGER.error("getBrokerThrottleClients@JmxServiceImpl, get failed, clusterId:{} brokerId:{} mbean:{}.",
                    clusterId,
                    brokerId,
                    mbean,
                    e);
        }
        return clientSet;
    }

    @Override
    public String getBrokerVersion(Long clusterId, Integer brokerId) {
        JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap) || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            return "";
        }

        List<MbeanV2> mbeanV2List = MbeanNameUtilV2.getMbeanList(KafkaMetricsCollections.BROKER_VERSION);
        if (mbeanV2List.isEmpty()) {
            return "";
        }
        MbeanV2 mbeanV2 = mbeanV2List.get(0);
        try {
            return (String) jmxConnectorWrap.getAttribute(
                    new ObjectName(
                            mbeanV2.getObjectName(KafkaVersion.VERSION_MAX)
                                    + ",id="
                                    + String.valueOf(brokerId)
                    ),
                    mbeanV2.getAttributeEnum().getAttribute()[0]
            );
        } catch (Exception e) {
            LOGGER.error("get broker version failed, clusterId:{} brokerId:{}.", clusterId, brokerId, e);
        }
        return "";
    }

    @Override
    public double getTopicAppThrottle(Long clusterId,
                                      Integer brokerId,
                                      String clientId,
                                      KafkaClientEnum kafkaClientEnum) {
        double throttle = 0.0;
        Mbean mbean = MbeanNameUtil.getMbean(kafkaClientEnum.getName() + "ThrottleTime");
        if (ValidateUtils.isNull(mbean)) {
            return throttle;
        }
        JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(clusterId, brokerId);
        if (ValidateUtils.isNull(jmxConnectorWrap) || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            return throttle;
        }
        String objectName = mbean.getObjectName().substring(0, mbean.getObjectName().length() - 1) + clientId;
        try {
            return (Double)jmxConnectorWrap.getAttribute(new ObjectName(objectName), mbean.getProperty());
        } catch (InstanceNotFoundException e) {
            // object不存在, 不打日志, 避免日志太多
        } catch (Exception e) {
            LOGGER.error("get topic app throttle failed, clusterId:{} brokerId:{}, clientId:{}, kafkaClientEnum:{}.",
                    clusterId, brokerId, clientId, kafkaClientEnum, e);
        }
        return throttle;
    }
}
