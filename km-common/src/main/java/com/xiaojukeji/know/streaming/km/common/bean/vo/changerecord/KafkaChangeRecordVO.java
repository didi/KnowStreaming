package com.xiaojukeji.know.streaming.km.common.bean.vo.changerecord;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class KafkaChangeRecordVO extends BaseTimeVO {
    @ApiModelProperty(value = "集群ID", example = "6")
    private Long clusterPhyId;

    @ApiModelProperty(value = "资源类型Code", example = "0")
    private Integer resTypeCode;

    @ApiModelProperty(value = "资源类型名称", example = "Topics")
    private String resTypeName;

    @ApiModelProperty(value = "资源名称", example = "know-streaming")
    private String resName;

    @ApiModelProperty(value = "变更说明", example = "修改配置")
    private String changeDesc;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;
}
