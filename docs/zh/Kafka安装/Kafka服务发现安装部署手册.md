# Kafka 云平台部署手册

[TOC]

---

## 1、安装包说明

|软件包|包名称|备注说明|
|---|---|---|---|
|kafka| kafka_2.12-2.5.0-d-204.tgz| kafka安装包|
|kafka-manager| kafka-manager-2.3.0.tar| kafka-manager安装包|
|kafka-service-discovery| kafka-sd-2.5.0-d-100.tar| kafka服务发现安装包|
|zookeeper| zookeeper-3.4.14.tar| zk安装包|

## 2、安装顺序

1. 安装`kafka-manager`
2. 安装`zookeeper`
3. 安装`kafka`
4. 安装`kafka-service-discovery`

---

### 2.1、安装kafka-manager

#### 2.1.1、环境要求

- **`java-8`**，要求预先安装`JDK8`

#### 2.1.2、安装步骤

1. 解压`kafka-manager-2.0.0.tar.gz`
```
$ tar -xvzf kafka-manager-2.3.0.tar.gz
x kafka-manager/
x kafka-manager/application.yml
x kafka-manager/create_mysql_table.sql
x kafka-manager/kafka-manager.jar
$
```

2. 初始化`mysql`库&表
```
mysql库可以任意指定，只需要和kafka-manager的配置文件里面一致即可。

mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

3. 修改配置文件`application.yml`

```
修改mysql配置

spring:
  application:
    name: kafkamanager
  datasource:
    kafka-manager:
      jdbc-url: jdbc:mysql://{mysql_addr}:{mysql_port}/{mysql_db}?characterEncoding=UTF-8&serverTimezone=GMT%2B8
      username: {mysql_username}
      password: {mysql_password}
      driver-class-name: com.mysql.jdbc.Driver
```

4. 启动`kafka-manager`
```
nohup java -jar kafka-manager.jar --spring.config.location=./application.yml > /dev/null 2>&1 &
```

---

### 2.2、安装zookeeper

和网上的一些文章基本一致，可以直接百度一下，或者参看下面的说明。

1. 解压安装包
```
tar -zxvf zookeeper-3.4.14.tar.gz
```


2. 修改配置文件(先在一台节点上配置)
```
拷贝新的配置：
cp conf/zoo_sample.cfg  conf/zoo.cfg

修改配置：
#如果没有该目录，先去创建，这里以tmp根目录为例，事先在tmp下创建好zookeeper-3.4.14/data
dataDir=/tmp/zookeeper-3.4.14/data  
#zk节点有几个，就配置几个，从1递增，下面的示例是有3个zk节点，注意host换成节点ip
server.1=host1:2888:3888
server.2=host2:2888:3888
server.3=host3:2888:3888
```

3. 创建一个myid文件
```
在 上述dataDir=/tmp/zookeeper-3.4.14/data 目录下，创建myid文件，vi myid

然后将里面的内容设置为对应的id.

如配置文件中该主机对应的server.N的N是多少，则myid中的数值就为多少，比如当前的节点作为zk的1号节点，内容就写1
```

4. 其他机器重复上述过程

5. 启动集群，
```
./zkServer.sh start
./zkServer.sh status #查看集群状态
```

---

### 2.3、安装 Kafka

#### 2.3.1、环境要求

- **`java-11`**，即要求预先安装`JDK11`

#### 2.3.2、安装步骤

1. 获取集群ID

```shell
访问kafka-manager(默认账号密码: admin/admin)

'运维管控' -> '集群列表' -> '注册集群'

zookeeper地址填写zk地址(加上namespace)

bootstrapServers填写kafka引擎节点地址，多个用英文逗号分隔. 例如: xxx.xxx.xxx.xxx:9093,xxx.xxx.xxx.xxx:9093

集群类型选共享集群

安全协议填写如下:

{
	"security.protocol": "SASL_PLAINTEXT",
	"sasl.mechanism": "PLAIN",
	"sasl.jaas.config": "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"dkm_admin\" password=\"km_kMl4N8as1Kp0CCY\";"
}

注册成功后，会生成集群ID（后面kafka引擎配置需要）

```

2. 初始化安装包

这里以 `kafka_2.12-2.5.0-d-204.tgz` 包为例。

```shell
# 解压压缩包：
tar -zxf kafka_2.12-2.5.0-d-204.tgz
```

3. 配置文件

```shell
# 配置数据地址
log.dirs=将其配置为数据盘的地址。

# 监听地址
listeners=PLAINTEXT://IP:9092,SASL_PLAINTEXT://IP:9093

# 配置zk地址
修改zookeeper.connect，将其配置为kafka集群的zk地址(加上namespace)。
zookeeper.connect=xxx.xxx.xxx.xxx:2181,xxx.xxx.xxx.xxx:2181/xxxxxxx （多个zk节点用英文逗号分隔）

# 修改 gateway.url
修改gateway.url，将其配置为为kafka-manager的地址。例如：http://127.0.0.1:8080/gateway
如果不清楚kafka-manager的地址，可以访问swagger来查看，例如：http://127.0.0.1:8080/swagger-ui.html
查看【GATEWAY-服务发现相关接口(REST)】，请求地址里/api的前缀即为kafka-manager的应用根前缀。
例如/gateway/api/xxxx

# 修改cluster.id
修改cluster.id，将其修改为为该集群添加进kafka-manager之后的集群ID(上述集群ID)

# 其他配置
依据需要进行相应的调整

```

4. 启动`kafka`

```
进入/bin目录
执行： nohup ./kafka-server-start.sh ../config/server.properties > /dev/null 2>&1 &
```

5. 集群模式

```
多台机器，重复上述过程即可
```

---

### 2.4、安装 `kafka-service-discovery`

#### 2.4.1、环境要求

- **`java-11`**，即要求预先安装`JDK11`

#### 2.4.2、安装步骤

1. 配置`kafka-gateway`

这一步比较特殊，当前没有操作界面，需要手动往`kafka-manager`的`MySQL  gateway_config`表中，手动写入如下数据：

```
INSERT INTO gateway_config(type, name, value, version) VALUES ('SD_CLUSTER_ID', '{clusterId}', '{bootstrapServers}', 1);

{clusterId}是新加集群的集群ID  例如: 1
{bootstrapServers}是新加集群的集群服务地址(集群Broker地址)  例如: xxx:xxx.xxx.xxx:9093,xxx.xxx.xxx.xxx:9093
version > 0 即可，建议从1开始.
```

2. 解压`kafka-sd-2.5.0-d-100.tar`文件

```
解压压缩包：tar -zxvf kafka-sd-2.5.0-d-100.tar
```

3. 配置文件(kafka-service-discovery/config/server.prperties)修改

```
修改log.dirs，将其配置为日志输出地址。
修改gateway.address，将其配置为为kafka-manager的地址。例如：http://127.0.0.1:8080/gateway
如果不清楚kafka-manager的地址，可以访问swagger来查看，例如：http://127.0.0.1:8080/swagger-ui.html
查看【GATEWAY-服务发现相关接口(REST)】，请求地址里/api的前缀即为kafka-manager的应用根前缀。
例如/gateway/api/xxxx
```

4. 启动`kafka-service-discovery`

```
进入kafka-service-discovery/bin目录
执行： nohup ./kafka-server-start.sh ../config/server.properties > /dev/null 2>&1 &
```
