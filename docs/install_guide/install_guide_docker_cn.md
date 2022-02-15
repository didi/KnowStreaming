---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

---


## 基于Docker部署Logikm

为了方便用户快速的在自己的环境搭建Logikm，可使用docker快速搭建

### 部署Mysql

```shell
docker run --name mysql -p 3306:3306 -d registry.cn-hangzhou.aliyuncs.com/zqqq/logikm-mysql:5.7.37
```

可选变量参考[文档](https://hub.docker.com/_/mysql)

默认参数

* MYSQL_ROOT_PASSWORD：root



### 部署Logikm Allinone

> 前后端部署在一起

```shell
docker run --name logikm -p 8080:8080 --link mysql -d registry.cn-hangzhou.aliyuncs.com/zqqq/logikm:2.6.0
```

参数详解：

* -p 映射容器8080端口至宿主机的8080
* --link 连接mysql容器



### 部署前后端分离

#### 部署后端 Logikm-backend

```shell
docker run --name logikm-backend --link mysql -d registry.cn-hangzhou.aliyuncs.com/zqqq/logikm-backend:2.6.0
```

可选参数：

* -e LOGI_MYSQL_HOST mysql连接地址，默认mysql
* -e LOGI_MYSQL_PORT mysql端口，默认3306
* -e LOGI_MYSQL_DATABASE 数据库，默认logi_kafka_manager
* -e LOGI_MYSQL_USER mysql用户名，默认root
* -e LOGI_MYSQL_PASSWORD mysql密码，默认root

#### 部署前端 Logikm-front

```shell
docker run --name logikm-front -p 8088:80 --link logikm-backend -d registry.cn-hangzhou.aliyuncs.com/zqqq/logikm-front:2.6.0
```



### Logi后端可配置参数

docker run 运行参数 -e 可指定环境变量如下

| 环境变量            | 变量解释      | 默认值             |
| ------------------- | ------------- | ------------------ |
| LOGI_MYSQL_HOST     | mysql连接地址 | mysql              |
| LOGI_MYSQL_PORT     | mysql端口     | 3306               |
| LOGI_MYSQL_DATABASE | 数据库        | logi_kafka_manager |
| LOGI_MYSQL_USER     | mysql用户名   | root               |
| LOGI_MYSQL_PASSWORD | mysql密码     | root               |




## 基于Docker源码构建

根据此文档用户可自行通过Docker 源码构建 Logikm

### 构建Mysql

```shell
docker build -t mysql:{TAG} -f container/dockerfiles/mysql/Dockerfile container/dockerfiles/mysql
```

### 构建Allinone

将前后端打包在一起

```shell
docker build -t logikm:{TAG} .
```

可选参数 --build-arg ：

* MAVEN_VERSION   maven镜像tag
* JAVA_VERSION   java镜像tag



### 构建前后端分离

前后端分离打包

#### 构建后端

```shell
docker build --build-arg CONSOLE_ENABLE=false -t logikm-backend:{TAG} .
```

参数：

* MAVEN_VERSION   maven镜像tag
* JAVA_VERSION   java镜像tag

* CONSOLE_ENABLE=false 不构建console模块

#### 构建前端

```shell
docker build -t logikm-front:{TAG} -f kafka-manager-console/Dockerfile kafka-manager-console
```

可选参数：

* --build-arg：OUTPUT_PATH 修改默认打包输出路径，默认当前目录下的dist