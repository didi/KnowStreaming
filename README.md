
---

![kafka-manager-logo](doc/assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

## 主要功能特性


### 集群监控维度

- 多版本集群管控，支持从`0.10.2`到`2.4`版本；
- 集群Topic、Broker等多维度历史与实时关键指标查看；


### 集群管控维度

- 集群运维，包括逻辑Region方式管理集群；
- Broker运维，包括优先副本选举；
- Topic运维，包括创建、查询、扩容、修改属性、数据采样及迁移等；
- 消费组运维，包括指定时间或指定偏移两种方式进行重置消费偏移；


### 用户使用维度

- 管理员用户与普通用户视角区分；
- 管理员用户与普通用户权限区分；

---

## kafka-manager架构图

![kafka-manager-arch](doc/assets/images/common/arch.png)


---

## 安装手册

### 环境依赖

- `Maven 3.5.0+`(后端打包依赖)
- `node v8.12.0+`(前端打包依赖)
- `Java 8+`(运行环境需要)
- `MySQL`(数据存储)

---

### 环境初始化

执行[create_mysql_table.sql](doc/create_mysql_table.sql)中的SQL命令，从而创建所需的MySQL库及表，默认创建的库名是`kafka_manager`。

```
#############  示例：
mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

---


### 打包

执行`mvn install`命令即可。

备注：每一次执行`mvn install`命令，都将在`web/src/main/resources/templates`下面生成最新的前端资源文件，如果`console`模块下的代码没有变更，可以修改`./pom.xml`文件，忽略对`console`模块的打包。

---

### 启动

```
############# application.yml 是配置文件
cp web/src/main/resources/application.yml web/target/
cd web/target/
nohup java -jar kafka-manager-web-1.0.0-SNAPSHOT.jar --spring.config.location=./application.yml > /dev/null 2>&1 &
```

### 使用

本地启动的话，访问`http://localhost:8080`，输入帐号及密码进行登录。更多参考：[kafka-manager使用手册](./user_cn_guide.md)


---

## 相关文档

- [kafka-manager使用手册](doc/user_cn_guide.md)


## 钉钉交流群

![dingding_group](doc/assets/images/common/dingding_group.jpg)


## 项目成员

### 内部核心人员

`iceyuhui`、`liuyaguang`、`limengmonty`、`zhangliangmike`、`nullhuangyiming`、`zengqiao`、`eilenexuzhe`、`huangjiaweihjw`


### 外部贡献者

`fangjunyu`、`zhoutaiyang`


## 协议

`kafka-manager`基于`Apache-2.0`协议进行分发和使用，更多信息参见[协议文件](./LICENSE)
