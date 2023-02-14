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

import com.yammer.metrics.core.MetricName;
import org.apache.kafka.common.TopicPartition;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DiskTopicPartitionMetrics {

    private static final String group = "kafka.server";
    private static final String type = "DiskMetrics";

    public static ConcurrentMap<String, DiskTopicPartitionMetrics> allMetrics = new ConcurrentHashMap<>();

    private final String ReplicRealDiskReadPerSec = "ReplicRealDiskReadPerSec";
    private final String ConsumerRealDiskReadPerSec = "ConsumerRealDiskReadPerSec";
    private final String ReassignmentRealDiskReadPerSec = "ReassignmentRealDiskReadPerSec";
    private final String ReplicDiskReadPerSec = "ReplicDiskReadPerSec";
    private final String ConsumerDiskReadPerSec = "ConsumerDiskReadPerSec";
    private final String ReassignmentDiskReadPerSec = "ReassignmentDiskReadPerSec";
    private final String ReplicDiskWritePerSec = "ReplicDiskWritePerSec";
    private final String ProducerDiskWritePerSec = "ProducerDiskWritePerSec";
    private final String ReassignmentDiskWritePerSec = "ReassignmentDiskWritePerSec";

    private final  ExMeter replicaRealDiskReadtRate;
    private final  ExMeter consumerRealDiskReadtRate;
    private final  ExMeter reassignmentRealDiskReadtRate;
    private final  ExMeter replicaDiskReadtRate;
    private final  ExMeter consumerDiskReadtRate;
    private final  ExMeter reassignmentDiskReadtRate;
    private final  ExMeter replicaDiskWritetRate;
    private final  ExMeter producerDiskWriteRate;
    private final  ExMeter reassignmentDiskWriteRate;

    private final String disk;
    private final TopicPartition topicPartition;

    public DiskTopicPartitionMetrics(String disk, TopicPartition topicPartition) {
        this.disk = disk;
        this.topicPartition = topicPartition;

        Map<String, String> metricsTags = new LinkedHashMap<>();
        metricsTags.put("disk", disk);
        if (topicPartition != null) {
            metricsTags.put("topic", topicPartition.toString());
        }

        MetricName metricName = KafkaExMetrics.createMetricsName(group, type, ReplicRealDiskReadPerSec, metricsTags);
        replicaRealDiskReadtRate = KafkaExMetrics.createMeter(metricName, "replica read bytes from disk no in page cache", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ConsumerRealDiskReadPerSec, metricsTags);
        consumerRealDiskReadtRate = KafkaExMetrics.createMeter(metricName, "consumer read bytes from disk no in page cache", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ReassignmentRealDiskReadPerSec, metricsTags);
        reassignmentRealDiskReadtRate = KafkaExMetrics.createMeter(metricName, "reassignment read bytes from disk no in page cache ", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ReplicDiskReadPerSec, metricsTags);
        replicaDiskReadtRate = KafkaExMetrics.createMeter(metricName, "replica read bytes from disk", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ConsumerDiskReadPerSec, metricsTags);
        consumerDiskReadtRate = KafkaExMetrics.createMeter(metricName, "consumer read bytes from disk", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ReassignmentDiskReadPerSec, metricsTags);
        reassignmentDiskReadtRate = KafkaExMetrics.createMeter(metricName, "reassignment read bytes from disk", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ReplicDiskWritePerSec, metricsTags);
        replicaDiskWritetRate= KafkaExMetrics.createMeter(metricName, "replica write bytes to disk", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ProducerDiskWritePerSec, metricsTags);
        producerDiskWriteRate = KafkaExMetrics.createMeter(metricName, "producer write bytes to disk", TimeUnit.SECONDS);

        metricName = KafkaExMetrics.createMetricsName(group, type, ReassignmentDiskWritePerSec, metricsTags);
        reassignmentDiskWriteRate = KafkaExMetrics.createMeter(metricName, "reassignment write bytes to disk", TimeUnit.SECONDS);
    }

    public void close() {
        KafkaExMetrics.removeMetrics(replicaRealDiskReadtRate);
        KafkaExMetrics.removeMetrics(consumerRealDiskReadtRate);
        KafkaExMetrics.removeMetrics(reassignmentRealDiskReadtRate);
        KafkaExMetrics.removeMetrics(replicaDiskReadtRate);
        KafkaExMetrics.removeMetrics(consumerDiskReadtRate);
        KafkaExMetrics.removeMetrics(reassignmentDiskReadtRate);
        KafkaExMetrics.removeMetrics(replicaDiskWritetRate);
        KafkaExMetrics.removeMetrics(producerDiskWriteRate);
        KafkaExMetrics.removeMetrics(reassignmentDiskWriteRate);
    }

    public static DiskTopicPartitionMetrics getDiskTopicMetrics(String disk) {
        return allMetrics.computeIfAbsent(disk, (k) -> new DiskTopicPartitionMetrics(disk, null));
    }

    public static DiskTopicPartitionMetrics getDiskTopicMetrics(String disk, TopicPartition topicPartition) {
        String key = topicPartition + "#" + disk;
        return allMetrics.computeIfAbsent(key, (k) -> new DiskTopicPartitionMetrics(disk, topicPartition));
    }

    /*
        role : 0 consumer, 1 replica, -1 reassignment
     */
    public void updateReadMetrics(TopicPartition topicPartition, int size, int role, boolean cached) {
        DiskTopicPartitionMetrics topicPartitionMetrics = KafkaExMetrics.diskTopicPartitionMetrics(this.disk, topicPartition);
        switch (role) {
            case -1:
                reassignmentDiskReadtRate.meter().mark(size);
                if (!cached) reassignmentRealDiskReadtRate.meter().mark(size);
                if (null != topicPartitionMetrics) {
                    topicPartitionMetrics.reassignmentDiskReadtRate.meter().mark(size);
                    if (!cached) topicPartitionMetrics.reassignmentRealDiskReadtRate.meter().mark(size);
                }
                break;

            case 0:
                consumerDiskReadtRate.meter().mark(size);
                if (!cached) consumerRealDiskReadtRate.meter().mark(size);
                if (null != topicPartitionMetrics) {
                    topicPartitionMetrics.consumerDiskReadtRate.meter().mark(size);
                    if (!cached) topicPartitionMetrics.consumerRealDiskReadtRate.meter().mark(size);
                }
                break;

            case 1:
                replicaDiskReadtRate.meter().mark(size);
                if (!cached) replicaRealDiskReadtRate.meter().mark(size);
                if (null != topicPartitionMetrics) {
                    topicPartitionMetrics.replicaDiskReadtRate.meter().mark(size);
                    if (!cached) topicPartitionMetrics.replicaRealDiskReadtRate.meter().mark(size);
                }
                break;

            default:
        }
    }

    public void updateWriteMetrics(TopicPartition topicPartition, int size, int role) {
        DiskTopicPartitionMetrics topicPartitionMetrics = KafkaExMetrics.diskTopicPartitionMetrics(this.disk, topicPartition);
        switch (role) {
            case -1:
                reassignmentDiskWriteRate.meter().mark(size);
                if (null != topicPartitionMetrics) {
                    topicPartitionMetrics.reassignmentDiskWriteRate.meter().mark(size);
                }
                break;

            case 0:
                producerDiskWriteRate.meter().mark(size);
                if (null != topicPartitionMetrics) {
                    topicPartitionMetrics.producerDiskWriteRate.meter().mark(size);
                }
                break;

            case 1:
                replicaDiskWritetRate.meter().mark(size);
                if (null != topicPartitionMetrics) {
                    topicPartitionMetrics.replicaDiskWritetRate.meter().mark(size);
                }
                break;

            default:
        }
    }

    public static void removeMetrics(String disk, TopicPartition topicPartition) {
        if (topicPartition == null) {
            DiskTopicPartitionMetrics metrics = allMetrics.remove(disk);
            if (null != metrics) {
                metrics.close();
            }
        }
        String key = topicPartition + "#" + disk;
        DiskTopicPartitionMetrics metrics = allMetrics.remove(key);
        if (null != metrics) {
            metrics.close();
        }
    }

    public static void removeMetrics(String topic) {
        var itr = allMetrics.entrySet().iterator();
        while(itr.hasNext()) {
            var entry = itr.next();
            String name = entry.getKey();
            if (name.startsWith(topic.concat("-"))) {
                DiskTopicPartitionMetrics metrics = entry.getValue();
                metrics.close();
                itr.remove();
            }
        }
    }

    public static String getType() {
        return type;
    }
}
