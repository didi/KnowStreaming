package com.xiaojukeji.kafka.manager.web.converters;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.web.vo.MigrationDetailVO;
import com.xiaojukeji.kafka.manager.web.vo.MigrationTaskVO;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.MigrationTaskDO;
import com.xiaojukeji.kafka.manager.web.vo.PartitionReassignmentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 19/4/16
 */
public class AdminMigrationConverter {
    private static final Logger logger = LoggerFactory.getLogger(AdminMigrationConverter.class);

    public static MigrationDetailVO convert2MigrationDetailVO(MigrationTaskDO migrationTaskDO,
                                                              Map<Integer, Integer> migrationStatusMap) {
        MigrationDetailVO migrationDetailVO = new MigrationDetailVO();
        migrationDetailVO.setTaskId(migrationTaskDO.getId());
        migrationDetailVO.setClusterId(migrationTaskDO.getClusterId());
        migrationDetailVO.setTopicName(migrationTaskDO.getTopicName());
        migrationDetailVO.setThrottle(migrationTaskDO.getThrottle());
        migrationDetailVO.setStatus(migrationTaskDO.getStatus());
        migrationDetailVO.setGmtCreate(migrationTaskDO.getGmtCreate());
        migrationDetailVO.setMigrationStatus(migrationStatusMap);

        Map<Integer, List<Integer>> reassignmentMap = new HashMap<>(2);
        try {
            JSONObject js = JSON.parseObject(migrationTaskDO.getReassignmentJson());
            List<PartitionReassignmentVO> partitionReassignmentVOList = JSONObject.parseArray(JSON.toJSONString(js.getJSONArray("partitions")), PartitionReassignmentVO.class);
            for (PartitionReassignmentVO partitionReassignmentVO: partitionReassignmentVOList) {
                reassignmentMap.put(partitionReassignmentVO.getPartition(), partitionReassignmentVO.getReplicas());
            }
        } catch (Exception e) {
            logger.error("convert2MigrationDetailVO@AdminMigrationConverter, migrationTaskDO:{}.", migrationTaskDO, e);
        }
        migrationDetailVO.setReassignmentMap(reassignmentMap);
        return migrationDetailVO;
    }

    public static List<MigrationTaskVO> convert2MigrationTaskVOList(List<MigrationTaskDO> migrationTaskDOList,
                                                                    List<ClusterDO> clusterDOList) {
        if (migrationTaskDOList == null) {
            return new ArrayList<>();
        }
        Map<Long, String> clusterMap = new HashMap<>();
        if (clusterDOList == null) {
            clusterDOList = new ArrayList<>();
        }
        for (ClusterDO clusterDO: clusterDOList) {
            clusterMap.put(clusterDO.getId(), clusterDO.getClusterName());
        }
        List<MigrationTaskVO> migrationTaskVOList = new ArrayList<>();
        for (MigrationTaskDO migrationTaskDO: migrationTaskDOList) {
            MigrationTaskVO migrationTaskVO = new MigrationTaskVO();
            migrationTaskVO.setTaskId(migrationTaskDO.getId());
            migrationTaskVO.setClusterId(migrationTaskDO.getClusterId());
            migrationTaskVO.setClusterName(clusterMap.getOrDefault(migrationTaskDO.getClusterId(), ""));
            migrationTaskVO.setTopicName(migrationTaskDO.getTopicName());
            migrationTaskVO.setStatus(migrationTaskDO.getStatus());
            migrationTaskVO.setThrottle(migrationTaskDO.getThrottle());
            migrationTaskVO.setGmtCreate(migrationTaskDO.getGmtCreate().getTime());
            migrationTaskVO.setOperator(migrationTaskDO.getOperator());
            migrationTaskVOList.add(migrationTaskVO);
        }
        return migrationTaskVOList;
    }
}