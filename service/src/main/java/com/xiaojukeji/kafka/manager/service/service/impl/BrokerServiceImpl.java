package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverallDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.BrokerDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;
import com.xiaojukeji.kafka.manager.dao.BrokerMetricsDao;
import com.xiaojukeji.kafka.manager.dao.BrokerDao;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author tukun, zengqiao
 * @date 2015/11/9
 */
@Service("brokerService")
public class BrokerServiceImpl implements BrokerService {
    private final static Logger logger = LoggerFactory.getLogger(BrokerServiceImpl.class);

    @Autowired
    private BrokerMetricsDao brokerMetricDao;

    @Autowired
    private BrokerDao brokerDao;

    @Autowired
    private JmxService jmxService;

    @Override
    public List<BrokerOverviewDTO> getBrokerOverviewList(Long clusterId, List<String> specifiedFieldList, boolean simple) {
        Map<Integer, BrokerMetrics> brokerMap = getSpecifiedBrokerMetrics(clusterId, specifiedFieldList, simple);

        List<BrokerOverviewDTO> brokerOverviewDTOList = new ArrayList<>();
        for (Integer brokerId: ClusterMetadataManager.getBrokerIdList(clusterId)) {
            BrokerMetadata brokerMetadata = ClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (brokerMetadata == null) {
                continue;
            }
            brokerOverviewDTOList.add(BrokerOverviewDTO.newInstance(brokerMetadata, brokerMap.get(brokerId)));
        }

        List<BrokerDO> deadBrokerList = brokerDao.getDead(clusterId);
        if (deadBrokerList == null) {
            return brokerOverviewDTOList;
        }
        for (BrokerDO brokerDO: deadBrokerList) {
            BrokerOverviewDTO brokerOverviewDTO = new BrokerOverviewDTO();
            brokerOverviewDTO.setBrokerId(brokerDO.getBrokerId());
            brokerOverviewDTO.setHost(brokerDO.getHost());
            brokerOverviewDTO.setPort(brokerDO.getPort());
            brokerOverviewDTO.setJmxPort(-1);
            brokerOverviewDTO.setStartTime(brokerDO.getGmtModify().getTime());
            brokerOverviewDTO.setStatus(DBStatusEnum.DELETED.getStatus());
            brokerOverviewDTOList.add(brokerOverviewDTO);
        }
        return brokerOverviewDTOList;
    }

    @Override
    public List<BrokerOverallDTO> getBrokerOverallList(Long clusterId, List<String> specifiedFieldList) {
        Map<Integer, BrokerMetrics> brokerMap = getSpecifiedBrokerMetrics(clusterId, specifiedFieldList, true);

        List<BrokerOverallDTO> brokerOverallDTOList = new ArrayList<>();
        for (Integer brokerId: ClusterMetadataManager.getBrokerIdList(clusterId)) {
            BrokerMetadata brokerMetadata = ClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (brokerMetadata == null) {
                continue;
            }
            brokerOverallDTOList.add(BrokerOverallDTO.newInstance(brokerMetadata, brokerMap.get(brokerId)));
        }
        return brokerOverallDTOList;
    }

    @Override
    public Map<Integer, BrokerMetrics> getSpecifiedBrokerMetrics(Long clusterId, List<String> specifiedFieldList, boolean simple) {
        Map<Integer, BrokerMetrics> brokerMap = new HashMap<>();
        for (Integer brokerId: ClusterMetadataManager.getBrokerIdList(clusterId)) {
            BrokerMetrics brokerMetrics = getSpecifiedBrokerMetrics(clusterId, brokerId, specifiedFieldList, simple);
            if (brokerMetrics == null) {
                continue;
            }
            brokerMap.put(brokerId, brokerMetrics);
        }
        return brokerMap;
    }

    @Override
    public BrokerMetrics getSpecifiedBrokerMetrics(Long clusterId, Integer brokerId, List<String> specifiedFieldList, Boolean simple) {
        if (clusterId == null || brokerId == null || specifiedFieldList == null) {
            return null;
        }
        BrokerMetrics brokerMetrics = jmxService.getSpecifiedBrokerMetricsFromJmx(clusterId, brokerId, specifiedFieldList, simple);
        if (brokerMetrics == null) {
            return brokerMetrics;
        }
        brokerMetrics.setClusterId(clusterId);
        brokerMetrics.setBrokerId(brokerId);
        return brokerMetrics;
    }

    @Override
    public List<BrokerMetrics> getBrokerMetricsByInterval(Long clusterId, Integer brokerId, Date startTime, Date endTime) {
        return brokerMetricDao.getBrokerMetricsByInterval(clusterId, brokerId, startTime, endTime);
    }

    @Override
    public BrokerBasicDTO getBrokerBasicDTO(Long clusterId, Integer brokerId, List<String> specifiedFieldList) {
        BrokerMetadata brokerMetadata = ClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
        if (brokerMetadata == null) {
            return null;
        }
        BrokerMetrics brokerMetrics = jmxService.getSpecifiedBrokerMetricsFromJmx(clusterId, brokerId, specifiedFieldList, true);
        BrokerBasicDTO brokerBasicDTO = new BrokerBasicDTO();
        brokerBasicDTO.setHost(brokerMetadata.getHost());
        brokerBasicDTO.setPort(brokerMetadata.getPort());
        brokerBasicDTO.setJmxPort(brokerMetadata.getJmxPort());
        brokerBasicDTO.setStartTime(brokerMetadata.getTimestamp());
        brokerBasicDTO.setPartitionCount(brokerMetrics.getPartitionCount());
        brokerBasicDTO.setLeaderCount(brokerMetrics.getLeaderCount());
        brokerBasicDTO.setTopicNum(ClusterMetadataManager.getTopicNameList(clusterId).size());
        return brokerBasicDTO;
    }
}
