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

package com.didichuxing.datachannel.kafka.partition;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PartitionMappingTable {
/*
    private static final Logger log = LoggerFactory.getLogger(PartitionRouter.class);

    static final int DISABLE_PARTITION = -1;

    final private String topicName;

    private int [] mapToTable;
    private int [] mapFromTable;

    private int partitionSize;
    private List<Integer> disablePartitions = new ArrayList<>();

    public PartitionMappingTable(String topicName, List<Integer> disablePartitions) {
        this.topicName = topicName;
        this.disablePartitions = disablePartitions;
    }

    public PartitionMappingTable(JSONObject json) {
        String topicName = json.getString("topicName");
        if (topicName == null || topicName.equals("")) {
            throw new IllegalArgumentException("missing topicName");
        }

        String disablePartitions = json.getString("disablePartitions");
        if (disablePartitions == null || disablePartitions.equals("")) {
            this.topicName = topicName;
            return;
        } else {
            String []partitionList =  disablePartitions.split(",");
            if (partitionList.length == 0) {
                throw new IllegalArgumentException("invalid disable partitions");
            }
            for (int i=0; i < partitionList.length; i++) {
                Integer partitionValue = Integer.valueOf(partitionList[i]);
                if (partitionValue == null) {
                    throw new IllegalArgumentException("invalid disable partitions");
                }
                this.disablePartitions.add(partitionValue);
            }
        }

        this.topicName = topicName;
    }

    synchronized private void initMaps(int size) {
        //partitionSize changed by other thread
        if (size < partitionSize)
            return;

        // if topic has 4 partitions 0,1,2,3. 1,2 is disable
        // mapToTable   [0,3,-1,-1]
        // mapFromTable [0,-1,-1,1]
        int partitions = PartitionRouter.getInstance().getNumberPartition(topicName);
        mapToTable = new int[partitions];
        mapFromTable = new int[partitions];
        for (int i = 0; i < mapToTable.length; i++) {
            mapToTable[i] = DISABLE_PARTITION;
            mapFromTable[i] = DISABLE_PARTITION;
        }

        int mappedPartition = 0;
        for (int i = 0; i < mapToTable.length; i++) {
            if (isDisable(i)) {
                continue;
            } else {
                mapToTable[i] = mappedPartition;
                mapFromTable[mappedPartition] = i;
                mappedPartition++;
            }
        }
        partitionSize = partitions;
        log.info("Init partition mapping table, topic: {}, number partitions: {}, disable partitions: {}",
                topicName, partitionSize, disablePartitions);
    }

    public String getTopicName() {
        return topicName;
    }

    public boolean isDisable(int partion) {
        return disablePartitions.contains(partion);
    }

    public int getMapFromPartition(int partition) {
        if (partition >= partitionSize) {
            initMaps(partition);
        }
        return mapFromTable[partition];
    }

    public int getMapToPartition(int partition) {
        if (partition >= partitionSize) {
            initMaps(partition);
        }
        return mapToTable[partition];
    }

    public List<Integer> getDisablePartitions() {
        return disablePartitions;
    }

    @Override
    public boolean equals(Object obj) {
        PartitionMappingTable mappingTable = (PartitionMappingTable)obj;
        if (!topicName.equals(mappingTable.topicName)) {
            return false;
        }

        if (mapToTable.length != mappingTable.mapToTable.length) {
            return false;
        }

        for (int i=0; i<mapToTable.length; i++) {
            if (mapToTable[i] != mappingTable.mapToTable[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("topic: %s ,disable partitions: %s", topicName, disablePartitions);
    }
 */
}
