package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.RegionDao;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 2017/11/13.
 */
@Service("regionService")
public class RegionServiceImpl implements RegionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionServiceImpl.class);

    @Autowired
    private RegionDao regionDao;

    @Override
    public List<RegionDO> listAll() {
        List<RegionDO> doList = null;
        try {
            doList = regionDao.listAll();
        }catch (Exception e) {
            LOGGER.error("list all region failed.", e);
        }
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }
        return doList;
    }

    @Override
    public ResultStatus createRegion(RegionDO regionDO) {
        if (ValidateUtils.isNull(regionDO)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            List<Integer> newBrokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (existBrokerIdAlreadyInRegion(
                    regionDO.getClusterId(),
                    newBrokerIdList,
                    null)) {
                return ResultStatus.RESOURCE_ALREADY_USED;
            }
            if (PhysicalClusterMetadataManager.getNotAliveBrokerNum(regionDO.getClusterId(), newBrokerIdList) > 0) {
                return ResultStatus.BROKER_NOT_EXIST;
            }
            if (regionDao.insert(regionDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (DuplicateKeyException e) {
            LOGGER.error("create region failed, name already existed, newRegionDO:{}.", regionDO, e);
            return ResultStatus.RESOURCE_ALREADY_EXISTED;
        } catch (Exception e) {
            LOGGER.error("create region failed, newRegionDO:{}.", regionDO, e);
            return ResultStatus.MYSQL_ERROR;
        }

        LOGGER.warn("class=RegionServiceImpl||method=createRegion||regionDO={}||msg=create region failed", regionDO);
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public RegionDO getById(Long id) {
        return regionDao.getById(id);
    }

    @Override
    public ResultStatus deleteById(Long id) {
        if (ValidateUtils.isNull(id)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            if (regionDao.deleteById(id) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("delete region failed, regionId:{}.", id, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.RESOURCE_NOT_EXIST;
    }

    @Override
    public ResultStatus updateRegion(RegionDO newRegionDO) {
        if (ValidateUtils.isNull(newRegionDO) || ValidateUtils.isNull(newRegionDO.getId())) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            RegionDO oldRegionDO = regionDao.getById(newRegionDO.getId());
            if (ValidateUtils.isNull(oldRegionDO)) {
                return ResultStatus.RESOURCE_NOT_EXIST;
            }
            if (oldRegionDO.getBrokerList().equals(newRegionDO.getBrokerList())) {
                // 没有改变broker列表，直接更新
                if (regionDao.updateById(newRegionDO) > 0) {
                    return ResultStatus.SUCCESS;
                }
                LOGGER.warn("class=RegionServiceImpl||method=updateRegion||newRegionDO={}||msg=update region failed", newRegionDO);
                return ResultStatus.MYSQL_ERROR;
            }
            List<Integer> newBrokerIdList = ListUtils.string2IntList(newRegionDO.getBrokerList());
            if (existBrokerIdAlreadyInRegion(
                    newRegionDO.getClusterId(),
                    newBrokerIdList,
                    newRegionDO.getId())) {
                return ResultStatus.RESOURCE_ALREADY_USED;
            }
            if (PhysicalClusterMetadataManager.getNotAliveBrokerNum(newRegionDO.getClusterId(), newBrokerIdList) > 0) {
                return ResultStatus.BROKER_NOT_EXIST;
            }
            if (regionDao.updateById(newRegionDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("update region failed, newRegionDO:{}", newRegionDO, e);
        }
        LOGGER.warn("class=RegionServiceImpl||method=updateRegion||newRegionDO={}||msg=update region failed", newRegionDO);
        return ResultStatus.MYSQL_ERROR;
    }


    @Override
    public ResultStatus updateRegion(Long clusterId, String newBrokerList) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isExistBlank(newBrokerList)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        RegionDO regionDO = getById(clusterId);
        if (ValidateUtils.isNull(regionDO)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        List<Integer> newBrokerIdList = ListUtils.string2IntList(newBrokerList);
        if (ValidateUtils.isEmptyList(newBrokerIdList)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        regionDO.setBrokerList(newBrokerList);
        return updateRegion(regionDO);
    }

    @Override
    public int updateCapacityById(RegionDO regionDO) {
        return regionDao.updateCapacityById(regionDO);
    }

    @Override
    public List<RegionDO> getByClusterId(Long clusterId) {
        return regionDao.getByClusterId(clusterId);
    }

    @Override
    public Map<Long, Integer> getRegionNum() {
        List<RegionDO> regionDoList = null;
        try {
            regionDoList = regionDao.listAll();
        }catch (Exception e) {
            LOGGER.error("get region number failed.", e);
        }
        if (regionDoList == null) {
            return new HashMap<>(0);
        }
        Map<Long, Integer> regionNumMap = new HashMap<>();
        for (RegionDO regionDO: regionDoList) {
            Integer regionNum = regionNumMap.getOrDefault(regionDO.getClusterId(), 0);
            regionNumMap.put(regionDO.getClusterId(), regionNum + 1);
        }
        return regionNumMap;
    }

    @Override
    public List<Integer> getFullBrokerIdList(Long clusterId, Long regionId, List<Integer> brokerIdList) {
        if (ValidateUtils.isNull(regionId)) {
            return ValidateUtils.isNull(brokerIdList)? new ArrayList<>(): brokerIdList;
        }

        RegionDO regionDO = regionDao.getById(regionId);
        if (ValidateUtils.isNull(regionDO)) {
            return ValidateUtils.isNull(brokerIdList)? new ArrayList<>(): brokerIdList;
        }
        List<Integer> regionBrokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
        if (ValidateUtils.isNull(regionBrokerIdList)) {
            return ValidateUtils.isNull(brokerIdList)? new ArrayList<>(): brokerIdList;
        }

        Set<Integer> fullBrokerIdSet = new HashSet<>(regionBrokerIdList);
        if (ValidateUtils.isNull(brokerIdList)) {
            return new ArrayList<>(fullBrokerIdSet);
        }
        fullBrokerIdSet.addAll(brokerIdList);
        return new ArrayList<>(fullBrokerIdSet);
    }

    @Override
    public Map<Integer, RegionDO> convert2BrokerIdRegionMap(List<RegionDO> regionDOList) {
        if (regionDOList == null || regionDOList.isEmpty()) {
            return new HashMap<>();
        }
        Map<Integer, RegionDO> brokerIdRegionMap = new HashMap<>();
        for (RegionDO regionDO: regionDOList) {
            List<Integer> brokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (brokerIdList == null) {
                continue;
            }
            for (Integer brokerId: brokerIdList) {
                brokerIdRegionMap.put(brokerId, regionDO);
            }
        }
        return brokerIdRegionMap;
    }

    private boolean existBrokerIdAlreadyInRegion(Long clusterId, List<Integer> newBrokerIdList, Long regionId) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isEmptyList(newBrokerIdList)) {
            return true;
        }

        List<RegionDO> doList = getByClusterId(clusterId);
        if (ValidateUtils.isEmptyList(doList)) {
            return false;
        }

        for (RegionDO regionDO : doList) {
            if (regionDO.getId().equals(regionId)) {
                continue;
            }
            List<Integer> regionBrokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (ValidateUtils.isEmptyList(regionBrokerIdList)) {
                continue;
            }
            if (regionBrokerIdList.stream().filter(brokerId -> newBrokerIdList.contains(brokerId)).count() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Integer> getIdleRegionBrokerList(Long physicalClusterId, List<Long> regionIdList) {
        if (ValidateUtils.isNull(physicalClusterId) || ValidateUtils.isEmptyList(regionIdList)) {
            return null;
        }

        List<RegionDO> regionDOList = getByClusterId(physicalClusterId);
        if (ValidateUtils.isEmptyList(regionDOList)) {
            return null;
        }

        RegionDO resultRegion = null;
        for (RegionDO elem: regionDOList) {
            if (!regionIdList.contains(elem.getId())
                    || elem.getStatus().equals(1)) {
                continue;
            }
            if (resultRegion == null) {
                resultRegion = elem;
            }

            Long left = elem.getCapacity() - elem.getEstimateUsed();
            if (left < 0 || left < (resultRegion.getCapacity() - resultRegion.getEstimateUsed())) {
                continue;
            }
            resultRegion = elem;
        }
        if (ValidateUtils.isNull(resultRegion)) {
            return null;
        }
        return ListUtils.string2IntList(resultRegion.getBrokerList());
    }

    @Override
    public Map<String, Set<Integer>> getTopicNameRegionBrokerIdMap(Long clusterId) {
        Map<Integer, List<Integer>> brokerIdRegionBrokerIdMap = new HashMap<>();
        for (RegionDO regionDO: this.getByClusterId(clusterId)) {
            List<Integer> brokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (ValidateUtils.isEmptyList(brokerIdList)) {
                continue;
            }
            for (Integer brokerId: brokerIdList) {
                brokerIdRegionBrokerIdMap.put(brokerId, brokerIdList);
            }
        }

        Map<String, Set<Integer>> topicNameRegionBrokerIdMap = new HashMap<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }
            for (Integer brokerId: topicMetadata.getBrokerIdSet()) {
                Set<Integer> brokerIdSet = topicNameRegionBrokerIdMap.getOrDefault(topicName, new HashSet<>());
                if (brokerIdRegionBrokerIdMap.containsKey(brokerId)) {
                    // Broker属于某个Region
                    brokerIdSet.addAll(brokerIdRegionBrokerIdMap.get(brokerId));
                } else {
                    // Broker不属于任何Region
                    brokerIdSet.add(brokerId);
                }
                topicNameRegionBrokerIdMap.put(topicName, brokerIdSet);
            }
        }
        return topicNameRegionBrokerIdMap;
    }

    @Override
    public List<RegionDO> getRegionListByTopicName(Long clusterId, String topicName) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (ValidateUtils.isNull(topicMetadata)) {
            return Collections.emptyList();
        }
        Set<Integer> brokerIdSet = topicMetadata.getBrokerIdSet();
        List<RegionDO> regionDOList = regionDao.getByClusterId(clusterId);
        return regionDOList.stream()
                .filter(regionDO -> {
                    List<Integer> brokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
                    brokerIdList.retainAll(brokerIdSet);
                    if (ValidateUtils.isEmptyList(brokerIdList)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
