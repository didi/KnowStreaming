package com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail;

import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobDetail {

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
     * 任务状态
     */
    private Integer jobStatus;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * 任务计划执行时间
     */
    private Date planTime;

    /**
     * 任务开始执行时间
     */
    private Date   startTime;

    /**
     * 任务完成执行时间
     */
    private Date   endTime;

    /**
     * 任务限流值
     */
    private Double  flowLimit;

    /**
     * 子任务成功数
     */
    private Integer total;

    /**
     * 子任务成功数
     */
    private Integer success;

    /**
     * 子任务失败数
     */
    private Integer fail;

    /**
     * 子任务进行数
     */
    private Integer doing;

    /**
     * 子任务列表，不同的子任务的实现不一样，需要具体场景具体处理
     */
    private List<SubJobVO> subJobs;
}
