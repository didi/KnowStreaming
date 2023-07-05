

![Logo](https://user-images.githubusercontent.com/71620349/185368586-aed82d30-1534-453d-86ff-ecfa9d0f35bd.png)


## 2、解决连接 JMX 失败

- [2、解决连接 JMX 失败](#2解决连接-jmx-失败)
  - [2.1、正异常现象](#21正异常现象)
  - [2.2、异因一：JMX未开启](#22异因一jmx未开启)
    - [2.2.1、异常现象](#221异常现象)
    - [2.2.2、解决方案](#222解决方案)
  - [2.3、异原二：JMX配置错误](#23异原二jmx配置错误)
    - [2.3.1、异常现象](#231异常现象)
    - [2.3.2、解决方案](#232解决方案)
  - [2.4、异因三：JMX开启SSL](#24异因三jmx开启ssl)
    - [2.4.1、异常现象](#241异常现象)
    - [2.4.2、解决方案](#242解决方案)
  - [2.5、异因四：连接了错误IP](#25异因四连接了错误ip)
    - [2.5.1、异常现象](#251异常现象)
    - [2.5.2、解决方案](#252解决方案)
  - [2.6、异因五：连接了错误端口](#26异因五连接了错误端口)
    - [2.6.1、异常现象](#261异常现象)
    - [2.6.2、解决方案](#262解决方案)


背景：Kafka 通过 JMX 服务进行运行指标的暴露，因此 `KnowStreaming` 会主动连接 Kafka 的 JMX 服务进行指标采集。如果我们发现页面缺少指标，那么可能原因之一是 Kafka 的 JMX 端口配置的有问题导致指标获取失败，进而页面没有数据。


### 2.1、正异常现象

**1、异常现象**

Broker 列表的 JMX PORT 列出现红色感叹号，则表示 JMX 连接存在异常。

<img src=http://img-ys011.didistatic.com/static/dc2img/do1_MLlLCfAktne4X6MBtBUd width="90%">


**2、正常现象**

Broker 列表的 JMX PORT 列出现绿色，则表示 JMX 连接正常。

<img src=http://img-ys011.didistatic.com/static/dc2img/do1_ymtDTCiDlzfrmSCez2lx width="90%">


---






### 2.2、异因一：JMX未开启

#### 2.2.1、异常现象

broker列表的JMX Port值为-1，对应Broker的JMX未开启。

<img src=http://img-ys011.didistatic.com/static/dc2img/do1_E1PD8tPsMeR2zYLFBFAu width="90%">

#### 2.2.2、解决方案

开启JMX，开启流程如下：

1、修改kafka的bin目录下面的：`kafka-server-start.sh`文件

```bash
# 在这个下面增加JMX端口的配置
if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
    export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"    
    export JMX_PORT=9999  # 增加这个配置, 这里的数值并不一定是要9999 
fi
```


2、修改kafka的bin目录下面对的：`kafka-run-class.sh`文件

```bash
# JMX settings 
if [ -z "$KAFKA_JMX_OPTS" ]; then
    KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=当前机器的IP" 
fi  

# JMX port to use 
if [  $JMX_PORT ]; then
    KAFKA_JMX_OPTS="$KAFKA_JMX_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT" 
fi
```



3、重启Kafka-Broker。


---







### 2.3、异原二：JMX配置错误

#### 2.3.1、异常现象

错误日志：

```log
# 错误一： 错误提示的是真实的IP，这样的话基本就是JMX配置的有问题了。 
2021-01-27 10:06:20.730 ERROR 50901 --- [ics-Thread-1-62] c.x.k.m.c.utils.jmx.JmxConnectorWrap     : JMX connect exception, host:192.168.0.1 port:9999. java.rmi.ConnectException: Connection refused to host: 192.168.0.1; nested exception is:    

# 错误二：错误提示的是127.0.0.1这个IP，这个是机器的hostname配置的可能有问题。 
2021-01-27 10:06:20.730 ERROR 50901 --- [ics-Thread-1-62] c.x.k.m.c.utils.jmx.JmxConnectorWrap     : JMX connect exception, host:127.0.0.1 port:9999. java.rmi.ConnectException: Connection refused to host: 127.0.0.1;; nested exception is: 
```

#### 2.3.2、解决方案

开启JMX，开启流程如下：

1、修改kafka的bin目录下面的：`kafka-server-start.sh`文件

```bash
# 在这个下面增加JMX端口的配置
if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
    export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"    
    export JMX_PORT=9999  # 增加这个配置, 这里的数值并不一定是要9999 
fi
```

2、修改kafka的bin目录下面对的：`kafka-run-class.sh`文件

```bash
# JMX settings 
if [ -z "$KAFKA_JMX_OPTS" ]; then
    KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=当前机器的IP" 
fi  

# JMX port to use 
if [  $JMX_PORT ]; then
    KAFKA_JMX_OPTS="$KAFKA_JMX_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT" 
fi
```

3、重启Kafka-Broker。


---







### 2.4、异因三：JMX开启SSL

#### 2.4.1、异常现象

```log
# 连接JMX的日志中，出现SSL认证失败的相关日志。TODO：欢迎补充具体日志案例。
```

#### 2.4.2、解决方案

<img src=http://img-ys011.didistatic.com/static/dc2img/do1_kNyCi8H9wtHSRkWurB6S width="50%">


---


### 2.5、异因四：连接了错误IP

#### 2.5.1、异常现象

Broker 配置了内外网，而JMX在配置时，可能配置了内网IP或者外网IP，此时`KnowStreaming` 需要连接到特定网络的IP才可以进行访问。

 比如：Broker在ZK的存储结构如下所示，我们期望连接到 `endpoints` 中标记为 `INTERNAL` 的地址，但是 `KnowStreaming` 却连接了 `EXTERNAL` 的地址。

```json
{
    "listener_security_protocol_map": {
        "EXTERNAL": "SASL_PLAINTEXT",
        "INTERNAL": "SASL_PLAINTEXT"
    },
    "endpoints": [
        "EXTERNAL://192.168.0.1:7092",
        "INTERNAL://192.168.0.2:7093"
    ],
    "jmx_port": 8099,
    "host": "192.168.0.1",
    "timestamp": "1627289710439",
    "port": -1,
    "version": 4
}
```

#### 2.5.2、解决方案

可以手动往`ks_km_physical_cluster`表的`jmx_properties`字段增加一个`useWhichEndpoint`字段，从而控制 `KnowStreaming` 连接到特定的JMX IP及PORT。

`jmx_properties`格式：

```json
{
    "maxConn": 100, // KM对单台Broker的最大JMX连接数   
    "username": "xxxxx", //用户名，可以不填写   
    "password": "xxxx", // 密码，可以不填写    
    "openSSL": true, //开启SSL, true表示开启ssl, false表示关闭    
    "useWhichEndpoint": "EXTERNAL" //指定要连接的网络名称，填写EXTERNAL就是连接endpoints里面的EXTERNAL地址
}
```



SQL例子：

```sql
UPDATE ks_km_physical_cluster SET jmx_properties='{ "maxConn": 10, "username": "xxxxx", "password": "xxxx", "openSSL": false , "useWhichEndpoint": "xxx"}' where id={xxx};
```


---






### 2.6、异因五：连接了错误端口

3.3.0 以上版本，或者是 master 分支最新代码，才具备该能力。

#### 2.6.1、异常现象

在 AWS 或者是容器上的 Kafka-Broker，使用同一个IP，但是外部服务想要去连接 JMX 端口时，需要进行映射。因此 KnowStreaming 如果直接连接 ZK 上获取到的 JMX 端口，会连接失败，因此需要具备连接端口可配置的能力。

TODO：补充具体的日志。


#### 2.6.2、解决方案

可以手动往`ks_km_physical_cluster`表的`jmx_properties`字段增加一个`specifiedJmxPortList`字段，从而控制 `KnowStreaming` 连接到特定的JMX PORT。

`jmx_properties`格式：
```json
{
    "jmxPort": 2445,                // 最低优先级使用的jmx端口
    "maxConn": 100,                 // KM对单台Broker的最大JMX连接数   
    "username": "xxxxx",            //用户名，可以不填写   
    "password": "xxxx",             // 密码，可以不填写    
    "openSSL": true,                //开启SSL, true表示开启ssl, false表示关闭    
    "useWhichEndpoint": "EXTERNAL", //指定要连接的网络名称，填写EXTERNAL就是连接endpoints里面的EXTERNAL地址
    "specifiedJmxPortList": [       // 配置最高优先使用的jmx端口
        {
            "serverId": "1",        // kafka-broker的brokerId, 注意这个是字符串类型，字符串类型的原因是要兼容connect的jmx端口的连接
            "jmxPort": 1234         // 该 broker 所连接的jmx端口
        },
        {
            "serverId": "2",
            "jmxPort": 1234
        },
    ]
}
```



SQL例子：

```sql
UPDATE ks_km_physical_cluster SET jmx_properties='{ "maxConn": 10, "username": "xxxxx", "password": "xxxx", "openSSL": false , "specifiedJmxPortList": [{"serverId": "1", "jmxPort": 1234}] }' where id={xxx};
```


---
