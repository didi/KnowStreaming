package com.xiaojukeji.know.streaming.km.common.jmx;

/**
 * @author zhongyuankai
 * @date 20/4/13
 */
public class JmxAttribute {

    public static final String RATE_MIN_1           = "OneMinuteRate";

    public static final String RATE_MIN_5           = "FiveMinuteRate";

    public static final String RATE_MIN_15          = "FifteenMinuteRate";

    public static final String PERCENTILE_50        = "50thPercentile";

    public static final String PERCENTILE_75        = "75thPercentile";

    public static final String PERCENTILE_95        = "95thPercentile";

    public static final String PERCENTILE_98        = "98thPercentile";

    public static final String PERCENTILE_99        = "99thPercentile";

    public static final String VALUE                = "Value";

    public static final String CONNECTION_COUNT     = "connection-count";

    public static final String VERSION              = "Version";

    private JmxAttribute() {
    }
}
