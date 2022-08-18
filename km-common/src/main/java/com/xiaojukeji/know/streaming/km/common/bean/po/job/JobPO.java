package com.xiaojukeji.know.streaming.km.common.bean.po.job;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.content.BaseJobCreateContent;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * WorkTask PO 对象
 * 
 * @author fengqiongfeng
 * @date 2020-12-21
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ks_km_job")
public class JobPO extends BasePO {

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * 标题 
     */
    private String jobName;

    /**
     * 标题
     */
    private String jobDesc;

    /**
     * 任务类型
     */
    private Integer jobType;

    /**
     * 创建任务的详细数据
     * @see BaseJobCreateContent
     */
    private String jobData;

    /**
     * 任务状态：
     * @see JobStatusEnum
     */
    private Integer jobStatus;

    /**
     * 任务运行详细状态(json), Success：7  Fail：1  Doing：2
     */
    private String runningStatus;

    /**
     * 任务执行对象
     */
    private String target;

    /**
     * 创建人 
     */
    private String creator;

    /**
     * 任务计划开始执行时间
     */
    private Date planTime;

    /**
     * 任务实际开始执行时间
     */
    private Date startTime;

}

