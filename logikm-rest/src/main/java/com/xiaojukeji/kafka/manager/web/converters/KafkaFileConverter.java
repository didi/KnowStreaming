package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.vo.rd.KafkaFileVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaFileDO;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/8
 */
public class KafkaFileConverter {

    public static List<KafkaFileVO> convertKafkaFileVOList(List<KafkaFileDO> kafkaFileDOList, ClusterService clusterService) {
        List<KafkaFileVO> kafkaFileVOList = new ArrayList<>();
        if (ValidateUtils.isEmptyList(kafkaFileDOList)) {
            return kafkaFileVOList;
        }
        for (KafkaFileDO kafkaFileDO : kafkaFileDOList) {
            KafkaFileVO kafkaFileVO = new KafkaFileVO();
            CopyUtils.copyProperties(kafkaFileVO, kafkaFileDO);
            ClusterDO clusterDO = clusterService.getById(kafkaFileDO.getClusterId());
            if (ValidateUtils.isNull(clusterDO)) {
                kafkaFileVO.setClusterName("*");
            } else {
                kafkaFileVO.setClusterName(clusterDO.getClusterName());
            }
            kafkaFileVOList.add(kafkaFileVO);
        }
        return kafkaFileVOList;
    }
}
