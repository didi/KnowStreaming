
---

![kafka-manager-logo](./assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 安装手册


## 环境依赖

- `Maven`(后端打包依赖)
- `node 10+`(前端打包依赖)
- `Java 8+`(运行环境需要)
- `MySQL`(数据存储)

---

## 环境初始化

执行[create_mysql_table.sql](./create_mysql_table.sql)中的SQL命令，从而创建所需的MySQL库及表，默认创建的库名是`kafka_manager`。

```
# 示例：
mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

---

## 打包

```bash
# 前端工程包
cd kafka-manager-opensource/console
npm install
npm run prod-build
mv dist/* ../web/src/main/resources/templates

# 后端工程包
cd ..
mvn install

```

---

## 启动

```
# application.yml 是配置文件

cp web/src/main/resources/application.yml web/target/
cd web/target/
nohup java -jar kafka-manager-web-1.0.0-SNAPSHOT.jar --spring.config.location=./application.yml > /dev/null 2>&1 &
```

## 使用

本地启动的话，访问`http://localhost:8080`，输入帐号及密码进行登录。更多参考：[kafka-manager使用手册](./user_cn_guide.md)

