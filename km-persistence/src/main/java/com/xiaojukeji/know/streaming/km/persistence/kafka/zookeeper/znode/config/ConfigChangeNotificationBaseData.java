package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/5/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfigChangeNotificationBaseData {
    protected Integer version;
}