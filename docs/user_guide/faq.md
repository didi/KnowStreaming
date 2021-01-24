
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# FAQ 

- 1、Topic申请时没有可选择的集群？
- 2、逻辑集群 & Region的用途？
- 3、登录失败？
- 4、页面流量信息等无数据？
- 5、如何对接夜莺的监控告警功能？
- 6、如何使用`MySQL 8`？

---

### 1、Topic申请时没有可选择的集群？

- 参看 [kafka-manager 接入集群](docs/user_guide/add_cluster/add_cluster.md) 手册，这里的Region和逻辑集群都必须添加。

---

### 2、逻辑集群 & Region的用途？

主要用途是进行大集群的管理 & 集群细节的屏蔽。

- 逻辑集群：通过逻辑集群概念，将集群Broker按业务进行归类，方便管理；
- Region：通过引入Region，同时Topic按Region维度创建，减少Broker间的连接；

---

### 3、登录失败？

- 检查使用的MySQL版本，8.0等版本暂不支持，具体版本辛苦查看 README 。

---

### 4、页面流量信息等无数据？

- 1、检查`Broker JMX`是否正确开启。

如若还未开启，具体可百度一下看如何开启。

![helpcenter](./assets/faq/jmx_check.jpg)

- 2、`MySQL`的版本是否过高。

建议使用`MySQL 5.7`版本。

- 3、数据库时区问题。

检查MySQL的topic_metrics、broker_metrics表，查看是否有数据，如果有数据，那么再检查设置的时区是否正确。

---

### 5、如何对接夜莺的监控告警功能？

- 参看 [kafka-manager 对接夜莺监控](../dev_guide/monitor_system_integrate_with_n9e.md) 说明。

---

### 6、如何使用`MySQL 8`？

- 参看 [kafka-manager 使用`MySQL 8`](../dev_guide/use_mysql_8.md) 说明。
