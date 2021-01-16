package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroupSummary;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupSummaryVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupVO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    public static List<ConsumerGroupVO> convert2ConsumerGroupVOList(List<ConsumerGroup> consumerGroupList) {
        if (ValidateUtils.isEmptyList(consumerGroupList)) {
            return Collections.emptyList();
        }
        List<ConsumerGroupVO> consumerGroupVOList = new ArrayList<>();
        for (ConsumerGroup consumerGroup : consumerGroupList) {
            ConsumerGroupVO vo = new ConsumerGroupVO();
            vo.setConsumerGroup(consumerGroup.getConsumerGroup());
            vo.setAppIds("");
            vo.setLocation(consumerGroup.getOffsetStoreLocation().location);
            consumerGroupVOList.add(vo);
        }
        return consumerGroupVOList;
    }

    public static List<ConsumerGroupSummaryVO> convert2ConsumerGroupSummaryVOList(List<ConsumerGroupSummary> summaryList) {
        if (ValidateUtils.isEmptyList(summaryList)) {
            return Collections.emptyList();
        }
        List<ConsumerGroupSummaryVO> voList = new ArrayList<>();
        for (ConsumerGroupSummary consumerGroupSummary : summaryList) {
            ConsumerGroupSummaryVO vo = new ConsumerGroupSummaryVO();
            vo.setConsumerGroup(consumerGroupSummary.getConsumerGroup());
            vo.setAppIds(ListUtils.strList2String(consumerGroupSummary.getAppIdList()));
            vo.setLocation(consumerGroupSummary.getOffsetStoreLocation().location);
            vo.setState(consumerGroupSummary.getState());
            voList.add(vo);
        }
        return voList;
    }
}