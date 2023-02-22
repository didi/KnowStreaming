---
order: 6
title: '6.开发手册'
toc: menu
---

## 6.1、本地源码启动手册

### 6.1.1、打包方式

`Know Streaming` 采用前后端分离的开发模式，使用 Maven 对项目进行统一的构建管理。maven 在打包构建过程中，会将前后端代码一并打包生成最终的安装包。

`Know Streaming` 除了使用安装包启动之外，还可以通过本地源码启动完整的带前端页面的项目，下面我们正式开始介绍本地源码如何启动 `Know Streaming`。

### 6.1.2、环境要求

**系统支持**

`windows7+`、`Linux`、`Mac`

**环境依赖**

- Maven 3.6.3
- Node v12.20.0
- Java 8+
- MySQL 5.7
- Idea
- Elasticsearch 7.6
- Git

### 6.1.3、环境初始化

安装好环境信息之后，还需要初始化 MySQL 与 Elasticsearch 信息，包括：

- 初始化 MySQL 表及数据
- 初始化 Elasticsearch 索引

具体见：[快速开始](./1-quick-start.md) 中的最后一步，部署 KnowStreaming 服务中的初始化相关工作。

### 6.1.4、本地启动

**第一步：本地打包**

执行 `mvn install` 可对项目进行前后端同时进行打包，通过该命令，除了可以对后端进行打包之外，还可以将前端相关的静态资源文件也一并打包出来。

**第二步：修改配置**

```yaml
# 修改 km-rest/src/main/resources/application.yml 中相关的配置

# 修改MySQL的配置，中间省略了一些非必需修改的配置
spring:
  datasource:
    know-streaming:
      jdbc-url: 修改为实际MYSQL地址
      username: 修改为实际MYSQL用户名
      password: 修改为实际MYSQL密码
  logi-job:
    jdbc-url: 修改为实际MYSQL地址
    username: 修改为实际MYSQL用户名
    password: 修改为实际MYSQL密码
  logi-security:
    jdbc-url: 修改为实际MYSQL地址
    username: 修改为实际MYSQL用户名
    password: 修改为实际MYSQL密码

# 修改ES的配置，中间省略了一些非必需修改的配置
es.client.address: 修改为实际ES地址
```

**第三步：配置 IDEA**

`Know Streaming`的 Main 方法在：

```java
km-rest/src/main/java/com/xiaojukeji/know/streaming/km/rest/KnowStreaming.java
```

IDEA 更多具体的配置如下图所示：

<p align="center">
<img src="http://img-ys011.didistatic.com/static/dc2img/do1_BW1RzgEMh4n6L4dL4ncl" width = "512" height = "318" div align=center />
</p>

**第四步：启动项目**

最后就是启动项目，在本地 console 中输出了 `KnowStreaming-KM started` 则表示我们已经成功启动 `Know Streaming` 了。

### 6.1.5、本地访问

`Know Streaming` 启动之后，可以访问一些信息，包括：

- 产品页面：http://localhost:8080 ，默认账号密码：`admin` / `admin` 进行登录。（注意！v3.0 beta1 版本默认账号和密码为：`admin` / `admin2022_`）
- 接口地址：http://localhost:8080/swagger-ui.html 查看后端提供的相关接口。

