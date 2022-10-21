package com.xiaojukeji.know.streaming.km.common.bean.entity.group;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wyb
 * @date 2022/10/10
 */
@Data
@NoArgsConstructor
public class GroupTopicMember {
    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 消费此Topic的成员数量
     */
    private Integer memberCount;

    public GroupTopicMember(String topicName, Integer memberCount) {
        this.topicName = topicName;
        this.memberCount = memberCount;
    }
}
