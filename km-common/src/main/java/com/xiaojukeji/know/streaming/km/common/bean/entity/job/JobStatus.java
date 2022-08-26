package com.xiaojukeji.know.streaming.km.common.bean.entity.job;

import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JobStatus {

    /**
     * @see JobStatusEnum
     */
    private int status;

    private int total;

    private int success;

    private int failed;

    private int doing;

    private int waiting;

    private int unknown;

    /**
     * 按照状态，直接进行聚合
     */
    public JobStatus(List<Integer> jobStatusList) {
        this.total = jobStatusList.size();
        this.success = 0;
        this.failed = 0;
        this.doing = 0;
        this.waiting = 0;
        this.unknown = 0;
        for (Integer jobStatus: jobStatusList) {
            if (JobStatusEnum.SUCCESS.getStatus() == jobStatus) {
                success += 1;
            } else if (JobStatusEnum.FAILED.getStatus() == jobStatus) {
                failed += 1;
            } else if (JobStatusEnum.RUNNING.getStatus() == jobStatus) {
                doing += 1;
            } else if (JobStatusEnum.WAITING.getStatus() == jobStatus) {
                waiting += 1;
            } else {
                unknown += 1;
            }
        }

        this.status = JobStatusEnum.getStatusBySubStatus(this.total, this.success, this.failed, this.doing).getStatus();
    }

}
