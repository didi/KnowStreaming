package com.xiaojukeji.know.streaming.km.testing.common.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengqiao
 * @date 21/8/19
 */
@Data
@NoArgsConstructor
@ApiModel(description = "测试生产结果")
@EnterpriseTesting
public class TestProducerVO extends BaseTestVO {
    @ApiModelProperty(value = "数据", example = "1")
    private Integer partitionId;

    @ApiModelProperty(value = "数据", example = "123")
    private Long offset;

    @ApiModelProperty(value = "数据", example = "12321231321231")
    private Long timestampUnitMs;

    public TestProducerVO(Long costTimeUnitMs, Integer partitionId, Long offset, Long timestampUnitMs) {
        super(costTimeUnitMs);
        this.partitionId = partitionId;
        this.offset = offset;
        this.timestampUnitMs = timestampUnitMs;
    }
}
