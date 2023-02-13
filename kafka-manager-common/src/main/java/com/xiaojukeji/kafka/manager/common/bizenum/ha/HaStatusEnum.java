package com.xiaojukeji.kafka.manager.common.bizenum.ha;

/**
 * @author zengqiao
 * @date 20/7/28
 */
public enum HaStatusEnum {
    UNKNOWN(-1, "未知状态"),

    STABLE(HaStatusEnum.STABLE_CODE, "稳定状态"),

//    SWITCHING(HaStatusEnum.SWITCHING_CODE, "切换中"),
        SWITCHING_PREPARE(
                HaStatusEnum.SWITCHING_PREPARE_CODE,
                "主备切换--源集群[%s]--预处理(阻止当前主Topic写入)"),

        SWITCHING_WAITING_IN_SYNC(
                HaStatusEnum.SWITCHING_WAITING_IN_SYNC_CODE,
                "主备切换--目标集群[%s]--等待主与备Topic数据同步完成"),

        SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH(
                HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH_CODE,
                "主备切换--目标集群[%s]--关闭旧的备Topic的副本同步"),
        SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH(
                HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH_CODE,
                "主备切换--源集群[%s]--开启新的备Topic的副本同步"),

        SWITCHING_CLOSEOUT(
                HaStatusEnum.SWITCHING_CLOSEOUT_CODE,
                "主备切换--目标集群[%s]--收尾(允许新的主Topic写入)"),

    ;

    public static final int UNKNOWN_CODE = -1;
    public static final int STABLE_CODE = 0;

    public static final int SWITCHING_CODE = 100;
    public static final int SWITCHING_PREPARE_CODE = 101;

    public static final int SWITCHING_WAITING_IN_SYNC_CODE = 102;
    public static final int SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH_CODE = 103;
    public static final int SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH_CODE = 104;

    public static final int SWITCHING_CLOSEOUT_CODE = 105;


    private final int code;

    private final String msg;

    public int getCode() {
        return code;
    }

    public String getMsg(String clusterName) {
        if (this.code == UNKNOWN_CODE || this.code == STABLE_CODE) {
            return this.msg;
        }
        return String.format(msg, clusterName);
    }

    HaStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Integer calProgress(Integer status) {
        if (status == null || status == HaStatusEnum.STABLE_CODE || status == UNKNOWN_CODE) {
            return 100;
        }

        // 最小进度为 1%
        return Math.max(1, (status - 101) * 100 / 5);
    }
}
