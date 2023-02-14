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

package com.didichuxing.datachannel.kafka.cache;

import com.alibaba.fastjson.JSONObject;

/**
 * DataCache Entry type
 */
public class DataRecord<KeyType, ValueType> {
    final private KeyType key;
    final private ValueType value;
    final private Operation operation;
    final private long timestamp;

    public DataRecord(KeyType key, ValueType value, Operation operation, long timestamp) {
        this.key = key;
        this.value = value;
        this.operation = operation;
        this.timestamp = timestamp;
    }

    public DataRecord(KeyType key, ValueType value, JSONObject json) {
        this.key = key;
        this.value = value;

        Long timestamp = json.getLong("timestamp");
        if (timestamp == null) {
            throw new IllegalArgumentException("missing timestamp");
        }
        Integer operation = json.getInteger("operation");
        if (operation == null) {
            throw new IllegalArgumentException("missing operation");
        }
        if (operation < 0 || operation > 2) {
            throw new IllegalArgumentException("invalid operation");
        }

        this.operation = Operation.values()[operation];
        this.timestamp = timestamp;
    }

    public KeyType getKey() {
        return key;
    }

    public ValueType getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Operation getOperation() {
        return operation;
    }

    public enum Operation {
        create,
        update,
        delete
    }
}
