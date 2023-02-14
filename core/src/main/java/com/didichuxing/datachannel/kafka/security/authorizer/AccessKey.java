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

package com.didichuxing.datachannel.kafka.security.authorizer;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

class AccessKey {

    final private String topicName;
    final private String userName;
    final private Operation operation;

    public AccessKey(String topicName, String userName, Operation operation) {
        this.topicName = topicName;
        this.userName = userName;
        this.operation = operation;
    }

    static public List<AccessKey> createAccessKeysfromJson(JSONObject json) {
        List<AccessKey> accessKeys = new ArrayList<>();

        String topicName = json.getString("topicName");
        if (topicName == null || topicName.equals("")) {
            throw new IllegalArgumentException("missing topicName");
        }

        String userName = json.getString("username");
        if (userName == null || userName.equals("")) {
            throw new IllegalArgumentException("missing username");
        }

        Integer access = json.getInteger("access");
        if (access == null) {
            throw new IllegalArgumentException("missing access");
        }

        switch (access) {
            case 0:
                break;
            case 1:
                accessKeys.add(new AccessKey(topicName, userName, Operation.Read));
                break;
            case 2:
                accessKeys.add(new AccessKey(topicName, userName, Operation.Write));
                break;
            case 3:
                accessKeys.add(new AccessKey(topicName, userName, Operation.Read));
                accessKeys.add(new AccessKey(topicName, userName, Operation.Write));
                break;
            default:
                throw new IllegalArgumentException("missing operation");
        }
        return  accessKeys;
    }

    @Override
    public boolean equals(Object obj) {
        AccessKey accessKey = (AccessKey)obj;
        return this.topicName.equals(accessKey.topicName) &&
            this.userName.equals(accessKey.userName) &&
            this.operation.equals(accessKey.operation);
    }

    @Override
    public int hashCode() {
        return topicName.hashCode() + userName.hashCode() + operation.ordinal() * 4751;
    }

    @Override
    public String toString() {
        return String.format("username: %s, toppic: %s, operation: %s", userName, topicName, operation);
    }
}
