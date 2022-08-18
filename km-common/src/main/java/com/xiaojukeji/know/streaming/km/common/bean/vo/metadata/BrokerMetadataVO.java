package com.xiaojukeji.know.streaming.km.common.bean.vo.metadata;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengqiao
 * @date 19/7/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerMetadataVO {
    @ApiModelProperty(value="id")
    private Integer brokerId;

    @ApiModelProperty(value="主机")
    private String host;
}
