package com.didichuxing.datachannel.kafka.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.kafka.util.KafkaUtils;
import com.didichuxing.datachannel.kafka.util.ScalaUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import kafka.server.KafkaConfig;
import kafka.utils.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.network.ListenerName;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.Seq;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KafkaGatewayTest extends kafka.integration.KafkaServerTestHarness {

    protected static final Logger log = LoggerFactory.getLogger(KafkaGatewayTest.class);

    private KafkaGatewayServics kafkaGatewayServics;

    private final String testUserName = "test_user";
    private final String testUserPassword = "12345";
    private final String testTopic = "test_0";

    private JSONObject sessionReportResult;

    @Override
    public void setUp() {
        createKafkaGatewayServices();
        super.setUp();
    }

    void createKafkaGatewayServices() {
        try {
            kafkaGatewayServics = new KafkaGatewayServics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(kafkaGatewayServics);
        assertNotNull(kafkaGatewayServics.getKafkaGatewayUrl());
    }

    @Override
    public Seq<KafkaConfig> generateConfigs() {
        Properties properties = TestUtils.createBrokerConfig(0, zkConnect(),
                true, true, TestUtils.RandomPort(), Option.apply(SecurityProtocol.SASL_PLAINTEXT),
                Option.apply(null), Option.apply(null), true, true, TestUtils.RandomPort(), false,
                TestUtils.RandomPort(), false, TestUtils.RandomPort(), Option.apply(null), 1, false, 1, (short) 1);
        properties.setProperty(KafkaConfig.GatewayUrlProp(), kafkaGatewayServics.getKafkaGatewayUrl());
        properties.setProperty(KafkaConfig.ClusterIdProp(), "0");
        properties.setProperty(KafkaConfig.SessionReportTimeMsProp(), "30000");
        properties.setProperty(KafkaConfig.KafkaExMetricsEnableAllProp(), "true");
        properties.setProperty("authorizer.class.name", "com.didichuxing.datachannel.kafka.security.authorizer.DidiAuthorizer");
        /*
        listeners=SASL_PLAINTEXT://:9093,PLAINTEXT://:9092

        security.inter.broker.protocol=SASL_PLAINTEXT
        #security.inter.broker.protocol=PLAINTEXT
        sasl.mechanism.inter.broker.protocol=PLAIN
        authorizer.class.name=com.didichuxing.datachannel.kafka.security.authorizer.DidiAuthorizer
         */
        configSaslServer(properties);
        var config = KafkaConfig.fromProps(properties);
        return ScalaUtil.toSeq(List.of(config));
    }


    private KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", TestUtils.bootstrapServers(servers(),
                ListenerName.forSecurityProtocol(SecurityProtocol.SASL_PLAINTEXT)));
        props.put("request.timeout.ms", "5000");
        //props.put("buffer.memory", 2*1024*1024);
        props.put("compression.type", "lz4"); //压缩方式
        //props.put("batch.size", 1024);
        props.put("linger.ms", 1000 );
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configSaslClient(props, "0", testUserName, testUserPassword);
        return new KafkaProducer<>(props);
    }

    private KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", TestUtils.bootstrapServers(servers(),
                ListenerName.forSecurityProtocol(SecurityProtocol.SASL_PLAINTEXT)));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "cg-007");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 999);
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configSaslClient(props, "0", testUserName, testUserPassword);
        return new KafkaConsumer<String, String>(props);
    }

    private static void configSaslServer(Properties properties) {
        properties.put("sasl.enabled.mechanisms", "PLAIN"); //安全认证机制
        properties.put("sasl.mechanism.inter.broker.protocol", "PLAIN");
        String jaas_config = "com.didichuxing.datachannel.kafka.security.sasl.plain.PlainLoginModule " +
                "required username=\"admin\" password=\"*\";";
        properties.put("listener.name.sasl_plaintext.plain.sasl.jaas.config", jaas_config);
    }

    private static void configSaslClient(Properties properties, String clusterId, String username, String password) {
        properties.put("security.protocol", "SASL_PLAINTEXT"); //安全认证协议
        properties.put("sasl.mechanism", "PLAIN"); //安全认证机制
        String format = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s.%s\" password=\"%s\";";
        String jaas_config = String.format(format, clusterId, username, password);
        properties.put("sasl.jaas.config", jaas_config);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        kafkaGatewayServics.stop();
    }

    @Test
    public void testProducerAndConsumer() throws Exception {
        long startTime = System.currentTimeMillis();
        TestUtils.createTopic(zkClient(), testTopic, 1, 1, servers(), new Properties());

        int numRecords = 50;
        final int[] success = {0};
        var producer = createProducer();
        for (int i = 0; i < numRecords; i++) {
            final int index = i;
            var f = producer.send(new ProducerRecord<String, String>(testTopic, "hellow world"), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (null != e) {
                        System.out.println(e.getMessage());
                    } else {
                        success[0]++;
                        assertEquals(recordMetadata.offset(), index);
                    }
                }
            });
            f.get();
        }
        producer.close();
        assertEquals(numRecords, success[0]);

        KafkaConsumer<String, String> consumer = createConsumer();
        consumer.subscribe(List.of(testTopic));
        for (int i=0; i<success[0]; ){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                assertEquals(record.offset(), i);
                i++;
            }
        }
        consumer.close();
        verifySessionReport();
    }

    private void verifySessionReport() throws Exception{
        synchronized (kafkaGatewayServics) {
            kafkaGatewayServics.wait(30000);
        }
        assertNotNull(sessionReportResult);
        JSONObject produceInfo = sessionReportResult.getJSONObject("produce");
        JSONArray connInfo = produceInfo.getJSONArray(testTopic);
        String value = connInfo.getString(0);
        assertEquals(value, "test_user#127.0.0.1#" +
                KafkaUtils.apiVersionToKafkaVersion(ApiKeys.PRODUCE.id, ApiKeys.PRODUCE.latestVersion()) +
                "#consumer-cg-007-1"
        );
        JSONObject fetchInfo = sessionReportResult.getJSONObject("fetch");
        connInfo = fetchInfo.getJSONArray(testTopic);
        value = connInfo.getString(0);
        assertEquals(value, "test_user#127.0.0.1#" +
                KafkaUtils.apiVersionToKafkaVersion(ApiKeys.FETCH.id, ApiKeys.FETCH.latestVersion()) +
                "#consumer-cg-007-1"
        );
        sessionReportResult = null;
    }

    class KafkaGatewayServics {
        private final Thread httpServerThread;
        private final HttpServer httpServer;
        private final String kafkaGatewayUrl;

        public KafkaGatewayServics() throws IOException {
            httpServer = HttpServer.create(new InetSocketAddress(0), 0);
            httpServer.createContext("/api/v1/security/users", (HttpExchange ex) -> {
                try {
                    String requestBody = new String(ex.getRequestBody().readAllBytes());
                    JSONObject jsonObject = JSONObject.parseObject(requestBody);
                    log.info("recieve request {} {}", ex.getRequestURI(), jsonObject.toJSONString());
                    JSONArray users = new JSONArray();
                    JSONObject user = new JSONObject();
                    user.put("username", testUserName);
                    user.put("password", testUserPassword);
                    user.put("userType", "0");
                    user.put("timestamp", System.currentTimeMillis() - 5000);
                    user.put("operation", "0");
                    users.add(user);
                    JSONObject resp = new JSONObject();
                    resp.put("code", 0);
                    resp.put("message", "");
                    JSONObject data = new JSONObject();
                    data.put("rows", users);
                    resp.put("data", data);
                    String respData = resp.toJSONString();
                    ex.sendResponseHeaders(200, respData.length());
                    var output = ex.getResponseBody();
                    output.write(respData.getBytes());
                    ex.close();
                }catch(Exception e) {
                    log.info("handle request exception: ", e);
                }
            });
            httpServer.createContext("/api/v1/security/acls", (HttpExchange ex) -> {
                try {
                    String requestBody = new String(ex.getRequestBody().readAllBytes());
                    JSONObject jsonObject = JSONObject.parseObject(requestBody);
                    log.info("recieve request {} {}", ex.getRequestURI(), jsonObject.toJSONString());
                    JSONArray acls = new JSONArray();
                    JSONObject acl = new JSONObject();
                    acl.put("topicName", testTopic);
                    acl.put("username", testUserName);
                    acl.put("access", "3");
                    acl.put("timestamp", System.currentTimeMillis()-5000);
                    acl.put("operation", "0");
                    acls.add(acl);
                    JSONObject resp = new JSONObject();
                    resp.put("code", 0);
                    resp.put("message", "");
                    JSONObject data = new JSONObject();
                    data.put("rows", acls);
                    resp.put("data", data);
                    String respData = resp.toJSONString();
                    ex.sendResponseHeaders(200, respData.length());
                    var output = ex.getResponseBody();
                    output.write(respData.getBytes());
                    ex.close();
                }catch (Exception e) {
                    log.info("handle request exception: ", e);
                }
        });
            httpServer.createContext("/api/v1/heartbeat/survive-user", (HttpExchange ex) -> {
                try {
                    String requestBody = new String(ex.getRequestBody().readAllBytes());
                    JSONObject jsonObject = JSONObject.parseObject(requestBody);
                    log.info("recieve request {} {}", ex.getRequestURI(), jsonObject.toJSONString());
                    sessionReportResult = jsonObject;
                    synchronized (kafkaGatewayServics) {
                        kafkaGatewayServics.notify();
                    }
                    JSONObject resp = new JSONObject();
                    resp.put("code", 0);
                    resp.put("message", "");
                    resp.put("data", "");
                    String respData = resp.toJSONString();
                    ex.sendResponseHeaders(200, respData.length());
                    var output = ex.getResponseBody();
                    output.write(respData.getBytes());
                    output.close();
                    ex.close();
                }catch (Exception e) {
                    log.info("handle request exception: ", e);
                }
            });
            httpServer.setExecutor(null);

            httpServerThread = new Thread(httpServer::start);
            var adders = httpServer.getAddress();
            kafkaGatewayUrl = String.format("http://localhost:%s", adders.getPort());
            httpServerThread.start();
        }

        public void stop() {
            try {
                httpServer.stop(0);
                httpServerThread.join();
            }catch (Exception e) {
                log.error("stop exception: ", e);
            }
        }

        public String getKafkaGatewayUrl() {
            return kafkaGatewayUrl;
        }
    }
}
