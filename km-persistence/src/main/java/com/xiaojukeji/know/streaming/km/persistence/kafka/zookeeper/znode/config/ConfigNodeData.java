package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/5/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfigNodeData<T> {
    public static final Integer CONFIGDATA_VERSION = 1;

    private T config;

    private Integer version;
}