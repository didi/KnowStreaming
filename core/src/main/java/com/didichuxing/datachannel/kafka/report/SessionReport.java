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

package com.didichuxing.datachannel.kafka.report;

import com.didichuxing.datachannel.kafka.security.authorizer.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionReport {

    private static final Logger log = LoggerFactory.getLogger(SessionReport.class);
    private String topicHeartBeatUrlPrefix;

    private SessionReport() {
    }

    static public SessionReport getInstance() {
        return SessionReportHolder.INSTANCE;
    }

    public void start(String clusterId, int brokerId, String urlPrefix, int sessionReportTimes, ScheduledExecutorService scheduledExecutorService) {
        topicHeartBeatUrlPrefix = urlPrefix;
        log.info("Session reporter startup");
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                sendTopicHeartBeat(clusterId, brokerId);
            } catch (Throwable t) {
                log.error("Uncaught error session report I/O thread: ", t);
            }
        }, sessionReportTimes, sessionReportTimes, TimeUnit.MILLISECONDS);
    }

    public String getTopicHeartBeat() {
        Map<String, List<String>> topicProduceUser = new HashMap<>();
        Map<String, List<String>> topicFetchUser = new HashMap<>();
        SessionManager.getInstance().getSessions().forEach((userName, v1) ->
                v1.forEach((sessionKey, session) ->
                        session.getAccessTopics().forEach((topicName, v3) -> {
                            if (v3.length == 2 && !"unknown".equals(session.getClientId())) {
                                if (v3[0] != null) {
                                    topicFetchUser.putIfAbsent(topicName, new ArrayList<>());
                                    topicFetchUser.get(topicName).add(getIdKey(userName, session.getHostAddress() + ":" + session.getClientPort(), session.getClientVersion(), session.getClientId(), v3[0].getActiveTimestamp()));
                                }
                                if (v3[1] != null) {
                                    topicProduceUser.putIfAbsent(topicName, new ArrayList<>());
                                    topicProduceUser.get(topicName).add(getIdKey(userName, session.getHostAddress() + ":" + session.getClientPort(), session.getClientVersion(), session.getClientId(), v3[1].getActiveTimestamp()));
                                }
                            }
                        })));
        Map<String, Map<String, List<String>>> resultMap = new HashMap<>();
        resultMap.put("produce", topicProduceUser);
        resultMap.put("fetch", topicFetchUser);
        // String result = JSON.toJSONString(resultMap);
        log.debug("Session report: {}", resultMap);
        return "";
    }

    public void sendTopicHeartBeat(String clusterId, int brokerId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("clusterId", clusterId);
        paramMap.put("brokerId", String.valueOf(brokerId));
        String data = getTopicHeartBeat();

    }

    public void shutdown() {
    }

    public String getIdKey(String appId, String ip, String version, String clientId, long activeTimestamp) {
        return appId + "#" + ip + "#" + version + "#" + clientId + "#" + activeTimestamp;
    }

    private static class SessionReportHolder {
        private static final SessionReport INSTANCE = new SessionReport();

    }
}
