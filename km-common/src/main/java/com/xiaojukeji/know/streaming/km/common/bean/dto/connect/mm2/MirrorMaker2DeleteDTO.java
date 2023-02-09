package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorDeleteDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zengqiao
 * @date 2022-12-12
 */
@Data
@ApiModel(description = "删除MM2")
public class MirrorMaker2DeleteDTO extends ConnectorDeleteDTO {
}
