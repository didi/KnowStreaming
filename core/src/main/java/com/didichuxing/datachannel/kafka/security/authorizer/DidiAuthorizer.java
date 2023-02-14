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

import com.didichuxing.datachannel.kafka.cache.DataCache;
import com.didichuxing.datachannel.kafka.cache.DataProvider;
import com.didichuxing.datachannel.kafka.cache.ZkUtil;
import kafka.network.RequestChannel;
import kafka.security.auth.Acl;
import kafka.security.auth.Authorizer;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 *  This class use by check access control for every request.
 *  It use Datacache cache all the acls.
 */
public class DidiAuthorizer implements Authorizer {

    private static final Logger log = LoggerFactory.getLogger("kafka.authorizer.logger");
    private static final Logger userErrorlog = LoggerFactory.getLogger("userError");

    final private String CACHE_NAME = "kafka_authorizer";
    final private String WIDECARD = "*";

    //access control map for normal user.
    final private AccessStatus[][] ALLOW_USER_OPERATION_RESOURCE =
            new AccessStatus[Operation.Other.ordinal() + 1][Resource.Type.Other.ordinal() + 1];

    private DataCache<AccessKey, AccessStatus> aclCache = null;

    public DidiAuthorizer() {
        //init access control map. default is deny.

        //Allow get topic metadata
        ALLOW_USER_OPERATION_RESOURCE[Operation.Describe.ordinal()][Resource.Type.Topic.ordinal()] = AccessStatus.Allow;

        //allow transaction
        ALLOW_USER_OPERATION_RESOURCE[Operation.Describe.ordinal()][Resource.Type.TransactionalId.ordinal()] = AccessStatus.Allow;
        ALLOW_USER_OPERATION_RESOURCE[Operation.Write.ordinal()][Resource.Type.TransactionalId.ordinal()] = AccessStatus.Allow;

        //allow idempoten
        ALLOW_USER_OPERATION_RESOURCE[Operation.IdempotentWrite.ordinal()][Resource.Type.Cluster.ordinal()] = AccessStatus.Allow;

        //Allow produce data
        ALLOW_USER_OPERATION_RESOURCE[Operation.Write.ordinal()][Resource.Type.Topic.ordinal()] = AccessStatus.Continue;

        //Allow consume data
        ALLOW_USER_OPERATION_RESOURCE[Operation.Read.ordinal()][Resource.Type.Topic.ordinal()] = AccessStatus.Continue;

        //allow consumer group
        ALLOW_USER_OPERATION_RESOURCE[Operation.Read.ordinal()][Resource.Type.Group.ordinal()] = AccessStatus.Allow;
        ALLOW_USER_OPERATION_RESOURCE[Operation.Describe.ordinal()][Resource.Type.Group.ordinal()] = AccessStatus.Allow;
    }

    public void start(String clusterId, int brokerId, KafkaZkClient zkClient,
                      ScheduledExecutorService scheduledExecutorService, String gatewayUrl, List<String> defaultAcls) {
        log.info("Didi Authorizer startup");
        ZkUtil zkUtil = new ZkUtil(zkClient::currentZooKeeper);
        DataProvider dataProvider = new AclDataProvider(clusterId, gatewayUrl, defaultAcls);
        aclCache = new DataCache<>(CACHE_NAME, brokerId, dataProvider, scheduledExecutorService,
                zkUtil, 60000, 4 * 3600 * 1000);
    }

    public void stop() {
        log.info("Didi Authorizer shutdown");
        if (aclCache != null) {
            aclCache.stop();
        }
    }

    /**
     * this funtion check the access for every requests. if allow reture true. otherwise return false
     */
    @Override
    public boolean authorize(RequestChannel.Session kafkaSession, kafka.security.auth.Operation kafkaOperation,
                             kafka.security.auth.Resource kafkaResource) {
        Session session = kafkaSession.kafkaSession();
        if (session == null) {
            log.error("Unknow User = {} is denied Operation = {} on resource = {}",
                    kafkaSession.principal().getName(), kafkaOperation.name(), kafkaResource.name());
            userErrorlog.error("Unknow User = {} is denied Operation = {} on resource = {}",
                    kafkaSession.principal().getName(), kafkaOperation.name(), kafkaResource.name());
            return false;
        }

        Operation operation = Operation.from(kafkaOperation);
        Resource resource = new Resource(kafkaResource);
        AccessStatus accessStatus = checkAccess(session, resource, operation);
        if (accessStatus == AccessStatus.Allow) {
            log.debug("User = {} is allowed Operation = {} on resource = {}",
                    session.getUsername(), kafkaOperation.name(), kafkaResource.name());
            return true;
        } else {
            log.debug("User = {} is denied Operation = {} on resource = {}",
                    session.getUsername(), kafkaOperation.name(), kafkaResource.name());
            userErrorlog.error("User = {} is denied Operation = {} on resource = {}",
                    session.getUsername(), kafkaOperation.name(), kafkaResource.name());
            return false;
        }
    }

