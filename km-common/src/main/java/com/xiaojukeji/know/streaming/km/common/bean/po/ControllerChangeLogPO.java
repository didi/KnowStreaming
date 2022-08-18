package com.xiaojukeji.know.streaming.km.common.bean.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "controller_change_log")
public class ControllerChangeLogPO extends BasePO {

    /**
     * 改变时间
     */
    private Long changeTime;

    /**
     * BrokerId
     */
    private Integer brokerId;

    /**
     * rack
     */
    private String rack;

    /**
     * 主机
     */
    private String host;

    /**
     * kafka 集群 id
     */
    private Long clusterPhyId;

}
