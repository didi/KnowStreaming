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
public class ConfigChangeNotificationDataV2 extends ConfigChangeNotificationBaseData {
    public static final Integer CHANGE_DATA_VERSION = 2;

    @JsonProperty("entity_path")
    private String entityPath;

    public static ConfigChangeNotificationDataV2 getChangeData(String entityPath) {
        ConfigChangeNotificationDataV2 changeData = new ConfigChangeNotificationDataV2();
        changeData.setEntityPath(entityPath);
        changeData.setVersion(CHANGE_DATA_VERSION);
        return changeData;
    }
}