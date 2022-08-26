package com.xiaojukeji.know.streaming.km.common.bean.vo.reassign;

import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.TopicMetadataVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@ApiModel(description = "迁移计划")
public class ReassignTopicOverviewVO extends TopicMetadataVO {
    @ApiModelProperty(value="Topic数据保存时间", example = "10")
    private Long retentionMs;

    @ApiModelProperty(value="近N天的avg的BytesIn")
    private List<MetricPointVO> latestDaysAvgBytesInList;

    @ApiModelProperty(value="近N天的max的BytesIn")
    private List<MetricPointVO> latestDaysMaxBytesInList;
}
