package com.xiaojukeji.kafka.manager.common.entity.pojo;

import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/24
 */
public class TopicDO {
    private Long id;

    private Date gmtCreate;

    private Date gmtModify;

    private String appId;

    private Long clusterId;

    private String topicName;

    private String description;

    private Long peakBytesIn;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Long peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
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
