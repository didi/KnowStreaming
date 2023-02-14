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

package com.didichuxing.datachannel.kafka.security.login;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.kafka.cache.CacheException;
import com.didichuxing.datachannel.kafka.cache.DataProvider;
import com.didichuxing.datachannel.kafka.cache.DataRecord;
import com.didichuxing.datachannel.kafka.cache.Dataset;
import com.didichuxing.datachannel.kafka.util.JsonUtils;
import joptsimple.internal.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class UserDataProvider implements DataProvider {

    private static final Logger log = LoggerFactory.getLogger(UserDataProvider.class);

    final static private int FETCDATA_TIMEOUT = 10000;
    final static private String API_PATH = "/api/v1/security/users";

    final private String clusterId;
    final private String fetchDataUrl;

    final private List<DataRecord<String, User>> defualtUsers = new ArrayList<>();

    public UserDataProvider(String clusterId, String gatewayUrl, List<String> users) {
        this.clusterId = clusterId;
        this.fetchDataUrl = gatewayUrl + API_PATH;

        if (users != null) {
            for (String user : users) {
                var record = createUserRecord(user);
                if (record != null) {
                    defualtUsers.add(record);
                }
            }
        }
    }


    @Override
    public Dataset fetchData(long startTime, long endTime) throws Exception {
        log.debug("Fetch data start: {} end {}", startTime, endTime);

        //send requeset to kafka gateway
        String req = String.format("{\"clusterId\":%s,\"start\":%d,\"end\":%d}", clusterId, startTime, endTime);
        List<DataRecord> entries = new ArrayList<>();
        //init system user when load data from empty
        if (startTime == 0) {
            entries.addAll(defualtUsers);
        }

        JSONArray users = JsonUtils.getJSONArray(fetchDataUrl, req, FETCDATA_TIMEOUT);
        for (int i = 0; i < users.size(); i++) {
            JSONObject jsonUser = users.getJSONObject(i);
            try {
                User user = new User(jsonUser);
                DataRecord<String, User> dataRecord = new DataRecord<>(user.getUsername(), user, jsonUser);
                entries.add(dataRecord);
            } catch (IllegalArgumentException e) {
                log.error("invalid data {}", users.toJSONString());
            }
        }
        if (users.size() > 0) {
            log.info("Fetch some new data total {}", users.size());
        } else {
            log.info("No new data in data soucre");
        }

        return new Dataset(entries, endTime);
    }

    DataRecord<String, User> createUserRecord(String user) {
        //user string should be name:passwd:issuper
        try {
            String[] entry = user.split(":");
            if (entry.length != 3) {
                throw new IllegalArgumentException("invalid user string");
            }
            String username = entry[0];
            if (Strings.isNullOrEmpty(username)) {
                throw new IllegalArgumentException("missing username");
            }
            String password = entry[1];
            if (Strings.isNullOrEmpty(password)) {
                throw new IllegalArgumentException("missing password");
            }
            boolean superUser = Boolean.parseBoolean(entry[2]);

            return new DataRecord<>(username,
                    new User(username, password, superUser), DataRecord.Operation.create, 0);
        } catch (Exception e) {
            log.error("parsing user string error: ", e);
            return null;
        }
    }
}
