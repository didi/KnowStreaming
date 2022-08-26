package com.xiaojukeji.know.streaming.km.common.bean.vo.group;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "TopicGroup消费详细信息")
public class GroupTopicConsumedDetailVO {
    @ApiModelProperty(value = "Topic名称", example = "konw-streaming")
    private String topicName;

    @ApiModelProperty(value = "分区Id", example = "200")
    private Integer partitionId;

    @ApiModelProperty(value = "memberId", example = "dkm_admin")
    private String memberId;

    @ApiModelProperty(value = "多个指标的当前值, 包括健康分/LogSize等")
    private BaseMetrics latestMetrics;

    @ApiModelProperty(value = "member的主机", example = "127.0.0.1")
    private String host;

    @ApiModelProperty(value = "member的ClientId", example = "consumer-1")
    private String clientId;
}
