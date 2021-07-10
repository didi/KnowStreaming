package com.xiaojukeji.kafka.manager.web.metrics;

import com.codahale.metrics.*;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.utils.factory.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zengqiao
 * @date 20/7/31
 */
@Component
public class MetricsRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogConstant.API_METRICS_LOGGER);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");

    private static final ScheduledExecutorService PRINT_API_METRICS_THREAD =
            Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("PrintApiMetricsThread"));

    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    @PostConstruct
    public void init() {
        PRINT_API_METRICS_THREAD.scheduleAtFixedRate(() -> {
            try {
                printTimerMetrics();
            } catch (Throwable ex) {

            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public static Timer newTimer(String metricName) {
        return METRIC_REGISTRY.timer(metricName);
    }

    synchronized private void printTimerMetrics() {
        SortedMap<String, Timer> timerMap = METRIC_REGISTRY.getTimers();
        for (Map.Entry<String, Timer> entry: timerMap.entrySet()) {
            final Snapshot snapshot = entry.getValue().getSnapshot();
            LOGGER.info("type=TIMER, name={}-Count, value={}, unit=event",
                    entry.getKey(), entry.getValue().getCount());
            LOGGER.info("type=TIMER, name={}-MeanRate, value={}, unit=qps",
                    entry.getKey(), DECIMAL_FORMAT.format(entry.getValue().getMeanRate()));
            LOGGER.info("type=TIMER, name={}-M1Rate, value={}, unit=qps",
                    entry.getKey(), DECIMAL_FORMAT.format(entry.getValue().getOneMinuteRate()));
            LOGGER.info("type=TIMER, name={}-M5Rate, value={}, unit=qps",
                    entry.getKey(), DECIMAL_FORMAT.format(entry.getValue().getFiveMinuteRate()));
            LOGGER.info("type=TIMER, name={}-M15Rate, value={}, unit=qps",
                    entry.getKey(), DECIMAL_FORMAT.format(entry.getValue().getFifteenMinuteRate()));
            LOGGER.info("type=TIMER, name={}-Min, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.getMin()));
            LOGGER.info("type=TIMER, name={}-Max, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.getMax()));
            LOGGER.info("type=TIMER, name={}-Mean, value={}, unit=ms",
                    entry.getKey(),  convertUnitFromNs2Ms(snapshot.getMean()));
            LOGGER.info("type=TIMER, name={}-StdDev, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.getStdDev()));
            LOGGER.info("type=TIMER, name={}-50thPercentile, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.getMedian()));
            LOGGER.info("type=TIMER, name={}-75thPercentile, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.get75thPercentile()));
            LOGGER.info("type=TIMER, name={}-95thPercentile, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.get95thPercentile()));
            LOGGER.info("type=TIMER, name={}-98thPercentile, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.get98thPercentile()));
            LOGGER.info("type=TIMER, name={}-99thPercentile, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.get99thPercentile()));
            LOGGER.info("type=TIMER, name={}-999thPercentile, value={}, unit=ms",
                    entry.getKey(), convertUnitFromNs2Ms(snapshot.get999thPercentile()));
        }
    }

    private String convertUnitFromNs2Ms(double value) {
        return DECIMAL_FORMAT.format(value / 1000000);
    }
}