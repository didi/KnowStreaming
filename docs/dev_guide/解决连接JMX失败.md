

![Logo](https://user-images.githubusercontent.com/71620349/185368586-aed82d30-1534-453d-86ff-ecfa9d0f35bd.png)

## JMX-连接失败问题解决

集群正常接入`KnowStreaming`之后，即可以看到集群的Broker列表，此时如果查看不了Topic的实时流量，或者是Broker的实时流量信息时，那么大概率就是`JMX`连接的问题了。

下面我们按照步骤来一步一步的检查。

### 1、问题说明

**类型一：JMX配置未开启**

未开启时，直接到`2、解决方法`查看如何开启即可。

![check_jmx_opened](http://img-ys011.didistatic.com/static/dc2img/do1_dRX6UHE2IUSHqsN95DGb)


**类型二：配置错误**

`JMX`端口已经开启的情况下，有的时候开启的配置不正确，此时也会导致出现连接失败的问题。这里大概列举几种原因：

- `JMX`配置错误：见`2、解决方法`。
- 存在防火墙或者网络限制：网络通的另外一台机器`telnet`试一下看是否可以连接上。
- 需要进行用户名及密码的认证：见`3、解决方法 —— 认证的JMX`。


错误日志例子：
```
# 错误一： 错误提示的是真实的IP，这样的话基本就是JMX配置的有问题了。
2021-01-27 10:06:20.730 ERROR 50901 --- [ics-Thread-1-62] c.x.k.m.c.utils.jmx.JmxConnectorWrap     : JMX connect exception, host:192.168.0.1 port:9999.
java.rmi.ConnectException: Connection refused to host: 192.168.0.1; nested exception is: 


# 错误二：错误提示的是127.0.0.1这个IP，这个是机器的hostname配置的可能有问题。
2021-01-27 10:06:20.730 ERROR 50901 --- [ics-Thread-1-62] c.x.k.m.c.utils.jmx.JmxConnectorWrap     : JMX connect exception, host:127.0.0.1 port:9999.
java.rmi.ConnectException: Connection refused to host: 127.0.0.1;; nested exception is: 
```

**类型三：连接特定IP**

Broker 配置了内外网，而JMX在配置时，可能配置了内网IP或者外网IP，此时 `KnowStreaming` 需要连接到特定网络的IP才可以进行访问。

比如：

Broker在ZK的存储结构如下所示，我们期望连接到 `endpoints` 中标记为 `INTERNAL` 的地址，但是 `KnowStreaming` 却连接了 `EXTERNAL` 的地址，此时可以看 `4、解决方法 —— JMX连接特定网络` 进行解决。

```json
 {
  	"listener_security_protocol_map": {"EXTERNAL":"SASL_PLAINTEXT","INTERNAL":"SASL_PLAINTEXT"},
  	"endpoints": ["EXTERNAL://192.168.0.1:7092","INTERNAL://192.168.0.2:7093"],
  	"jmx_port": 8099,
  	"host": "192.168.0.1",
  	"timestamp": "1627289710439",
  	"port": -1,
    "version": 4
  }
```

### 2、解决方法

这里仅介绍一下比较通用的解决方式，如若有更好的方式，欢迎大家指导告知一下。

修改`kafka-server-start.sh`文件：
```
# 在这个下面增加JMX端口的配置
if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
    export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"
    export JMX_PORT=9999  # 增加这个配置, 这里的数值并不一定是要9999
fi
```

&nbsp;

修改`kafka-run-class.sh`文件
```
# JMX settings
if [ -z "$KAFKA_JMX_OPTS" ]; then
  KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=${当前机器的IP}"
fi

# JMX port to use
if [  $JMX_PORT ]; then
  KAFKA_JMX_OPTS="$KAFKA_JMX_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT"
fi
```


### 3、解决方法 —— 认证的JMX

如果您是直接看的这个部分，建议先看一下上一节：`2、解决方法`以确保`JMX`的配置没有问题了。

在`JMX`的配置等都没有问题的情况下，如果是因为认证的原因导致连接不了的，可以在集群接入界面配置你的`JMX`认证信息。

<img src='http://img-ys011.didistatic.com/static/dc2img/do1_EUU352qMEX1Jdp7pxizp' width=350>



### 4、解决方法 —— JMX连接特定网络

可以手动往`ks_km_physical_cluster`表的`jmx_properties`字段增加一个`useWhichEndpoint`字段，从而控制 `KnowStreaming` 连接到特定的JMX IP及PORT。

`jmx_properties`格式：
```json
{
    "maxConn": 100,           # KM对单台Broker的最大JMX连接数
    "username": "xxxxx",     # 用户名，可以不填写
    "password": "xxxx",      # 密码，可以不填写
    "openSSL": true,         # 开启SSL, true表示开启ssl, false表示关闭
    "useWhichEndpoint": "EXTERNAL"  #指定要连接的网络名称，填写EXTERNAL就是连接endpoints里面的EXTERNAL地址
}
```

&nbsp;

SQL例子：
```sql
UPDATE ks_km_physical_cluster SET jmx_properties='{ "maxConn": 10, "username": "xxxxx", "password": "xxxx", "openSSL": false , "useWhichEndpoint": "xxx"}' where id={xxx};
```

注意：

+ 目前此功能只支持采用 `ZK` 做分布式协调的kafka集群。

  