
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 安装手册

## 1、环境依赖

如果是以Release包进行安装的，则仅安装`Java`及`MySQL`即可。如果是要先进行源码包进行打包，然后再使用，则需要安装`Maven`及`Node`环境。

- `Java 8+`(运行环境需要)
- `MySQL 5.7`(数据存储)
- `Maven 3.5+`(后端打包依赖)
- `Node 10+`(前端打包依赖)

---

## 2、获取安装包

**1、Release直接下载**

这里如果觉得麻烦，然后也不想进行二次开发，则可以直接下载Release包，下载地址：[Github Release包下载地址](https://github.com/didi/Logi-KafkaManager/releases)

如果觉得Github的下载地址太慢了，也可以进入`Logi-KafkaManager`的用户群获取，群地址在README中。


**2、源代码进行打包**

下载好代码之后，进入`Logi-KafkaManager`的主目录，执行`sh build.sh`命令即可，执行完成之后会在`output/kafka-manager-xxx`目录下面生成一个jar包。

对于`windows`环境的用户，估计执行不了`sh build.sh`命令，因此可以直接执行`mvn install`，然后在`kafka-manager-web/target`目录下生成一个kafka-manager-web-xxx.jar的包。

获取到jar包之后，我们继续下面的步骤。

---

## 3、MySQL-DB初始化

执行[create_mysql_table.sql](create_mysql_table.sql)中的SQL命令，从而创建所需的MySQL库及表，默认创建的库名是`logi_kafka_manager`。

```
# 示例：
mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

---

## 4、启动

```
# application.yml 是配置文件，最简单的是仅修改MySQL相关的配置即可启动

nohup java -jar kafka-manager.jar --spring.config.location=./application.yml > /dev/null 2>&1 &
```

### 5、使用

本地启动的话，访问`http://localhost:8080`，输入帐号及密码(默认`admin/admin`)进行登录。更多参考：[kafka-manager 用户使用手册](../user_guide/user_guide_cn.md)

