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


class Resource {
    final private String name;
    final private Type type;

    public Resource(kafka.security.auth.Resource resource) {
        this.name = resource.name();
        this.type = Type.fromString(resource);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    enum Type {
        Topic,
        Group,
        Cluster,
        TransactionalId,
        DelegationToken,
        Other;

        static Type fromString(kafka.security.auth.Resource resource) {
            String resourceTypeName = resource.resourceType().name();
            if (resourceTypeName.equals(Topic.name())) {
                return Topic;
            } else if (resourceTypeName.equals(Group.name())) {
                return Group;
            } else if (resourceTypeName.equals(Cluster.name())) {
                return Cluster;
            } else if (resourceTypeName.equals(TransactionalId.name())) {
                return TransactionalId;
            } else if (resourceTypeName.equals(DelegationToken.name())) {
                return DelegationToken;
            } else {
                return Other;
            }
        }
    }
}

