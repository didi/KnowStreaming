package com.xiaojukeji.know.streaming.km.common.bean.entity.topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicPartitionKS implements Serializable {
    private static final long serialVersionUID = 19459887202113667L;
    private String topic;

    private Integer partition;
}
