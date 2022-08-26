package com.xiaojukeji.know.streaming.km.common.bean.entity.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTopicPartition implements Serializable {
    private String groupName;

    private String topicName;

    private List<Integer> partitionIdList;

    public GroupTopicPartition(String groupName, String topicName) {
        this.groupName = groupName;
        this.topicName = topicName;
    }
}