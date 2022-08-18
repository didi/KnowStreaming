package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

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
@ApiModel(description = "物理集群指标查询信息")
public class MetricsClusterPhyDTO extends MetricDTO {

    @ApiModelProperty("物理集群Id列表")
    private List<Long> clusterPhyIds;
}
