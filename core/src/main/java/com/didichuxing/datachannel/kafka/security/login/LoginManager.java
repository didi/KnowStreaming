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

package com.didichuxing.datachannel.kafka.security.login;

import com.didichuxing.datachannel.kafka.cache.DataCache;
import com.didichuxing.datachannel.kafka.cache.DataProvider;
import com.didichuxing.datachannel.kafka.cache.ZkUtil;
import kafka.cluster.Broker;
import kafka.cluster.EndPoint;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.common.network.ListenerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConverters;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class LoginManager {

    final private static String USER_ANONYMOUS = "ANONYMOUS";
    final private static String USER_admin = "admin";

    final public static User ANONYMOUS_USER = new User(USER_ANONYMOUS, "*", true);
    final public static User admin_USER = new User(USER_admin, "*", true);
    final public static User unknown_USER = new User("unknown", "*", true);

    final private static ListenerName DEFAULT_LISTENER_NAME = new ListenerName("SASL_PLAINTEXT");

    private static final Logger log = LoggerFactory.getLogger("kafka.authorizer.logger");
    private static final Logger userErrorlog = LoggerFactory.getLogger("userError");

    final private String CACHE_NAME = "kafka_login";

    private KafkaZkClient zkUtil;

    private ListenerName listenerName;

    //map user name to User.
    private DataCache<String, User> cache = null;

    private LoginManager(){}

    static public LoginManager getInstance() {
        return LoginMangerHolder.INSTANCE;
    }

    public void start(String clusterId, int brokerId, KafkaZkClient zkUtil,
                      ScheduledExecutorService scheduledExecutorService,
                      String gatewayUrl, List<String> defaultUsers) {
        this.start(clusterId, brokerId, DEFAULT_LISTENER_NAME, zkUtil, scheduledExecutorService, gatewayUrl, defaultUsers);
    }

    public void start(String clusterId, int brokerId, ListenerName listenerName, KafkaZkClient zkUtil,
                      ScheduledExecutorService scheduledExecutorService,
                      String gatewayUrl, List<String> defaultUsers) {
        log.info("Login Manager startup");
        this.listenerName = listenerName;
        this.zkUtil = zkUtil;
        ZkUtil dataCacheZkUtil = new ZkUtil(zkUtil::currentZooKeeper);

        List<String> systemUsers = new ArrayList<>();
        systemUsers.add(String.format("%s:%s:%s",
                ANONYMOUS_USER.getUsername(), ANONYMOUS_USER.getPassword(), admin_USER.isSuperUser()));
        systemUsers.add(String.format("%s:%s:%s",
                admin_USER.getUsername(), admin_USER.getPassword(), admin_USER.isSuperUser()));
        if (defaultUsers != null)
            systemUsers.addAll(defaultUsers);

        DataProvider dataProvider = new UserDataProvider(clusterId, gatewayUrl, systemUsers);
        cache = new DataCache<>(CACHE_NAME, brokerId, dataProvider, scheduledExecutorService,
                dataCacheZkUtil, 60000, 4 * 3600 * 1000);
        if (cache.size() == 0) {
            throw new RuntimeException(String.format("System don't have any users clusterId: %s", clusterId));
        }
    }

    public void shutdown() {
        log.info("Login Manager shutdown");
        if (cache != null) {
            cache.stop();
        }
    }

    /**
     * check login
     * @param username
     * @param password
     * @return login status
     */
    public boolean login(String username, String password, String host) {
        User user = cache.get(username);
        if (user != null) {
            if (username.equals(USER_ANONYMOUS)) {
                userErrorlog.error("User = {} from {} login failed. no permission for this user.",
                        username, host);
                return false;
            }

            if (user.getUsername().equals(USER_admin)) {
                List<Broker> brokers = JavaConverters.seqAsJavaList(zkUtil.getAllBrokersInCluster().seq());
                for (Broker broker : brokers) {
                    EndPoint endPoint = broker.endPoint(this.listenerName);
                    String brokerHost = "";
                    try {
                        brokerHost = InetAddress.getByName(endPoint.host()).getHostAddress();
                    } catch (Exception e) {
                        log.error("User = {} from {} login failed", username, host, e);
                    }
                    if (host.equals(brokerHost) || host.equals("127.0.0.1")) {
                        log.info("User = {} from {} login success", username, host);
                        return true;
                    }
                }
                userErrorlog.error("User = {} from {} login failed. no permission for this user.",
                        username, host);
                return false;
            }

            boolean success = false;
            try {
                success = password.equals(SecurityUtils.decrypt(user.getPassword()));
            } catch (RuntimeException e) {
                log.warn("User = {} from {} decrypt password failed", username, host);
            }

            if (password.equals(user.getPassword()) || success) {
                log.info("User = {} from {} login success", username, host);
                return true;
            }
        }
        log.error("User = {} from {} login failed", username, host);
        userErrorlog.error("User = {} and Password = {} from {} login failed. error username or password.",
                username, hiddenText(password), host);
        return false;
    }

    /**
     * get Kafka User
     * @param userName
     * @return user
     */
    public User getUser(String userName) {
        //this case happend in testing
        if (cache == null) {
            return null;
        }
        return cache.get(userName);
    }

    private String hiddenText(String s) {
        if (s == null || s.isEmpty()) return s;
        StringBuilder builder = new StringBuilder();
        builder.append(s.charAt(0));
        for (int i = 1; i < s.length() - 1; i++) {
            builder.append('*');
        }
        builder.append(s.charAt(s.length() - 1));
        return builder.toString();
    }

    private static class LoginMangerHolder{

        private static final LoginManager INSTANCE = new LoginManager();

    }
}
