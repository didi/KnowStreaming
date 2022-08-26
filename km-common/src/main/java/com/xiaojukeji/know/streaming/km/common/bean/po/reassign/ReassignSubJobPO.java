package com.xiaojukeji.know.streaming.km.common.bean.po.reassign;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

import java.util.Date;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "reassign_sub_job")
public class ReassignSubJobPO extends BasePO {
    /**
     * 主任务ID
     */
    private Long jobId;

    /**
     * 物流集群ID
     */
    private Long clusterPhyId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 分区ID
     */
    private Integer partitionId;

    /**
     * 源Broker列表
     */
    private String originalBrokerIds;

    /**
     * 目标Broker列表
     */
    private String reassignBrokerIds;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date finishedTime;

    /**
     * 扩展数据
     * @see com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignSubJobExtendData
     */
    private String extendData;

    /**
     * 状态
     * @see com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum
     */
    private Integer status;
}
