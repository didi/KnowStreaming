package com.xiaojukeji.know.streaming.km.core.service.job;

import com.xiaojukeji.know.streaming.km.common.bean.dto.job.JobDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.job.JobPaginationDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.Job;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.*;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;

import java.util.List;

/**
 * 任务 Service
 *
 * @author d06679
 * @date 2020/12/21
 */
public interface JobService {

    /**
     * 提交一个任务
     * @param jobDTO 任务数据
     * @return Result
     * @throws AdminOperateException 异常
     */
    Result<Void> addTask(Long clusterPhyId, JobDTO jobDTO, String operator);

    /**
     * 删除一个任务
     * @param jobId
     * @param operator
     * @return
     */
    Result<Void> deleteById(Long clusterPhyId, Long jobId, String operator);

    /**
     * 通过id更新任务
     * @param task task
     * @return int
     */
    Result<Void> updateTask(Long clusterPhyId, JobDTO task, String operator);

    /**
     * 通过id获取任务
     *
     * @param id 任务id
     * @return TaskPO
     */
    Job getById(Long clusterPhyId, Long id);

    /**
     * 通过集群id和type获取任务
     *
     * @param type 类型
     * @return clusterPhyId 集群id
     */
    Job getByClusterIdAndType(Long clusterPhyId, Integer type);

    /**
     * 获取 job 的详细信息
     * @param jobId
     * @return
     */
    Result<JobDetailVO> getJobDetail(Long clusterPhyId, Long jobId);

    /**
     * 获取 job 的详细信息
     * @param jobId
     * @return
     */
    Result<JobModifyDetailVO> getJobModifyDetail(Long clusterPhyId, Long jobId);

    /**
     * 获取 job 相关的子任务的 partition 流量信息
     * @param jobId
     * @return
     */
    Result<List<SubJobPartitionDetailVO>> getSubJobPartitionDetail(Long clusterPhyId, Long jobId, String topic);

    /**
     *  获取 job 相关的 broker 节点流量信息
     * @param jobId
     * @return
     */
    Result<List<JobTrafficBrokerVO>> getJobNodeTraffic(Long clusterPhyId, Long jobId);

    /**
     * 分页查询
     * @param clusterPhyId
     * @return
     */
    PaginationResult<JobOverViewVO> pagingJobs(Long clusterPhyId, JobPaginationDTO dto);

    /**
     *  获取 job 模块的状态统计信息
     * @param clusterPhyId
     * @return
     */
    Result<JobStateVO> state(Long clusterPhyId);

    /**
     * 根据某个 job 的限流值
     * @param clusterPhyId
     * @param jobId
     * @param limit
     * @return
     */
    Result<Void> updateJobTrafficLimit(Long clusterPhyId, Long jobId, Long limit, String operator);

    /**
     * 定时检查集群中是否有需要执行和正在执行的任务
     * @param clusterPhyId
     * @return
     */
    void scheduleJobByClusterId(Long clusterPhyId);

    Integer countJobsByCluster(Long clusterPhyId);

    Integer countJobsByClusterAndJobStatus(Long clusterPhyId, Integer jobStatus);
}
