package com.xiaojukeji.know.streaming.km.common.enums.job;

public enum JobStatusEnum {
    /**执行中*/
    RUNNING(1, "running"),

    WAITING(2, "waiting"),

    SUCCESS(3, "success"),

    FAILED(4, "failed"),

    CANCELED(5, "canceled"),

    UNKNOWN(-1, "unknown");

    JobStatusEnum(int status, String value) {
        this.status = status;
        this.value = value;
    }

    private final int status;

    private final String value;

    public int getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }

    public static JobStatusEnum valueOfStatus(int status) {
        for (JobStatusEnum statusEnum : JobStatusEnum.values()) {
            if (status == statusEnum.getStatus()) {
                return statusEnum;
            }
        }

        return JobStatusEnum.UNKNOWN;
    }

    public static JobStatusEnum getStatusBySubStatus(int totalJobNum,
                                                     int successJobNu,
                                                     int failedJobNu,
                                                     int runningJobNu){
        if (totalJobNum > 0 && (successJobNu + failedJobNu + runningJobNu == 0)) {
            return WAITING;
        }

        if(runningJobNu > 0) {return RUNNING;}
        if(0 == runningJobNu && 0 == failedJobNu && successJobNu > 0){return SUCCESS;}
        if(0 == runningJobNu && failedJobNu > 0){return FAILED;}

        return UNKNOWN;
    }

    /**
     * 当前的任务状态, 能否执行任务
     */
    public static boolean canExecuteJob(Integer jobStatus) {
        // 只有waiting状态才可以执行任务
        return jobStatus != null && WAITING.status == jobStatus;
    }

    /**
     * 当前的任务状态, 不能删除任务
     */
    public static boolean canNotDeleteJob(Integer jobStatus) {
        // 处于 running 的任务都不可以删除
        return jobStatus == null || RUNNING.status == jobStatus;
    }

    /**
     * 当前的任务状态, 能否取消任务
     */
    public static boolean canCancelJob(Integer jobStatus) {
        // 只有waiting状态才可以取消任务
        return jobStatus != null && WAITING.status == jobStatus;
    }

    public static boolean isRunning(Integer jobStatus) {
        return jobStatus != null && RUNNING.status == jobStatus;
    }

    public static boolean isWaiting(Integer jobStatus) {
        return jobStatus != null && WAITING.status == jobStatus;
    }

    public static boolean isFinished(Integer jobStatus) {
        return jobStatus != null && (SUCCESS.status == jobStatus || FAILED.status == jobStatus || CANCELED.status == jobStatus);
    }
}
