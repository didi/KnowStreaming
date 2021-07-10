package com.xiaojukeji.kafka.manager.common.entity.dto.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/7/7
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Kafka用户查询")
public class KafkaUserSearchDTO {
    @ApiModelProperty(value = "开始时间(ms)")
    private Long start;

    @ApiModelProperty(value = "结束时间(ms)")
    private Long end;

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "KafkaUserSearchDTO{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNullOrLessThanZero(start) || ValidateUtils.isNullOrLessThanZero(end)) {
            return false;
        }
        return true;
    }
}