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

import com.didichuxing.datachannel.kafka.server.OSMetrics;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.MetricName;
import joptsimple.internal.Strings;
import kafka.network.RequestChannel;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaExMetrics implements Configurable {

    private static final Logger log = LoggerFactory.getLogger(KafkaExMetrics.class);

    private static ConcurrentHashMap<String, Rule> metricsRules = new ConcurrentHashMap<>();
    private static OSMetrics osMetrics;

    public static void init(org.apache.kafka.common.metrics.Metrics metrics, boolean enableAll,
                            ScheduledExecutorService executorService) throws Exception {
        osMetrics = new OSMetrics(metrics, executorService);
        if (enableAll) {
            new KafkaExMetrics().configure(Map.of("*", "true"));
        }
    }

    public static MetricName createMetricsName(String group, String type, String name, Map<String, String> tags) {
        StringBuilder builder = new StringBuilder();
        builder.append(group);
        builder.append(":type=");
        builder.append(type);
        builder.append(",name=");
        builder.append(name);

        Map<String, String> sorted = new TreeMap<String, String>(tags);
        sorted.forEach((k, v) -> {
            builder.append(String.format(",%s=%s", k, v.replaceAll("[,=]", ".")));
        });
        return new MetricName(group, type, name, null, builder.toString());
    }

    public static TopicRequestMetrics fetchRequestMetrics(String topic) {
        boolean matched = matchRules(TopicRequestMetrics.getType(), topic);
        if (!matched) return null;
        return TopicRequestMetrics.getFetchRequestMetrics(topic);
    }

    public static TopicRequestMetrics produceRequestMetrics(String topic) {
        boolean matched = matchRules(TopicRequestMetrics.getType(), topic);
        if (!matched) return null;
        return TopicRequestMetrics.getProduceRequestMetrics(topic);
    }

    public static AppIdTopicMetrics appIdTopicMetrics(String topic) {
        boolean matched = matchRules(AppIdTopicMetrics.getType(), topic);
        if (!matched) return null;
        String appId = RequestChannel.currentSession().getUsername();
        return AppIdTopicMetrics.getAppIdTopicMetrics(topic, appId);
    }

    public static AppIdHostTopicMetrics appIdHostTopicMetrics(String topic) {
        boolean matched = matchRules(AppIdHostTopicMetrics.getType(), topic);
        if (!matched) return null;
        String appId = RequestChannel.currentSession().getUsername();
        String host = RequestChannel.currentSession().getHostAddress();
        return AppIdHostTopicMetrics.getClientMetrics(topic, appId, host);
    }

    public static DiskTopicPartitionMetrics diskTopicPartitionMetrics(String disk) {
        return DiskTopicPartitionMetrics.getDiskTopicMetrics(disk);
    }

    public static DiskTopicPartitionMetrics diskTopicPartitionMetrics(String disk, TopicPartition topicPartition) {
        boolean matched = matchRules(DiskTopicPartitionMetrics.getType(), topicPartition.topic());
        if (!matched) return null;
        return DiskTopicPartitionMetrics.getDiskTopicMetrics(disk, topicPartition);
    }

    public static ExMeter createMeter(MetricName name, String describe, TimeUnit timeUnit) {
        return new ExMeter(name, describe ,timeUnit);
    }

    public static ExHistogram createHistogram(MetricName name, boolean biased) {
        return new ExHistogram(name, biased);
    }

    public static <T> ExGauge<T> createGauge(MetricName name, Gauge<T> gauge) {
        return new ExGauge<T>(name, gauge);
    }

    @Override
    public void configure(Map<String, ?> configs) {
        if (configs.isEmpty()) return;
        StringBuilder builder = new StringBuilder();
        ConcurrentHashMap<String, Rule> configRules = new ConcurrentHashMap<>();
        for (var entry: configs.entrySet()) {
            String name = entry.getKey();
            boolean allow = false;
            try {
                allow = Boolean.parseBoolean((String) entry.getValue());
            }catch(Exception ignored){
            }
            Rule rule = new Rule(allow, false);
            configRules.put(name, rule);
            builder.append(String.format("\n\t%s=%b", name, allow));
        }
        log.info("KafkaExMetrics config is:{}", builder.toString());

        ConcurrentHashMap<String, Rule> oldiMetricsRules = metricsRules;
        metricsRules = configRules;
        for (var entry : oldiMetricsRules.entrySet()) {
            String[] keys = entry.getKey().split("\\.");
            if (keys.length == 1 || keys[1].equals("*")) {
                continue;
            }
            boolean matched = matchRules(keys[0], keys[1]);
            if (!matched) {
                switch (keys[0]) {
                    case "TopicRequestMetrics":
                        TopicRequestMetrics.removeRequestMetrics(keys[1]);
                        break;
                    case "AppIdTopicMetrics":
                        AppIdTopicMetrics.removeAppIdTopicMetrics(keys[1]);
                        break;
                    case "ClientRequestMetrics":
                        AppIdHostTopicMetrics.removeClientMetrics(keys[1]);
                        break;
                    case "DiskMetrics":
                        DiskTopicPartitionMetrics.removeMetrics(keys[1]);
                        break;
                }
            }
        }
    }

    public static void removeMetrics(ExMetrics metrics) {
        Metrics.defaultRegistry().removeMetric(metrics.name);
    }

    private static boolean matchRules(String type, String tag) {
        assert !Strings.isNullOrEmpty(type);
        assert !Strings.isNullOrEmpty(tag);
        String key = type + "." + tag;
        Rule rule = metricsRules.computeIfAbsent(key, (k) -> new Rule(false, true));
        Rule.E status = rule.check();
        if (status != Rule.E.Continue) {
            return status == Rule.E.Allow;
        }

        String matchKey = type + ".*";
        Rule parentRule = metricsRules.computeIfAbsent(matchKey, (k) -> new Rule(false, true));
        status = parentRule.check();
        if (status != Rule.E.Continue) {
            boolean result = status == Rule.E.Allow;
            rule.setAllow(result);
            return result;
        }

        Rule rootRule = metricsRules.computeIfAbsent("*", (k) -> new Rule(false, false));
        status = rootRule.check();
        boolean result = status == Rule.E.Allow;
        rule.setAllow(result);
        parentRule.setAllow(result);
        return result;
    }

    static class Rule {
        enum E{
            Allow,
            Deny,
            Continue
        }
        private long timestamp;
        private final boolean shadow;
        private boolean allow;

        public Rule(boolean allow, boolean shadow) {
            this.timestamp = 0;
            this.allow = allow;
            this.shadow = shadow;
        }

        E check() {
            E result;
            if (shadow && System.currentTimeMillis() - timestamp > 100000) {
                result = E.Continue;
            } else {
                result = allow?E.Allow:E.Deny;
            }
            return result;
        }

        public void setAllow(boolean allow) {
            this.allow = allow;
            timestamp = System.currentTimeMillis();
        }
    }
}


