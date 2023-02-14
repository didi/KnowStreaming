# Java客户端 消息顺序写入 Kafka例子

## 发送例子

例子中，客户端有序发送最重要的配置是`max.in.flight.requests.per.connection=1`。

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.producer.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class SequentialProducer {

    private static Properties createProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "**kafka集群服务地址***");

        properties.put("acks", "all");
        properties.put("max.in.flight.requests.per.connection", 1); // 顺序发送必须加上
        properties.put("retries", 30);
        properties.put("request.timeout.ms", 12000);
        properties.put("linger.ms", 10);
        properties.put("batch.size", 65536);

        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 安全管控配置，如若没有可进行去除下面三个配置
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"{clusterId}.{username}\" password=\"{password}\";");

        return properties;
    }

    private static boolean sendRecords(KafkaProducer<String, String> producer, List<ProducerRecord<String, String>> records) {
        final CountDownLatch countDownLatch = new CountDownLatch(records.size());
        final AtomicBoolean  failedFlag     = new AtomicBoolean(Boolean.FALSE);

        for (ProducerRecord<String, String> record : records) {
            System.out.println(record.value());
            producer.send(new ProducerRecord<String, String>(record.topic(), record.key(), record.value()), new Callback() {

                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    // 发送完成后的回调, 如果exception != null 表示发送失败了
                    if (exception != null) {
                        // 发送失败 -> 设置发送失败标记
                        failedFlag.set(Boolean.TRUE);
                    }
                    countDownLatch.countDown();
                }
            });
        }

        boolean success = false;
        try {
            // 等待 60 秒超时
            success = countDownLatch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (failedFlag.get()) {
            success = false;
        }
        return success;
    }

    public static void main(String[] args){
        String mysqlTableName = "DB_Table_Name";

        // kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(createProperties());

        // 需要发送的数据数组
        List<ProducerRecord<String, String>> records = new ArrayList<ProducerRecord<String, String>>();

        // 发送的结果
        boolean status = true;

        long mysqlTableId = 1;
        while (true) {
            if (status) {
                // 发送成功, 则清除上一次的数据, 然后获取新数据,
                records.clear();
                for (int i = 0; i < 5; ++i) {
                    // 构造数据时, 需要指定key, key可以是mysql表的表名, 这样同个表的数据将会发送到同一个分区
                    records.add(new ProducerRecord<String, String>("{topicName}", mysqlTableName, "hello kafka, id=" + mysqlTableId++));
                }
            } else {
                // 发送失败, 则不做任何事情, 即继续尝试上一批数据的发送
                System.out.println("send failed, ------- records:" + records.get(0));
            }

            status = sendRecords(producer, records);
        }
    }
}
```

