package com.xiaojukeji.kafka.manager.openapi.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/8/24
 */
@ApiModel(description="TopicOffset变化")
public class TopicOffsetChangedVO {
    @ApiModelProperty(value="Offset是否变化, 0:否, 1:是, -1:未知")
    private Integer offsetChanged;

    public TopicOffsetChangedVO(Integer offsetChanged) {
        this.offsetChanged = offsetChanged;
    }

    public Integer getOffsetChanged() {
        return offsetChanged;
    }

    public void setOffsetChanged(Integer offsetChanged) {
        this.offsetChanged = offsetChanged;
    }

    @Override
    public String toString() {
        return "TopicOffsetChangedVO{" +
                "offsetChanged=" + offsetChanged +
                '}';
    }
}