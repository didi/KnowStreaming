
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# FAQ 

- 0、Github图裂问题解决
- 1、Topic申请、新建监控告警等操作时没有可选择的集群？
- 2、逻辑集群 & Region的用途？
- 3、登录失败？
- 4、页面流量信息等无数据？
- 5、如何对接夜莺的监控告警功能？
- 6、如何使用`MySQL 8`？
- 7、`Jmx`连接失败如何解决？
- 8、`topic biz data not exist`错误及处理方式

---

### 0、Github图裂问题解决

可以在本地机器`ping github.com`这个地址，获取到`github.com`地址的IP地址。

然后将IP绑定到`/etc/hosts`文件中。

例如

```shell
# 在 /etc/hosts文件中增加如下信息

140.82.113.3 github.com
```

---

### 1、Topic申请、新建监控告警等操作时没有可选择的集群？

缺少逻辑集群导致的，在Topic管理、监控告警、集群管理这三个Tab下面都是普通用户视角，普通用户看到的集群都是逻辑集群，因此在这三个Tab下进行操作时，都需要有逻辑集群。

逻辑集群的创建参看：

- [kafka-manager 接入集群](docs/user_guide/add_cluster/add_cluster.md) 手册，这里的Region和逻辑集群都必须添加。

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
