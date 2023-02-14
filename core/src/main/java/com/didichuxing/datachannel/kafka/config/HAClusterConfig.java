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

package com.didichuxing.datachannel.kafka.config;

import kafka.api.ApiVersion;
import kafka.api.ApiVersionValidator$;
import kafka.server.Defaults;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.SecurityConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.apache.kafka.common.config.ConfigDef.Range.atLeast;

/**
 * @author leewei
 * @date 2021/10/21
 * The mirror configuration keys
 */
public class HAClusterConfig extends AbstractConfig {
    private static final ConfigDef CONFIG;

    /** <code>didi.kafka.enable</code> */
    public static final String DIDI_KAFKA_ENABLE_CONFIG = "didi.kafka.enable";
    private static final String DIDI_KAFKA_ENABLE_DOC = "Enable didi kafka premium features.";

    /** <code>didi.kafka.enable</code> */
    public static final String DIDI_MIRROR_FETCHER_SPLIT_BATCHES_ENABLE_CONFIG = "didi.mirror.fetcher.split.batches.enable";
    private static final String DIDI_MIRROR_FETCHER_SPLIT_BATCHES_ENABLE_DOC = "Enable mirror fetcher split multi batch and foreach append to leader.";

    public static final String BROKER_PROTOCOL_VERSION_CONFIG = "broker.protocol.version";
    public static final String BROKER_PROTOCOL_VERSION_DOC = "Specify which version of the inter-broker protocol will be used.\n" +
            " This is typically bumped after all brokers were upgraded to a new version.\n" +
            " Example of some valid values are: 0.8.0, 0.8.1, 0.8.1.1, 0.8.2, 0.8.2.0, 0.8.2.1, 0.9.0.0, 0.9.0.1 Check ApiVersion for the full list.";

    /**
     * <code>bootstrap.servers</code>
     */
    public static final String BOOTSTRAP_SERVERS_CONFIG = CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

    /** <code>max.poll.records</code> */
    public static final String MAX_POLL_RECORDS_CONFIG = "max.poll.records";
    private static final String MAX_POLL_RECORDS_DOC = "The maximum number of records returned in a single call to poll().";

    /** <code>max.poll.interval.ms</code> */
    public static final String MAX_POLL_INTERVAL_MS_CONFIG = CommonClientConfigs.MAX_POLL_INTERVAL_MS_CONFIG;
    private static final String MAX_POLL_INTERVAL_MS_DOC = CommonClientConfigs.MAX_POLL_INTERVAL_MS_DOC;
    /**
     * <code>session.timeout.ms</code>
     */
    public static final String SESSION_TIMEOUT_MS_CONFIG = CommonClientConfigs.SESSION_TIMEOUT_MS_CONFIG;
    private static final String SESSION_TIMEOUT_MS_DOC = CommonClientConfigs.SESSION_TIMEOUT_MS_DOC;

    /**
     * <code>fetch.min.bytes</code>
     */
    public static final String FETCH_MIN_BYTES_CONFIG = "fetch.min.bytes";
    private static final String FETCH_MIN_BYTES_DOC = "The minimum amount of data the server should return for a fetch request. If insufficient data is available the request will wait for that much data to accumulate before answering the request. The default setting of 1 byte means that fetch requests are answered as soon as a single byte of data is available or the fetch request times out waiting for data to arrive. Setting this to something greater than 1 will cause the server to wait for larger amounts of data to accumulate which can improve server throughput a bit at the cost of some additional latency.";

    /**
     * <code>fetch.max.bytes</code>
     */
    public static final String FETCH_MAX_BYTES_CONFIG = "fetch.max.bytes";
    private static final String FETCH_MAX_BYTES_DOC = "The maximum amount of data the server should return for a fetch request. " +
            "Records are fetched in batches by the mirror, and if the first record batch in the first non-empty partition of the fetch is larger than " +
            "this value, the record batch will still be returned to ensure that the mirror can make progress. As such, this is not a absolute maximum. " +
            "The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or " +
            "<code>max.message.bytes</code> (topic config). Note that the mirror performs multiple fetches in parallel.";
    public static final int DEFAULT_FETCH_MAX_BYTES = 10 * 1024 * 1024;

    /**
     * <code>fetch.max.wait.ms</code>
     */
    public static final String FETCH_MAX_WAIT_MS_CONFIG = "fetch.max.wait.ms";
    private static final String FETCH_MAX_WAIT_MS_DOC = "The maximum amount of time the server will block before answering the fetch request if there isn't sufficient data to immediately satisfy the requirement given by fetch.min.bytes.";

