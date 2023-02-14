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

public enum Operation {
    Read,
    Write,
    Create,
    Delete,
    Describe,
    Alert,
    ClusterAction,
    DescribeConfigs,
    AlterConfigs,
    IdempotentWrite,
    Other;

    static Operation from(kafka.security.auth.Operation operation) {
        return from(operation.name());
    }

    static Operation from(String value) {
        if (value.equals(Read.name())) {
            return Read;
        } else if (value.equals(Write.name())) {
            return Write;
        } else if (value.equals(Create.name())) {
            return Create;
        } else if (value.equals(Delete.name())) {
            return Delete;
        } else if (value.equals(Describe.name())) {
            return Describe;
        } else if (value.equals(Alert.name())) {
            return Alert;
        } else if (value.equals(ClusterAction.name())) {
            return ClusterAction;
        } else if (value.equals(DescribeConfigs.name())) {
            return DescribeConfigs;
        } else if (value.equals(AlterConfigs.name())) {
            return AlterConfigs;
        } else if (value.equals(IdempotentWrite.name())) {
            return IdempotentWrite;
        } else {
            return Other;
        }
    }

}
