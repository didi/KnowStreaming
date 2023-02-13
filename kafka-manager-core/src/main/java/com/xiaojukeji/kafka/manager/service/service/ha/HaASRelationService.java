package com.xiaojukeji.kafka.manager.service.service.ha;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;

import java.util.List;

public interface HaASRelationService {
    Result<Void> replaceTopicRelationsToDB(Long standbyClusterPhyId, List<HaASRelationDO> topicRelationDOList);

    Result<Void> addHAToDB(HaASRelationDO haASRelationDO);

    Result<Void> deleteById(Long id);

    int updateRelationStatus(Long relationId, Integer newStatus);
    int updateById(HaASRelationDO haASRelationDO);

    /**
     * 获取主集群关系
     */
    HaASRelationDO getActiveClusterHAFromDB(Long activeClusterPhyId);

    /**
     * 获取主备关系
     */
    HaASRelationDO getSpecifiedHAFromDB(Long activeClusterPhyId,
                                        String activeResName,
                                        Long standbyClusterPhyId,
                                        String standbyResName,
                                        HaResTypeEnum resTypeEnum);

    /**
     * 获取主备关系
     */
    HaASRelationDO getHAFromDB(Long firstClusterPhyId,
                               String firstResName,
                               HaResTypeEnum resTypeEnum);

    /**
     * 获取备集群主备关系
     */
    List<HaASRelationDO> getStandbyHAFromDB(Long standbyClusterPhyId, HaResTypeEnum resTypeEnum);
    List<HaASRelationDO> getActiveHAFromDB(Long activeClusterPhyId, HaResTypeEnum resTypeEnum);

    /**
     * 获取主备关系
     */
    List<HaASRelationDO> listAllHAFromDB(HaResTypeEnum resTypeEnum);

    /**
     * 获取主备关系
     */
    List<HaASRelationDO> listAllHAFromDB(Long firstClusterPhyId, HaResTypeEnum resTypeEnum);

    /**
     * 获取主备关系
     */
    List<HaASRelationDO> listAllHAFromDB(Long firstClusterPhyId, Long secondClusterPhyId, HaResTypeEnum resTypeEnum);

}
