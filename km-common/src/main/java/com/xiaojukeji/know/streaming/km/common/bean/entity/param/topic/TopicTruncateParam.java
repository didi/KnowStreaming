package com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicTruncateParam extends ClusterPhyParam {
    protected String topicName;
    protected long offset;

    public TopicTruncateParam(Long clusterPhyId, String topicName, long offset) {
        super(clusterPhyId);
        this.topicName = topicName;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "TopicParam{" +
                "clusterPhyId=" + clusterPhyId +
                ", topicName='" + topicName + '\'' +
                ", offset='" + offset + '\'' +
                '}';
    }
}
