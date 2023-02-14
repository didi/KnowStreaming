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

public class AccessStatusAndTimestamp {

    //access status for topic and operation
    final private AccessStatus accessStatus;
    //acl cache timestamp .if not match cache has expired
    volatile private long aclTimestamp;
    //topic and operation active time.
    volatile private long activeTimestamp;

    public AccessStatusAndTimestamp(AccessStatus accessStatus, long aclTimestamp) {
        this.accessStatus = accessStatus;
        this.aclTimestamp = aclTimestamp;
        activeTimestamp = System.currentTimeMillis();
    }

    public AccessStatus getAccessStatus() {
        return accessStatus;
    }

    public long getAclTimestamp() {
        return aclTimestamp;
    }

    public long getActiveTimestamp() {
        return activeTimestamp;
    }

    public void setActiveTimestamp(long activeTimestamp) {
        this.activeTimestamp = activeTimestamp;
    }
}
