package com.xiaojukeji.know.streaming.km.core.service.job;

import com.xiaojukeji.know.streaming.km.common.bean.entity.job.JobStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobModifyDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.Job;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.component.BaseHandle;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobActionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;

import java.util.List;

/**
 * @author d06679
 * @date 2020/12/21
 */
public interface JobHandler extends BaseHandle {

    JobTypeEnum type();

    /**
     * 创建一个任务
     * 1、校验任务内容是否合法
     * 2、提交任务，但是不执行
     * @param job 任务数据
     * @return result
     */
    Result<Void> submit(Job job, String operator);

    /**
     * 创建一个任务
     *
     * 1、校验任务内容是否合法
     * 2、提交任务
     * @param job 任务数据
     * @return result
     */
    Result<Void> delete(Job job, String operator);

    /**
     * 修改一个任务
     * 1、校验任务内容是否合法
     * 2、提交任务
     * @param job 任务数据
     * @return result
     */
    Result<Void> modify(Job job, String operator);

    /**
     * 修改一个任务的限流值
     * @param job   任务数据
     * @param limit 限流值
     * @return result
     */
    Result<Void> updateLimit(Job job, Long limit, String operator);

    /**
     * 处理任务
     * @param job 任务
     * @return result
     */
    Result<Void> process(Job job, JobActionEnum action, String operator);

    /**
     * 获取任务详细状态
     * @param job job
     * @return JobStatus
     */
    Result<JobStatus> status(Job job);

    /**
     * 获取任务详细信息
     * @param job job
     * @return AbstractTaskDetail
     */
    Result<JobDetail> getTaskDetail(Job job);

    /**
     * 获取任务详细信息
     * @param job job
     * @return AbstractTaskDetail
     */
    Result<JobModifyDetail> getTaskModifyDetail(Job job);

    /**
     * 获取任务详细信息
     * @param job job
     * @return AbstractTaskDetail
     */
    Result<List<SubJobPartitionDetailVO>> getSubJobPartitionDetail(Job job, String topic);
}