    /**
     * <code>fetch.backoff.ms</code>
     */
    public static final String FETCH_BACKOFF_MS_CONFIG = "fetch.backoff.ms";
    private static final String FETCH_BACKOFF_MS_DOC = "The amount of time to sleep when fetch partition error occurs.";

    /** <code>metadata.max.age.ms</code> */
    public static final String METADATA_MAX_AGE_CONFIG = CommonClientConfigs.METADATA_MAX_AGE_CONFIG;

    /**
     * <code>max.partition.fetch.bytes</code>
     */
    public static final String MAX_PARTITION_FETCH_BYTES_CONFIG = "max.partition.fetch.bytes";
    private static final String MAX_PARTITION_FETCH_BYTES_DOC = "The maximum amount of data per-partition the server " +
            "will return. Records are fetched in batches by the mirror. If the first record batch in the first non-empty " +
            "partition of the fetch is larger than this limit, the " +
            "batch will still be returned to ensure that the mirror can make progress. The maximum record batch size " +
            "accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or " +
            "<code>max.message.bytes</code> (topic config). See " + FETCH_MAX_BYTES_CONFIG + " for limiting the mirror request size.";
    public static final int DEFAULT_MAX_PARTITION_FETCH_BYTES = 1 * 1024 * 1024;

    /** <code>send.buffer.bytes</code> */
    public static final String SEND_BUFFER_CONFIG = CommonClientConfigs.SEND_BUFFER_CONFIG;

    /** <code>receive.buffer.bytes</code> */
    public static final String RECEIVE_BUFFER_CONFIG = CommonClientConfigs.RECEIVE_BUFFER_CONFIG;

    /**
     * <code>reconnect.backoff.ms</code>
     */
    public static final String RECONNECT_BACKOFF_MS_CONFIG = CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG;

    /**
     * <code>reconnect.backoff.max.ms</code>
     */
    public static final String RECONNECT_BACKOFF_MAX_MS_CONFIG = CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_CONFIG;

    /**
     * <code>retry.backoff.ms</code>
     */
    public static final String RETRY_BACKOFF_MS_CONFIG = CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG;

    /** <code>connections.max.idle.ms</code> */
    public static final String CONNECTIONS_MAX_IDLE_MS_CONFIG = CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_CONFIG;

    /** <code>request.timeout.ms</code> */
    public static final String REQUEST_TIMEOUT_MS_CONFIG = CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG;
    private static final String REQUEST_TIMEOUT_MS_DOC = CommonClientConfigs.REQUEST_TIMEOUT_MS_DOC;

    /** <code>default.api.timeout.ms</code> */
    public static final String DEFAULT_API_TIMEOUT_MS_CONFIG = CommonClientConfigs.DEFAULT_API_TIMEOUT_MS_CONFIG;

    /**
     * <code>security.providers</code>
     */
    public static final String SECURITY_PROVIDERS_CONFIG = SecurityConfig.SECURITY_PROVIDERS_CONFIG;
    private static final String SECURITY_PROVIDERS_DOC = SecurityConfig.SECURITY_PROVIDERS_DOC;

