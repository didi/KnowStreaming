package com.xiaojukeji.know.streaming.km.common.bean.vo.broker;

import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.BrokerMetadataVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 集群Broker信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@NoArgsConstructor
@ApiModel(description = "Broker基本信息")
public class BrokerBasicVO extends BrokerMetadataVO {
    @ApiModelProperty(value = "集群名称", example = "know-streaming")
    private String clusterName;

    public BrokerBasicVO(Integer brokerId, String host, String clusterName) {
        super(brokerId, host);
        this.clusterName = clusterName;
    }
}
