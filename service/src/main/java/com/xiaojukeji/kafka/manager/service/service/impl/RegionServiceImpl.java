package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.dao.RegionDao;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author zengqiao
 * @date 2017/11/13.
 */
@Service("regionService")
public class RegionServiceImpl implements RegionService {
    private static final Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);

    @Autowired
    private RegionDao regionDao;

    @Override
    public Result createRegion(RegionDO regionDO) {
        if (regionDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param is null");
        }
        try {
            if (brokerAlreadyAssigned2Region(null, regionDO.getClusterId(), regionDO.getBrokerList())) {
                return new Result(StatusCode.PARAM_ERROR, "exist already used brokerId");
            }
            regionDao.insert(regionDO);
        } catch (Exception e) {
            logger.error("createRegion@RegionServiceImpl, create region failed, newRegion:{}.", regionDO, e);
            return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result();
    }

    @Override
    public Boolean deleteById(Long id) {
        return regionDao.deleteById(id) > 0;
    }

    @Override
    public Result updateRegion(RegionDO newRegionDO) {
        if (newRegionDO == null || newRegionDO.getId() == null) {
            return new Result(StatusCode.PARAM_ERROR, "param is null");
        }
        RegionDO oldRegionDO = regionDao.getById(newRegionDO.getId());
        if (oldRegionDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "region not exist");
        }
        if (brokerAlreadyAssigned2Region(newRegionDO.getId(), newRegionDO.getClusterId(), newRegionDO.getBrokerList())) {
            return new Result(StatusCode.PARAM_ERROR, "exist already used brokerId");
        }
        regionDao.updateById(newRegionDO);
        return new Result();
    }

    @Override
    public List<RegionDO> getByClusterId(Long clusterId) {
        return regionDao.getByClusterId(clusterId);
    }

    @Override
    public Map<Long, Long> getRegionNum() {
        List<RegionDO> regionDoList = null;
        try {
            regionDoList = regionDao.listAll();
        }catch (Exception e) {
            logger.error("getRegionNum@RegionServiceImpl, select mysql:region_info failed.", e);
        }
        if (regionDoList == null) {
            return new HashMap<>(0);
        }
        Map<Long, Long> regionNumMap = new HashMap<>();
        for (RegionDO regionDO: regionDoList) {
            Long regionNum = regionNumMap.getOrDefault(regionDO.getClusterId(), 0L);
            regionNumMap.put(regionDO.getClusterId(), regionNum + 1);
        }
        return regionNumMap;
    }

    private boolean brokerAlreadyAssigned2Region(Long regionId, Long clusterId, String newbrokerIdStr) {
        if (clusterId == null || StringUtils.isEmpty(newbrokerIdStr)) {
            return true;
        }
        List<RegionDO> regionDOList = getByClusterId(clusterId);
        if (regionDOList == null || regionDOList.isEmpty()) {
            return false;
        }
        List<Integer> newBrokerIdList = ListUtils.string2IntList(newbrokerIdStr);
        for (RegionDO regionDO : regionDOList) {
            if (!DBStatusEnum.NORMAL.getStatus().equals(regionDO.getStatus())) {
                continue;
            }
            if (regionDO.getId().equals(regionId)) {
                continue;
            }
            List<Integer> regionBrokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (regionBrokerIdList == null) {
                continue;
            }
            if (regionBrokerIdList.stream().filter(brokerId -> newBrokerIdList.contains(brokerId)).count() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<RegionDO> getRegionByTopicName(Long clusterId, String topicName) {
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return new ArrayList<>();
        }
        List<RegionDO> regionList = getByClusterId(clusterId);
        if (regionList == null) {
            return new ArrayList<>();
        }
        List<RegionDO> result = new ArrayList<>();
        for (RegionDO region: regionList) {
            List<Integer> brokerIdList = ListUtils.string2IntList(region.getBrokerList());
            if (brokerIdList == null) {
                continue;
            }
            for (Integer brokerId: brokerIdList) {
                if (topicMetadata.getBrokerIdSet().contains(brokerId)) {
                    result.add(region);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<Integer> getFullBrokerId(Long clusterId, List<Long> regionIdList, List<Integer> brokerIdList) {
        if (regionIdList == null || regionIdList.isEmpty()) {
            return new ArrayList<>(brokerIdList);
        }
        List<RegionDO> regionDOList = null;
        try {
            regionDOList = getByClusterId(clusterId);
        } catch (Exception e) {
            logger.error("getFullBrokerId@RegionServiceImpl, select mysql:region_info failed.", e);
        }
        if (regionDOList == null) {
            regionDOList = new ArrayList<>();
        }
        Set<Integer> brokerIdSet = new HashSet<>(brokerIdList == null? new ArrayList<>(): brokerIdList);
        for (RegionDO regionDO: regionDOList) {
            if (!regionIdList.contains(regionDO.getId())) {
                continue;
            }
            List<Integer> regionBrokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (regionBrokerIdList == null) {
                continue;
            }
            brokerIdSet.addAll(regionBrokerIdList);
        }
        return new ArrayList<>(brokerIdSet);
    }
}
