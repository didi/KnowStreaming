![kafka-manger-logo](../../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

---

# 2. Kafka Go 示例

## 1. 简介

我们的`golang kafka`客户端采用`sarama+sarama-cluster`。
文档：

- https://godoc.org/github.com/Shopify/sarama

- https://github.com/bsm/sarama-cluster

滴滴内部对`sarama`和`sarama-cluster`进行了一些改进，我们使用`go`客户端生产和消费时候使用滴滴内部的`sarama`和`sarama-cluster`，否则可能出现无法消费等不可预知的情况;

## 2. 申请资源

使用前请确保有下列资源，如果可以跳过！

- topic（[Topic 申请](../5. Kafka 云平台——资源申请/Topic 申请.md)）
- appId和password（[应用申请](../5. Kafka 云平台——资源申请/应用申请.md)）
- clusterId（可以在Topic详情中查看）

**程序启动前请确保AppID具有Topic的使用权限！**

## 3. 导入依赖

项目代码中必须引入`import`

**！！！注意：在项目中必须使用我们提供的sarama和sarama-cluster**

```go
import (
    "git.xiaojukeji.com/bigdata-databus/sarama-cluster"
    "git.xiaojukeji.com/bigdata-databus/sarama" //support automatic consumer-group rebalancing and offset tracking
)
```

## 4. 实例

如果需要开启`kafka`日志，请在代码中添加：

```go
var logger = log.New(os.Stdout, "kafka", log.LstdFlags)
sarama.Logger = logger
```

### 4.1 Producer发送实例

推荐采用异步发送的方式

```go
import (
    "git.xiaojukeji.com/bigdata-databus/sarama-cluster"
    "git.xiaojukeji.com/bigdata-databus/sarama" //support automatic consumer-group rebalancing and offset tracking
)
 
// asyncProducer 异步生产者
// 并发量大时，必须采用这种方式
func asyncProducer() {
    //请填写正确的bootstrap server地址,参考本文第三项找到正确的连接地址
    bootstrap := "****bootstrap.servers****";
    config := sarama.NewConfig()
    config.Version = sarama.V0_10_2_0; //注意版本
    config.Producer.Compression = sarama.CompressionSnappy; //注意压缩方式
    config.Producer.Return.Successes = true //必须有这个选项
    config.Producer.Timeout = 5 * time.Second
    //如果是异步发送建议按照配置config.Producer.Flush.Bytes=100 * 1024和config.Producer.Flush.Frequency=1000 * time.Millisecond
    config.Producer.Flush.Bytes = 100 * 1024//到了100k则批量发送
    config.Producer.Flush.Frequency = 1000 * time.Millisecond//不到100k但是到了1秒则发送
  
    //请填写正确的clusterId，appId，密码
    config.Net.SASL.User = "****clusterId****.****AppId****"； //clusterId对应关系见上表，eg:44.appId_000
    config.Net.SASL.Password = "****password****"
    config.Net.SASL.Handshake = true
    config.Net.SASL.Enable = true
    topic := "xxx" //topic
 
    p, err := sarama.NewAsyncProducer(strings.Split(bootstrap, ","), config)
    defer p.Close()
    if err != nil {
        return
    }
 
    //必须有这个匿名函数内容
    go func(p sarama.AsyncProducer) {
        errors := p.Errors()
        success := p.Successes()
        for {
            select {
            case err := <-errors:
                if err != nil {
                    glog.Errorln(err)
                }
            case <-success:
            }
        }
    }(p)
 
    v := "async: " + strconv.Itoa(rand.New(rand.NewSource(time.Now().UnixNano())).Intn(10000))
    fmt.Fprintln(os.Stdout, v)
    msg := &sarama.ProducerMessage{
        Topic: topic,
        Value: sarama.ByteEncoder(v),
    }
    p.Input() <- msg
}
```

### 4.2 consumer消费实例
**consumer 用 sarama-cluster，能够提供 consumer rebalance 和 offset track！**
使用过程中，不要用同一个消费组消费多个`topic`，否则会造成删除其中一个`topic`时，影响其他`topic`消费。

```go
import (
    "git.xiaojukeji.com/bigdata-databus/sarama-cluster"
    "git.xiaojukeji.com/bigdata-databus/sarama" //support automatic consumer-group rebalancing and offset tracking
)
 
// consumer 消费者、
func consumer() {
    //请填写正确的bootstrap server地址,参考本文第三项找到正确的连接地址
    bootstrap := "****bootstrap.servers****";
    config := cluster.NewConfig()
    config.Config.Version = sarama.V0_10_2_0;
    config.Group.Return.Notifications = true
    config.Consumer.Offsets.CommitInterval = 1 * time.Second
    config.Consumer.Offsets.Initial = sarama.OffsetNewest //初始从最新的offset开始
 
    //请填写正确的clusterId，appId，密码
    config.Net.SASL.User = "****clusterId****.****AppId****"; //eg: 44.appId_000
    config.Net.SASL.Password = "****password****"
    config.Net.SASL.Handshake = true
    config.Net.SASL.Enable = true
  
    topics := "xxx,xxx" //topic 列表
    groupID := "cg-xxx" //必须以cg开头
    c, err := cluster.NewConsumer(strings.Split(bootstrap, ","), groupID, strings.Split(topics, ","), config)
    if err != nil {
        glog.Errorf("Failed open consumer: %v", err)
        return
    }
    defer c.Close()
    go func(c *cluster.Consumer) {
        errors := c.Errors()
        noti := c.Notifications()
        for {
            select {
            case err := <-errors:
                glog.Errorln(err)
            case <-noti:
            }
        }
    }(c);
 
    for msg := range c.Messages() {
        fmt.Fprintf(os.Stdout, "%s/%d/%d\t%s\n", msg.Topic, msg.Partition, msg.Offset, msg.Value)
        c.MarkOffset(msg, "") //MarkOffset 并不是实时写入kafka，有可能在程序crash时丢掉未提交的offset
    }
}
```



