# C++客户端 接入 Kafka

- 使用`librdkafka`

## 发送例子

```cpp
#include <iostream>
#include <string>
#include <cstdlib>
#include <cstdio>
#include <csignal>
#include <cstring>
#include <getopt.h>
#include "rdkafkacpp.h"

static bool run = true;

static void sigterm (int sig) {
    run = false;
}

class ExampleDeliveryReportCb : public RdKafka::DeliveryReportCb {
public:
    void dr_cb (RdKafka::Message &message) {
        std::cout << "Message delivery for (" << message.len() << " bytes): " <<
                     message.errstr() << std::endl;
        if (message.key())
            std::cout << "Key: " << *(message.key()) << ";" << std::endl;
    }
};

class ExampleEventCb : public RdKafka::EventCb {
public:
    void event_cb (RdKafka::Event &event) {
        switch (event.type())
        {
        case RdKafka::Event::EVENT_ERROR:
            std::cerr << "ERROR (" << RdKafka::err2str(event.err()) << "): " <<
                         event.str() << std::endl;
            if (event.err() == RdKafka::ERR__ALL_BROKERS_DOWN)
                run = false;
            break;
        case RdKafka::Event::EVENT_STATS:
            std::cerr << "\"STATS\": " << event.str() << std::endl;
            break;
        case RdKafka::Event::EVENT_LOG:
            fprintf(stderr, "LOG-%i-%s: %s\n",
                    event.severity(), event.fac().c_str(), event.str().c_str());
            break;
        default:
            std::cerr << "EVENT " << event.type() <<
                         " (" << RdKafka::err2str(event.err()) << "): " <<
                         event.str() << std::endl;
            break;
        }
    }
};

int main (int argc, char **argv)
{
    if (argc != 5)  {
        return 0;
        std::cout << "missing args" << std::endl;
    }
    std::string brokers = argv[1];
    std::string topic_str = argv[2];
    std::string username = argv[3];
    std::string password = argv[4];
    std::string errstr;
    int32_t partition = RdKafka::Topic::PARTITION_UA;
    RdKafka::Conf *conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
    RdKafka::Conf *tconf = RdKafka::Conf::create(RdKafka::Conf::CONF_TOPIC);
    conf->set("bootstrap.servers", brokers, errstr);
    conf->set("security.protocol", "SASL_PLAINTEXT", errstr);
    conf->set("sasl.mechanisms", "PLAIN", errstr);
    conf->set("sasl.username", username, errstr);
    conf->set("sasl.password", password, errstr);
    conf->set("batch.num.messages", 40000, errstr);
    conf->set("linger.ms", 2000, errstr);
    conf->set("sasl.debug", "topic", errstr);
    ExampleEventCb ex_event_cb;
    conf->set("event_cb", &ex_event_cb, errstr);
    signal(SIGINT, sigterm);
    signal(SIGTERM, sigterm);
    ExampleDeliveryReportCb ex_dr_cb;
    conf->set("dr_cb", &ex_dr_cb, errstr);
    RdKafka::Producer *producer = RdKafka::Producer::create(conf, errstr);
    if (!producer) {
        std::cerr << "Failed to create producer: " << errstr << std::endl;
        exit(1);
    }
    std::cout << "% Created producer " << producer->name() << std::endl;
    RdKafka::Topic *topic = RdKafka::Topic::create(producer, topic_str,
                                                   tconf, errstr);
    if (!topic) {
        std::cerr << "Failed to create topic: " << errstr << std::endl;
        exit(1);
    }
    for (std::string line; run && std::getline(std::cin, line);) {
        if (line.empty()) {
            producer->poll(0);
            continue;
        }
        RdKafka::ErrorCode resp =
                producer->produce(topic, partition,
                                  RdKafka::Producer::RK_MSG_COPY /* Copy payload */,
                                  const_cast<char *>(line.c_str()), line.size(),
                                  NULL, NULL);
        if (resp != RdKafka::ERR_NO_ERROR)
            std::cerr << "% Produce failed: " <<
                         RdKafka::err2str(resp) << std::endl;
        else
            std::cerr << "% Produced message (" << line.size() << " bytes)" <<
                         std::endl;
        producer->poll(0);
    }
    run = true;
   // 退出前处理完输出队列中的消息
    while (run && producer->outq_len() > 0) {
        std::cerr << "Waiting for " << producer->outq_len() << std::endl;
        producer->poll(1000);
    }
    delete conf;
    delete tconf;
    delete topic;
    delete producer;
    RdKafka::wait_destroyed(5000);
    return 0;
}
//g++ producer.cpp ~/git/librdkafka/src-cpp/librdkafka++.a ~/git/librdkafka/src/librdkafka.a -I ~/git/librdkafka/src-cpp -lm -lsasl2   -lssl -lcrypto   -lcrypto  -lz  -ldl -lpthread -lrt
//./a.out 127.0.0.1:9093 test2 95.test0 12345
```


