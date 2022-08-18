package com.xiaojukeji.know.streaming.km.common.bean.po.reassign;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

import java.util.Date;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "reassign_job")
public class ReassignJobPO extends BasePO {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * 迁移Json
     */
    private String reassignmentJson;

    /**
     * 备注
     */
    private String description;

    /**
     * 限流值
     */
    private Long throttleUnitByte;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date finishedTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 状态
     */
    private Integer status;
}
