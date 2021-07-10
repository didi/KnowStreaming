package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicConnectionDO;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.gateway.TopicConnectionDao;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zyk
 */
@Service("topicConnectionService")
public class TopicConnectionServiceImpl implements TopicConnectionService {
    private final static Logger LOGGER = LoggerFactory.getLogger(TopicConnectionServiceImpl.class);

    @Autowired
    private TopicConnectionDao topicConnectionDao;

    private int splitInterval = 50;

    @Override
    public void batchAdd(List<TopicConnectionDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return;
        }
        int allSize = doList.size();
        int successSize = 0;

        int part = doList.size() / splitInterval;
        for (int i = 0; i < part; ++i) {
            List<TopicConnectionDO> subList = doList.subList(0, splitInterval);
            successSize += topicConnectionDao.batchReplace(subList);
            doList.subList(0, splitInterval).clear();
        }
        if (!ValidateUtils.isEmptyList(doList)) {
            successSize += topicConnectionDao.batchReplace(doList);
        }
        LOGGER.info("class=TopicConnectionServiceImpl||method=batchAdd||allSize={}||successSize={}", allSize, successSize);

    }

    @Override
    public List<TopicConnection> getByTopicName(Long clusterId,
                                                String topicName,
                                                Date startTime,
                                                Date endTime) {
        List<TopicConnectionDO> doList = null;
        try {
            doList = topicConnectionDao.getByTopicName(clusterId, topicName, startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("get topic connections failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
        }
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        return getByTopicName(clusterId, doList);
    }

    @Override
    public List<TopicConnection> getByTopicName(Long clusterId,
                                                String topicName,
                                                String appId,
                                                Date startTime,
                                                Date endTime) {
        List<TopicConnectionDO> doList = null;
        try {
            doList = topicConnectionDao.getByTopicName(clusterId, topicName, startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("get topic connections failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
        }
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        return getByTopicName(
                clusterId,
                doList.stream().filter(elem -> elem.getAppId().equals(appId)).collect(Collectors.toList())
        );
    }

    @Override
    public List<TopicConnection> getByAppId(String appId, Date startTime, Date endTime) {
        List<TopicConnectionDO> doList = null;
        try {
            doList = topicConnectionDao.getByAppId(appId, startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("get topic connections failed, appId:{} .", appId, e);
        }
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        return getByTopicName(null, doList);
    }

    @Override
    public boolean isExistConnection(Long clusterId,
                                    String topicName,
                                    Date startTime,
                                    Date endTime) {
        List<TopicConnection> connectionList = this.getByTopicName(
                clusterId,
                topicName,
                startTime,
                endTime);
        if (!ValidateUtils.isEmptyList(connectionList)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isExistConnection(Long clusterId,
                                     String topicName,
                                     String appId,
                                     Date startTime,
                                     Date endTime) {
        List<TopicConnection> connectionList = this.getByTopicName(
                clusterId,
                topicName,
                appId,
                startTime,
                endTime);
        if (!ValidateUtils.isEmptyList(connectionList)) {
            return true;
        }
        return false;
    }

    private List<TopicConnection> getByTopicName(Long clusterId, List<TopicConnectionDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        Set<String> brokerHostnameSet = new HashSet<>();
        if (!ValidateUtils.isNull(clusterId)) {
            brokerHostnameSet = PhysicalClusterMetadataManager.getBrokerHostnameSet(clusterId);
        }

        // 去重复及版本信息无效的数据
        Map<String, List<TopicConnection>> dtoListMap = new HashMap<>();
        for (TopicConnectionDO connectionDO: doList) {
            //如果存在其他版本信息, 则过滤掉版本为-1的用户
            String deDuplicateKey = connectionDO.uniqueKey();
            List<TopicConnection> dtoList = dtoListMap.getOrDefault(deDuplicateKey, new ArrayList<>());
            if (!dtoList.isEmpty() && (
                    KafkaConstant.CLIENT_VERSION_CODE_UNKNOWN.equals(connectionDO.getClientVersion())
                            || KafkaConstant.CLIENT_VERSION_NAME_UNKNOWN.equals(connectionDO.getClientVersion()))
                    ) {
                // 非空 && connectionDO是Version_UNKNOWN
                continue;
            }
            if (!dtoList.isEmpty() && (
                    KafkaConstant.CLIENT_VERSION_NAME_UNKNOWN.equals(dtoList.get(0).getClientVersion())
                            || KafkaConstant.CLIENT_VERSION_CODE_UNKNOWN.equals(dtoList.get(0).getClientVersion())
            )) {
                // 非空 && dtoList里面是Version_UNKNOWN
                dtoList.remove(0);
            }
            TopicConnection dto = convert2TopicConnectionDTO(connectionDO);

            // 过滤掉broker的机器
            if (brokerHostnameSet.contains(dto.getHostname()) || brokerHostnameSet.contains(dto.getIp())) {
                // 发现消费的机器是broker, 则直接跳过. brokerHostnameSet有的集群存储的是IP
                continue;
            }

            dtoList.add(dto);
            dtoListMap.put(deDuplicateKey, dtoList);
        }

        List<TopicConnection> dtoList = new ArrayList<>();
        for (Map.Entry<String, List<TopicConnection>> entry: dtoListMap.entrySet()) {
            dtoList.addAll(entry.getValue());
        }
        return dtoList;
    }

    private TopicConnection convert2TopicConnectionDTO(TopicConnectionDO connectionDO) {
        TopicConnection dto = new TopicConnection();
        dto.setClusterId(connectionDO.getClusterId());
        dto.setTopicName(connectionDO.getTopicName());
        switch (connectionDO.getType()) {
            case "produce": dto.setClientType("producer"); break;
            case "fetch": dto.setClientType("consumer"); break;
            default: dto.setClientType("");
        }
        dto.setAppId(connectionDO.getAppId());
        dto.setClientVersion(
                connectionDO.getClientVersion().equals(KafkaConstant.CLIENT_VERSION_CODE_UNKNOWN) ?
                        KafkaConstant.CLIENT_VERSION_NAME_UNKNOWN : connectionDO.getClientVersion()
        );
        dto.setIp(connectionDO.getIp());

        String hostName = connectionDO.getIp();
        try {
            InetAddress ia = InetAddress.getByAddress(getIpBytes(connectionDO.getIp()));
            hostName = ia.getHostName();
        } catch (Exception e) {
            LOGGER.error("get hostname failed. ip:{}.", connectionDO.getIp(), e);
        }
        dto.setHostname(hostName.replace(KafkaConstant.BROKER_HOST_NAME_SUFFIX, ""));
        return dto;
    }

    private byte[] getIpBytes(String ip) {
        String[] ipStr=ip.split("[.]");
        byte[] ipBytes=new byte[4];
        for (int i = 0; i < 4; i++) {
            int m=Integer.parseInt(ipStr[i]);
            byte b=(byte)(m&0xff);
            ipBytes[i]=b;
        }
        return ipBytes;
    }
}