    @Override
    public void addAcls(Set<Acl> acls, kafka.security.auth.Resource resource) {

    }

    @Override
    public boolean removeAcls(Set<Acl> acls, kafka.security.auth.Resource resource) {
        return false;
    }

    @Override
    public boolean removeAcls(kafka.security.auth.Resource resource) {
        return false;
    }

    @Override
    public Set<Acl> getAcls(kafka.security.auth.Resource resource) {
        return null;
    }

    @Override
    public Map<kafka.security.auth.Resource, Set<Acl>> getAcls(KafkaPrincipal principal) {
        return null;
    }

    @Override
    public Map<kafka.security.auth.Resource, Set<Acl>> getAcls() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(java.util.Map<String, ?> configs) {

    }

    /**
     * This function use to check access.
     * 1. check the user is super use. if yes. retrun allow
     * 2. check resource and topic. if resource is no topic or group, opration is not read ,write and describe.
     *    reture deny. normal user can allow these resourcese and oprations.
     * 3. check access from session if it's cached.
     * 4. check access from acls sotre in Datacache.
     * @param session
     * @param resource
     * @param operation
     * @return
     */
    private AccessStatus checkAccess(Session session, Resource resource, Operation operation) {
       //check supper user. all operation is allow.
        if (session.getUser().isSuperUser()) {
            if (resource.getType().ordinal() == Resource.Type.Topic.ordinal() &&
                   operation.ordinal() <= Operation.Write.ordinal()) {
               //update session
                long cacheTimestamp = aclCache.getCommitTimestamp();
                AccessStatus result = checkAccessFromSession(session,
                       resource.getName(), operation, cacheTimestamp);
                if (result == AccessStatus.Continue) {
                    session.setAccessStatus(resource.getName(), operation,
                           new AccessStatusAndTimestamp(AccessStatus.Allow, cacheTimestamp));
                }
            }
            log.trace("Super user = {} is allowed Operation = {} on resource = {}",
                   session.getUsername(), operation, resource.getName());
            return AccessStatus.Allow;
        }

       //check access for normal user.
       // look up ALLOW_USER_OPERATION_RESOURCE table. only produce and consume need go to next step.
        AccessStatus result;
        result = checkOperationAndResouce(session, operation, resource);
        if (result != AccessStatus.Continue) {
            log.trace("Normal user = {} is {} Operation = {} on resource = {}",
                   session.getUsername(), result == AccessStatus.Allow ? "allowed" : "denied",
                   operation, resource.getName());
            return result;
        }

        long cacheTimestamp = aclCache.getCommitTimestamp();
        //get access status from seesion, the seesion cache the access status.
        result = checkAccessFromSession(session, resource.getName(), operation, cacheTimestamp);
        if (result != AccessStatus.Continue) {
            return result;
        }

       //session cache expired, check the access from aclcache
        result = checkAccessFromAcls(session, resource.getName(), operation, cacheTimestamp);

       //update session access cache
        session.setAccessStatus(resource.getName(), operation, new AccessStatusAndTimestamp(result, cacheTimestamp));
        return result;
    }

    private AccessStatus checkAccessFromAcls(Session session, String topicname,
                                             Operation operation, long cacheTimestamp) {
        //normal acl that indicate the user can access the topic;
        AccessKey accessKey = new AccessKey(topicname, session.getUsername(), operation);
        AccessStatus status = aclCache.get(accessKey);
        if (status != null) {
            log.trace("match allow acl: User: {} Topic: {} Operation: {}", session.getUsername(),
                    topicname, operation);
            return status;
        }
        //spacial acl that indicate the user can access all topic;
        accessKey = new AccessKey(WIDECARD, session.getUsername(), operation);
        status = aclCache.get(accessKey);
        if (status != null) {
            log.trace("match allow acl: User: {} Topic: * Operation: {}", topicname, operation);
            return status;
        }

        //spacial acl that indicate all the user can access the topic;
        accessKey = new AccessKey(topicname, WIDECARD, operation);
        status = aclCache.get(accessKey);
        if (status != null) {
            log.trace("match allow acl: User: * Topic: {} Operation: {}", session.getUsername(), operation);
            return status;
        }
        return AccessStatus.Deny;
    }

    //call this function should not be super user.
    private AccessStatus checkOperationAndResouce(Session session, Operation operation, Resource resource) {
        return ALLOW_USER_OPERATION_RESOURCE[operation.ordinal()][resource.getType().ordinal()];
    }

    private AccessStatus checkAccessFromSession(Session session, String topicName,
                                                Operation operation, long cacheTimestamp) {
        AccessStatusAndTimestamp accessStatusAndTimestamp = session.getAccessStatus(topicName, operation);
        //check weather access status cache in session is expired. if yes return continue. otherwise return status.
        if (accessStatusAndTimestamp != null && cacheTimestamp == accessStatusAndTimestamp.getAclTimestamp()) {
            return accessStatusAndTimestamp.getAccessStatus();
        }
        return AccessStatus.Continue;
    }
}
