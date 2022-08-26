package com.xiaojukeji.know.streaming.km.common.bean.entity.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zengqiao
 * @date 19/5/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTopic implements Serializable {
    private String groupName;

    private String topicName;
}