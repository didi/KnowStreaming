/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.metrics;

import com.didichuxing.datachannel.kafka.util.KafkaUtils;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;
import kafka.server.BrokerTopicStats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class AppIdTopicMetrics {

    private static final String group = "kafka.server";
    private static final String type = "AppIdTopicMetrics";

    public static ConcurrentMap<String, AppIdTopicMetrics> allMetrics = new ConcurrentHashMap<>();

    private final ExMeter messagesInRate;
    private final ExMeter bytesInRate;
    private final ExMeter bytesOutRate;
    private final ExMeter bytesRejectedRate;
    private final ExMeter totalProduceRequestsRate;
    private final ExMeter totalFetchRequestsRate;
    private final ExMeter failedProduceRequestsRate;
    private final ExMeter failedFetchRequestsRate;
    private final ExMeter produceMessageConversionsRate;
    private final ExMeter fetchMessageConversionsRate;

    private final ExGauge<String> produceApiVersionGauge;
    private final ExGauge<String> fetchApiVersionGauge;
    private final ExGauge<String> recordCompressionGauge;

    private String produceApiVersion = "";
    private String fetchApiVersion = "";
    private String recordCompression = "";

    private final String topic;

    public AppIdTopicMetrics(String topic, String appId) {
        this.topic = topic;

        Map<String, String> metricsTags = new LinkedHashMap<>();
        metricsTags.put("topic", topic);
        metricsTags.put("appId", appId);

        MetricName metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.MessagesInPerSec(), metricsTags);
        messagesInRate = KafkaExMetrics.createMeter(metricName, "message", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.BytesInPerSec(), metricsTags);
        bytesInRate= KafkaExMetrics.createMeter(metricName, "bytes in", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.BytesOutPerSec(), metricsTags);
        bytesOutRate= KafkaExMetrics.createMeter(metricName, "bytes out", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.BytesRejectedPerSec(), metricsTags);
        bytesRejectedRate = KafkaExMetrics.createMeter(metricName, "bytes rejected", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.TotalProduceRequestsPerSec(), metricsTags);
        totalProduceRequestsRate = KafkaExMetrics.createMeter(metricName, "requests", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.TotalFetchRequestsPerSec(), metricsTags);
        totalFetchRequestsRate= KafkaExMetrics.createMeter(metricName, "requests", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.FailedProduceRequestsPerSec(), metricsTags);
        failedProduceRequestsRate = KafkaExMetrics.createMeter(metricName, "requests", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.FailedFetchRequestsPerSec(), metricsTags);
        failedFetchRequestsRate= KafkaExMetrics.createMeter(metricName, "requests", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.ProduceMessageConversionsPerSec(), metricsTags);
        produceMessageConversionsRate = KafkaExMetrics.createMeter(metricName, "requests", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, BrokerTopicStats.FetchMessageConversionsPerSec(), metricsTags);
        fetchMessageConversionsRate = KafkaExMetrics.createMeter(metricName, "requests", TimeUnit.SECONDS);


        metricName = KafkaExMetrics.createMetricsName(group, type, "ProduceApiVer", metricsTags);
        produceApiVersionGauge = KafkaExMetrics.createGauge(metricName, new Gauge<>() {
            @Override
            public String value() {
                return produceApiVersion;
            }
        });

        metricName = KafkaExMetrics.createMetricsName(group, type, "FetchApiVer", metricsTags);
        fetchApiVersionGauge = KafkaExMetrics.createGauge(metricName, new Gauge<>() {
            @Override
            public String value() {
                return fetchApiVersion;
            }
        });

        metricName = KafkaExMetrics.createMetricsName(group, type, "RecordCompression", metricsTags);
        recordCompressionGauge = KafkaExMetrics.createGauge(metricName, new Gauge<>() {
            @Override
            public String value() {
                return recordCompression;
            }
        });

    }

    public void close() {
        KafkaExMetrics.removeMetrics(messagesInRate);
        KafkaExMetrics.removeMetrics(bytesInRate);
        KafkaExMetrics.removeMetrics(bytesOutRate);
        KafkaExMetrics.removeMetrics(bytesRejectedRate);
        KafkaExMetrics.removeMetrics(totalProduceRequestsRate);
        KafkaExMetrics.removeMetrics(totalFetchRequestsRate);
        KafkaExMetrics.removeMetrics(failedProduceRequestsRate);
        KafkaExMetrics.removeMetrics(failedFetchRequestsRate);
        KafkaExMetrics.removeMetrics(produceMessageConversionsRate);
        KafkaExMetrics.removeMetrics(fetchMessageConversionsRate);
        KafkaExMetrics.removeMetrics(produceApiVersionGauge);
        KafkaExMetrics.removeMetrics(fetchApiVersionGauge);
        KafkaExMetrics.removeMetrics(recordCompressionGauge);
    }

    public static AppIdTopicMetrics getAppIdTopicMetrics(String topic, String appId) {
        String key = topic + "#" + appId;
        return allMetrics.computeIfAbsent(key, (k)->new AppIdTopicMetrics(topic, appId));
    }

    public static void removeAppIdTopicMetrics(String topic, String appId) {
        String key = topic + "#" + appId;
        AppIdTopicMetrics metrics = allMetrics.remove(key);
        metrics.close();
    }

    public static void removeAppIdTopicMetrics(String topic) {
        var itr = allMetrics.entrySet().iterator();
        while(itr.hasNext()) {
            var entry = itr.next();
            String name = entry.getKey();
            if (name.startsWith(topic.concat("#"))) {
                AppIdTopicMetrics metrics = entry.getValue();
                metrics.close();
                itr.remove();
            }
        }
    }

    public void markMessagesInRate(long size) {
        messagesInRate.meter().mark(size);
    }

    public void markBytesInRate(long size) {
        bytesInRate.meter().mark(size);
        AppIdHostTopicMetrics appIdHostTopicMetrics = KafkaExMetrics.appIdHostTopicMetrics(topic);
        if (null != appIdHostTopicMetrics) {
            appIdHostTopicMetrics.produceRequestBytesInRate().mark(size);
        }
    }

    public void markBytesOutRate(long size) {
        bytesOutRate.meter().mark(size);
        AppIdHostTopicMetrics appIdHostTopicMetrics = KafkaExMetrics.appIdHostTopicMetrics(topic);
        if (null != appIdHostTopicMetrics) {
            appIdHostTopicMetrics.fetchRequestBytesOutRate().mark(size);
        }
    }

    public void markBytesRejectedRate(long size) {
        bytesRejectedRate.meter().mark(size);
    }

    public void markTotalProduceRequestsRate() {
        totalProduceRequestsRate.meter().mark();
        AppIdHostTopicMetrics appIdHostTopicMetrics = KafkaExMetrics.appIdHostTopicMetrics(topic);
        if (null != appIdHostTopicMetrics) {
            appIdHostTopicMetrics.produceRequestInRate().mark();
        }
    }

    public void markTotalFetchRequestsRate() {
        totalFetchRequestsRate.meter().mark();
        AppIdHostTopicMetrics appIdHostTopicMetrics = KafkaExMetrics.appIdHostTopicMetrics(topic);
        if (null != appIdHostTopicMetrics) {
            appIdHostTopicMetrics.fetchRequestInRate().mark();
        }
    }

    public void markFailedProduceRequestsRate() {
        failedProduceRequestsRate.meter().mark();
    }

    public void markFailedFetchRequestsRate() {
        failedFetchRequestsRate.meter().mark();
    }

    public Meter produceMessageConversionsRate() {
        return produceMessageConversionsRate.meter();
    }

    public Meter fetchMessageConversionsRate() {
        return fetchMessageConversionsRate.meter();
    }

    public void markProduceApiVersion(int version) {
        this.produceApiVersion = KafkaUtils.apiVersionToKafkaVersion(0, version);
        this.produceApiVersionGauge.mark();
    }

    public void markFetchApiVersion(int version) {
        this.fetchApiVersion = KafkaUtils.apiVersionToKafkaVersion(1, version);
        this.fetchApiVersionGauge.mark();
    }

    public void markRecordCompression(String recordCompression) {
        this.recordCompression = recordCompression;
        this.recordCompressionGauge.mark();
    }

    public static String getType() {
        return type;
    }
}
