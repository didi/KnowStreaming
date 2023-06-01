package com.xiaojukeji.know.streaming.km.common.bean.entity.jmx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerIdJmxPort implements Serializable {
    /**
     * serverID
     */
    private String serverId;

    /**
     * JMX端口
     */
    private Integer jmxPort;
}
