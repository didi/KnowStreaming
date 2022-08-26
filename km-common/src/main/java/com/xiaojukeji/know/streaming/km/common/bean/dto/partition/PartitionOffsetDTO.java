package com.xiaojukeji.know.streaming.km.common.bean.dto.partition;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * Topic Offset
 * @author zengqiao
 * @date 19/6/2
 */
@Data
@NoArgsConstructor
public class PartitionOffsetDTO extends BaseDTO {
    @Min(value = 0, message = "partitionId不允许小于0")
    @ApiModelProperty(value = "分区ID", example = "1")
    private Integer partitionId;

    @Min(value = 0, message = "offset不允许小于0")
    @ApiModelProperty(value = "分区offset", example = "123")
    private Long offset;

    public PartitionOffsetDTO(Integer partitionId, Long offset) {
        this.partitionId = partitionId;
        this.offset = offset;
    }
}

