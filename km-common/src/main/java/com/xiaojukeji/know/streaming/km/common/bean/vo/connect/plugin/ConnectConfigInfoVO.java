package com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin;

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
public class ConnectConfigInfoVO {
    private ConnectConfigKeyInfoVO definition;

    private ConnectConfigValueInfoVO value;
}
