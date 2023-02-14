# Java客户端 接入 Kafka

## Producer

```java
public class ProducerTest {
    private static String topicName;
    private static int msgNum;
    private static int key;
    public static void main(String[] args) {
        Properties props = new Properties(); 
        //请填写正确的bootstrap server地址
        props.put("bootstrap.servers", "****bootstrap.servers****"); //填写服务发现地址
        props.put("compression.type", "lz4"); //压缩方式，在平衡存储与cpu使用率后推荐使用lz4
        props.put("linger.ms", 500 ); // 建议500,务必要改
        props.put("batch.size", 100000 );//每个请求的批量大小
        props.put("max.in.flight.requests.per.connection", 1 ); 如果需要保证消息顺序，需要设置为1，默认为5
        props.put("security.protocol", "SASL_PLAINTEXT");//注意安全管控的security.protocol内容
        props.put("sasl.mechanism", "PLAIN");//注意安全管控的security.protocol内容
        //请填写正确的clusterId，appId，密码  clusterId对应关系见上表
        String format = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s.%s\" password=\"%s\";";
        String jaas_config = String.format(format, {clusterId}, {appId}, {password});
        props.put("sasl.jaas.config", jaas_config);
        //根据实际场景选择序列化类
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        topicName = "test";
        msgNum = *; // 发送的消息数
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < msgNum; i++) {
            String msg = i + " This is prodecer test.";
            producer.send(new ProducerRecord<String, String>(topicName, msg));
        }
        producer.close();
    }
}
  
PS：相比之前版本，使用gateway版本需要新增三个参数：
props.put("security.protocol", "SASL_PLAINTEXT");
props.put("sasl.mechanism", "PLAIN");
props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
    "username=\"clusterId.appId\" password=\"password\";");
```


## Consumer

```java
public class ConsumerTest {
    private static String topicName;
    private static String group;
    public static void main(String[] args) {
        Properties props = new Properties();
        //请填写正确的bootstrap server地址
        props.put("bootstrap.servers", "****bootstrap.servers****");//填写服务发现地址
        props.put("group.id", group);//group不需要申请，根据自己业务属性起名字
        props.put("auto.offset.reset", "latest");//earliest/latest消息消费起始位置，earliest代表消费历史数据，latest代表消费最新的数据
        props.put("enable.auto.commit", "true"); // 自动commit
        props.put("auto.commit.interval.ms", "1000"); // 自动commit的间隔
        props.put("session.timeout.ms", "30000");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        //请填写正确的clusterId，appId，密码
        String format = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s.%s\" password=\"%s\";";
        String jaas_config = String.format(format, {clusterId}, {appId}, {password});
        props.put("sasl.jaas.config", jaas_config);
        //根据实际场景选择序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topicName)); // 可消费多个topic,组成一个list
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("offset = " + record.offset() + ", key = " + record.key() + ", value = " + record.value());
                }
            }catch (Throwable e){
                //TODO print  your error,特别注意这里的poll 可能因为网络问题等原因发生异常,不能catch到异常后就close KafkaConsumer实例,否则无法继续消费
            }
        }
    }
}
  
PS：相比之前版本，使用gateway版本需要新增三个参数：
props.put("security.protocol", "SASL_PLAINTEXT");
props.put("sasl.mechanism", "PLAIN");
props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
    "username=\"clusterId.appId\" password=\"password\";");
```