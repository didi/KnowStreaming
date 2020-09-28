package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value = "Topic采样数据")
public class TopicDataSampleVO {
    @ApiModelProperty(value = "Topic数据")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TopicSampleVO{" +
                "value='" + value + '\'' +
                '}';
    }
}