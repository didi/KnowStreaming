package com.xiaojukeji.kafka.manager.common.constant;

/**
 * 采样相关配置
 * @author zengqiao
 * @date 20/5/8
 */
public class TopicSampleConstant {
    /**
     * TOPIC_SAMPLE_MAX_MSG_NUM: 最大采样条数
     * TOPIC_SAMPLE_MAX_TIMEOUT_MS：采样超时时间
     * TOPIC_SAMPLE_POLL_TIME_OUT_MS：采样单次poll超时时间
     * TOPIC_SAMPLE_MAX_DATA_LENGTH：截断情况下, 采样的数据最大长度
     */
    public static final Integer MAX_MSG_NUM = 100;
    public static final Integer MAX_TIMEOUT_UNIT_MS = 10000;
    public static final Integer POLL_TIME_OUT_UNIT_MS = 2000;
    public static final Integer MAX_DATA_LENGTH_UNIT_BYTE = 2048;

    private TopicSampleConstant() {
    }
}