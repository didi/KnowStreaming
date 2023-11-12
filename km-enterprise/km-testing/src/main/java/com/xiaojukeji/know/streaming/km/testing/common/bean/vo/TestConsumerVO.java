package com.xiaojukeji.know.streaming.km.testing.common.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicRecordVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 21/8/19
 */
@Data
@ApiModel(description = "测试消费结果")
@EnterpriseTesting
public class TestConsumerVO extends BaseTestVO {
    @ApiModelProperty(value = "消费信息")
    private List<TestPartitionConsumedVO> partitionConsumedList;

    @ApiModelProperty(value = "记录信息")
    private List<TopicRecordVO> recordList;

    @ApiModelProperty(value = "本次消费到的RecordSize总大小", example = "1234567")
    private Long totalRecordSizeUnitB;

    @ApiModelProperty(value = "本次消费到的总消息条数", example = "23")
    private Integer totalRecordCount;

    @ApiModelProperty(value = "时间戳最大的消息时间", example = "34335532342")
    private Long maxRecordTimestampUnitMs;
}
