package com.xiaojukeji.know.streaming.km.common.bean.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpPortData implements Serializable {
    private static final long serialVersionUID = -428897032994630685L;

    private String ip;

    private String port;
}
