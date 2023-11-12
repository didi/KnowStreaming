package com.xiaojukeji.know.streaming.km.testing.common.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.partition.PartitionOffsetDTO;
import com.xiaojukeji.know.streaming.km.testing.common.enums.KafkaConsumerStartFromEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@EnterpriseTesting
@ApiModel(description="Kafka消费者测试")
public class KafkaConsumerStartFromDTO extends BaseDTO {
    /**
     * @see KafkaConsumerStartFromEnum
     */
    @Range(min = 0, max = 5, message = "startFromType最大和最小值必须在[0, 5]之间")
    @ApiModelProperty(value = "开始消费位置的类型", example = "2")
    private Integer startFromType;

    @ApiModelProperty(value = "指定时间戳消费", example = "2453535465")
    private Long timestampUnitMs;

    @ApiModelProperty(value = "指定offset消费", example = "[]")
    private List<PartitionOffsetDTO> offsetList;

    @ApiModelProperty(value = "指定消费组消费", example = "6")
    private String consumerGroup;

    @ApiModelProperty(value = "指定从最近多少条开始消费", example = "10")
    private Long latestMinusX;
}
