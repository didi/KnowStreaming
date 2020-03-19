package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.RegionDO;

import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 19/4/21
 */
public interface RegionService {
    /**
     * 创建Region
     */
    Result createRegion(RegionDO regionDO);

    /**
     * 删除Region
     */
    Boolean deleteById(Long regionId);

    /**
     * 修改Region信息
     */
    Result updateRegion(RegionDO regionDO);

    /**
     * 查询Region详情
     */
    List<RegionDO> getByClusterId(Long clusterId);

    /**
     * 获取集群有几个Region
     */
    Map<Long, Long> getRegionNum();

    /**
     * 获取Topic所属Region
     */
    List<RegionDO> getRegionByTopicName(Long clusterId, String topicName);

    /**
     * 合并regionIdList和brokerIdList中的brokerId
     */
    List<Integer> getFullBrokerId(Long clusterId, List<Long> regionIdList, List<Integer> brokerIdList);
}
