package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 19/4/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ControllerData {
    private Integer brokerid;

    private Integer version;

    private Long timestamp;
}