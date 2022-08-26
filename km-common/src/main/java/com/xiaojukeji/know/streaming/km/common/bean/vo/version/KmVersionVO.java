package com.xiaojukeji.know.streaming.km.common.bean.vo.version;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/03/04
 */
@Data
@ApiModel(description = "Km版本信息")
public class KmVersionVO {
    private String kmVersion;
}
