package com.xiaojukeji.know.streaming.km.common.bean.po.km;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

import java.util.Date;

/**
 * @author didi
 */

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "app_node")
public class KmNodePO extends BasePO {

    /**
     * hostName
     */
    private String hostName;

    /**
     * ip
     */
    private String ip;

    /**
     * 心跳时间
     */
    private Date beatTime;

    /**
     * 应用名称
     */
    private String appName;
}
