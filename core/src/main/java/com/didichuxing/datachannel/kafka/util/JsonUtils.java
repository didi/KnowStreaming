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

package com.didichuxing.datachannel.kafka.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.kafka.cache.CacheException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JSONObject.class);

    public static Map<String, Object> jsonString2Map(String jsonString) {
        Map<String, Object> resultMap = new HashMap<>();
        return resultMap;
    }

    public static ResponseCommonResult string2ResponseCommonResult(String jsonString) {
        return JSON.parseObject(jsonString, ResponseCommonResult.class);
    }

    public static JSONArray getJSONArray(String url, String req, int timeoutMs) throws Exception {
        try {
            //send requeset to kafka gateway
            ResponseCommonResult resp =
                    HttpUtils.post(url, null, req.getBytes(), timeoutMs);
            if (resp == null || resp.getCode() == ResponseCommonResult.FAILED_STATUS) {
                throw new CacheException(String.format("send request Data failed: %s %s", url, req));
            }

            String respStr = (String) resp.getData();
            if (respStr == null || respStr.equals("")) {
                throw new CacheException(String.format("Invalid response: %s", resp));
            }

            JSONObject respJson = JSON.parseObject(respStr);
            if (respJson == null || !respJson.containsKey("data")) {
                throw new CacheException(String.format("Invalid data: missing 'data' resp: %s", resp));
            }
            JSONObject data = respJson.getJSONObject("data");
            if (data == null || !data.containsKey("rows")) {
                throw new CacheException(String.format("Invalid data: missing 'rows' resp: %s", resp));
            }

            JSONArray records = data.getJSONArray("rows");
            return records;
        } catch (Exception e) {
            log.error("send http request excption: {}", e.getMessage());
            return new JSONArray();
        }

    }

    public static boolean checkClusterAddress(Map<Integer, List<String>> inputMap) {
        for (Map.Entry<Integer, List<String>> entry : inputMap.entrySet()) {
            List<String> value = entry.getValue();
            for (String bootstrap : value) {
                String[] ipPort = bootstrap.split(":");
                if (ipPort.length != 2)
                    return false;
                if (!StringUtils.isNumeric(ipPort[1]))
                    return false;
            }
        }
        return true;
    }

    public static Map<Integer, List<String>> jsonString2MapForCluster(String jsonString) {
        Map<Integer, List<String>> resultMap = new HashMap<>();
        JSONObject.parseObject(jsonString).forEach((key, value) -> {
            if (StringUtils.isNumeric(key) && value != null && value instanceof JSONArray) {
                resultMap.put(Integer.parseInt(key), JSON.parseArray(((JSONArray) value).toJSONString(), String.class));
            }
        });
        return resultMap;
    }
}
