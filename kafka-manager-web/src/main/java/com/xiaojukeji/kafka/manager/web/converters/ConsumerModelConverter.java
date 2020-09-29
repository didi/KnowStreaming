package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroupDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupVO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public class ConsumerModelConverter {
    public static List<ConsumerGroupDetailVO> convert2ConsumerGroupDetailVO(String topicName,
                                                                            String consumeGroup,
                                                                            String location,
                                                                            List<ConsumeDetailDTO> consumeDetailDTOList) {
        if (consumeDetailDTOList == null || consumeDetailDTOList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ConsumerGroupDetailVO> consumerGroupDetailVOList = new ArrayList<>();
        for (ConsumeDetailDTO consumeDetailDTO : consumeDetailDTOList) {
            ConsumerGroupDetailVO consumerGroupDetailVO = new ConsumerGroupDetailVO();
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

    public static List<ConsumerGroupVO> convert2ConsumerGroupVOList(List<ConsumerGroupDTO> consumeGroupDTOList) {
        if (consumeGroupDTOList == null || consumeGroupDTOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ConsumerGroupVO> consumerGroupVOList = new ArrayList<>();
        for (ConsumerGroupDTO consumeGroupDTO : consumeGroupDTOList) {
            ConsumerGroupVO vo = new ConsumerGroupVO();
            vo.setConsumerGroup(consumeGroupDTO.getConsumerGroup());
            vo.setAppIds(ListUtils.strList2String(consumeGroupDTO.getAppIdList()));
            vo.setLocation(consumeGroupDTO.getOffsetStoreLocation().location);
            consumerGroupVOList.add(vo);
        }
        return consumerGroupVOList;
    }
}