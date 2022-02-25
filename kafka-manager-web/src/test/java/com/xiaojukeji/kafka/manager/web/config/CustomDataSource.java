package com.xiaojukeji.kafka.manager.web.config;

import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionAddGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionModifyGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.config.ConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.LogicalClusterDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.RegionDTO;

import java.util.Arrays;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/2/17
 */
public class CustomDataSource {

    public static RegionDTO getRegionDTO(Map<String, String> configMap) {
        RegionDTO regionDTO = new RegionDTO();
        Long physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        regionDTO.setClusterId(physicalClusterId);
        Integer brokerId = Integer.parseInt(configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        regionDTO.setBrokerIdList(Arrays.asList(brokerId));
        regionDTO.setDescription("");
        regionDTO.setName(configMap.get(ConfigConstant.REGION_NAME));
        regionDTO.setStatus(0);
        return regionDTO;
    }

    public static LogicalClusterDTO getLogicalClusterDTO(Map<String, String> configMap) {
        LogicalClusterDTO logicalClusterDTO = new LogicalClusterDTO();
        Long physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        logicalClusterDTO.setClusterId(physicalClusterId);
        logicalClusterDTO.setName(configMap.get(ConfigConstant.LOGICAL_CLUSTER_NAME));
        logicalClusterDTO.setAppId(configMap.get(ConfigConstant.APPID));
        logicalClusterDTO.setIdentification(configMap.get(ConfigConstant.LOGICAL_CLUSTER_NAME));
        logicalClusterDTO.setDescription("");
        logicalClusterDTO.setMode(0);
        return logicalClusterDTO;
    }

    public static ConfigDTO getConfigDTO() {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setConfigKey(ConfigConstant.CONFIG_KEY);
        configDTO.setConfigValue(ConfigConstant.CONFIG_VALUE);
        configDTO.setConfigDescription("测试config");
        return configDTO;
    }

    public static TopicDeletionDTO getTopicDeletionDTO(Map<String, String> configMap) {
        TopicDeletionDTO deletionDTO = new TopicDeletionDTO();
        Long physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        deletionDTO.setClusterId(physicalClusterId);
        deletionDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        deletionDTO.setUnForce(true);
        return deletionDTO;
    }

    public static TopicCreationDTO getTopicCreationDTO(Map<String, String> configMap) {
        // 在broker1上创建1分区，1副本的createTopicTest
        TopicCreationDTO creationDTO = new TopicCreationDTO();
        creationDTO.setAppId(configMap.get(ConfigConstant.APPID));
        // 在broker1上创建
        Integer brokerId = Integer.parseInt(configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        creationDTO.setBrokerIdList(Arrays.asList(brokerId));
        creationDTO.setPartitionNum(1);
        creationDTO.setReplicaNum(1);
        creationDTO.setRetentionTime(1000L * 60 * 60 * 168);
        creationDTO.setPeakBytesIn(10L * 1024 * 1024);
        // 物理集群id
        Long physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        creationDTO.setClusterId(physicalClusterId);
        creationDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        return creationDTO;
    }

    public static OrderExtensionAddGatewayConfigDTO getOrderExtensionAddGatewayConfigDTO(Map<String, String> configMap) {
        OrderExtensionAddGatewayConfigDTO orderExtensionAddGatewayConfigDTO = new OrderExtensionAddGatewayConfigDTO();
        orderExtensionAddGatewayConfigDTO.setName(configMap.get(ConfigConstant.GATEWAY_NAME));
        orderExtensionAddGatewayConfigDTO.setType(configMap.get(ConfigConstant.GATEWAY_TYPE));
        orderExtensionAddGatewayConfigDTO.setValue(configMap.get(ConfigConstant.GATEWAY_VALUE));
        orderExtensionAddGatewayConfigDTO.setValue(configMap.get(ConfigConstant.GATEWAY_DESCRIPTION));
        return orderExtensionAddGatewayConfigDTO;
    }

    public static OrderExtensionModifyGatewayConfigDTO getOrderExtensionModifyGatewayConfigDTO(Map<String, String> configMap) {
        OrderExtensionModifyGatewayConfigDTO orderExtensionModifyGatewayConfigDTO = new OrderExtensionModifyGatewayConfigDTO();
        orderExtensionModifyGatewayConfigDTO.setName(configMap.get(ConfigConstant.GATEWAY_NAME));
        orderExtensionModifyGatewayConfigDTO.setType(configMap.get(ConfigConstant.GATEWAY_TYPE));
        orderExtensionModifyGatewayConfigDTO.setValue(configMap.get(ConfigConstant.GATEWAY_VALUE));
        orderExtensionModifyGatewayConfigDTO.setDescription(configMap.get(ConfigConstant.GATEWAY_DESCRIPTION));
        return orderExtensionModifyGatewayConfigDTO;
    }

    public static OrderDTO getOrderDTO(Map<String, String> configMap) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setApplicant(ConfigConstant.ADMIN_USER);
        orderDTO.setType(0);
        orderDTO.setDescription(ConfigConstant.DESCRIPTION);
        long logicalClusterId = Long.parseLong(configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID));
        String topicName = configMap.get(ConfigConstant.TOPIC_NAME);
        String appId = configMap.get(ConfigConstant.APPID);

        String extensions = "{\"clusterId\":\"" + logicalClusterId +
                "\",\"topicName\":\"" + topicName + "\",\"appId\":\"" + appId + "\",\"peakBytesIn\":104857600000}";
        orderDTO.setExtensions(extensions);
        return orderDTO;
    }
}
