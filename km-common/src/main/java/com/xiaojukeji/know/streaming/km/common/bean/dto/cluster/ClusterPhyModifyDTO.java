package com.xiaojukeji.know.streaming.km.common.bean.dto.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description = "集群信息修改")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterPhyModifyDTO extends ClusterPhyAddDTO {
    @Min(value = 0, message = "id不允许小于0")
    @ApiModelProperty(value="集群Id", example = "1")
    private Long id;
}
