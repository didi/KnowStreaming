package com.xiaojukeji.know.streaming.km.common.bean.vo.topic.broker;

import com.xiaojukeji.know.streaming.km.common.bean.vo.broker.BrokerReplicaSummaryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "TopicBroker状态信息")
public class TopicBrokerSingleVO {
    @ApiModelProperty(value = "brokerId", example = "1")
    private Integer brokerId;

    @ApiModelProperty(value = "主机名", example = "2")
    private String host;

    @ApiModelProperty(value = "是否存活, true:是 false:否", example = "true")
    private Boolean alive;

    @ApiModelProperty(value = "每秒流入流量(B/s)", example = "13242424.32423")
    private Float bytesInOneMinuteRate;

    @ApiModelProperty(value = "每秒流出流量(B/s)", example = "313242424.32423")
    private Float bytesOutOneMinuteRate;

    @ApiModelProperty(value = "Broker副本信息")
    private List<BrokerReplicaSummaryVO> replicaList;
}
