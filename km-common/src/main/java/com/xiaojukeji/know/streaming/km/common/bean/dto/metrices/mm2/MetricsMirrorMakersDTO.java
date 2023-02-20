package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
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
@ApiModel(description = "MirrorMaker指标查询信息")
public class MetricsMirrorMakersDTO extends MetricDTO {
    @ApiModelProperty("MirrorMaker的SourceConnect列表")
    private List<ClusterConnectorDTO> connectorNameList;
}
