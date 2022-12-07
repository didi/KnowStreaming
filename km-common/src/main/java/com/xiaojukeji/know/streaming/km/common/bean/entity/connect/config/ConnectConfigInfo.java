package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfo;


/**
 * @see ConfigInfo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectConfigInfo {
    private ConnectConfigKeyInfo definition;

    private ConnectConfigValueInfo value;
}
