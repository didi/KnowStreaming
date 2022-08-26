package com.xiaojukeji.know.streaming.km.common.bean.vo.config.platform;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlatformClusterConfigVO extends BaseTimeVO {
    /**
     * 配置组
     */
    private Long clusterId;

    /**
     * 配置组
     */
    private String valueGroup;

    /**
     * 配置项的名称
     */
    private String valueName;

    /**
     * 配置项的值
     */
    private String value;

    /**
     * 备注
     */
    private String description;

    /**
     * 操作人
     */
    private String operator;
}
