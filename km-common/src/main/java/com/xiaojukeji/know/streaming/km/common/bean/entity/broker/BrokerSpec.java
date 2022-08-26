package com.xiaojukeji.know.streaming.km.common.bean.entity.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerSpec {

    private Integer brokerId;

    private Double cpu;

    private Double disk;

    private Double flow;

}
