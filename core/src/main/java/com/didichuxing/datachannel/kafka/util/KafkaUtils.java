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

package com.didichuxing.datachannel.kafka.util;

import java.util.HashMap;
import java.util.Map;

public class KafkaUtils {
    static private HashMap<Integer, String> fetchApiVersionMap = new HashMap<>();
    static private HashMap<Integer, String> produceApiVersionMap = new HashMap<>();

    static {
        fetchApiVersionMap.put(0, "0.8");
        fetchApiVersionMap.put(1, "0.9");
        fetchApiVersionMap.put(2, "0.10.x");
        fetchApiVersionMap.put(3, "0.10.x");
        fetchApiVersionMap.put(4, "0.11");
        fetchApiVersionMap.put(5, "0.11");
        fetchApiVersionMap.put(6, "1.x");
        fetchApiVersionMap.put(7, "1.x");
        fetchApiVersionMap.put(8, "2.0");
        fetchApiVersionMap.put(9, "2.0");
        fetchApiVersionMap.put(10, "2.2");
        fetchApiVersionMap.put(11, "2.4");

        produceApiVersionMap.put(0, "0.8");
        produceApiVersionMap.put(1, "0.9");
        produceApiVersionMap.put(2, "0.10.x");
        produceApiVersionMap.put(3, "0.11");
        produceApiVersionMap.put(4, "1.x");
        produceApiVersionMap.put(5, "1.x");
        produceApiVersionMap.put(6, "2.0");
        produceApiVersionMap.put(7, "2.2");
        produceApiVersionMap.put(8, "2.4");
    }

    static public String apiVersionToKafkaVersion(int apiKey, int apiVersion) {
        String result = null;
        switch (apiKey) {
            case 0:
                result = produceApiVersionMap.get(apiVersion);
                break;
            case 1:
                result = fetchApiVersionMap.get(apiVersion);
        }
        if (result == null) {
            result = "unknown";
        }
        return result;
    }
}
