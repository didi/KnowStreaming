package com.xiaojukeji.kafka.manager.common.entity.ao.ha.job;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.job.HaJobStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HaJobState {

    /**
     * @see com.xiaojukeji.kafka.manager.common.bizenum.ha.job.HaJobStatusEnum
     */
    private int status;

    private int total;

    private int success;

    private int failed;

    private int doing;
    private int doingInTimeout;

    private int unknown;

    private Integer progress;

    /**
     * 按照状态，直接进行聚合
     */
    public HaJobState(List<Integer> jobStatusList, Integer progress) {
        this.total = jobStatusList.size();
        this.success = 0;
        this.failed = 0;
        this.doing = 0;
        this.doingInTimeout = 0;
        this.unknown = 0;
        for (Integer jobStatus: jobStatusList) {
            if (HaJobStatusEnum.SUCCESS.getStatus() == jobStatus) {
                success += 1;
            } else if (HaJobStatusEnum.FAILED.getStatus() == jobStatus) {
                failed += 1;
            } else if (HaJobStatusEnum.RUNNING.getStatus() == jobStatus) {
                doing += 1;
            } else if (HaJobStatusEnum.RUNNING_IN_TIMEOUT.getStatus() == jobStatus) {
                doingInTimeout += 1;
            } else {
                unknown += 1;
            }
        }

        this.status = HaJobStatusEnum.getStatusBySubStatus(this.total, this.success, this.failed, this.doing, this.doingInTimeout, this.unknown).getStatus();

        this.progress = progress;
    }

    public HaJobState(Integer doingSize, Integer progress) {
        this.total = doingSize;
        this.success = 0;
        this.failed = 0;
        this.doing = doingSize;
        this.doingInTimeout = 0;
        this.unknown = 0;

        this.progress = progress;
    }
}
