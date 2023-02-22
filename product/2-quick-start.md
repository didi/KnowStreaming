---
order: 2
title: '2.快速开始'
toc: menu
---

## 2.1、单机部署

**风险提示**

⚠️ 脚本全自动安装，会将所部署机器上的 MySQL、JDK、ES 等进行删除重装，请注意原有服务丢失风险。

### 2.1.1、产品下载

| KnowStreaming Version |                                                           Offline installer                                                            |                                                 Helm Chart                                                  |                  Docker Image                   |
| :-------------------: | :------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------: | :---------------------------------------------: |
|     3.0.0-beta.1      | [KnowStreaming-3.0.0-beta.1-offline.tar.gz](http://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-beta.1-offline.tar.gz) | [knowstreaming-manager-0.1.3.tgz](http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.3.tgz) | [0.1.0](https://hub.docker.com/u/knowstreaming) |
|     3.0.0-beta.2      | [KnowStreaming-3.0.0-beta.2-offline.tar.gz](http://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-beta.2-offline.tar.gz) | [knowstreaming-manager-0.1.4.tgz](http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.4.tgz) | [0.2.0](https://hub.docker.com/u/knowstreaming) |
|     3.0.0-beta.3      | [KnowStreaming-3.0.0-beta.3-offline.tar.gz](http://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-beta.3-offline.tar.gz) | [knowstreaming-manager-0.1.5.tgz](http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.5.tgz) | [0.3.0](https://hub.docker.com/u/knowstreaming) |
|         3.0.0         |        [KnowStreaming-3.0.0-offline.tar.gz](http://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-offline.tar.gz)        | [knowstreaming-manager-0.1.6.tgz](http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.6.tgz) | [0.4.0](https://hub.docker.com/u/knowstreaming) |
|         3.0.1         |        [KnowStreaming-3.0.1-offline.tar.gz](http://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.1-offline.tar.gz)        | [knowstreaming-manager-0.1.7.tgz](http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.7.tgz) | [0.5.0](https://hub.docker.com/u/knowstreaming) |
|         3.1.0         |        [KnowStreaming-3.1.0-offline.tar.gz](http://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.1.0-offline.tar.gz)        | [knowstreaming-manager-0.1.8.tgz](http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.8.tgz) | [0.6.0](https://hub.docker.com/u/knowstreaming) |
|         3.2.0         |        [KnowStreaming-3.2.0-offline.tar.gz](http://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.1.0-offline.tar.gz)        | [knowstreaming-manager-0.1.9.tgz](http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.8.tgz) | [0.7.0](https://hub.docker.com/u/knowstreaming) |

### 2.1.2、安装说明

- 以 `v3.0.0-beta.1` 版本为例进行部署；
- 以 CentOS-7 为例，系统基础配置要求 4C-8G；
- 部署完成后，可通过浏览器：`IP:PORT` 进行访问，默认端口是 `8080`，系统默认账号密码: `admin` / `admin2022_`；（注意！`v3.0.0-beta.2` 及以后版本默认账号和密码为：`admin` / `admin`）
- 本文为单机部署，如需分布式部署，[请联系我们](https://knowstreaming.com/support-center)

**软件依赖**

| 软件名        | 版本要求     | 默认端口 |
| ------------- | ------------ | -------- |
| MySQL         | v5.7 或 v8.0 | 3306     |
| ElasticSearch | v7.6+        | 8060     |
| JDK           | v8+          | -        |
| CentOS        | v6+          | -        |
| Ubuntu        | v16+         | -        |

&nbsp;

### 2.1.3、脚本部署

**在线安装**

```bash
# 在服务器中下载安装脚本, 该脚本中会在当前目录下，重新安装MySQL。重装后的mysql密码存放在当前目录的mysql.password文件中。
wget https://s3-gzpu.didistatic.com/pub/knowstreaming/deploy_KnowStreaming.sh

# 执行脚本（会提示选择安装的版本）
sh deploy_KnowStreaming.sh

# 访问地址（根据实际IP地址更换）
127.0.0.1:8080
```

**离线安装**

```bash
# 将安装包下载到本地且传输到目标服务器
wget https://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-beta.3-offline.tar.gz

# 解压安装包
tar -zxf KnowStreaming-3.0.0-beta.1-offline.tar.gz

# 执行安装脚本
sh deploy_KnowStreaming-offline.sh

# 访问地址（根据实际IP地址更换）
127.0.0.1:8080
```

&nbsp;

### 2.1.4、容器化部署

#### 2.1.4.1、Helm

**环境依赖**

- Kubernetes >= 1.14 ，Helm >= 2.17.0
- 系统基础配置要求 14C-26G,可根据需要调整应用副本数。
- 默认依赖全部安装，ElasticSearch（3 节点集群模式） + MySQL(单机) + KnowStreaming-manager(2 副本) + KnowStreaming-ui(2 副本)
- 请配置持久化存储卷
- 使用已有的 ElasticSearch(7.6.x) 和 MySQL(5.7) 只需调整 values.yaml 部分参数即可

**安装命令**

```bash
# 相关镜像在Docker Hub都可以下载
# 快速安装(NAMESPACE需要更改为已存在的，安装启动需要几分钟初始化请稍等~)
helm install -n [NAMESPACE] [NAME] http://download.knowstreaming.com/charts/knowstreaming-manager-0.1.9.tgz

# 获取KnowStreaming前端ui的service. 默认nodeport方式.
# (http://nodeIP:nodeport，默认用户名密码：admin/admin2022_)
# `v3.0.0-beta.2`版本开始（helm chart包版本0.1.4开始），默认账号密码为`admin` / `admin`；

# 添加仓库
helm repo add knowstreaming http://download.knowstreaming.com/charts

# 拉取最新版本
helm pull knowstreaming/knowstreaming-manager
```

&nbsp;

#### 2.1.4.2、Docker Compose

**环境依赖**

- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

**安装命令**

```bash
# `v3.0.0-beta.2`版本开始(docker镜像为0.2.0版本开始)，默认账号密码为`admin` / `admin`；
# https://hub.docker.com/u/knowstreaming 在此处寻找最新镜像版本
# mysql与es可以使用自己搭建的服务,调整对应配置即可

# 复制docker-compose.yml到指定位置后执行下方命令即可启动
docker-compose up -d
```

**验证安装**

```shell
docker-compose ps
# 验证启动 - 状态为 UP 则表示成功
        Name                       Command                       State                  Ports
----------------------------------------------------------------------------------------------------
elasticsearch-single    /usr/local/bin/docker-entr ...   Up                      9200/tcp, 9300/tcp
knowstreaming-init      /bin/bash /es_template_cre ...   Up
knowstreaming-manager   /bin/sh /ks-start.sh             Up                      80/tcp
knowstreaming-mysql     /entrypoint.sh mysqld            Up (health: starting)   3306/tcp, 33060/tcp
knowstreaming-ui        /docker-entrypoint.sh ngin ...   Up                      0.0.0.0:80->80/tcp

# 稍等一分钟左右 knowstreaming-init 会退出，表示es初始化完成，可以访问页面
        Name                       Command                  State              Ports
-------------------------------------------------------------------------------------------
knowstreaming-init      /bin/bash /es_template_cre ...   Exit 0
knowstreaming-mysql     /entrypoint.sh mysqld            Up (healthy)   3306/tcp, 33060/tcp
```

**访问**

```http request
http://127.0.0.1:80/
```

**docker-compose.yml**

```yml
version: '2'
services:
  # *不要调整knowstreaming-manager服务名称，ui中会用到
  knowstreaming-manager:
    image: knowstreaming/knowstreaming-manager:0.7.0
    container_name: knowstreaming-manager
    privileged: true
    restart: always
    depends_on:
      - elasticsearch-single
      - knowstreaming-mysql
    expose:
      - 80
    command:
      - /bin/sh
      - /ks-start.sh
    environment:
      TZ: Asia/Shanghai
      # mysql服务地址
      SERVER_MYSQL_ADDRESS: knowstreaming-mysql:3306
      # mysql数据库名
      SERVER_MYSQL_DB: know_streaming
      # mysql用户名
      SERVER_MYSQL_USER: root
      # mysql用户密码
      SERVER_MYSQL_PASSWORD: admin2022_
      # es服务地址
      SERVER_ES_ADDRESS: elasticsearch-single:9200
      # 服务JVM参数
      JAVA_OPTS: -Xmx1g -Xms1g
      # 对于kafka中ADVERTISED_LISTENERS填写的hostname可以通过该方式完成
  #    extra_hosts:
  #      - "hostname:x.x.x.x"
  # 服务日志路径
  #    volumes:
  #      - /ks/manage/log:/logs
  knowstreaming-ui:
    image: knowstreaming/knowstreaming-ui:0.7.0
    container_name: knowstreaming-ui
    restart: always
    ports:
      - '80:80'
    environment:
      TZ: Asia/Shanghai
    depends_on:
      - knowstreaming-manager
  #    extra_hosts:
  #      - "hostname:x.x.x.x"
  elasticsearch-single:
    image: docker.io/library/elasticsearch:7.6.2
    container_name: elasticsearch-single
    restart: always
    expose:
      - 9200
      - 9300
    #    ports:
    #      - '9200:9200'
    #      - '9300:9300'
    environment:
      TZ: Asia/Shanghai
      # es的JVM参数
      ES_JAVA_OPTS: -Xms512m -Xmx512m
      # 单节点配置，多节点集群参考 https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docker.html#docker-compose-file
      discovery.type: single-node
      # 数据持久化路径
  #    volumes:
  #      - /ks/es/data:/usr/share/elasticsearch/data

  # es初始化服务，与manager使用同一镜像
  # 首次启动es需初始化模版和索引,后续会自动创建
  knowstreaming-init:
    image: knowstreaming/knowstreaming-manager:0.7.0
    container_name: knowstreaming-init
    depends_on:
      - elasticsearch-single
    command:
      - /bin/bash
      - /es_template_create.sh
    environment:
      TZ: Asia/Shanghai
      # es服务地址
      SERVER_ES_ADDRESS: elasticsearch-single:9200

  knowstreaming-mysql:
    image: knowstreaming/knowstreaming-mysql:0.7.0
    container_name: knowstreaming-mysql
    restart: always
    environment:
      TZ: Asia/Shanghai
      # root 用户密码
      MYSQL_ROOT_PASSWORD: admin2022_
      # 初始化时创建的数据库名称
      MYSQL_DATABASE: know_streaming
      # 通配所有host,可以访问远程
      MYSQL_ROOT_HOST: '%'
    expose:
      - 3306
#    ports:
#      - '3306:3306'
# 数据持久化路径
#    volumes:
#      - /ks/mysql/data:/data/mysql
```

&nbsp;

### 2.1.5、手动部署

**部署流程**

1. 安装 `JDK-11`、`MySQL`、`ElasticSearch` 等依赖服务
2. 安装 KnowStreaming

&nbsp;

#### 2.1.5.1、安装 MySQL 服务

**yum 方式安装**

```bash
# 配置yum源
wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm
rpm -ivh mysql57-community-release-el7-9.noarch.rpm

# 执行安装
yum -y install mysql-server mysql-client

# 服务启动
systemctl start mysqld

# 获取初始密码并修改
old_pass=`grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}' | tail -n 1`

mysql -NBe "alter user USER() identified by 'Didi_km_678';" --connect-expired-password -uroot -p$old_pass
```

**rpm 方式安装**

```bash
# 下载安装包
wget https://s3-gzpu.didistatic.com/knowsearch/mysql5.7.tar.gz

# 解压到指定目录
tar -zxf mysql5.7.tar.gz -C /tmp/

# 执行安装
yum -y localinstall /tmp/libaio-*.rpm /tmp/mysql-*.rpm

# 服务启动
systemctl start mysqld


# 获取初始密码并修改
old_pass=`grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}' | tail -n 1`

mysql -NBe "alter user USER() identified by 'Didi_km_678';" --connect-expired-password -uroot -p$old_pass

```

&nbsp;

#### 2.1.5.2、配置 JDK 环境

```bash
# 下载安装包
wget https://s3-gzpu.didistatic.com/pub/jdk11.tar.gz

# 解压到指定目录
tar -zxf jdk11.tar.gz -C /usr/local/

# 更改目录名
mv /usr/local/jdk-11.0.2 /usr/local/java11

# 添加到环境变量
echo "export JAVA_HOME=/usr/local/java11" >> ~/.bashrc
echo "export CLASSPATH=/usr/java/java11/lib" >> ~/.bashrc
echo "export PATH=$JAVA_HOME/bin:$PATH:$HOME/bin" >> ~/.bashrc

source ~/.bashrc

```

&nbsp;

#### 2.1.5.3、ElasticSearch 实例搭建

- ElasticSearch 用于存储平台采集的 Kafka 指标；
- 以下安装示例为单节点模式，如需集群部署可以参考：[Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/7.6/elasticsearch-intro.html)

```bash
# 下载安装包
wget https://s3-gzpu.didistatic.com/pub/elasticsearch.tar.gz

# 创建ES数据存储目录
mkdir -p /data/es_data

# 创建ES所属用户
useradd arius

# 配置用户的打开文件数
echo "arius soft nofile 655350" >> /etc/security/limits.conf
echo "arius hard nofile 655350" >> /etc/security/limits.conf
echo "vm.max_map_count = 655360" >> /etc/sysctl.conf
sysctl -p

# 解压安装包
tar -zxf elasticsearch.tar.gz -C /data/

# 更改目录所属组
chown -R arius:arius /data/

# 修改配置文件(参考以下配置)
vim /data/elasticsearch/config/elasticsearch.yml
    cluster.name: km_es
    node.name: es-node1
    node.master: true
    node.data: true
    path.data: /data/es_data
    http.port: 8060
    discovery.seed_hosts: ["127.0.0.1:9300"]

# 修改内存配置
vim /data/elasticsearch/config/jvm.options
    -Xms2g
    -Xmx2g

# 启动服务
su - arius
export JAVA_HOME=/usr/local/java11
sh /data/elasticsearch/control.sh start

# 确认状态
sh /data/elasticsearch/control.sh status
```

&nbsp;

#### 2.1.5.4、KnowStreaming 实例搭建

```bash
# 下载安装包
wget https://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-beta.1.tar.gz

# 解压安装包到指定目录
tar -zxf KnowStreaming-3.0.0-beta.1.tar.gz -C /data/

# 修改启动脚本并加入systemd管理
cd  /data/KnowStreaming/

# 创建相应的库和导入初始化数据
mysql -uroot -pDidi_km_678 -e "create database know_streaming;"
mysql -uroot -pDidi_km_678  know_streaming < ./init/sql/ddl-ks-km.sql
mysql -uroot -pDidi_km_678  know_streaming < ./init/sql/ddl-logi-job.sql
mysql -uroot -pDidi_km_678  know_streaming < ./init/sql/ddl-logi-security.sql
mysql -uroot -pDidi_km_678  know_streaming < ./init/sql/dml-ks-km.sql
mysql -uroot -pDidi_km_678  know_streaming < ./init/sql/dml-logi.sql

# 创建elasticsearch初始化数据
sh ./bin/init_es_template.sh

# 修改配置文件
vim ./conf/application.yml

# 监听端口
server:
    port: 8080 # web 服务端口
    tomcat:
        accept-count: 1000
        max-connections: 10000

# ES地址
es.client.address: 127.0.0.1:8060

# 数据库配置（一共三处地方，修改正确的mysql地址和数据库名称以及用户名密码）
jdbc-url: jdbc:mariadb://127.0.0.1:3306/know_streaming?.....
username: root
password: Didi_km_678

# 启动服务
cd /data/KnowStreaming/bin/
sh startup.sh
```

### 2.1.6、Rainbond 部署

#### 2.1.6.1、根据快速安装文档部署 [Rainbond](/docs/quick-start/quick-install/)

KnowStreaming 已发布至 `开源应用商店` ，用户可搜索 `KnowStreaming`，一键安装 KnowStreaming

![](https://static.goodrain.com/wechat/logikm/1.png)

安装后，访问 `KnowStreaming-UI` 进入控制台，默认密码：admin / admin

![](https://static.goodrain.com/wechat/logikm/2.png)

#### 2.1.6.2、快速部署 Kafka 集群

上面我们已经部署了 `KnowStreaming`，接下来我们也可通过 `开源应用商店` 安装 Kafka 集群并进行对接。

Kafka 已发布至 `开源应用商店` ，用户可搜索 `kafka`，一键安装 Kafka-Zookeeper-Bitnami

![](https://static.goodrain.com/wechat/logikm/install-kafka.png)

安装完成后，我们进入 `kafka1` 和 `kafka2` 的组件 -> 环境变量

修改 `KAFKA_CFG_ADVERTISED_LISTENERS` 环境变量为组件`9092`端口的对外 IP + 端口，例如：`PLAINTEXT://192.168.3.162:10000`

修改 `JMX_PORT` 环境变量为 TCP 对外端口地址供 `Logikm` 获取指标。例如：`JMX_PORT`为 9999，组件对应的端口也要为 9999，同时 tcp 对外端口也是 9999

#### 2.1.6.3、使用 KnowStreaming 对接并管理 Kafka 集群

接下来我们通过 `KnowStreaming` 对接刚刚安装的 `kafka`集群。

访问 `KnowStreaming`，接入集群，根据页面提示填写对应信息。

对接完成后，我们就可通过 `KnowStreaming` 管理 `kafka` 集群啦。

---

## 2.2、登录系统

部署完成后，打开浏览器输入 IP 地址 + 端口(默认端口 8080) 即可访问 `Know Streaming`。

默认账号和密码为：`admin` / `admin`。（注意！v3.0 beta1 版本默认账号和密码为：`admin` / `admin2022_`）

---

## 2.3、开始使用

登录完成后，可根据以下场景快速体验基本功能。

### 2.3.1、接入集群

- 步骤 1:点击“多集群管理”>“接入集群”

- 步骤 2:填写相关集群信息

  - 集群名称：平台内不能重复
  - Bootstrap Servers：输入完成之后会进行连接测试，测试完成之后会给出测试结果连接成功 or 连接失败（以及失败的原因）。
  - Zookeeper：输入完成之后会进行连接测试，测试完成之后会给出测试结果连接成功 or 连接失败（以及失败的原因）
  - Metrics 选填：JMX Port，输入 JMX 端口号；MaxConn，输入服务端最大允许的连接数
  - Security：JMX 账号密码
  - Version：kafka 版本，如果没有匹配则可以选择相近版本
  - 集群配置选填：创建 kafka 客户端进行信息获取的相关配置

![text](http://img-ys011.didistatic.com/static/dc2img/do1_2uxzaT3GTLWUifVg7xhd)

### 2.3.2、新增 Topic

- 步骤 1:点击“多集群管理”>“集群卡片”>“Topic”>“Topics”>“新增 Topic”按钮>“创建 Topic“抽屉

- 步骤 2:输入“Topic 名称（不能重复）”、“Topic 描述”、“分区数”、“副本数”、“数据保存时间”、“清理策略（删除或压缩）”

- 步骤 3:展开“更多配置”可以打开高级配置选项，根据自己需要输入相应配置参数

- 步骤 4:点击“确定”，创建 Topic 完成

![text](http://img-ys011.didistatic.com/static/dc2img/do1_ZsaKRRqT69Ugw5yCHpE7)

### 2.3.3、Topic 扩分区

- 步骤 1:点击“多集群管理”>“集群卡片”>“Topic”>“Topics”>“Topic 列表“>操作项”扩分区“>“扩分区”抽屉

- 步骤 2:扩分区抽屉展示内容为“流量的趋势图”、“当前分区数及支持的最低消息写入速率”、“扩分区后支持的最低消息写入速率”

- 步骤 3:输入所需的分区总数，自动计算出扩分区后支持的最低消息写入速率

- 步骤 4:点击确定，扩分区完成

![text](http://img-ys011.didistatic.com/static/dc2img/do1_ifCma3pKlUnGd3UXunNi)

### 2.3.4、Topic 批量扩缩副本

- 步骤 1:点击“多集群管理”>“集群卡片”>“Topic”>“Topics”>“批量操作下拉“>“批量扩缩副本“>“批量扩缩容”抽屉

- 步骤 2:选择所需要进行扩缩容的 Topic，可多选，所选择的 Topic 出现在下方 Topic 列表中

- 步骤 3:Topic 列表展示 Topic“近三天平均流量”、“近三天峰值流量及时间”、“Partition 数”、”当前副本数“、“新副本数”

- 步骤 4:扩容时，选择目标节点，新增的副本会在选择的目标节点上；缩容时不需要选择目标节点，自动删除最后一个（或几个）副本

- 步骤 5:输入迁移任务配置参数，包含限流值和任务执行时间

- 步骤 6:输入任务描述

- 步骤 7:点击“确定”，创建 Topic 扩缩副本任务

- 步骤 8:去“Job”模块的 Job 列表查看创建的任务，如果已经执行则可以查看执行进度；如果未开始执行则可以编辑任务

![text](http://img-ys011.didistatic.com/static/dc2img/do1_DNIdGs7Uym3yppmvGrBd)

### 2.3.5、Topic 批量迁移

- 步骤 1:点击“多集群管理”>“集群卡片”>“Topic”>“Topics”>“批量操作下拉“>“批量迁移“>“批量迁移”抽屉

- 步骤 2:选择所需要进行迁移的 Topic，可多选，所选择的 Topic 出现在下方 Topic 列表中

- 步骤 3:选择所需要迁移的 partition 和迁移数据的时间范围

- 步骤 4:选择目标节点（节点数必须不小于最大副本数）

- 步骤 5:点击“预览任务计划”，打开“任务计划”二次抽屉，可对目标 Broker ID 进行编辑

- 步骤 6:输入迁移任务配置参数，包含限流值和任务执行时间

- 步骤 7:输入任务描述

- 步骤 8:点击“确定”，创建 Topic 迁移任务

- 步骤 9:去“Job”模块的 Job 列表查看创建的任务，如果已经执行则可以查看执行进度；如果未开始执行则可以编辑任务

![text](http://img-ys011.didistatic.com/static/dc2img/do1_zIL8ytfUYGBbmalrgZqU)

### 2.3.6、设置 Cluster 健康检查规则

- 步骤 1:点击“多集群管理”>“集群卡片”>“Cluster”>“Overview”>“集群健康状态旁边 icon”>“健康度设置抽屉”

- 步骤 2:健康度设置抽屉展示出了检查项和其对应的权重，可以修改检查项的检查规则

- 步骤 3:检查规则可配置，分别为

  - Cluster：集群 controller 数不等于 1（数字不可配置）不通过
  - Broker：RequestQueueSize 大于等于 10（默认为 10，可配置数字）不通过
  - Broker：NetworkProcessorAvgIdlePercent 的 Idle 小于等于 0.8%（默认为 0.8%，可配置数字）不通过
  - Topic：无 leader 的 Topic 数量，大于等于 1（默认为 1，数字可配置）不通过
  - Topic：Topic 在 10（默认为 10，数字可配置）个周期内 8（默认为 8，数字可配置）个周期内处于未同步的状态则不通过
  - ConsumerGroup：Group 在 10（默认为 10，数字可配置）个周期内进行 8（默认为 8，数字可配置）次 re-balance 不通过

- 步骤 4:设置完成后，点击“确认”，健康检查规则设置成功

![text](http://img-ys011.didistatic.com/static/dc2img/do1_Md6TtfIGYQ2BWUytqeF4)

### 2.3.7、图表指标筛选

- 步骤 1:点击“多集群管理”>“集群卡片”>“Cluster”>“Overview”>“指标筛选 icon”>“指标筛选抽屉”

- 步骤 2:指标筛选抽屉展示信息为以下几类“Health”、“Cluster”、“Broker”、“Consumer”、“Security”、“Job”

- 步骤 3:默认勾选比较重要的指标进行展示。根据需要选中/取消选中相应指标，点击”确认“，指标筛选成功，展示的图表随之变化

![text](http://img-ys011.didistatic.com/static/dc2img/do1_bRWCetcKReMAT3BjAlSZ)

### 2.3.8、新增 ACL

- 步骤 1:点击“多集群管理”>“集群卡片”>“Security”>“Users”>“新增 ACL”

- 步骤 2:输入 ACL 配置参数

  - ACL 用途：生产权限、消费权限、自定义权限
  - 生产权限时：可选择应用于所有 Kafka User 或者特定 Kafka User；可选择应用于所有 Topic 或者特定 Topic
  - 消费权限时：可选择应用于所有 Kafka User 或者特定 Kafka User；可选择应用于所有 Topic 或者特定 Topic；可选择应用于所有 Consumer Group 或者特定 Consumer Group

- 步骤 3:点击“确定”，新增 ACL 成功

![text](http://img-ys011.didistatic.com/static/dc2img/do1_ygNmK5QIQcC8BsskMDy7)

### 2.3.9、用户管理

用户管理是提供给管理员进行人员管理和用户角色管理的功能模块，可以进行新增用户和分配角色。下面是一个典型的场景：
eg：团队加入了新成员，需要给这位成员分配一个使用系统的账号，需要以下几个步骤

- 步骤 1:点击“系统管理”>“用户管理”>“人员管理”>“新增用户”，输入“账号”、“实名”、“密码”，根据此账号所需要的权限，选择此账号所对应的角色。如果有满足权限的角色，则用户新增成功。如果没有满足权限的角色，则需要新增角色（步骤 2）

- 步骤 2:点击“系统管理”>“用户管理”>“角色管理”>“新增角色”。输入角色名称和描述，给此角色分配权限，点击“确定”，角色新增成功

- 步骤 3:根据此新增的角色，参考步骤 1，重新新增用户

- 步骤 4:此用户账号新增成功，可以进行登录产品使用

![text](http://img-ys011.didistatic.com/static/dc2img/do1_1gectG2B9xHKfEsapUJq)
