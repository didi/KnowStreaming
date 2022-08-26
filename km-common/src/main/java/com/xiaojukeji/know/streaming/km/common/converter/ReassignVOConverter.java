package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignPlan;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan.ReassignPartitionPlanVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan.ReassignTopicPlanVO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReassignVOConverter {
    private ReassignVOConverter() {
    }

    public static ReassignTopicPlanVO convert2ReassignTopicPlanVO(ReassignPlan reassignmentPlan) {
        Set<Integer> currentBrokerIdSet = new HashSet<>();
        Set<Integer> reassignBrokerIdSet = new HashSet<>();
        List<ReassignPartitionPlanVO> partitionPlanList = new ArrayList<>();

        reassignmentPlan.getReAssignPlanMap().entrySet().stream().forEach(entry -> {
            reassignBrokerIdSet.addAll(entry.getValue());

            List<Integer> currentBrokerIdList = reassignmentPlan.getCurrentAssignMap().get(entry.getKey());
            currentBrokerIdSet.addAll(currentBrokerIdList);

            // 构造分区迁移plan
            ReassignPartitionPlanVO vo = new ReassignPartitionPlanVO();
            vo.setClusterId(reassignmentPlan.getClusterPhyId());
            vo.setTopicName(entry.getKey().topic());
            vo.setPartitionId(entry.getKey().partition());
            vo.setCurrentBrokerIdList(currentBrokerIdList);
            vo.setReassignBrokerIdList(entry.getValue());
            partitionPlanList.add(vo);
        });

        // 返回结果
        ReassignTopicPlanVO vo = new ReassignTopicPlanVO();

        vo.setClusterId(reassignmentPlan.getClusterPhyId());
        vo.setTopicName(reassignmentPlan.getTopicName());
        vo.setCurrentBrokerIdList(new ArrayList<>(currentBrokerIdSet));
        vo.setReassignBrokerIdList(new ArrayList<>(reassignBrokerIdSet));
        vo.setPartitionPlanList(partitionPlanList);

        return vo;
    }
}
