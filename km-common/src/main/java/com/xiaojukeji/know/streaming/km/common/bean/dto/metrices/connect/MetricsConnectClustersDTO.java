package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Connect集群指标查询信息")
public class MetricsConnectClustersDTO extends MetricDTO {
    @ApiModelProperty("Connect集群ID")
    private List<Long> connectClusterIdList;
}
