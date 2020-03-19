package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.web.vo.consumer.ConsumerGroupDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerGroupDTO;
import com.xiaojukeji.kafka.manager.web.vo.consumer.ConsumerGroupVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public class ConsumerModelConverter {
    public static List<ConsumerGroupVO> convert2ConsumerGroupVOList(List<ConsumerGroupDTO> consumeGroupDTOList) {
        if (consumeGroupDTOList == null || consumeGroupDTOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ConsumerGroupVO> consumerGroupVOList = new ArrayList<>();
        for (ConsumerGroupDTO consumeGroupDTO : consumeGroupDTOList) {
            consumerGroupVOList.add(
                    new ConsumerGroupVO(consumeGroupDTO.getConsumerGroup(), consumeGroupDTO.getOffsetStoreLocation().name())
            );
        }
        return consumerGroupVOList;
    }

    public static List<ConsumerGroupDetailVO> convert2ConsumerGroupDetailVO(Long clusterId, String topicName,
                                                                            String consumeGroup, String location,
                                                                            List<ConsumeDetailDTO> consumeDetailDTOList) {
        if (consumeDetailDTOList == null || consumeDetailDTOList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ConsumerGroupDetailVO> consumerGroupDetailVOList = new ArrayList<>();
        for (ConsumeDetailDTO consumeDetailDTO : consumeDetailDTOList) {
            ConsumerGroupDetailVO consumerGroupDetailVO = new ConsumerGroupDetailVO();
            consumerGroupDetailVO.setClusterId(clusterId);
            consumerGroupDetailVO.setTopicName(topicName);
            consumerGroupDetailVO.setConsumerGroup(consumeGroup);
            consumerGroupDetailVO.setLocation(location);

            consumerGroupDetailVO.setPartitionId(consumeDetailDTO.getPartitionId());
            consumerGroupDetailVO.setClientId(consumeDetailDTO.getConsumerId());
            consumerGroupDetailVO.setConsumeOffset(consumeDetailDTO.getConsumeOffset());
            consumerGroupDetailVO.setPartitionOffset(consumeDetailDTO.getOffset());
            if (consumeDetailDTO.getOffset() != null && consumeDetailDTO.getConsumeOffset() != null) {
                consumerGroupDetailVO.setLag(consumeDetailDTO.getOffset() - consumeDetailDTO.getConsumeOffset());
            }
            consumerGroupDetailVOList.add(consumerGroupDetailVO);
        }
        return consumerGroupDetailVOList;
    }
}