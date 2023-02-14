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

package com.didichuxing.datachannel.kafka.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * DataProvider fetch data result.
 */
public class Dataset {

    //all the entries shoud sorted by timestamp
    private List<DataRecord> entries = new ArrayList<>();

    // the endtime by fetchData
    private long timestamp;

    public Dataset(long timestamp) {
        this.timestamp = timestamp;
    }

    public Dataset(List<DataRecord> entries, long timestamp) {
        this.entries = entries;
        this.timestamp = timestamp;
    }

    public List<DataRecord> getEntries() {
        return entries;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
