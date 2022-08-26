package com.xiaojukeji.know.streaming.km.common.bean.po.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "platform_cluster_config")
public class PlatformClusterConfigPO extends BasePO {
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
