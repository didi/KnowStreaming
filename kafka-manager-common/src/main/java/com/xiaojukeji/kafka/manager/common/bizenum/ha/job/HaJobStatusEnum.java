package com.xiaojukeji.kafka.manager.common.bizenum.ha.job;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;

public enum HaJobStatusEnum {
    /**执行中*/
    RUNNING(TaskStatusEnum.RUNNING),
    RUNNING_IN_TIMEOUT(TaskStatusEnum.RUNNING_IN_TIMEOUT),

    SUCCESS(TaskStatusEnum.SUCCEED),

    FAILED(TaskStatusEnum.FAILED),

    UNKNOWN(TaskStatusEnum.UNKNOWN);

    HaJobStatusEnum(TaskStatusEnum taskStatusEnum) {
        this.status = taskStatusEnum.getCode();
        this.value = taskStatusEnum.getMessage();
    }

    private final int status;

    private final String value;

    public int getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }

    public static HaJobStatusEnum valueOfStatus(int status) {
        for (HaJobStatusEnum statusEnum : HaJobStatusEnum.values()) {
            if (status == statusEnum.getStatus()) {
                return statusEnum;
            }
        }

        return HaJobStatusEnum.UNKNOWN;
    }

    public static HaJobStatusEnum getStatusBySubStatus(int totalJobNum,
                                                       int successJobNu,
                                                       int failedJobNu,
                                                       int runningJobNu,
                                                       int runningInTimeoutJobNu,
                                                       int unknownJobNu) {
        if (unknownJobNu > 0) {
            return UNKNOWN;
        }

        if((failedJobNu + runningJobNu + runningInTimeoutJobNu + unknownJobNu) == 0) {
            return SUCCESS;
        }

        if((runningJobNu + runningInTimeoutJobNu + unknownJobNu) == 0 && failedJobNu > 0) {
            return FAILED;
        }

        if (runningInTimeoutJobNu > 0) {
            return RUNNING_IN_TIMEOUT;
        }

        return RUNNING;
    }

    public static boolean isRunning(Integer jobStatus) {
        return jobStatus != null && (RUNNING.status == jobStatus || RUNNING_IN_TIMEOUT.status == jobStatus);
    }

    public static boolean isFinished(Integer jobStatus) {
        return jobStatus != null && (SUCCESS.status == jobStatus || FAILED.status == jobStatus);
    }
}
