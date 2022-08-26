package com.xiaojukeji.know.streaming.km.common.bean.entity.job;

import com.xiaojukeji.know.streaming.km.common.bean.entity.BaseEntity;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.content.BaseJobCreateContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job extends BaseEntity {
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

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
     * 任务状态
     */
    private Integer jobStatus;

    /**
     * 创建任务的详细数据
     * @see BaseJobCreateContent
     */
    private String jobData;

    /**
     * 任务执行对象
     */
    private String target;

    /**
     * 任务运行详细状态(json), Success：7  Fail：1  Doing：2
     */
    private String runningStatus;

    /**
     * 任务计划开始执行时间
     */
    private Date   planTime;

    /**
     * 任务实际开始执行时间
     */
    private Date   startTime;

    /**
     * 创建人
     */
    private String  creator;
}
