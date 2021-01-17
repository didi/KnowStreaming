
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 监控系统集成——夜莺

- `Kafka-Manager`通过将 监控的数据 以及 监控的规则 都提交给夜莺，然后依赖夜莺的监控系统从而实现监控告警功能。

- 监控数据上报 & 告警规则的创建等能力已经具备。但类似查看告警历史，告警触发时的监控数据等正在集成中(暂时可以到夜莺系统进行查看)，欢迎有兴趣的同学进行共建 或 贡献代码。

## 1、配置说明

```yml
# 配置文件中关于监控部分的配置
monitor:
  enabled: false
  n9e:
    nid: 2
    user-token: 123456
    # 夜莺 mon监控服务 地址
    mon:
      base-url: http://127.0.0.1:8006
    # 夜莺 transfer上传服务 地址
    sink:
      base-url: http://127.0.0.1:8008
    # 夜莺 rdb资源服务 地址
    rdb:
      base-url: http://127.0.0.1:80

# enabled: 表示是否开启监控告警的功能, true: 开启, false: 不开启
# n9e.nid: 夜莺的节点ID
# n9e.user-token: 用户的密钥，在夜莺的个人设置中
# n9e.mon.base-url: 监控地址
# n9e.sink.base-url: 数据上报地址
# n9e.rdb.base-url: 用户资源中心地址
```