    static {
        CONFIG = new ConfigDef()
                .define(DIDI_KAFKA_ENABLE_CONFIG,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.HIGH,
                        DIDI_KAFKA_ENABLE_DOC)
                .define(DIDI_MIRROR_FETCHER_SPLIT_BATCHES_ENABLE_CONFIG,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.LOW,
                        DIDI_MIRROR_FETCHER_SPLIT_BATCHES_ENABLE_DOC)
                .define(BROKER_PROTOCOL_VERSION_CONFIG,
                        ConfigDef.Type.STRING,
                        Defaults.InterBrokerProtocolVersion(),
                        ApiVersionValidator$.MODULE$,
                        ConfigDef.Importance.MEDIUM,
                        BROKER_PROTOCOL_VERSION_DOC)
                .define(BOOTSTRAP_SERVERS_CONFIG,
                        ConfigDef.Type.LIST,
                        Collections.emptyList(),
                        ConfigDef.Importance.HIGH,
                        CommonClientConfigs.BOOTSTRAP_SERVERS_DOC)
                .define(SESSION_TIMEOUT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        10000,
                        ConfigDef.Importance.HIGH,
                        SESSION_TIMEOUT_MS_DOC)
                .define(METADATA_MAX_AGE_CONFIG,
                        ConfigDef.Type.LONG,
                        5 * 60 * 1000,
                        atLeast(0),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METADATA_MAX_AGE_DOC)
                .define(MAX_PARTITION_FETCH_BYTES_CONFIG,
                        ConfigDef.Type.INT,
                        DEFAULT_MAX_PARTITION_FETCH_BYTES,
                        atLeast(0),
                        ConfigDef.Importance.HIGH,
                        MAX_PARTITION_FETCH_BYTES_DOC)
                .define(SEND_BUFFER_CONFIG,
                        ConfigDef.Type.INT,
                        128 * 1024,
                        atLeast(CommonClientConfigs.SEND_BUFFER_LOWER_BOUND),
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.SEND_BUFFER_DOC)
                .define(RECEIVE_BUFFER_CONFIG,
                        ConfigDef.Type.INT,
                        64 * 1024,
                        atLeast(CommonClientConfigs.RECEIVE_BUFFER_LOWER_BOUND),
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.RECEIVE_BUFFER_DOC)
                .define(FETCH_MIN_BYTES_CONFIG,
                        ConfigDef.Type.INT,
                        1,
                        atLeast(0),
                        ConfigDef.Importance.HIGH,
                        FETCH_MIN_BYTES_DOC)
                .define(FETCH_MAX_BYTES_CONFIG,
                        ConfigDef.Type.INT,
                        DEFAULT_FETCH_MAX_BYTES,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        FETCH_MAX_BYTES_DOC)
                .define(FETCH_MAX_WAIT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        500,
                        atLeast(0),
                        ConfigDef.Importance.LOW,
                        FETCH_MAX_WAIT_MS_DOC)
                .define(FETCH_BACKOFF_MS_CONFIG,
                        ConfigDef.Type.INT,
                        1000,
                        atLeast(0),
                        ConfigDef.Importance.LOW,
                        FETCH_BACKOFF_MS_DOC)
                .define(RECONNECT_BACKOFF_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        50L,
                        atLeast(0L),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.RECONNECT_BACKOFF_MS_DOC)
                .define(RECONNECT_BACKOFF_MAX_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        1000L,
                        atLeast(0L),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_DOC)
                .define(RETRY_BACKOFF_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        100L,
                        atLeast(0L),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.RETRY_BACKOFF_MS_DOC)
                .define(REQUEST_TIMEOUT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        30000,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        REQUEST_TIMEOUT_MS_DOC)
                .define(DEFAULT_API_TIMEOUT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        60 * 1000,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.DEFAULT_API_TIMEOUT_MS_DOC)
                /* default is set to be a bit lower than the server default (10 min), to avoid both client and server closing connection at same time */
                .define(CONNECTIONS_MAX_IDLE_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        9 * 60 * 1000,
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_DOC)
                .define(MAX_POLL_RECORDS_CONFIG,
                        ConfigDef.Type.INT,
                        500,
                        atLeast(1),
                        ConfigDef.Importance.MEDIUM,
                        MAX_POLL_RECORDS_DOC)
                .define(MAX_POLL_INTERVAL_MS_CONFIG,
                        ConfigDef.Type.INT,
                        300000,
                        atLeast(1),
                        ConfigDef.Importance.MEDIUM,
                        MAX_POLL_INTERVAL_MS_DOC)
                // security support
                .define(SECURITY_PROVIDERS_CONFIG,
                        ConfigDef.Type.STRING,
                        null,
                        ConfigDef.Importance.LOW,
                        SECURITY_PROVIDERS_DOC)
                .define(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
                        ConfigDef.Type.STRING,
                        CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL,
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.SECURITY_PROTOCOL_DOC)
                .withClientSslSupport()
                .withClientSaslSupport();
    }

    @Override
    protected Map<String, Object> postProcessParsedConfig(final Map<String, Object> parsedValues) {
        return CommonClientConfigs.postProcessReconnectBackoffConfigs(this, parsedValues);
    }

    public HAClusterConfig(Properties props) {
        super(CONFIG, props);
    }

    public HAClusterConfig(Map<String, Object> props) {
        super(CONFIG, props);
    }

    protected HAClusterConfig(Map<?, ?> props, boolean doLog) {
        super(CONFIG, props, doLog);
    }

    public Boolean isDidiKafka() {
        return getBoolean(DIDI_KAFKA_ENABLE_CONFIG);
    }

    public Boolean isSplitFetchedBatches() {
        return getBoolean(DIDI_MIRROR_FETCHER_SPLIT_BATCHES_ENABLE_CONFIG);
    }

    public ApiVersion brokerProtocolVersion() {
        String brokerProtocolVersionString = getString(BROKER_PROTOCOL_VERSION_CONFIG);
        return ApiVersion.apply(brokerProtocolVersionString);
    }

    public static Set<String> configNames() {
        return CONFIG.names();
    }

    public static ConfigDef configDef() {
        return  new ConfigDef(CONFIG);
    }

    public static void main(String[] args) {
        System.out.println(CONFIG.toHtml());
    }
}
