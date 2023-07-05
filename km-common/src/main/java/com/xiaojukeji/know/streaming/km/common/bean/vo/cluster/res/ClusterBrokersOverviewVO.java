package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.BrokerMetadataVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 集群Broker信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Broker信息概览")
public class ClusterBrokersOverviewVO extends BrokerMetadataVO {
    @ApiModelProperty(value = "多个指标的当前值, 包括健康分/LogSize等")
    private BaseMetrics latestMetrics;

    @ApiModelProperty(value = "角色", example = "[gc, tc, controller]")
    private List<String> kafkaRoleList;

    @ApiModelProperty(value = "启动时间", example = "31324242434")
    private Long startTimeUnitMs;

    @ApiModelProperty(value = "rack")
    private String rack;

    @ApiModelProperty(value = "jmx端口")
    private Integer jmxPort;

    @ApiModelProperty(value = "jmx连接状态 true:连接成功 false:连接失败")
    private Boolean jmxConnected;

    @ApiModelProperty(value = "是否存活 true：存活 false：不存活")
    private Boolean alive;
}
