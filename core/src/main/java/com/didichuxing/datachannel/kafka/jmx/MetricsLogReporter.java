package com.didichuxing.datachannel.kafka.jmx;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.*;
import com.yammer.metrics.stats.Snapshot;
import org.apache.kafka.common.metrics.Gauge;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MetricsLogReporter implements MetricsReporter, Runnable, MetricProcessor<PrintStream> {

    private static final Logger log = LoggerFactory.getLogger(MetricsLogReporter.class);

    private List<KafkaMetric> metricList = Collections.synchronizedList(new ArrayList<KafkaMetric>());
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
    private Set<String> metricNameSet;

    public void configure(final Map<String, ?> configs) {}

    public void init(List<KafkaMetric> metrics) {
        metricNameSet = Collections.synchronizedSet(new HashSet<>());
        metricNameSet.add("request-rate");
        metricNameSet.add("response-rate");
        metricNameSet.add("request-fail-rate");
        metricNameSet.add("DiscovererRequestQueueSize");
        metricNameSet.add("DiscoverAvgIdlePercent");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=Metadata");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=LocalTimeMs,request=Metadata");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=Metadata");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=Metadata");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=Metadata");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=Metadata");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Metadata");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=TotalTimeMs,request=ApiVersions");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=TotalTimeMs,request=FindCoordinator");
        metricNameSet.add("kafka.network:type=RequestMetrics,name=TotalTimeMs,request=InitProducerId");
        executor.scheduleWithFixedDelay(this, 60, 60, TimeUnit.SECONDS);
    }

    public void metricChange(final KafkaMetric metric) {
        metricList.add(metric);
    }

    public void metricRemoval(KafkaMetric metric) {
        metricList.remove(metric);
    }

    public void close() {
        metricList = null;
        try {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Unable to shutdown executor gracefully", e);
        }
    }

    public void run() {
        if (metricNameSet.isEmpty()) {
            return;
        }

        try {
            Map<String, Double> resultMap = new HashMap<>();
            final Charset charset = StandardCharsets.UTF_8;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, charset.name());
            for (Map.Entry<MetricName, Metric> entry : Metrics.defaultRegistry().allMetrics().entrySet()) {

                if (metricNameSet.contains(entry.getKey().getMBeanName()) || metricNameSet.contains(entry.getKey().getName())) {
                    entry.getValue().processWith(this, entry.getKey(), ps);
                    String formatLog = new String(baos.toByteArray(), charset);
                    baos.reset();
                    log.info(formatLog);
                }
            }
            ps.close();

            for (KafkaMetric metric : metricList) {
                String name = metric.metricName().name();
                if (metricNameSet.contains(name)) {
                    resultMap.put(name, resultMap.getOrDefault(name, 0.0) + metric.value());
                }
            }

            for (Map.Entry<String, Double> entry: resultMap.entrySet()) {
                log.info("Metric name:{}, value:{}", entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            log.error("Error writing to local log, detail:", e);
        }
    }


    private String sanitizeName(org.apache.kafka.common.MetricName name) {
        StringBuilder result = new StringBuilder().append(name.group()).append('.');
        for (Map.Entry<String, String> tag : name.tags().entrySet()) {
            result.append(tag.getValue()).append('.');
        }
        return result.append(name.name()).toString().replace(' ', '_').replace("\\.", "_");
    }

    @Override
    public void processCounter(MetricName name, Counter counter, PrintStream stream) {
        stream.printf("count = %d\n", counter.count());
    }

    @Override
    public void processMeter(MetricName name, Metered meter, PrintStream stream) {
//        final String unit = abbrev(meter.rateUnit());
//        stream.printf("count = %d\n", meter.count());
//        stream.printf("mean rate = %2.2f %s/%s\n",
//                meter.meanRate(),
//                meter.eventType(),
//                unit);
//        stream.printf("1-minute rate = %2.2f %s/%s\n",
//                meter.oneMinuteRate(),
//                meter.eventType(),
//                unit);
//        stream.printf("5-minute rate = %2.2f %s/%s\n",
//                meter.fiveMinuteRate(),
//                meter.eventType(),
//                unit);
//        stream.printf("15-minute rate = %2.2f %s/%s\n",
//                meter.fifteenMinuteRate(),
//                meter.eventType(),
//                unit);

        String metricName = String.format("%s-%s", name.getType(), name.getName());
        stream.printf("Metric name:%s, value:%2.2f", metricName, meter.oneMinuteRate()*100);
    }

    @Override
    public void processHistogram(MetricName name, Histogram histogram, PrintStream stream) {
        final Snapshot snapshot = histogram.getSnapshot();
//        stream.printf("min = %2.2f\n", histogram.min());
//        stream.printf("max = %2.2f\n", histogram.max());
//        stream.printf("mean = %2.2f\n", histogram.mean());
//        stream.printf("stddev = %2.2f\n", histogram.stdDev());
//        stream.printf("median = %2.2f\n", snapshot.getMedian());
//        stream.printf("75%% <= %2.2f\n", snapshot.get75thPercentile());
//        stream.printf("95%% <= %2.2f\n", snapshot.get95thPercentile());
//        stream.printf("98%% <= %2.2f\n", snapshot.get98thPercentile());
//        stream.printf("99%% <= %2.2f\n", snapshot.get99thPercentile());
//        stream.printf("99.9%% <= %2.2f\n", snapshot.get999thPercentile());
        String oriMetricName = name.getName();
        if (oriMetricName.equals("RemoteTimeMs")) {
            oriMetricName = "LocalQueueTimeMs";
        } else if (oriMetricName.equals("ThrottleTimeMs")) {
            oriMetricName = "TransmitTimeMs";
        }
        String metricName = String.format("%s-%s-%s-%s", name.getType(), oriMetricName, name.getScope(), "95th");
        stream.printf("Metric name:%s, value:%2.2f", metricName, snapshot.get95thPercentile());
    }

    @Override
    public void processTimer(MetricName name, com.yammer.metrics.core.Timer timer, PrintStream stream) {
        processMeter(name, timer, stream);
        final String durationUnit = abbrev(timer.durationUnit());
        final Snapshot snapshot = timer.getSnapshot();
        stream.printf("min = %2.2f%s\n", timer.min(), durationUnit);
        stream.printf("max = %2.2f%s\n", timer.max(), durationUnit);
        stream.printf("mean = %2.2f%s\n", timer.mean(), durationUnit);
        stream.printf("stddev = %2.2f%s\n", timer.stdDev(), durationUnit);
        stream.printf("median = %2.2f%s\n", snapshot.getMedian(), durationUnit);
        stream.printf("75%% <= %2.2f%s\n", snapshot.get75thPercentile(), durationUnit);
        stream.printf("95%% <= %2.2f%s\n", snapshot.get95thPercentile(), durationUnit);
        stream.printf("98%% <= %2.2f%s\n", snapshot.get98thPercentile(), durationUnit);
        stream.printf("99%% <= %2.2f%s\n", snapshot.get99thPercentile(), durationUnit);
        stream.printf("99.9%% <= %2.2f%s\n", snapshot.get999thPercentile(), durationUnit);
    }

    @Override
    public void processGauge(MetricName name, com.yammer.metrics.core.Gauge<?> gauge, PrintStream context) throws Exception {
        String metricName = String.format("%s-%s-%s", name.getType(), name.getName(), name.getScope());
        context.printf("Metric name:%s, value:%s", metricName, gauge.value().toString());
    }

    private String abbrev(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "us";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "m";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new IllegalArgumentException("Unrecognized TimeUnit: " + unit);
        }
    }

}