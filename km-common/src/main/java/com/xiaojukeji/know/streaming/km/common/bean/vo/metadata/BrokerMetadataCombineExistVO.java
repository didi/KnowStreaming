package com.xiaojukeji.know.streaming.km.common.bean.vo.metadata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@Data
@NoArgsConstructor
@ApiModel(description="Broker是否存在及元信息")
public class BrokerMetadataCombineExistVO extends BrokerMetadataVO {
    @ApiModelProperty(value="是否存在，true:是 , false:否")
    private Boolean exist;

    @ApiModelProperty(value="是否存活，true:是 , false:否")
    private Boolean alive;

    public BrokerMetadataCombineExistVO(Integer brokerId, String host, Boolean exist, Boolean alive) {
        super(brokerId, host);
        this.exist = exist;
        this.alive = alive;
    }
}
