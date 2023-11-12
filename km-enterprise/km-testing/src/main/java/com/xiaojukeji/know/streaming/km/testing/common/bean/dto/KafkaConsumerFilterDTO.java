package com.xiaojukeji.know.streaming.km.testing.common.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import com.xiaojukeji.know.streaming.km.testing.common.enums.KafkaConsumerFilterEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@EnterpriseTesting
@ApiModel(description="Kafka消费者测试")
public class KafkaConsumerFilterDTO extends BaseDTO {
    /**
     * @see KafkaConsumerFilterEnum
     */
    @Range(min = 0, max = 5, message = "filterType最大和最小值必须在[0, 5]之间")
    @ApiModelProperty(value = "开始消费位置的类型", example = "2")
    private Integer filterType;

    @ApiModelProperty(value = "比较匹配的Key", example = "ks-km")
    private String filterCompareKey;

    @ApiModelProperty(value = "比较匹配的Value", example = "ks-km")
    private String filterCompareValue;

    @ApiModelProperty(value = "比较匹配的大小", example = "1024")
    private Long filterCompareSizeUnitB;
}
