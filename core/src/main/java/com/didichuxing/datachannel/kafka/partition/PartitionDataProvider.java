/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE;
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

package com.didichuxing.datachannel.kafka.partition;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.kafka.cache.CacheException;
import com.didichuxing.datachannel.kafka.cache.DataProvider;
import com.didichuxing.datachannel.kafka.cache.DataRecord;
import com.didichuxing.datachannel.kafka.cache.Dataset;
import com.didichuxing.datachannel.kafka.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/*
public class PartitionDataProvider implements DataProvider {
    private static final Logger log = LoggerFactory.getLogger(PartitionDataProvider.class);

    final static private int FETCDATA_TIMEOUT = 10000;
    final static private String API_PATH = "/api/v1/partition/topicPartitions";

    final private String clusterId;
    final private String fetchDataUrl;

    public PartitionDataProvider(String clusterId, String gatewayUrl) {
        this.clusterId = clusterId;
        this.fetchDataUrl= gatewayUrl + API_PATH;
    }

    @Override
    public Dataset fetchData(long startTime, long endTime) throws Exception {
        log.debug("Fetch data start: {} end {}", startTime, endTime);

        String req = String.format("{\"clusterId\":%s,\"start\":%d,\"end\":%d}", clusterId, startTime, endTime);
        List<DataRecord> entries = new ArrayList<>();
        try {
            JSONArray topicPartitions = JsonUtils.getJSONArray(fetchDataUrl, req, FETCDATA_TIMEOUT);

            for (int i = 0; i < topicPartitions.size(); i++) {
                JSONObject jsonTopicPartitions = topicPartitions.getJSONObject(i);
                try {
                    PartitionMappingTable partitionMappingTable = new PartitionMappingTable((jsonTopicPartitions));
                    DataRecord<String, PartitionMappingTable> dataRecord = new
                        DataRecord<>(partitionMappingTable.getTopicName(), partitionMappingTable, jsonTopicPartitions);
                    entries.add(dataRecord);
                } catch (IllegalArgumentException e) {
                    log.error("invalid data {}", jsonTopicPartitions.toJSONString());
                }
            }
            if (topicPartitions.size() > 0) {
                log.info("Fetch some new data total {}", topicPartitions.size());
            } else {
                log.info("No new data in data soucre");
            }
        } catch (Exception e) {
            log.error("Fetch data error: ", e);
            throw new CacheException("Fetch Data error " +   e.getMessage());
        }
        Dataset dataset = new Dataset(entries, endTime);
        return dataset;
    }
}
 */
