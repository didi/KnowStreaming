package com.xiaojukeji.know.streaming.km.common.constant;

public class ESConstant {

    public static final String TIME_STAMP       = "timestamp";

    public static final String HIST             = "hist";

    public static final String KEY              = "key";

    public static final String VALUE            = "value";

    public static final String MATCH            = "match";

    public static final String TERM             = "term";

    public static final String PREFIX           = "prefix";

    public static final String METRICS_DOT      = "metrics.";

    public static final String RANGE            = "range";

    public static final String FIELD            = "field";

    public static final String ORDER            = "order";

    public static final String DESC             = "desc";

    public static final String ASC              = "asc";

    public static final String GTE              = "gte";

    public static final String LTE              = "lte";

    public static final String TOTAL            = "total";

    public static final Integer DEFAULT_RETRY_TIME = 3;

    /**
     * 获取Topic-Latest指标时，单次允许的Topic数
     */
    public static final int SEARCH_LATEST_TOPIC_METRIC_CNT_PER_REQUEST = 500;

    private ESConstant() {
    }
}
