package com.xiaojukeji.know.streaming.km.core.service.ha.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaojukeji.know.streaming.km.common.bean.entity.ha.HaActiveStandbyRelation;
import com.xiaojukeji.know.streaming.km.common.bean.po.ha.HaActiveStandbyRelationPO;
import com.xiaojukeji.know.streaming.km.common.enums.ha.HaResTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.ha.HaActiveStandbyRelationService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.ha.HaActiveStandbyRelationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.ha.HaResTypeEnum.MIRROR_TOPIC;

@Service
public class HaActiveStandbyRelationServiceImpl implements HaActiveStandbyRelationService {
    @Autowired
    private HaActiveStandbyRelationDAO haActiveStandbyRelationDAO;

    @Override
    public void batchReplaceTopicHA(Long activeClusterPhyId, Long standbyClusterPhyId, List<String> topicNameList) {
        Map<String, HaActiveStandbyRelationPO> poMap = this.listPOs(activeClusterPhyId, standbyClusterPhyId, MIRROR_TOPIC)
                .stream()
                .collect(Collectors.toMap(HaActiveStandbyRelationPO::getResName, Function.identity()));
        for (String topicName: topicNameList) {
            HaActiveStandbyRelationPO oldPO = poMap.get(topicName);
            if (oldPO != null) {
                continue;
            }

            try {
                haActiveStandbyRelationDAO.insert(new HaActiveStandbyRelationPO(activeClusterPhyId, standbyClusterPhyId, topicName, MIRROR_TOPIC.getCode()));
            } catch (DuplicateKeyException dke) {
                // ignore
            }
        }
    }

    @Override
    public void batchDeleteTopicHA(Long activeClusterPhyId, Long standbyClusterPhyId, List<String> topicNameList) {
        Map<String, HaActiveStandbyRelationPO> poMap = this.listPOs(activeClusterPhyId, standbyClusterPhyId, MIRROR_TOPIC)
                .stream()
                .collect(Collectors.toMap(HaActiveStandbyRelationPO::getResName, Function.identity()));
        for (String topicName: topicNameList) {
            HaActiveStandbyRelationPO oldPO = poMap.get(topicName);
            if (oldPO == null) {
                continue;
            }

            haActiveStandbyRelationDAO.deleteById(oldPO.getId());
        }
    }

    @Override
    public List<HaActiveStandbyRelation> listByClusterAndType(Long firstClusterId, HaResTypeEnum haResTypeEnum) {
        // 查询HA列表
        List<HaActiveStandbyRelationPO> poList = this.listPOs(firstClusterId, haResTypeEnum);
        if (ValidateUtils.isNull(poList)) {
            return new ArrayList<>();
        }

        return ConvertUtil.list2List(poList, HaActiveStandbyRelation.class);
    }

    @Override
    public List<HaActiveStandbyRelation> listAllTopicHa() {
        LambdaQueryWrapper<HaActiveStandbyRelationPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaActiveStandbyRelationPO::getResType, MIRROR_TOPIC.getCode());
        List<HaActiveStandbyRelationPO> poList = haActiveStandbyRelationDAO.selectList(lambdaQueryWrapper);
        if (ValidateUtils.isNull(poList)) {
            return new ArrayList<>();
        }

        return ConvertUtil.list2List(poList, HaActiveStandbyRelation.class);
    }

    private List<HaActiveStandbyRelationPO> listPOs(Long firstClusterId, HaResTypeEnum haResTypeEnum) {
        LambdaQueryWrapper<HaActiveStandbyRelationPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaActiveStandbyRelationPO::getResType, haResTypeEnum.getCode());
        lambdaQueryWrapper.and(lambda ->
                lambda.eq(HaActiveStandbyRelationPO::getActiveClusterPhyId, firstClusterId).or().eq(HaActiveStandbyRelationPO::getStandbyClusterPhyId, firstClusterId)
        );

        // 查询HA列表
        return haActiveStandbyRelationDAO.selectList(lambdaQueryWrapper);
    }

    private List<HaActiveStandbyRelationPO> listPOs(Long activeClusterPhyId, Long standbyClusterPhyId, HaResTypeEnum haResTypeEnum) {
        LambdaQueryWrapper<HaActiveStandbyRelationPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaActiveStandbyRelationPO::getResType, haResTypeEnum.getCode());
        lambdaQueryWrapper.eq(HaActiveStandbyRelationPO::getActiveClusterPhyId, activeClusterPhyId);
        lambdaQueryWrapper.eq(HaActiveStandbyRelationPO::getStandbyClusterPhyId, standbyClusterPhyId);


        // 查询HA列表
        return haActiveStandbyRelationDAO.selectList(lambdaQueryWrapper);
    }
}
