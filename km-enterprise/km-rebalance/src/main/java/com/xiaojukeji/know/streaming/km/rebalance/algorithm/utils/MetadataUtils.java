package com.xiaojukeji.know.streaming.km.rebalance.algorithm.utils;

import org.apache.kafka.clients.*;
import org.apache.kafka.clients.admin.LogDirDescription;
import org.apache.kafka.clients.admin.ReplicaInfo;
import org.apache.kafka.clients.consumer.internals.NoAvailableBrokersException;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.message.DescribeLogDirsRequestData;
import org.apache.kafka.common.message.DescribeLogDirsResponseData;
import org.apache.kafka.common.network.ChannelBuilder;
import org.apache.kafka.common.network.NetworkReceive;
import org.apache.kafka.common.network.Selector;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.*;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author leewei
 * @date 2022/5/27
 */
public class MetadataUtils {
    private static final Logger logger = LoggerFactory.getLogger(MetadataUtils.class);

    public static Cluster metadata(Properties props) {
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.BytesSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.BytesSerializer");
        ProducerConfig config = new ProducerConfig(props);

        Time time = Time.SYSTEM;
        LogContext logContext = new LogContext("Metadata client");

        ChannelBuilder channelBuilder = ClientUtils.createChannelBuilder(config, time, logContext);
        Selector selector = new Selector(
                NetworkReceive.UNLIMITED,
                config.getLong(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG),
                new org.apache.kafka.common.metrics.Metrics(),
                time,
                "metadata-client",
                Collections.singletonMap("client", "metadata-client"),
                false,
                channelBuilder,
                logContext
        );

        NetworkClient networkClient = new NetworkClient(
                selector,
                new ManualMetadataUpdater(),
                "metadata-client",
                1,
                config.getLong(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG),
                config.getLong(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG),
                config.getInt(ProducerConfig.SEND_BUFFER_CONFIG),
                config.getInt(ProducerConfig.RECEIVE_BUFFER_CONFIG),
                config.getInt(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG),
                config.getLong(ProducerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG),
                config.getLong(ProducerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_CONFIG),
                ClientDnsLookup.DEFAULT,
                time,
                true,
                new ApiVersions(),
                logContext
        );

        try {
            List<String> nodes = config.getList(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);
            for (int i = 0; i < nodes.size(); i++) {
                Node sourceNode = new Node(i, Utils.getHost(nodes.get(i)), Utils.getPort(nodes.get(i)));
                try {
                    if (NetworkClientUtils.awaitReady(networkClient, sourceNode, time, 10 * 1000)) {
                        ClientRequest clientRequest = networkClient.newClientRequest(String.valueOf(i), MetadataRequest.Builder.allTopics(),
                                time.milliseconds(), true);
                        ClientResponse clientResponse = NetworkClientUtils.sendAndReceive(networkClient, clientRequest, time);
                        MetadataResponse metadataResponse = (MetadataResponse) clientResponse.responseBody();
                        return metadataResponse.buildCluster();
                    }
                } catch (IOException e) {
                    logger.warn("Connection to " + sourceNode + " error", e);
                }
            }
            throw new NoAvailableBrokersException();
        } finally {
            networkClient.close();
        }
    }




    public static Map<Integer, Map<String, LogDirDescription>> describeLogDirs(Properties props, List<Node> nodes) {
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.BytesSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.BytesSerializer");
        ProducerConfig config = new ProducerConfig(props);

        Time time = Time.SYSTEM;
        LogContext logContext = new LogContext("Metadata client");

        ChannelBuilder channelBuilder = ClientUtils.createChannelBuilder(config, time, logContext);
        Selector selector = new Selector(
                NetworkReceive.UNLIMITED,
                config.getLong(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG),
                new org.apache.kafka.common.metrics.Metrics(),
                time,
                "metadata-client",
                Collections.singletonMap("client", "metadata-client"),
                false,
                channelBuilder,
                logContext
        );

        NetworkClient networkClient = new NetworkClient(
                selector,
                new ManualMetadataUpdater(),
                "metadata-client",
                1,
                config.getLong(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG),
                config.getLong(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG),
                config.getInt(ProducerConfig.SEND_BUFFER_CONFIG),
                config.getInt(ProducerConfig.RECEIVE_BUFFER_CONFIG),
                config.getInt(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG),
                config.getLong(ProducerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG),
                config.getLong(ProducerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_CONFIG),
                ClientDnsLookup.DEFAULT,
                time,
                true,
                new ApiVersions(),
                logContext
        );

        Map<Integer, Map<String, LogDirDescription>> allDescriptions = new HashMap<>();
        try {
            for (int i = 0; i < nodes.size(); i++) {
                Node sourceNode = nodes.get(i);
                try {
                    if (NetworkClientUtils.awaitReady(networkClient, sourceNode, time, 10 * 1000)) {
                        ClientRequest describeLogDirsRequest = networkClient.newClientRequest(String.valueOf(sourceNode.id()), new DescribeLogDirsRequest.Builder(new DescribeLogDirsRequestData().setTopics(null)), time.milliseconds(), true);
                        ClientResponse describeLogDirsResponse = NetworkClientUtils.sendAndReceive(networkClient, describeLogDirsRequest, time);
                        DescribeLogDirsResponse logDirsResponse = (DescribeLogDirsResponse)describeLogDirsResponse.responseBody();
                        Map<String, LogDirDescription> descriptions = logDirDescriptions(logDirsResponse);
                        allDescriptions.put(sourceNode.id(), descriptions);
                    }
                } catch (IOException e) {
                    logger.warn("Connection to " + sourceNode + " error", e);
                    throw new NoAvailableBrokersException();
                }
            }
        } finally {
            networkClient.close();
        }
        return allDescriptions;
    }


    private static Map<String, LogDirDescription> logDirDescriptions(DescribeLogDirsResponse response) {
        Map<String, LogDirDescription> result = new HashMap<>(response.data().results().size());
        for (DescribeLogDirsResponseData.DescribeLogDirsResult logDirResult : response.data().results()) {
            Map<TopicPartition, ReplicaInfo> replicaInfoMap = new HashMap<>();
            for (DescribeLogDirsResponseData.DescribeLogDirsTopic t : logDirResult.topics()) {
                for (DescribeLogDirsResponseData.DescribeLogDirsPartition p : t.partitions()) {
                    replicaInfoMap.put(
                            new TopicPartition(t.name(), p.partitionIndex()),
                            new ReplicaInfo(p.partitionSize(), p.offsetLag(), p.isFutureKey()));
                }
            }
            result.put(logDirResult.logDir(), new LogDirDescription(Errors.forCode(logDirResult.errorCode()).exception(), replicaInfoMap));
        }
        return result;
    }
}
