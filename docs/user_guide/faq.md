
![Logo](https://user-images.githubusercontent.com/71620349/185368586-aed82d30-1534-453d-86ff-ecfa9d0f35bd.png)

# FAQ

- [FAQ](#faq)
  - [1、支持哪些 Kafka 版本？](#1支持哪些-kafka-版本)
  - [1、2.x 版本和 3.0 版本有什么差异？](#12x-版本和-30-版本有什么差异)
  - [3、页面流量信息等无数据？](#3页面流量信息等无数据)
  - [4、`Jmx`连接失败如何解决？](#4jmx连接失败如何解决)
  - [5、有没有 API 文档？](#5有没有-api-文档)
  - [6、删除 Topic 成功后，为何过段时间又出现了？](#6删除-topic-成功后为何过段时间又出现了)
  - [7、如何在不登录的情况下，调用接口？](#7如何在不登录的情况下调用接口)
  - [8、Specified key was too long; max key length is 767 bytes](#8specified-key-was-too-long-max-key-length-is-767-bytes)
  - [9、出现 ESIndexNotFoundEXception 报错](#9出现-esindexnotfoundexception-报错)
  - [10、km-console 打包构建失败](#10km-console-打包构建失败)
  - [11、在 `km-console` 目录下执行 `npm run start` 时看不到应用构建和热加载过程？如何启动单个应用？](#11在-km-console-目录下执行-npm-run-start-时看不到应用构建和热加载过程如何启动单个应用)
  - [12、权限识别失败问题](#12权限识别失败问题)
  - [13、接入开启kerberos认证的kafka集群](#13接入开启kerberos认证的kafka集群)
  - [14、对接Ldap的配置](#14对接ldap的配置)
  - [15、测试时使用Testcontainers的说明](#15测试时使用testcontainers的说明)
  - [16、JMX连接失败怎么办](#16jmx连接失败怎么办)
  - [17、zk监控无数据问题](#17zk监控无数据问题)
  - [18、启动失败，报NoClassDefFoundError如何解决](#18启动失败报noclassdeffounderror如何解决)
  - [19、依赖ElasticSearch 8.0以上版本部署后指标信息无法正常显示如何解决]

## 1、支持哪些 Kafka 版本？

- 支持 0.10+ 的 Kafka 版本；
- 支持 ZK 及 Raft 运行模式的 Kafka 版本；

&nbsp;

## 1、2.x 版本和 3.0 版本有什么差异？

**全新设计理念**

- 在 0 侵入、0 门槛的前提下提供直观 GUI 用于管理和观测 Apache Kafka®，帮助用户降低 Kafka CLI 操作门槛，轻松实现对原生 Kafka 集群的可管、可见、可掌控，提升 Kafka 使用体验和降低管理成本。
- 支持海量集群一键接入，无需任何改造，即可实现集群深度纳管，真正的 0 侵入、插件化系统设计，覆盖 0.10.x-3.x.x 众多 Kafka 版本无缝纳管。

**开源协议调整**

- 3.x：AGPL 3.0
- 2.x：Apache License 2.0

更多具体内容见：[新旧版本对比](https://doc.knowstreaming.com/product/9-attachment#92%E6%96%B0%E6%97%A7%E7%89%88%E6%9C%AC%E5%AF%B9%E6%AF%94)

&nbsp;

## 3、页面流量信息等无数据？

- 1、`Broker JMX`未正确开启

可以参看：[Jmx 连接配置&问题解决](https://doc.knowstreaming.com/product/9-attachment#91jmx-%E8%BF%9E%E6%8E%A5%E5%A4%B1%E8%B4%A5%E9%97%AE%E9%A2%98%E8%A7%A3%E5%86%B3)

- 2、`ES` 存在问题

建议使用`ES 7.6`版本，同时创建近 7 天的索引，具体见：[快速开始](./1-quick-start.md) 中的 ES 索引模版及索引创建。

&nbsp;

## 4、`Jmx`连接失败如何解决？

- 参看 [Jmx 连接配置&问题解决](https://doc.knowstreaming.com/product/9-attachment#91jmx-%E8%BF%9E%E6%8E%A5%E5%A4%B1%E8%B4%A5%E9%97%AE%E9%A2%98%E8%A7%A3%E5%86%B3) 说明。

&nbsp;

## 5、有没有 API 文档？

`KnowStreaming` 采用 Swagger 进行 API 说明，在启动 KnowStreaming 服务之后，就可以从下面地址看到。

Swagger-API 地址： [http://IP:PORT/swagger-ui.html#/](http://IP:PORT/swagger-ui.html#/)

&nbsp;

## 6、删除 Topic 成功后，为何过段时间又出现了？

**原因说明：**

`KnowStreaming` 会去请求 Topic 的 endoffset 信息，要获取这个信息就需要发送 metadata 请求，发送 metadata 请求的时候，如果集群允许自动创建 Topic，那么当 Topic 不存在时，就会自动将该 Topic 创建出来。

**问题解决：**

因为在 `KnowStreaming` 上，禁止 Kafka 客户端内部元信息获取这个动作非常的难做到，因此短时间内这个问题不好从 `KnowStreaming` 上解决。

当然，对于不存在的 Topic，`KnowStreaming` 是不会进行元信息请求的，因此也不用担心会莫名其妙的创建一个 Topic 出来。

但是，另外一点，对于开启允许 Topic 自动创建的集群，建议是关闭该功能，开启是非常危险的，如果关闭之后，`KnowStreaming` 也不会有这个问题。

最后这里举个开启这个配置后，非常危险的代码例子吧：

```java
for (int i= 0; i < 100000; ++i) {
    // 如果是客户端类似这样写的，那么一启动，那么将创建10万个Topic出来，集群元信息瞬间爆炸，controller可能就不可服务了。
    producer.send(new ProducerRecord<String, String>("know_streaming" + i,"hello logi_km"));
}
```

&nbsp;

## 7、如何在不登录的情况下，调用接口？

步骤一：接口调用时，在 header 中，增加如下信息：

```shell
# 表示开启登录绕过
Trick-Login-Switch : on

# 登录绕过的用户, 这里可以是admin, 或者是其他的, 但是必须在系统管理->用户管理中设置了该用户。
Trick-Login-User : admin
```

&nbsp;

步骤二：点击右上角"系统管理"，选择配置管理，在页面中添加以下键值对。

```shell
# 模块选择
SECURITY.LOGIN

# 设置的配置键，必须是这个
SECURITY.TRICK_USERS

# 设置的value，是json数组的格式，包含步骤一header中设置的用户名，例如
[ "admin", "logi"]
```

&nbsp;

步骤三：解释说明

设置完成上面两步之后，就可以直接调用需要登录的接口了。

但是还有一点需要注意，绕过的用户仅能调用他有权限的接口，比如一个普通用户，那么他就只能调用普通的接口，不能去调用运维人员的接口。

## 8、Specified key was too long; max key length is 767 bytes

**原因：** 不同版本的 InoDB 引擎，参数‘innodb_large_prefix’默认值不同，即在 5.6 默认值为 OFF，5.7 默认值为 ON。

对于引擎为 InnoDB，innodb_large_prefix=OFF，且行格式为 Antelope 即支持 REDUNDANT 或 COMPACT 时，索引键前缀长度最大为 767 字节。innodb_large_prefix=ON，且行格式为 Barracuda 即支持 DYNAMIC 或 COMPRESSED 时，索引键前缀长度最大为 3072 字节。

**解决方案：**

- 减少 varchar 字符大小低于 767/4=191。
- 将字符集改为 latin1（一个字符=一个字节）。
- 开启‘innodb_large_prefix’，修改默认行格式‘innodb_file_format’为 Barracuda，并设置 row_format=dynamic。

## 9、出现 ESIndexNotFoundEXception 报错

**原因 ：**没有创建 ES 索引模版

**解决方案：**执行 init_es_template.sh 脚本，创建 ES 索引模版即可。

## 10、km-console 打包构建失败

首先，**请确保您正在使用最新版本**，版本列表见 [Tags](https://github.com/didi/KnowStreaming/tags)。如果不是最新版本，请升级后再尝试有无问题。

常见的原因是由于工程依赖没有正常安装，导致在打包过程中缺少依赖，造成打包失败。您可以检查是否有以下文件夹，且文件夹内是否有内容

```
KnowStreaming/km-console/node_modules
KnowStreaming/km-console/packages/layout-clusters-fe/node_modules
KnowStreaming/km-console/packages/config-manager-fe/node_modules
```

如果发现没有对应的 `node_modules` 目录或着目录内容为空，说明依赖没有安装成功。请按以下步骤操作，

1. 手动删除上述三个文件夹（如果有）

2. 如果之前是通过 `mvn install` 打包 `km-console`，请到项目根目录（KnowStreaming）下重新输入该指令进行打包。观察打包过程有无报错。如有报错，请见步骤 4。

3. 如果是通过本地独立构建前端工程的方式（指直接执行 `npm run build`），请进入 `KnowStreaming/km-console` 目录，执行下述步骤（注意：执行时请确保您在使用 `node v12` 版本）

   a. 执行 `npm run i`。如有报错，请见步骤 4。

   b. 执行 `npm run build`。如有报错，请见步骤 4。

4. 麻烦联系我们协助解决。推荐提供以下信息，方面我们快速定位问题，示例如下。

```
操作系统: Mac
命令行终端：bash
Node 版本: v12.22.12
复现步骤: 1. -> 2.
错误截图:
```

## 11、在 `km-console` 目录下执行 `npm run start` 时看不到应用构建和热加载过程？如何启动单个应用？

需要到具体的应用中执行 `npm run start`，例如 `cd packages/layout-clusters-fe` 后，执行 `npm run start`。

应用启动后需要到基座应用中查看（需要启动基座应用，即 layout-clusters-fe）。


## 12、权限识别失败问题
1、使用admin账号登陆KnowStreaming时，点击系统管理-用户管理-角色管理-新增角色，查看页面是否正常。

<img src="http://img-ys011.didistatic.com/static/dc2img/do1_gwGfjN9N92UxzHU8dfzr" width = "400" >

2、查看'/logi-security/api/v1/permission/tree'接口返回值，出现如下图所示乱码现象。
![接口返回值](http://img-ys011.didistatic.com/static/dc2img/do1_jTxBkwNGU9vZuYQQbdNw)

3、查看logi_security_permission表，看看是否出现了中文乱码现象。

根据以上几点，我们可以确定是由于数据库乱码造成的权限识别失败问题。

+ 原因：由于数据库编码和我们提供的脚本不一致，数据库里的数据发生了乱码，因此出现权限识别失败问题。
+ 解决方案：清空数据库数据，将数据库字符集调整为utf8，最后重新执行[dml-logi.sql](https://github.com/didi/KnowStreaming/blob/master/km-dist/init/sql/dml-logi.sql)脚本导入数据即可。


## 13、接入开启kerberos认证的kafka集群

1. 部署KnowStreaming的机器上安装krb客户端；
2. 替换/etc/krb5.conf配置文件；
3. 把kafka对应的keytab复制到改机器目录下；
4. 接入集群时认证配置，配置信息根据实际情况填写；
```json
{
  "security.protocol": "SASL_PLAINTEXT",
  "sasl.mechanism": "GSSAPI",
  "sasl.jaas.config": "com.sun.security.auth.module.Krb5LoginModule required useKeyTab=true keyTab=\"/etc/keytab/kafka.keytab\" storeKey=true useTicketCache=false principal=\"kafka/kafka@TEST.COM\";",
  "sasl.kerberos.service.name": "kafka"
}
```


## 14、对接Ldap的配置

```yaml
# 需要在application.yml中增加如下配置。相关配置的信息，按实际情况进行调整
account:
  ldap:
    url: ldap://127.0.0.1:8080/
    basedn: DC=senz,DC=local
    factory: com.sun.jndi.ldap.LdapCtxFactory
    filter: sAMAccountName
    security:
      authentication: simple
      principal: CN=search,DC=senz,DC=local
      credentials: xxxxxxx
    auth-user-registration: false # 是否注册到mysql，默认false
    auth-user-registration-role: 1677 # 1677是超级管理员角色的id，如果赋予想默认赋予普通角色，可以到ks新建一个。

# 需要在application.yml中修改如下配置
spring:
  logi-security:
    login-extend-bean-name: ksLdapLoginService # 表示使用ldap的service
```

## 15、测试时使用Testcontainers的说明

1. 需要docker运行环境 [Testcontainers运行环境说明](https://www.testcontainers.org/supported_docker_environment/)
2. 如果本机没有docker，可以使用[远程访问docker](https://docs.docker.com/config/daemon/remote-access/) [Testcontainers配置说明](https://www.testcontainers.org/features/configuration/#customizing-docker-host-detection)  


## 16、JMX连接失败怎么办

详细见：[解决连接JMX失败](../dev_guide/%E8%A7%A3%E5%86%B3%E8%BF%9E%E6%8E%A5JMX%E5%A4%B1%E8%B4%A5.md)


## 17、zk监控无数据问题

**现象：** 
zookeeper集群正常，但Ks上zk页面所有监控指标无数据，`KnowStreaming` log_error.log日志提示

```vim
[MetricCollect-Shard-0-8-thread-1] ERROR class=c.x.k.s.k.c.s.h.c.z.HealthCheckZookeeperService||method=checkWatchCount||param=ZookeeperParam(zkAddressList=[Tuple{v1=192.168.xxx.xx, v2=2181}, Tuple{v1=192.168.xxx.xx, v2=2181}, Tuple{v1=192.168.xxx.xx, v2=2181}], zkConfig=null)||config=HealthAmountRatioConfig(amount=100000, ratio=0.8)||result=Result{message='mntr is not executed because it is not in the whitelist.
', code=8031, data=null}||errMsg=get metrics failed, may be collect failed or zk mntr command not in whitelist.
2023-04-23 14:39:07.234 [MetricCollect-Shard-0-8-thread-1] ERROR class=c.x.k.s.k.c.s.h.checker.AbstractHeal
```


原因就很明确了。需要开放zk的四字命令，在`zoo.cfg`配置文件中添加
```
4lw.commands.whitelist=mntr,stat,ruok,envi,srvr,envi,cons,conf,wchs,wchp
```


建议至少开放上述几个四字命令，当然，您也可以全部开放
```
4lw.commands.whitelist=*
```

## 18、启动失败，报NoClassDefFoundError如何解决

**错误现象：** 
```log
# 启动失败，报nested exception is java.lang.NoClassDefFoundError: Could not initialize class com.didiglobal.logi.job.core.WorkerSingleton$Singleton


2023-08-11 22:54:29.842 [main] ERROR class=org.springframework.boot.SpringApplication||Application run failed
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'quartzScheduler' defined in class path resource [com/didiglobal/logi/job/LogIJobAutoConfiguration.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [com.didiglobal.logi.job.core.Scheduler]: Factory method 'quartzScheduler' threw exception; nested exception is java.lang.NoClassDefFoundError: Could not initialize class com.didiglobal.logi.job.core.WorkerSingleton$Singleton
at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:657)
```


**问题原因：**
1. `KnowStreaming` 依赖的 `Logi-Job` 初始化 `WorkerSingleton$Singleton` 失败。
2. `WorkerSingleton$Singleton` 初始化的过程中，会去获取一些操作系统的信息，如果获取时出现了异常，则会导致 `WorkerSingleton$Singleton` 初始化失败。


**临时建议：**

`Logi-Job` 问题的修复时间不好控制，之前我们测试验证了一下，在 `Windows`、`Mac`、`CentOS` 这几个操作系统下基本上都是可以正常运行的。

所以，如果有条件的话，可以暂时先使用这几个系统部署 `KnowStreaming`。

如果在在 `Windows`、`Mac`、`CentOS` 这几个操作系统下也出现了启动失败的问题，可以重试2-3次看是否还是启动失败，或者换一台机器试试。

## 依赖ElasticSearch 8.0以上版本部署后指标信息无法正常显示如何解决
**错误现象**
```log
Warnings: [299 Elasticsearch-8.9.1-a813d015ef1826148d9d389bd1c0d781c6e349f0 "Legacy index templates are deprecated in favor of composable templates."]
```
**问题原因**
1. ES8.0和ES7.0版本存在Template模式的差异，建议使用 /_index_template 端点来管理模板；
2. ES java client在此版本的行为很奇怪表现为读取数据为空；

**解决方法**
修改`es_template_create.sh`脚本中所有的`/_template`为`/_index_template`后执行即可。

