package com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.connect.runtime.rest.entities.ConfigValueInfo;

import java.util.List;

/**
 * @see ConfigValueInfo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectConfigValueInfoVO {
    private String name;

    private String value;

    private List<String> recommendedValues;

    private List<String> errors;

    private boolean visible;
}
