# Python客户端 接入 Kafka

## 安装

```shell
# python kafka客户端采用kafka-python(1.4.6)及以上
pip install kafka-python

#使用lz4压缩需要
pip install lz4

#使用snappy压缩需要
pip install snappy
pip install python-snappy
```

## 发送例子

```python
from kafka import KafkaConsumer,KafkaProducer
import logging
import time
import json
#logging.basicConfig(level=logging.DEBUG)
BOOTSTRAP_SERVERS='127.0.0.1:9093'
TOPIC='test0'
 
producer=KafkaProducer(bootstrap_servers=BOOTSTRAP_SERVERS,
                       compression_type="lz4",
                       security_protocol="SASL_PLAINTEXT",
                       sasl_mechanism="PLAIN",
                       sasl_plain_username='95.appId_000001',    //clusterId.appId
                       sasl_plain_password='12345'
)
for i in range(10):
    producer.send(TOPIC, bytes("Hello World".encode('utf-8')))
producer.flush()
```


## 消费例子

```python
from kafka import KafkaConsumer,KafkaProducer
import logging
import time
import json
#logging.basicConfig(level=logging.DEBUG)
BOOTSTRAP_SERVERS='127.0.0.1:9093'
TOPIC='test0'
consumer = KafkaConsumer(TOPIC,
                        group_id = 'test_group'
                        bootstrap_servers=BOOTSTRAP_SERVERS,
                        auto_offset_reset='earliest',
                        security_protocol='SASL_PLAINTEXT',
                        sasl_mechanism='PLAIN',
                        sasl_plain_username='95.appId_000001',     //clusterId.appId
                        sasl_plain_password='12345',
                        receive_buffer_bytes=1024,
                        enable_auto_commit='False')
 
 
for msg in consumer:
    print(msg)
 
consumer.commit()
```