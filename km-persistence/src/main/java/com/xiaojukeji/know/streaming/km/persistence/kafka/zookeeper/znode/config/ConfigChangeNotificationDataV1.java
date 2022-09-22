package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.config;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ConfigChangeNotificationDataV1 extends ConfigChangeNotificationBaseData {
    public static final Integer CHANGE_DATA_VERSION = 1;

    @JsonProperty("entity_type")
    private String entityType;

    @JsonProperty("entity_name")
    private String entityName;
}