package com.xiaojukeji.kafka.manager.common.entity.pojo;

import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/24
 */
@Data
@NoArgsConstructor
public class TopicDO {
    private Long id;

    private Date gmtCreate;

    private Date gmtModify;

    private String appId;

    private Long clusterId;

    private String topicName;

    private String description;

    private Long peakBytesIn;

    public TopicDO(String appId, Long clusterId, String topicName, String description, Long peakBytesIn) {
        this.appId = appId;
        this.clusterId = clusterId;
        this.topicName = topicName;
        this.description = description;
        this.peakBytesIn = peakBytesIn;
    }

    public static TopicDO buildFrom(TopicCreationDTO dto) {
        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(dto.getAppId());
        topicDO.setClusterId(dto.getClusterId());
        topicDO.setTopicName(dto.getTopicName());
        topicDO.setDescription(dto.getDescription());
        topicDO.setPeakBytesIn(ValidateUtils.isNull(dto.getPeakBytesIn())  ? -1L : dto.getPeakBytesIn());
        return topicDO;
    }
}
