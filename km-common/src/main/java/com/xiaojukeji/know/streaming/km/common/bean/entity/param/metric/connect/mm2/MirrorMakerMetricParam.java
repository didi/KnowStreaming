package com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.connect.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.MetricParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wyb
 * @date 2022/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MirrorMakerMetricParam extends MetricParam {
    private Long connectClusterId;

    private String mirrorMakerName;

    private List<MirrorMakerTopic> mirrorMakerTopicList;

    private String metric;
}
