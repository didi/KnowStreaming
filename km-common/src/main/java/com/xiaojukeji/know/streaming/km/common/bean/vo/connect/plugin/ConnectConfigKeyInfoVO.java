package com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.connect.runtime.rest.entities.ConfigKeyInfo;

import java.util.List;

/**
 * @see ConfigKeyInfo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectConfigKeyInfoVO {
    private String name;

    private String type;

    private boolean required;

    private String defaultValue;

    private String importance;

    private String documentation;

    private String group;

    private int orderInGroup;

    private String width;

    private String displayName;

    private List<String> dependents;
}
