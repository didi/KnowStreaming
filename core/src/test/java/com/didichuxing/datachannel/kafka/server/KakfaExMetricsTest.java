package com.didichuxing.datachannel.kafka.server;

import com.didichuxing.datachannel.kafka.metrics.DiskTopicPartitionMetrics;
import com.didichuxing.datachannel.kafka.metrics.ExMetrics;
import com.didichuxing.datachannel.kafka.metrics.KafkaExMetrics;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.MetricName;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class KakfaExMetricsTest {

    protected static final Logger log = LoggerFactory.getLogger(KakfaExMetricsTest.class);
    private final String testTopic = "test_0";

    @Test
    public void testKafkaExMetricsConfig() throws Exception {
        var kafkaExMetrics = new KafkaExMetrics();
        TopicPartition tp = new TopicPartition(testTopic, 0);
        kafkaExMetrics.configure(Map.of("*","true"));
        DiskTopicPartitionMetrics metrics = KafkaExMetrics.diskTopicPartitionMetrics("disk1s");
        metrics.updateWriteMetrics(tp, 100, 1);
        DiskTopicPartitionMetrics metrics1 = KafkaExMetrics.diskTopicPartitionMetrics("disk1s", tp);
        var allMetrics = Metrics.defaultRegistry().allMetrics();
        var metricName = getMetricsName(metrics1, "replicaDiskWritetRate");
        assertNotNull(allMetrics.get(metricName));
        kafkaExMetrics.configure(Map.of("*","false"));
        for (int i=0; i<1; i++) {
            Thread.sleep(1000);
        }
        assertNull(allMetrics.get(metricName));

        kafkaExMetrics.configure(Map.of("DiskMetrics.*","true"));
        metrics.updateWriteMetrics(tp, 100, 1);
        assertNotNull(allMetrics.get(metricName));
    }

   private MetricName getMetricsName(DiskTopicPartitionMetrics metrics, String fieldName) {
       try {
           Field field  = null;
           field = metrics.getClass().getDeclaredField(fieldName);
           field.setAccessible(true);
           return ((ExMetrics)(field.get(metrics))).getName();
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }
}
