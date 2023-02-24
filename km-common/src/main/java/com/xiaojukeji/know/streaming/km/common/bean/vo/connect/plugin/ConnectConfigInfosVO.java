package com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfos;

import java.util.List;

/**
 * @see ConfigInfos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectConfigInfosVO {
    private String name;

    private int errorCount;

    private List<String> groups;

    private List<ConnectConfigInfoVO> configs;
}
