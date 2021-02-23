
---

![kafka-manager-logo](../../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 升级至`2.3.0`版本

`2.3.0`版本在`gateway_config`表增加了一个描述说明的字段，因此需要执行下面的sql进行字段的增加。

```sql
ALTER TABLE `gateway_config` 
ADD COLUMN `description` TEXT NULL COMMENT '描述信息' AFTER `version`;
```
