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

package com.didichuxing.datachannel.kafka.server;

import kafka.server.LogOffsetMetadata;
import org.apache.kafka.common.TopicPartition;

public class FetchInfo {

    public enum ReadPolicy {
        // normally read
        Normal,
        // empty read
        Empty,
        // give up current reading and wait to next round
        Evade,
        // can read more data than normal
        Greedy
    }

    private static final TopicPartitionAndReplica UnknownTopicOrPartitionAndReplica =
            new TopicPartitionAndReplica(new TopicPartition("unknown", -1), -1);

    //if fetcher is consumer the replica id is -1, otherwise is the broker id
    private TopicPartitionAndReplica topicPartitionAndReplica = UnknownTopicOrPartitionAndReplica;
    //fetch offset
    private LogOffsetMetadata logOffsetMetadata;
    // fetch length
    private int length;
    private String filename;
    // the read policy indicate the caller read the data or not when the read data is be confirmed by log offset and length
    private ReadPolicy policy;
    private String diskname = OSUtil.DEFAULT_DISKNAME;
    // the lag of fetch offset and log end offset
    private long lag;

    // hotdata is recently used data by read or write. it has been cached in page cache by os.
    // normally the data in active log segment should be hotdata
    // if the os support mincore, by this way it can get the status directly
    private boolean hotdata;

    public static FetchInfo EMPTY = new FetchInfo("unknown_file", LogOffsetMetadata.UnknownOffsetMetadata(), 0);

    public FetchInfo(String filename, LogOffsetMetadata logOffsetMetadata, int length) {
        this.filename = filename;
        this.length = length;
        this.logOffsetMetadata = logOffsetMetadata;
        if (logOffsetMetadata == LogOffsetMetadata.UnknownOffsetMetadata()) {
            this.policy = ReadPolicy.Empty;
        } else {
            this.policy = ReadPolicy.Normal;
        }
    }

    public String getFilename() {
        return filename;
    }

    public long getMessageOffset() {
        return logOffsetMetadata.messageOffset();
    }

    public long getLogOffset() {
        return logOffsetMetadata.relativePositionInSegment();
    }

    public void setLogOffsetMetadata(LogOffsetMetadata logOffsetMetadata) {
        this.logOffsetMetadata = logOffsetMetadata;
    }

    public TopicPartitionAndReplica getTopicPartitionAndReplica() {
        return topicPartitionAndReplica;
    }

    public void setTopicPartitionAndReplica(TopicPartitionAndReplica topicPartitionAndReplica) {
        this.topicPartitionAndReplica = topicPartitionAndReplica;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ReadPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(ReadPolicy policy) {
        this.policy = policy;
    }

    public String getDiskname() {
        return diskname;
    }

    public void setDiskname(String diskname) {
        this.diskname = diskname;
    }

    public boolean isHotdata() {
        return hotdata;
    }

    public void setHotdata(boolean hotdata) {
        this.hotdata = hotdata;
    }

    public long getLag() {
        return lag;
    }

    public void setLag(long lag) {
        this.lag = lag;
    }

    @Override
    public String toString() {
        return String.format("[file:%s, log offset:%s, length:%d, replica:%d, disk:%s, policy:%s, hotdata:%b",
                filename, logOffsetMetadata, length, topicPartitionAndReplica.replicaId(), diskname, policy, hotdata);
    }
}


