/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.jmx;

import com.didichuxing.datachannel.kafka.metrics.AppIdHostTopicMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JmxConfigManager {

    private static final Logger log = LoggerFactory.getLogger(JmxConfigManager.class);

    final static private int DATA_SYNC_TIME_MS = 1 * 60 * 1000;

    private Set<String> topicSet = null;
    private Set<String> metricNameSet = null;
    private JmxConfigProvider jmxConfigProvider = null;

    private DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
    private final String initTime = "23:00:00";

    private JmxConfigManager() {}

    public static JmxConfigManager getInstance() {
        return JmxConfigManagerHolder.INSTANCE;
    }

    public void start(String clusterId, ScheduledExecutorService scheduledExecutorService, String gatewayUrl) {
        jmxConfigProvider = new JmxConfigProvider(clusterId, gatewayUrl);
        topicSet = new ConcurrentSkipListSet<>();
        metricNameSet = new ConcurrentSkipListSet<>();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                getJmxConfigTopic();
            } catch (Throwable t) {
                log.error("Uncaught error from JmxConfigManager-getJmxConfigTopic: ", t);
            }
        }, DATA_SYNC_TIME_MS, DATA_SYNC_TIME_MS, TimeUnit.MILLISECONDS);

        long initDelay = getTimeMillis(initTime) - System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000;
        initDelay = initDelay > 0 ? initDelay : initDelay + oneDay;
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                clearJmxMetric();
            } catch (Throwable t) {
                log.error("Uncatch error from JmxConfigManager-clearJmxMetric: ", t);
            }
        }, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

    public Set<String> getTopicSet() {
        return topicSet;
    }

    public void addMetricName(String metricName) {
        metricNameSet.add(metricName);
    }

    public void shutdown() {
        log.info("JmxConfig Manager shutdown");
    }

    private static class JmxConfigManagerHolder {
        private static final JmxConfigManager INSTANCE = new JmxConfigManager();
    }

    private void getJmxConfigTopic() {
        try {
            String jmxConfigTopic = jmxConfigProvider.fetchData();
            if (jmxConfigTopic == null || jmxConfigTopic == "") {
                topicSet.clear();
                return;
            }
            topicSet = new ConcurrentSkipListSet<>(Arrays.asList(jmxConfigTopic.split(",")));
            log.debug("TopicSet update success, topics:" + topicSet.toString());
        } catch (Exception e) {
            log.error("Get jmx config topic error, detail: ", e);
        }

    }

    private void clearJmxMetric() {
        Set<String> clearSet = metricNameSet;
        metricNameSet = new ConcurrentSkipListSet<>();
        clearSet.forEach(AppIdHostTopicMetrics::removeClientMetrics);
        clearSet.clear();
    }

    synchronized private long getTimeMillis(String time) {
        try {
            Date currentDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return currentDate.getTime();
        } catch (ParseException e) {
            log.error("Parse date error, initTime: {}", initTime);
        }
        return 0;
    }
}
