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

import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.MetricName;
import kafka.network.RequestMetrics;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.Errors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class TopicRequestMetrics {

    private static final String group = "kafka.server";
    private static final String type = "TopicRequestMetrics";

    private static ConcurrentMap<String, TopicRequestMetrics> allProduceMetrics = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, TopicRequestMetrics> allFetchMetrics = new ConcurrentHashMap<>();

    // request rate
    private final ConcurrentHashMap<Integer, ExMeter> requestRateInternal = new ConcurrentHashMap<>();
    // time a request spent in a request queue
    private final ExHistogram requestQueueTimeHist;
    // time a request takes to be processed at the local broker
    private final ExHistogram localTimeHist;
    // time a request takes to wait on remote brokers (currently only relevant to fetch and produce requests)
    private final ExHistogram remoteTimeHist;
    // time a request is throttled (only relevant to fetch and produce requests)
    private final ExHistogram throttleTimeHist;
    // time a response spent in a response queue
    private final ExHistogram responseQueueTimeHist;
    // time to send the response to the requester
    private final ExHistogram responseSendTimeHist;
    // time to send the response to the requester
    private final ExHistogram totalTimeHist;
    // request size in bytes
    private final ExHistogram requestBytesHist;
    // response size in bytes
    private final ExHistogram responseBytesHist;
    // time for message conversions (only relevant to fetch and produce requests)
    private final ExHistogram messageConversionsTimeHist;
    // Temporary memory allocated for processing request (only populated for fetch and produce requests)
    // This shows the memory allocated for compression/conversions excluding the actual request size
    private final ExHistogram tempMemoryBytesHist;

    private final ConcurrentHashMap<String, ExMeter> compressionMeters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Errors, ExMeter> errorMeters = new ConcurrentHashMap<>();

    private final String topic;
    private final String requestType;

    public TopicRequestMetrics(String topic, String requestType) {
        this.topic = topic;
        this.requestType = requestType;

        Map<String, String> metricsTags = new LinkedHashMap<>();
        metricsTags.put("request", requestType);
        metricsTags.put("topic", topic);

        MetricName metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.RequestQueueTimeMs(), metricsTags);
        requestQueueTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.LocalTimeMs(), metricsTags);
        localTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.RemoteTimeMs(), metricsTags);
        remoteTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.ThrottleTimeMs(), metricsTags);
        throttleTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.RequestQueueTimeMs(), metricsTags);
        responseQueueTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.ResponseSendTimeMs(), metricsTags);
        responseSendTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.TotalTimeMs(), metricsTags);
        totalTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.RequestBytes(), metricsTags);
        requestBytesHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.ResponseBytes(), metricsTags);
        responseBytesHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.MessageConversionsTimeMs(), metricsTags);
        messageConversionsTimeHist = KafkaExMetrics.createHistogram(metricName, true);

        metricName = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.TemporaryMemoryBytes(), metricsTags);
        tempMemoryBytesHist = KafkaExMetrics.createHistogram(metricName, true);
    }

    public void close() {
        requestRateInternal.forEach((k,v)-> {KafkaExMetrics.removeMetrics(v);});
        requestRateInternal.clear();
        KafkaExMetrics.removeMetrics(requestQueueTimeHist);
        KafkaExMetrics.removeMetrics(localTimeHist);
        KafkaExMetrics.removeMetrics(remoteTimeHist);
        KafkaExMetrics.removeMetrics(throttleTimeHist);
        KafkaExMetrics.removeMetrics(responseQueueTimeHist);
        KafkaExMetrics.removeMetrics(responseSendTimeHist);
        KafkaExMetrics.removeMetrics(totalTimeHist);
        KafkaExMetrics.removeMetrics(requestBytesHist);
        KafkaExMetrics.removeMetrics(responseBytesHist);
        KafkaExMetrics.removeMetrics(messageConversionsTimeHist);
        KafkaExMetrics.removeMetrics(tempMemoryBytesHist);
        compressionMeters.forEach((k,v)-> {KafkaExMetrics.removeMetrics(v);});
        compressionMeters.clear();
        errorMeters.forEach((k,v)-> {KafkaExMetrics.removeMetrics(v);});
        errorMeters.clear();
    }

    public ExMeter requestRate(int version) {
        return requestRateInternal.computeIfAbsent(version, (k)-> {
            Map<String, String> metricsTags = new LinkedHashMap<>();
            metricsTags.put("request", requestType);
            metricsTags.put("topic", topic);
            metricsTags.put("version", String.valueOf(version));
            MetricName name = KafkaExMetrics.createMetricsName(group, type, RequestMetrics.RequestsPerSec(), metricsTags);
            return KafkaExMetrics.createMeter(name, "requests", TimeUnit.SECONDS);
        });
    }

    ExMeter compressionMetrics(String compression) {
        return compressionMeters.computeIfAbsent(compression, (k)->{
            Map<String, String> metricsTags = new LinkedHashMap<>();
            metricsTags.put("request", requestType);
            metricsTags.put("topic", topic);
            metricsTags.put("compression", compression);
            MetricName metricName = KafkaExMetrics.createMetricsName(group, type, "CompressionPerSec", metricsTags);
            return KafkaExMetrics.createMeter(metricName, "record compression", TimeUnit.SECONDS);
        });
    }

    ExMeter errorMetrics(Errors error, String name) {
        return errorMeters.computeIfAbsent(error, (k)->{
            Map<String, String> metricsTags = new LinkedHashMap<>();
            metricsTags.put("request", requestType);
            metricsTags.put("topic", topic);
            metricsTags.put("error", name);
            MetricName metricName = KafkaExMetrics.createMetricsName(group, type, "ErrorPerSec", metricsTags);
            return KafkaExMetrics.createMeter(metricName, "errors", TimeUnit.SECONDS);
        });
    }

    public void markError(Errors errors, String name) {
        errorMetrics(errors, name).meter().mark(1);
    }

    public void markRequest(int verrsion) {
        requestRate(verrsion).meter().mark();
        AppIdTopicMetrics appIdTopicMetrics = KafkaExMetrics.appIdTopicMetrics(topic);
        if (null != appIdTopicMetrics) {
            if (requestType.equals(ApiKeys.PRODUCE.name)) {
                appIdTopicMetrics.markProduceApiVersion(verrsion);
            }else {
                appIdTopicMetrics.markFetchApiVersion(verrsion);
            }
        }
        AppIdHostTopicMetrics appIdHostTopicMetrics = KafkaExMetrics.appIdHostTopicMetrics(topic);
        if (null != appIdHostTopicMetrics) {
            appIdHostTopicMetrics.netRequestInRate().mark();
        }
    }


    public void markCompress(String compression) {
        compressionMetrics(compression).meter().mark();
        AppIdTopicMetrics appIdTopicMetrics = KafkaExMetrics.appIdTopicMetrics(topic);
        if (null != appIdTopicMetrics) {
            appIdTopicMetrics.markRecordCompression(compression);
        }
    }

    public static TopicRequestMetrics getProduceRequestMetrics(String topic) {
        return allProduceMetrics.computeIfAbsent(topic, (k)->new TopicRequestMetrics(topic, ApiKeys.PRODUCE.name));
    }

    public static TopicRequestMetrics getFetchRequestMetrics(String topic) {
        return allFetchMetrics.computeIfAbsent(topic, (k)->new TopicRequestMetrics(topic, ApiKeys.FETCH.name));
    }

    public static void removeRequestMetrics(String topic) {
        TopicRequestMetrics metrics = allProduceMetrics.remove(topic);
        if (metrics != null) {
            metrics.close();
        }

        metrics = allFetchMetrics.remove(topic);
        if (metrics != null) {
            metrics.close();
        }
    }

    public Histogram requestQueueTimeHist() {
        return requestQueueTimeHist.histogram();
    }

    public Histogram localTimeHist() {
        return localTimeHist.histogram();
    }

    public Histogram remoteTimeHist() {
        return remoteTimeHist.histogram();
    }

    public Histogram throttleTimeHist() {
        return throttleTimeHist.histogram();
    }

    public Histogram responseQueueTimeHist() {
        return responseQueueTimeHist.histogram();
    }

    public Histogram responseSendTimeHist() {
        return responseSendTimeHist.histogram();
    }

    public Histogram totalTimeHist() {
        return totalTimeHist.histogram();
    }

    public Histogram requestBytesHist() {
        return requestBytesHist.histogram();
    }

    public Histogram responseBytesHist() {
        return responseBytesHist.histogram();
    }

    public Histogram messageConversionsTimeHist() {
        return messageConversionsTimeHist.histogram();
    }

    public Histogram tempMemoryBytesHist() {
        return tempMemoryBytesHist.histogram();
    }

    static public String getType() {
        return type;
    }
}
