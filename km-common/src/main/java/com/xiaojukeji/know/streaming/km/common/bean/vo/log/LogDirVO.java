package com.xiaojukeji.know.streaming.km.common.bean.vo.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * LogDir信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "LogDir信息")
public class LogDirVO {
    @ApiModelProperty(value = "目录", example = "/root/data/apache-kafka")
    private String dir;

    @ApiModelProperty(value = "Topic名", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value = "分区ID", example = "10")
    private Integer partitionId;

    @ApiModelProperty(value = "副本offsetLag", example = "1000")
    private Long offsetLag;

    @ApiModelProperty(value = "log的大小", example = "100000")
    private Long logSizeUnitB;
}
