package com.xiaojukeji.know.streaming.km.testing.common.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Topic Offset
 * @author zengqiao
 * @date 22/03/01
 */
@Data
@EnterpriseTesting
public class TestPartitionConsumedVO {
    @ApiModelProperty(value = "分区ID", example = "1")
    private Integer partitionId;

    @ApiModelProperty(value = "分区end-offset", example = "123")
    private Long logEndOffset;

    @ApiModelProperty(value = "消费到的offset", example = "23")
    private Long consumedOffset;

    @ApiModelProperty(value = "消费到的LogSize", example = "23")
    private Long recordSizeUnitB;

    @ApiModelProperty(value = "消费到的消息条数", example = "23")
    private Integer recordCount;
}

