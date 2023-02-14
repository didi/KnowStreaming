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

package com.didichuxing.datachannel.kafka.security.authorizer;

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

class AclDataProvider implements DataProvider {

    private static final Logger log = LoggerFactory.getLogger(AclDataProvider.class);

    final static private int FETCDATA_TIMEOUT = 10000;
    final static private String API_PATH = "/api/v1/security/acls";

    final private String clusterId;
    final private String fetchDataUrl;

    final private List<DataRecord<AccessKey, AccessStatus>> defualtAcls = new ArrayList<>();

    public AclDataProvider(String clusterId, String gatewayUrl, List<String> acls) {
        this.clusterId = clusterId;
        this.fetchDataUrl = gatewayUrl + API_PATH;

        if (acls != null) {
            for (String acl : acls) {
                var record = createAclRecord(acl);
                if (record != null) {
                    defualtAcls.add(record);
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
        if (startTime == 0) {
            entries.addAll(defualtAcls);
        }

        JSONArray acls = JsonUtils.getJSONArray(fetchDataUrl, req, FETCDATA_TIMEOUT);

        for (int i = 0; i < acls.size(); i++) {
            JSONObject jsonAcl = acls.getJSONObject(i);
            try {
                List<AccessKey> accessKeys = AccessKey.createAccessKeysfromJson(jsonAcl);
                for (AccessKey accessKey : accessKeys) {
                    DataRecord<AccessKey, AccessStatus> dataRecord = new
                            DataRecord<>(accessKey, AccessStatus.Allow, jsonAcl);
                    entries.add(dataRecord);
                }
            } catch (IllegalArgumentException e) {
                log.error("invalid data {}", acls.toJSONString());
            }
        }
        if (acls.size() > 0) {
            log.info("Fetch some new data total {}", acls.size());
        } else {
            log.info("No new data in data soucre");
        }

        return new Dataset(entries, endTime);
    }

    DataRecord<AccessKey, AccessStatus> createAclRecord(String acl) {
        //acl string should be topic:user:operation:status
        //operation int:  1(read) 2(write)
        //status int: 0(deny), 1(allow)
        try {
            String[] entry = acl.split(":");
            if (entry.length != 4) {
                throw new IllegalArgumentException("invalid acl string");
            }
            String topic = entry[0];
            if (Strings.isNullOrEmpty(topic)) {
                throw new IllegalArgumentException("missing topicName");
            }
            String user = entry[1];
            if (Strings.isNullOrEmpty(user)) {
                throw new IllegalArgumentException("missing user");
            }
            Operation operation = Operation.from(entry[2]);
            if (operation == Operation.Other) {
                throw new IllegalArgumentException("invalid operation");
            }
            AccessStatus access = AccessStatus.from(entry[3]);
            if (access == AccessStatus.Continue) {
                throw new IllegalArgumentException("invalid operation");
            }
            return new DataRecord<>(new AccessKey(topic, user, operation),
                    access, DataRecord.Operation.create, 0);
        } catch (Exception e) {
            log.error("parsing acl string error: ", e);
            return null;
        }
    }
}
