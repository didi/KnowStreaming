package com.xiaojukeji.know.streaming.km.common.bean.dto.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import com.xiaojukeji.know.streaming.km.common.enums.OffsetTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zengqiao
 * @date 2022-03-15
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Topic记录")
public class TopicRecordDTO extends PaginationSortDTO {
    @NotNull(message = "truncate不允许为空")
    @ApiModelProperty(value = "是否截断", example = "true")
    private Boolean truncate;

    @ApiModelProperty(value = "过滤的分区，为空时表示不过滤", example = "1")
    private Integer filterPartitionId;

    @ApiModelProperty(value = "过滤的key", example = "")
    private String filterKey;

    @ApiModelProperty(value = "过滤的value", example = "")
    private String filterValue;

    @ApiModelProperty(value = "预览最大消息条数", example = "100")
    private Integer maxRecords = 100;

    @ApiModelProperty(value = "预览超时时间", example = "10000")
    private Long pullTimeoutUnitMs = 8000L;

    /**
     * @see OffsetTypeEnum
     */
    @ApiModelProperty(value = "offset", example = "")
    private Integer filterOffsetReset = 0;

    @ApiModelProperty(value = "开始日期时间戳", example = "")
    private Long startTimestampUnitMs;
}
