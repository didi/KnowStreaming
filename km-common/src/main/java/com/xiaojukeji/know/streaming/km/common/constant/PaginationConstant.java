package com.xiaojukeji.know.streaming.km.common.constant;

/**
 * @author zengqiao
 * @date 20/2/28
 */
public class PaginationConstant {

    private PaginationConstant() {
    }

    /**
     * 默认页
     */
    public static final Integer DEFAULT_PAGE_NO = 1;

    /**
     * 默认页大小
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * group列表的默认排序规则
     */
    public static final String DEFAULT_GROUP_SORTED_FIELD = "name";

    /**
     * groupTopic列表的默认排序规则
     */
    public static final String DEFAULT_GROUP_TOPIC_SORTED_FIELD     = "topicName";

    public static final String TOPIC_RECORDS_TIME_SORTED_FIELD      = "timestampUnitMs";
    public static final String TOPIC_RECORDS_OFFSET_SORTED_FIELD    = "offset";
}
