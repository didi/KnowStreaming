package com.xiaojukeji.know.streaming.km.common.bean.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionControlItemPO extends BasePO{

    /**
     * VersionItemTypeEnum
     */
    private int type;

    /**
     * name
     */
    private String name;

    /**
     * 归一化之后的版本号
     */
    private long minVersion;

    /**
     * 归一化之后的版本号
     */
    private long maxVersion;

    /**
     * 扩展
     */
    private String extend;
}
