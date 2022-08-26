package com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/28
 */
@Data
@ApiModel(description = "KafkaInZK配置信息")
public class KafkaInZKConfigChangedVO extends BaseTimeVO {
    @ApiModelProperty(value = "配置变更信息", example = "broker-1001配置修改")
    private String changedMessage;
}
