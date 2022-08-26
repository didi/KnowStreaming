package com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail;

import com.xiaojukeji.know.streaming.km.common.bean.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class JobModifyDetail extends BaseEntity {

    /**
     * 任务id
     */
    private Long id;

    /**
     * 任务类型
     */
    private Integer jobType;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * 任务状态
     */
    private Integer jobStatus;

    /**
     * 任务扩展数据
     */
    private String jobData;

    /**
     * 任务计划开始执行时间
     */
    private Date planTime;
}
