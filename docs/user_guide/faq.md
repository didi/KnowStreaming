
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# FAQ

- 0、支持哪些Kafka版本？
- 1、Topic申请、新建监控告警等操作时没有可选择的集群？
- 2、逻辑集群 & Region的用途？
- 3、登录失败？
- 4、页面流量信息等无数据？
- 5、如何对接夜莺的监控告警功能？
- 6、如何使用`MySQL 8`？
- 7、`Jmx`连接失败如何解决？
- 8、`topic biz data not exist`错误及处理方式
- 9、进程启动后，如何查看API文档
- 10、如何创建告警组？
- 11、连接信息、耗时信息为什么没有数据？
- 12、逻辑集群申请审批通过之后为什么看不到逻辑集群？

---

### 0、支持哪些Kafka版本？

基本上只要所使用的Kafka还依赖于Zookeeper，那么该版本的主要功能基本上应该就是支持的。

---

### 1、Topic申请、新建监控告警等操作时没有可选择的集群？

缺少逻辑集群导致的，在Topic管理、监控告警、集群管理这三个Tab下面都是普通用户视角，普通用户看到的集群都是逻辑集群，因此在这三个Tab下进行操作时，都需要有逻辑集群。

逻辑集群的创建参看：

- [kafka-manager 接入集群](add_cluster/add_cluster.md) 手册，这里的Region和逻辑集群都必须添加。

---

### 2、逻辑集群 & Region的用途？

主要用途是进行大集群的管理 & 集群细节的屏蔽。

- 逻辑集群：通过逻辑集群概念，将集群Broker按业务进行归类，方便管理；
- Region：通过引入Region，同时Topic按Region纬度创建，减少Broker间的连接；

---

### 3、登录失败？

- 检查使用的MySQL版本，8.0等版本暂不支持，具体版本辛苦查看 README 。

---

### 4、页面流量信息等无数据？

- 1、检查`Broker JMX`是否正确开启。

如若还未开启，具体可百度一下看如何开启，或者参看：[Jmx连接配置&问题解决说明文档](../dev_guide/connect_jmx_failed.md)

![helpcenter](./assets/faq/jmx_check.jpg)

- 2、`MySQL`的版本是否过高。

建议使用`MySQL 5.7`版本。

- 3、数据库时区问题。

检查MySQL的topic_metrics表，查看是否有数据，如果有数据，那么再检查设置的时区是否正确。

---

### 5、如何对接夜莺的监控告警功能？

- 参看 [kafka-manager 对接夜莺监控](../dev_guide/monitor_system_integrate_with_n9e.md) 说明。

---

### 6、如何使用`MySQL 8`？

- 参看 [kafka-manager 使用`MySQL 8`](../dev_guide/use_mysql_8.md) 说明。

---

### 7、`Jmx`连接失败如何解决？

- 参看 [Jmx连接配置&问题解决](../dev_guide/connect_jmx_failed.md) 说明。

---

### 8、`topic biz data not exist`错误及处理方式

**错误原因**

在进行权限审批的时候，可能会出现这个错误，出现这个错误的原因是因为Topic相关的业务信息没有在DB中存储，或者更具体的说就是该Topic不属于任何应用导致的，只需要将这些无主的Topic挂在某个应用下面即可。

**解决方式**

可以在`运维管控->集群列表->Topic信息`下面，编辑申请权限的Topic，为Topic选择一个应用即可。

以上仅仅只是针对单个Topic的场景，如果你有非常多的Topic需要进行初始化的，那么此时可以在配置管理中增加一个配置，来定时的对无主的Topic进行同步，具体见：[动态配置管理 - 1、Topic定时同步任务](../dev_guide/dynamic_config_manager.md)

---

### 9、进程启动后，如何查看API文档

- 滴滴Logi-KafkaManager采用Swagger-API工具记录API文档。Swagger-API地址： [http://IP:PORT/swagger-ui.html#/](http://IP:PORT/swagger-ui.html#/)


### 10、如何创建告警组？

这块需要配合监控系统进行使用，现在默认已经实现了夜莺的对接，当然也可以对接自己内部的监控系统，不过需要实现一些接口。

具体的文档可见：[监控功能对接夜莺](../dev_guide/monitor_system_integrate_with_n9e.md)、[监控功能对接其他系统](../dev_guide/monitor_system_integrate_with_self.md)

### 11、连接信息、耗时信息为什么没有数据？

这块需要结合滴滴内部的kafka-gateway一同使用才会有数据，滴滴kafka-gateway暂未开源。

### 12、逻辑集群申请审批通过之后为什么看不到逻辑集群？

逻辑集群的申请与审批仅仅只是一个工单流程，并不会去实际创建逻辑集群，逻辑集群的创建还需要手动去创建。

具体的操作可见：[kafka-manager 接入集群](add_cluster/add_cluster.md)。
