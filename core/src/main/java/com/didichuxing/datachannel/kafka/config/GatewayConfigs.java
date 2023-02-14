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


package com.didichuxing.datachannel.kafka.config;

public class GatewayConfigs {

    public static final String TOPIC_HEART_BEAT_URL = "/api/v1/heartbeat/survive-user";
    public static final String TOPIC_JMX_REPORT_URL = "/api/v1/report/jmx/topics";

    public static final String INIT = "/api/v1/discovery/init";

    public static final String UPDATE = "/api/v1/discovery/update";

    public static final String SYNC_MAX_REQUEST_NUM = "/api/v1/discovery/max-request-num";

    public static final String SYNC_APP_ID_RATE = "/api/v1/discovery/appId-rate";

    public static final String SYNC_IP_RATE = "/api/v1/discovery/ip-rate";

    public static final String SYNC_SP_LIMIT = "/api/v1/discovery/sp-limit";

    public static String getTopicHeartBeatUrl(String prefix) {
        return prefix + TOPIC_HEART_BEAT_URL;
    }

    public static String getTopicJmxReportUrl(String prefix) {
        return prefix + TOPIC_JMX_REPORT_URL;
    }

    public static String getGatewayAddressUrl(String prefix) {
        return prefix + INIT;
    }

    public static String updateGateWayAddressUrl(String prefix) {
        return prefix + UPDATE;
    }

    public static String updateGateWayMaxRequestNum(String prefix) {
        return prefix + SYNC_MAX_REQUEST_NUM;
    }

    public static String updateGateWayAppIdRate(String prefix) {
        return prefix + SYNC_APP_ID_RATE;
    }

    public static String updateGateWayIpRate(String prefix) {
        return prefix + SYNC_IP_RATE;
    }

    public static String updateGateWaySpLimit(String prefix) {
        return prefix + SYNC_SP_LIMIT;
    }
}
