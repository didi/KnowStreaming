
---

![kafka-manager-logo](../../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 升级至`2.2.0`版本

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

