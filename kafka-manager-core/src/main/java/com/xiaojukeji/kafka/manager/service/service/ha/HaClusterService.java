package com.xiaojukeji.kafka.manager.service.service.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.HaClusterVO;

import java.util.List;
import java.util.Map;

/**
 * 集群主备关系
 */
public interface HaClusterService {
    /**
     * 创建主备关系
     */
    Result<Void> createHA(Long activeClusterPhyId, Long standbyClusterPhyId, String operator);
    Result<Void> createHAInKafka(String zookeeper, ClusterDO needWriteToZKClusterDO, String operator);

    /**
     * 切换主备关系
     */
    Result<Void> switchHA(Long newActiveClusterPhyId, Long newStandbyClusterPhyId);

    /**
     * 删除主备关系
     */
    Result<Void> deleteHA(Long activeClusterPhyId, Long standbyClusterPhyId);

    /**
     * 获取主备关系
     */
    HaASRelationDO getHA(Long activeClusterPhyId);

    /**
     * 获取集群主备关系
     */
    Map<Long, Integer> getClusterHARelation();

    /**
     * 获取主备关系
     */
    Result<List<HaClusterVO>> listAllHA();
}
