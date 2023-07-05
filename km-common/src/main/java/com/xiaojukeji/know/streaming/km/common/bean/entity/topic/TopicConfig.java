package com.xiaojukeji.know.streaming.km.common.bean.entity.topic;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicConfig implements Serializable {
    /**
     * 表主键ID
     */
    private Long id;

    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 保存时间
     */
    private Long retentionMs;
}
