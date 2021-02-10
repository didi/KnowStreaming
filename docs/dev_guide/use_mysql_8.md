
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 使用`MySQL 8`

感谢 [herry-hu](https://github.com/herry-hu) 提供的方案。


当前因为无法同时兼容`MySQL 8`与`MySQL 5.7`，因此代码中默认的版本还是`MySQL 5.7`。


当前如需使用`MySQL 8`，则需按照下述流程进行简单修改代码。


- Step1. 修改application.yml中的MySQL驱动类
```shell

# 将driver-class-name后面的驱动类修改为:
# driver-class-name: com.mysql.jdbc.Driver
driver-class-name: com.mysql.cj.jdbc.Driver
```


- Step2. 修改MySQL依赖包
```shell
# 将根目录下面的pom.xml文件依赖的`MySQL`依赖包版本调整为

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
#    <version>5.1.41</version>
    <version>8.0.20</version>
</dependency>
```

