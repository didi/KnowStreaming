
## 版本升级配置变更
> 本文件 从 V2.2.0 开始记录; 如果配置有变更则会填写到下文中; 如果没有,则表示无变更;
> 当您从一个很低的版本升级时候,应该依次执行中间有过变更的sql脚本



**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

### 1.升级至`V2.2.0`版本

#### 1.mysql变更

`2.2.0`版本在`cluster`表及`logical_cluster`各增加了一个字段，因此需要执行下面的sql进行字段的增加。

```sql
# 往cluster表中增加jmx_properties字段, 这个字段会用于存储jmx相关的认证以及配置信息
ALTER TABLE `cluster` ADD COLUMN `jmx_properties` TEXT NULL COMMENT 'JMX配置' AFTER `security_properties`;

# 往logical_cluster中增加identification字段, 同时数据和原先name数据相同, 最后增加一个唯一键. 
# 此后, name字段还是表示集群名称, 而identification字段表示的是集群标识, 只能是字母数字及下划线组成, 
# 数据上报到监控系统时, 集群这个标识采用的字段就是identification字段, 之前使用的是name字段.
ALTER TABLE `logical_cluster` ADD COLUMN `identification` VARCHAR(192) NOT NULL DEFAULT '' COMMENT '逻辑集群标识' AFTER `name`;

UPDATE `logical_cluster` SET `identification`=`name` WHERE id>=0;

ALTER TABLE `logical_cluster` ADD INDEX `uniq_identification` (`identification` ASC);
```

### 升级至`2.3.0`版本

#### 1.mysql变更
`2.3.0`版本在`gateway_config`表增加了一个描述说明的字段，因此需要执行下面的sql进行字段的增加。

```sql
ALTER TABLE `gateway_config` 
ADD COLUMN `description` TEXT NULL COMMENT '描述信息' AFTER `version`;
```

### 升级至`2.6.0`版本

#### 1.mysql变更
`2.6.0`版本在`account`表增加用户姓名，部门名，邮箱三个字段，因此需要执行下面的sql进行字段的增加。

```sql
ALTER TABLE `account`
ADD COLUMN `display_name` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '用户名' AFTER `role`,
ADD COLUMN `department` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '部门名' AFTER `display_name`,
ADD COLUMN `mail` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '邮箱' AFTER `department`;
```
