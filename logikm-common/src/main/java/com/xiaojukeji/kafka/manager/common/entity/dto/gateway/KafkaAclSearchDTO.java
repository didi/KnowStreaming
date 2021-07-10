package com.xiaojukeji.kafka.manager.common.entity.dto.gateway;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/7/7
 */
public class KafkaAclSearchDTO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "开始时间(ms)")
    private Long start;

    @ApiModelProperty(value = "结束时间(ms)")
    private Long end;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

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
        return "KafkaAclSearchDTO{" +
                "clusterId=" + clusterId +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(start)
                || ValidateUtils.isNull(end)) {
            return false;
        }
        return true;
    }
}