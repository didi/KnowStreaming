
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 安装手册


## 环境依赖

- `Maven 3.5+`(后端打包依赖)
- `node v12+`(前端打包依赖)
- `Java 8+`(运行环境需要)
- `MySQL 5.7`(数据存储)

---

## 环境初始化

执行[create_mysql_table.sql](create_mysql_table.sql)中的SQL命令，从而创建所需的MySQL库及表，默认创建的库名是`kafka_manager`。

```
# 示例：
mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

---

## 打包

```bash

# 一次性打包
cd ..
mvn install

```

---

## 启动

```
# application.yml 是配置文件

cp kafka-manager-web/src/main/resources/application.yml kafka-manager-web/target/
cd kafka-manager-web/target/
nohup java -jar kafka-manager-web-2.1.0-SNAPSHOT.jar --spring.config.location=./application.yml > /dev/null 2>&1 &
```

## 使用

本地启动的话，访问`http://localhost:8080`，输入帐号及密码(默认`admin/admin`)进行登录。更多参考：[kafka-manager 用户使用手册](../user_guide/user_guide_cn.md)