更多信息，详见：[KnowStreaming 官网](https://knowstreaming.com/)

## 6.2、版本升级手册

注意：如果想升级至具体版本，需要将你当前版本至你期望使用版本的变更统统执行一遍，然后才能正常使用。

### 6.2.0、升级至 `master` 版本

暂无

### 6.2.1、升级至 `v3.0.0-beta.2`版本

**配置变更**

```yaml

# 新增配置
spring:
  logi-security: # know-streaming 依赖的 logi-security 模块的数据库的配置，默认与 know-streaming 的数据库配置保持一致即可
    login-extend-bean-name: logiSecurityDefaultLoginExtendImpl # 使用的登录系统Service的Bean名称，无需修改

# 线程池大小相关配置，在task模块中，新增了三类线程池，
# 从而减少不同类型任务之间的相互影响，以及减少对logi-job内的线程池的影响
thread-pool:
  task:                             # 任务模块的配置
    metrics:                        # metrics采集任务配置
      thread-num: 18                # metrics采集任务线程池核心线程数
      queue-size: 180               # metrics采集任务线程池队列大小
    metadata:                       # metadata同步任务配置
      thread-num: 27                # metadata同步任务线程池核心线程数
      queue-size: 270               # metadata同步任务线程池队列大小
    common:                         # 剩余其他任务配置
      thread-num: 15                # 剩余其他任务线程池核心线程数
      queue-size: 150               # 剩余其他任务线程池队列大小

# 删除配置，下列配置将不再使用
thread-pool:
  task:                             # 任务模块的配置
    heaven:                         # 采集任务配置
      thread-num: 20                # 采集任务线程池核心线程数
      queue-size: 1000              # 采集任务线程池队列大小

```

**SQL 变更**

```sql
-- 多集群管理权限2022-09-06新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2000', '多集群管理查看', '1593', '1', '2', '多集群管理查看', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2002', 'Topic-迁移副本', '1593', '1', '2', 'Topic-迁移副本', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2004', 'Topic-扩缩副本', '1593', '1', '2', 'Topic-扩缩副本', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2006', 'Cluster-LoadReBalance-周期均衡', '1593', '1', '2', 'Cluster-LoadReBalance-周期均衡', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2008', 'Cluster-LoadReBalance-立即均衡', '1593', '1', '2', 'Cluster-LoadReBalance-立即均衡', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2010', 'Cluster-LoadReBalance-设置集群规格', '1593', '1', '2', 'Cluster-LoadReBalance-设置集群规格', '0', 'know-streaming');


-- 系统管理权限2022-09-06新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('3000', '系统管理查看', '1595', '1', '2', '系统管理查看', '0', 'know-streaming');


INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2000', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2002', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2004', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2006', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2008', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2010', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '3000', '0', 'know-streaming');

-- 修改字段长度
ALTER TABLE `logi_security_oplog`
    CHANGE COLUMN `operator_ip` `operator_ip` VARCHAR(64) NOT NULL COMMENT '操作者ip' ,
    CHANGE COLUMN `operator` `operator` VARCHAR(64) NULL DEFAULT NULL COMMENT '操作者账号' ,
    CHANGE COLUMN `operate_page` `operate_page` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '操作页面' ,
    CHANGE COLUMN `operate_type` `operate_type` VARCHAR(64) NOT NULL COMMENT '操作类型' ,
    CHANGE COLUMN `target_type` `target_type` VARCHAR(64) NOT NULL COMMENT '对象分类' ,
    CHANGE COLUMN `target` `target` VARCHAR(1024) NOT NULL COMMENT '操作对象' ,
    CHANGE COLUMN `operation_methods` `operation_methods` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '操作方式' ;
```

---

### 6.2.2、升级至 `v3.0.0-beta.1`版本

**SQL 变更**

1、在`ks_km_broker`表增加了一个监听信息字段。
2、为`logi_security_oplog`表 operation_methods 字段设置默认值''。
因此需要执行下面的 sql 对数据库表进行更新。

```sql
ALTER TABLE `ks_km_broker`
ADD COLUMN `endpoint_map` VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '监听信息' AFTER `update_time`;

ALTER TABLE `logi_security_oplog`
ALTER COLUMN `operation_methods` set default '';

```

---

### 6.2.3、`2.x`版本 升级至 `v3.0.0-beta.0`版本

**升级步骤：**

1. 依旧使用**`2.x 版本的 DB`**，在上面初始化 3.0.0 版本所需数据库表结构及数据；
2. 将 2.x 版本中的集群，在 3.0.0 版本，手动逐一接入；
3. 将 Topic 业务数据，迁移至 3.0.0 表中，详见下方 SQL；

**注意事项**

- 建议升级 3.0.0 版本过程中，保留 2.x 版本的使用，待 3.0.0 版本稳定使用后，再下线 2.x 版本；
- 3.0.0 版本仅需要`集群信息`及`Topic的描述信息`。2.x 版本的 DB 的其他数据 3.0.0 版本都不需要；
- 部署 3.0.0 版本之后，集群、Topic 等指标数据都为空，3.0.0 版本会周期进行采集，运行一段时间之后就会有该数据了，因此不会将 2.x 中的指标数据进行迁移；

**迁移数据**

```sql
-- 迁移Topic的备注信息。
-- 需在 3.0.0 部署完成后，再执行该SQL。
-- 考虑到 2.x 版本中还存在增量数据，因此建议改SQL周期执行，是的增量数据也能被迁移至 3.0.0 版本中。

UPDATE ks_km_topic
    INNER JOIN
    (SELECT
        topic.cluster_id AS cluster_id,
        topic.topic_name AS topic_name,
        topic.description AS description
    FROM topic WHERE description != ''
    ) AS t

    ON ks_km_topic.cluster_phy_id = t.cluster_id
        AND ks_km_topic.topic_name = t.topic_name
        AND ks_km_topic.id > 0
SET ks_km_topic.description = t.description;
```
