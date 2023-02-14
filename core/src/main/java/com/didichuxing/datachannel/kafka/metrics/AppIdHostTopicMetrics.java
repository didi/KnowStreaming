/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.kafka.metrics;

import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class AppIdHostTopicMetrics {
    public static ConcurrentMap<String, AppIdHostTopicMetrics> allClientRequestMetrics = new ConcurrentHashMap<>();

    private static final String group = "kafka.server";
    private static final String type = "ClientRequestMetrics";

    private final String NetRequestBytesPerSec = "NetRequestPerSecnd";
    private final String ProduceRequestPerSec = "ProduceRequestPerSec";
    private final String ProduceRequestBytesPerSec = "ProduceRequestBytesPerSec";
    private final String FetchRequestPerSec = "FetchRequestPerSec";
    private final String FetchRequestBytesPerSec = "FetchRequestBytesPerSec";

    private final ExMeter netRequestInRate;
    private final ExMeter produceRequestInRate;
    private final ExMeter produceRequestBytesInRate;
    private final ExMeter fetchRequestInRate;
    private final ExMeter fetchRequestBytesOutRate;

    public AppIdHostTopicMetrics(String topic, String appId, String ip) {
        Map<String, String> metricsTags = new LinkedHashMap<>();
        metricsTags.put("topic", topic);
        metricsTags.put("appId", appId);
        metricsTags.put("ip", ip);

        MetricName metricName = KafkaExMetrics.createMetricsName(group, type, NetRequestBytesPerSec, metricsTags);
        netRequestInRate = KafkaExMetrics.createMeter(metricName, "net request metrics", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ProduceRequestPerSec, metricsTags);
        produceRequestInRate = KafkaExMetrics.createMeter(metricName, "produce request metrics", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ProduceRequestBytesPerSec, metricsTags);
        produceRequestBytesInRate = KafkaExMetrics.createMeter(metricName, "produce request metrics", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, FetchRequestPerSec, metricsTags);
        fetchRequestInRate = KafkaExMetrics.createMeter(metricName, "fetch request metrics", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, FetchRequestBytesPerSec, metricsTags);
        fetchRequestBytesOutRate = KafkaExMetrics.createMeter(metricName, "fetch request metrics", TimeUnit.SECONDS);
    }

    public void close() {
        KafkaExMetrics.removeMetrics(netRequestInRate);
        KafkaExMetrics.removeMetrics(produceRequestInRate);
        KafkaExMetrics.removeMetrics(produceRequestBytesInRate);
        KafkaExMetrics.removeMetrics(fetchRequestInRate);
        KafkaExMetrics.removeMetrics(fetchRequestBytesOutRate);
    }

    public static AppIdHostTopicMetrics getClientMetrics(String topic, String appId, String ip) {
        String key = topic + "#" + appId + "#" + ip;
        return allClientRequestMetrics.computeIfAbsent(key, (k) -> new AppIdHostTopicMetrics(topic, appId, ip));
    }

    public static void removeClientMetrics(String topic, String appId, String ip) {
        String key = topic + "#" + appId + "#" + ip;
        removeClientMetricsByKey(key);
    }

    public static void removeClientMetrics(String topic) {
        var itr = allClientRequestMetrics.entrySet().iterator();
        while (itr.hasNext()) {
            var entry = itr.next();
            String name = entry.getKey();
            if (name.startsWith(topic.concat("#"))) {
                AppIdHostTopicMetrics metrics = entry.getValue();
                metrics.close();
                itr.remove();
            }
        }
    }

    private static void removeClientMetricsByKey(String key) {
        AppIdHostTopicMetrics metrics = allClientRequestMetrics.remove(key);
        metrics.close();
    }

    public Meter netRequestInRate() {
        return netRequestInRate.meter();
    }

    public Meter produceRequestInRate() {
        return produceRequestInRate.meter();
    }

    public Meter produceRequestBytesInRate() {
        return produceRequestBytesInRate.meter();
    }

    public Meter fetchRequestInRate() {
        return fetchRequestInRate.meter();
    }

    public Meter fetchRequestBytesOutRate() {
        return fetchRequestBytesOutRate.meter();
    }

    public static String getType() {
        return type;
    }
}
