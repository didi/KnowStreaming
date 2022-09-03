package com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicParam extends ClusterPhyParam {
    protected String topicName;

    public TopicParam(Long clusterPhyId, String topicName) {
        super(clusterPhyId);
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return "TopicParam{" +
                "clusterPhyId=" + clusterPhyId +
                ", topicName='" + topicName + '\'' +
                '}';
    }
}
