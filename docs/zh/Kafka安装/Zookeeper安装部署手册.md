# Zookeeper安装部署手册

[TOC]

## 1、前言

假设我们要部署三台ZK，机器IP分别为IP1、IP2、IP3。

如果部署五台的时候，过程也类似。

```bash
# 若要部署zk集群和kafka集群，最好做IP映射
vim /etc/hosts
# 1. 注释掉含有127.0.0.1的行
# 2. 文件最下面写入以下内容(10.190.x.x表示每个服务器具体IP，IP1、IP2、IP3表示机器hostname)
10.190.x.x    IP1
10.190.x.x    IP2
10.190.x.x    IP3

# 测试
ping IP2
```


## 2、部署

**步骤一：环境初始化**

- 安装JDK-11，这块比较通用，不解释。
- POC或测试的时候，建议在/home/kafka目录下进行安装，以使得kafka相关的组件都在一块。

**步骤二：解压安装包**

```bash

cd /home/kafka

tar xvzf zookeeper-3.4.14.tar.gz
```

**步骤三：创建数据目录**

```bash
# 创建数据目录
mkdir /home/kafka/zookeeper-data

# 创建myid文件
touch /home/kafka/zookeeper-data/myid

# 数据目录这里写入ZK的ID，如果是第一台机器则这里写1，第二台写2
echo "1" > /home/kafka/zookeeper-data/myid

# 查看myid里面的数据，下面一行那个1是输出的数据
cat /home/kafka/zookeeper-data/myid
1
```

**步骤四：修改ZK配置**

```bash
# 进入配置文件
cd zookeeper-3.4.14/conf
cp zoo_sample.cfg zoo.cfg


# 修改zoo.cfg这个配置文件

# 修改dataDir的配置，将其设置为zk的数据地址，比如
dataDir=/home/kafka/zookeeper-data

# 增加ZK集群之间的交互配置。
# 这里的server.1后面的1，必须要和myid中的输入数字对应上。
# 这里是安装三台ZK的情况，如果是安装五台，那么情况类似。
server.1=IP1:2888:3888
server.2=IP2:2888:3888
server.3=IP3:2888:3888
```

**步骤五：启动ZK及检查**

```bash
# 进入bin目录
cd /home/kafka/zookeeper-3.4.14/bin

# 启动命令
./zkServer.sh start

# 停止命令
./zkServer.sh stop

# 查看状态命令
./zkServer.sh status

ZooKeeper JMX enabled by default
Using config: /home/kafka/zookeeper-3.4.14/bin/../conf/zoo.cfg
Mode: follower 

# 部署三台的话，只要三台里面有两台follower，一台leader，那么部署基本就成功了。

```

**步骤六：集群部署**

重复上面的步骤一到步骤五，在另外两台进行部署即可完成最终的部署。

部署时，注意myid的内容。

## 3、历史安装问题记录

### 3.1、防火墙问题

在安装的过程中，我们发现zk的状态一直是异常的，查看日志发现当前的zk和其他机器连接失败，后面关闭防火墙之后问题就解决了（注意容器可能没有防火墙服务）。

### 3.2、启动顺序问题
第一台服务器上的zk启动后，./zkServer.sh status查看状态是无法连接服务，cat zookeeper.out 发现是无法连接其他主机。  
原因：启动的时候是分开启动的，如果是三台服务器，第一台启动连接不上其他两台。  
解决方法：启动其他一台或者两台后，在第一台上./zkServer.sh restart重新启动就好了
