package com.xiaojukeji.know.streaming.km.common.bean.entity.version;

import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import lombok.Data;

/**
 * @author wyb
 * @date 2022/11/24
 */
@Data
public class VersionConnectJmxInfo extends VersionJmxInfo{
    private ConnectorTypeEnum type;
}
