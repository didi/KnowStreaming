package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengqiao
 * @date 19/4/21
 */
public interface RegionService {
    List<RegionDO> listAll();

    /**
     * 创建Region
     */
    ResultStatus createRegion(RegionDO regionDO);

    /**
     * 通过id查找
     */
    RegionDO getById(Long id);

    /**
     * 删除Region
     */
    ResultStatus deleteById(Long regionId);

    /**
     * 修改Region信息
     */
    ResultStatus updateRegion(RegionDO regionDO);

    int updateCapacityById(RegionDO regionDO);

    /**
     * 查询Region详情
     */
    List<RegionDO> getByClusterId(Long clusterId);

    /**
     * 获取集群有几个Region
     */
    Map<Long, Integer> getRegionNum();

    /**
     * 合并regionId和brokerIdList中的brokerId
     */
    List<Integer> getFullBrokerIdList(Long clusterId, Long regionId, List<Integer> brokerIdList);

    Map<Integer, RegionDO> convert2BrokerIdRegionMap(List<RegionDO> regionDOList);

    /**
     * 更新逻辑集群容量
     * @param clusterId 集群id
     * @param newBrokerList 新的broker列表
     * @return ResultStatus
     */
    ResultStatus updateRegion(Long clusterId, String newBrokerList);

    /**
     * 获取空闲的region的broker列表
     */
    List<Integer> getIdleRegionBrokerList(Long physicalClusterId, List<Long> regionIdList);

    Map<String, Set<Integer>> getTopicNameRegionBrokerIdMap(Long clusterId);

    /**
     * 获取topic所在的region
     */
    List<RegionDO> getRegionListByTopicName(Long clusterId, String topicName);
}