## 消费例子

```cpp
#include <iostream>
#include <string>
#include <cstdlib>
#include <cstdio>
#include <csignal>
#include <cstring>
#include <string>
 
#include <sys/time.h>
#include <getopt.h>
#include <unistd.h>
 
#include "rdkafkacpp.h"
 
static bool run = true;
static bool exit_eof = true;
static int eof_cnt = 0;
static int partition_cnt = 0;
static int verbosity = 1;
static long msg_cnt = 0;
static int64_t msg_bytes = 0;
 
static void sigterm (int sig) {
    run = false;
}
 
class ExampleEventCb : public RdKafka::EventCb {
public:
    void event_cb (RdKafka::Event &event) {
        switch (event.type())
        {
        case RdKafka::Event::EVENT_ERROR:
            std::cerr << "ERROR (" << RdKafka::err2str(event.err()) << "): " <<
                         event.str() << std::endl;
            //if (event.err() == RdKafka::ERR__ALL_BROKERS_DOWN)
            //    run = false;
            break;
 
        case RdKafka::Event::EVENT_STATS:
            std::cerr << "\"STATS\": " << event.str() << std::endl;
            break;
 
        case RdKafka::Event::EVENT_LOG:
            fprintf(stderr, "LOG-%i-%s: %s\n",
                    event.severity(), event.fac().c_str(), event.str().c_str());
            break;
 
        case RdKafka::Event::EVENT_THROTTLE:
            std::cerr << "THROTTLED: " << event.throttle_time() << "ms by " <<
                         event.broker_name() << " id " << (int)event.broker_id() << std::endl;
            break;
 
        default:
            std::cerr << "EVENT " << event.type() <<
                         " (" << RdKafka::err2str(event.err()) << "): " <<
                         event.str() << std::endl;
            break;
        }
    }
};
 
void msg_consume(RdKafka::Message* message, void* opaque) {
    switch (message->err()) {
    case RdKafka::ERR__TIMED_OUT:
        //std::cerr << "RdKafka::ERR__TIMED_OUT"<<std::endl;
        break;
 
    case RdKafka::ERR_NO_ERROR:
        /* Real message */
        msg_cnt++;
        msg_bytes += message->len();
        if (verbosity >= 3)
            std::cerr << "Read msg at offset " << message->offset() << std::endl;
        RdKafka::MessageTimestamp ts;
        ts = message->timestamp();
        if (verbosity >= 2 &&
                ts.type != RdKafka::MessageTimestamp::MSG_TIMESTAMP_NOT_AVAILABLE) {
            std::string tsname = "?";
            if (ts.type == RdKafka::MessageTimestamp::MSG_TIMESTAMP_CREATE_TIME)
                tsname = "create time";
            else if (ts.type == RdKafka::MessageTimestamp::MSG_TIMESTAMP_LOG_APPEND_TIME)
                tsname = "log append time";
            std::cout << "Timestamp: " << tsname << " " << ts.timestamp << std::endl;
        }
        if (verbosity >= 2 && message->key()) {
            std::cout << "Key: " << *message->key() << std::endl;
        }
        if (verbosity >= 1) {
            printf("%.*s\n",
                   static_cast<int>(message->len()),
                   static_cast<const char *>(message->payload()));
        }
        break;
 
    case RdKafka::ERR__PARTITION_EOF:
        /* Last message */
        if (exit_eof && ++eof_cnt == partition_cnt) {
            std::cerr << "%% EOF reached for all " << partition_cnt <<
                         " partition(s)" << std::endl;
            run = false;
        }
        break;
 
    case RdKafka::ERR__UNKNOWN_TOPIC:
    case RdKafka::ERR__UNKNOWN_PARTITION:
        std::cerr << "Consume failed: " << message->errstr() << std::endl;
        run = false;
        break;
 
    default:
        /* Errors */
        std::cerr << "Consume failed: " << message->errstr() << std::endl;
        run = false;
    }
}
 
class ExampleConsumeCb : public RdKafka::ConsumeCb {
public:
    void consume_cb (RdKafka::Message &msg, void *opaque) {
        msg_consume(&msg, opaque);
    }
};
 
int main (int argc, char **argv) {
    if (argc != 5)  {
        std::cout << "missing args. ex: ./consume.out bootstrap topic user password" << std::endl;
        return 0;
    }
 
    std::string brokers = argv[1];
    std::string topic_str = argv[2];
    std::string username = argv[3];
    std::string password = argv[4];
 
    std::string errstr;
    std::vector<std::string> topics;
    std::string group_id="cg_104" + std::to_string(time(NULL));
 
    RdKafka::Conf *conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
    RdKafka::Conf *tconf = RdKafka::Conf::create(RdKafka::Conf::CONF_TOPIC);
    //group.id必须设置
    if (conf->set("group.id", group_id, errstr) != RdKafka::Conf::CONF_OK) {
           std::cerr << errstr << std::endl;
           exit(1);
         }
 
    topics.push_back(topic_str);
   //bootstrap.servers可以替换为metadata.broker.list
    conf->set("bootstrap.servers", brokers, errstr);
 
    conf->set("security.protocol", "SASL_PLAINTEXT", errstr);
    conf->set("sasl.mechanisms", "PLAIN", errstr);
    conf->set("sasl.username", username, errstr);
    conf->set("sasl.password", password, errstr);
    conf->set("debug", "topic,broker", errstr);
    conf->set("api.version.request", "true", errstr);
 
    //ExampleConsumeCb ex_consume_cb;
    //conf->set("consume_cb", &ex_consume_cb, errstr);
    //ExampleEventCb ex_event_cb;
    //conf->set("event_cb", &ex_event_cb, errstr);
 
    tconf->set("auto.offset.reset", "smallest", errstr);
 
    conf->set("default_topic_conf", tconf, errstr);
 
    signal(SIGINT, sigterm);
    signal(SIGTERM, sigterm);
 
    RdKafka::KafkaConsumer *consumer = RdKafka::KafkaConsumer::create(conf, errstr);
    if (!consumer) {
        std::cerr << "Failed to create consumer: " << errstr << std::endl;
        exit(1);
    }
    std::cout << "% Created consumer " << consumer->name() << std::endl;
 
    RdKafka::ErrorCode err = consumer->subscribe(topics);
    if (err) {
        std::cerr << "Failed to subscribe to " << topics.size() << " topics: "
                  << RdKafka::err2str(err) << std::endl;
        exit(1);
    }
 
    while (run) {
        //5000毫秒未订阅到消息，触发RdKafka::ERR__TIMED_OUT
        RdKafka::Message *msg = consumer->consume(5000);
        msg_consume(msg, NULL);
        delete msg;
    }
 
    consumer->close();
 
    delete conf;
    delete tconf;
    delete consumer;
 
    std::cerr << "% Consumed " << msg_cnt << " messages ("
              << msg_bytes << " bytes)" << std::endl;
 
    //应用退出之前等待rdkafka清理资源
    RdKafka::wait_destroyed(5000);
 
    return 0;
}
 
//g++ consumer.cpp ~/git/librdkafka/src-cpp/librdkafka++.a ~/git/librdkafka/src/librdkafka.a -I ~/git/librdkafka/src-cpp -lm -lsasl2   -lssl -lcrypto   -lcrypto  -lz  -ldl -lpthread -lrt -o consume.out
//./consume.out 127.0.0.1:9093 test2 95.test0 12345
```