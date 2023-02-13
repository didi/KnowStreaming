package com.xiaojukeji.kafka.manager.service.biz.ha.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaRelationTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.HaClusterTopicVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.HaClusterTopicHaStatusVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaASRelationManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HaASRelationManagerImpl implements HaASRelationManager {
    @Autowired
    private HaASRelationService haASRelationService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private HaTopicService haTopicService;

    @Autowired
    private AuthorityService authorityService;

    @Override
    public List<HaClusterTopicVO> getHATopics(Long firstClusterPhyId, Long secondClusterPhyId, boolean filterSystemTopics) {
        List<HaASRelationDO> doList = haASRelationService.listAllHAFromDB(firstClusterPhyId, secondClusterPhyId, HaResTypeEnum.TOPIC);
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }

        List<HaClusterTopicVO> voList = new ArrayList<>();
        for (HaASRelationDO relationDO: doList) {
            if (filterSystemTopics
                    && (relationDO.getActiveResName().startsWith("__") || relationDO.getStandbyResName().startsWith("__"))) {
                // 过滤掉系统Topic && 存在系统Topic，则过滤掉
                continue;
            }

            HaClusterTopicVO vo = new HaClusterTopicVO();
            vo.setClusterId(firstClusterPhyId);
            if (firstClusterPhyId.equals(relationDO.getActiveClusterPhyId())) {
                vo.setTopicName(relationDO.getActiveResName());
            } else {
                vo.setTopicName(relationDO.getStandbyResName());
            }

            vo.setProduceAclNum(0);
            vo.setConsumeAclNum(0);
            vo.setActiveClusterId(relationDO.getActiveClusterPhyId());
            vo.setStandbyClusterId(relationDO.getStandbyClusterPhyId());
            vo.setStatus(relationDO.getStatus());

            // 补充ACL信息
            List<AuthorityDO> authorityDOList = authorityService.getAuthorityByTopicFromCache(relationDO.getActiveClusterPhyId(), relationDO.getActiveResName());
            authorityDOList.forEach(elem -> {
                if ((elem.getAccess() & TopicAuthorityEnum.WRITE.getCode()) > 0) {
                    vo.setProduceAclNum(vo.getProduceAclNum() + 1);
                }
                if ((elem.getAccess() & TopicAuthorityEnum.READ.getCode()) > 0) {
                    vo.setConsumeAclNum(vo.getConsumeAclNum() + 1);
                }
            });

            voList.add(vo);
        }

        return voList;
    }

    @Override
    public Result<List<HaClusterTopicHaStatusVO>> listHaStatusTopics(Long clusterPhyId, Boolean checkMetadata) {
        ClusterDO clusterDO = PhysicalClusterMetadataManager.getClusterFromCache(clusterPhyId);
        if (clusterDO == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        List<TopicDO> topicDOS = topicManagerService.getByClusterId(clusterPhyId);
        if (ValidateUtils.isEmptyList(topicDOS)) {
            return Result.buildSuc(new ArrayList<>());
        }

        Map<String, Integer> haRelationMap = haTopicService.getRelation(clusterPhyId);
        List<HaClusterTopicHaStatusVO> statusVOS = new ArrayList<>();
        topicDOS.stream().filter(topicDO -> !topicDO.getTopicName().startsWith("__"))//过滤引擎自带topic
                .forEach(topicDO -> {
                    if(checkMetadata && !PhysicalClusterMetadataManager.isTopicExist(clusterPhyId, topicDO.getTopicName())){
                        return;
                    }
                    HaClusterTopicHaStatusVO statusVO = new HaClusterTopicHaStatusVO();
                    statusVO.setClusterId(clusterPhyId);
                    statusVO.setClusterName(clusterDO.getClusterName());
                    statusVO.setTopicName(topicDO.getTopicName());
                    statusVO.setHaRelation(haRelationMap.get(topicDO.getTopicName()));
                    statusVOS.add(statusVO);
        });

        return Result.buildSuc(statusVOS);
    }

    @Override
    public Integer getRelation(Long clusterId, String topicName) {
        HaASRelationDO relationDO = haASRelationService.getHAFromDB(clusterId, topicName, HaResTypeEnum.TOPIC);
        if (relationDO == null){
            return HaRelationTypeEnum.UNKNOWN.getCode();
        }
        if (topicName.equals(KafkaConstant.COORDINATOR_TOPIC_NAME)){
            return HaRelationTypeEnum.MUTUAL_BACKUP.getCode();
        }
        if (clusterId.equals(relationDO.getActiveClusterPhyId())){
            return HaRelationTypeEnum.ACTIVE.getCode();
        }
        if (clusterId.equals(relationDO.getStandbyClusterPhyId())){
            return HaRelationTypeEnum.STANDBY.getCode();
        }
        return HaRelationTypeEnum.UNKNOWN.getCode();
    }

    @Override
    public HaASRelationDO getASRelation(Long clusterId, String topicName) {
        return haASRelationService.getHAFromDB(clusterId, topicName, HaResTypeEnum.TOPIC);
    }
}
