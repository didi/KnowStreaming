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

package com.didichuxing.datachannel.kafka.jmx;

import com.didichuxing.datachannel.kafka.cache.CacheException;
import com.didichuxing.datachannel.kafka.config.GatewayConfigs;
import com.didichuxing.datachannel.kafka.util.HttpUtils;
import com.didichuxing.datachannel.kafka.util.JsonUtils;
import com.didichuxing.datachannel.kafka.util.ResponseCommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JmxConfigProvider {

    private static final Logger log = LoggerFactory.getLogger(com.didichuxing.datachannel.kafka.jmx.JmxConfigProvider.class);

    final private String clusterId;
    final private String fetchDataUrl;

    public JmxConfigProvider(String clusterId, String gatewayUrl) {
        this.clusterId = clusterId;
        this.fetchDataUrl = GatewayConfigs.getTopicJmxReportUrl(gatewayUrl);
    }

    public String fetchData() throws Exception {
        log.debug("Fetch jmx config start");

        Map<String, String> params = new HashMap<>();
        params.put("clusterId", clusterId);
        ResponseCommonResult httpResult = HttpUtils.get(fetchDataUrl, params, 0);

        if (httpResult.getCode() == ResponseCommonResult.FAILED_STATUS) {
            throw new Exception(String.format("Http response error, detail: %s", httpResult.toString()));
        }

        ResponseCommonResult configDataResult = JsonUtils.string2ResponseCommonResult(httpResult.getData().toString());

        if (configDataResult.getCode() == ResponseCommonResult.FAILED_STATUS) {
            throw new Exception(String.format("Get jmxConfigTopic error, detail: %s", configDataResult.toString()));
        }

        return configDataResult.getData().toString();
    }
}
