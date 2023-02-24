package com.xiaojukeji.know.streaming.km.ha.mirror.service;

import com.xiaojukeji.know.streaming.km.common.bean.dto.ha.mirror.MirrorTopicCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.ha.mirror.MirrorTopicDeleteDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.ha.mirror.TopicMirrorInfoVO;

import java.util.List;

public interface MirrorTopicService {

    /**
     * @param dtoList
     * @return
     */
    Result<Void> batchCreateMirrorTopic(List<MirrorTopicCreateDTO> dtoList);

    /**
     * @param dtoList
     * @return
     */
    Result<Void> batchDeleteMirrorTopic(List<MirrorTopicDeleteDTO> dtoList);

    /**
     * @param clusterPhyId
     * @param topicName
     * @return
     */
    Result<List<TopicMirrorInfoVO>> getTopicsMirrorInfo(Long clusterPhyId, String topicName);
}
