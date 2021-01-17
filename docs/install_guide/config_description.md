
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 配置说明

```yaml
server:
  port: 8080  # 服务端口
  tomcat:
    accept-count: 1000
    max-connections: 10000
    max-threads: 800
    min-spare-threads: 100

spring:
  application:
    name: kafkamanager
  datasource:
    kafka-manager: # 数据库连接配置
      jdbc-url: jdbc:mysql://127.0.0.1:3306/kafka_manager?characterEncoding=UTF-8&serverTimezone=GMT%2B8  #数据库的地址
      username: admin # 用户名
      password: admin # 密码
      driver-class-name: com.mysql.jdbc.Driver
  main:
    allow-bean-definition-overriding: true

  profiles:
    active: dev # 启用的配置
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

logging:
  config: classpath:logback-spring.xml

custom:
  idc: cn # 部署的数据中心, 忽略该配置, 后续会进行删除
  jmx:
    max-conn: 10 # 和单台 broker 的最大JMX连接数 
  store-metrics-task:
    community:
      broker-metrics-enabled: true # 社区部分broker metrics信息收集开关, 关闭之后metrics信息将不会进行收集及写DB
      topic-metrics-enabled: true # 社区部分topic的metrics信息收集开关, 关闭之后metrics信息将不会进行收集及写DB
    didi:
      app-topic-metrics-enabled: false # 滴滴埋入的指标, 社区AK不存在该指标，因此默认关闭
      topic-request-time-metrics-enabled: false # 滴滴埋入的指标, 社区AK不存在该指标，因此默认关闭
      topic-throttled-metrics: false # 滴滴埋入的指标, 社区AK不存在该指标，因此默认关闭
    save-days: 7 #指标在DB中保持的天数，-1表示永久保存，7表示保存近7天的数据

# 任务相关的开关
task:
  op:
    sync-topic-enabled: false # 未落盘的Topic定期同步到DB中

account: # ldap相关的配置, 社区版本暂时支持不够完善，可以先忽略，欢迎贡献代码对这块做优化
  ldap:

kcm: # 集群升级部署相关的功能，需要配合夜莺及S3进行使用，这块我们后续专门补充一个文档细化一下，牵扯到kcm_script.sh脚本的修改
  enabled: false # 默认关闭
  storage:
    base-url: http://127.0.0.1 # 存储地址
  n9e:
    base-url: http://127.0.0.1:8004 # 夜莺任务中心的地址
    user-token: 12345678 # 夜莺用户的token
    timeout: 300 # 集群任务的超时时间，单位秒
    account: root # 集群任务使用的账号
    script-file: kcm_script.sh # 集群任务的脚本

monitor: # 监控告警相关的功能，需要配合夜莺进行使用
  enabled: false # 默认关闭，true就是开启
  n9e:
    nid: 2
    user-token: 1234567890
    mon:
      # 夜莺 mon监控服务 地址
      base-url: http://127.0.0.1:8032
    sink:
      # 夜莺 transfer上传服务 地址
      base-url: http://127.0.0.1:8006
    rdb:
      # 夜莺 rdb资源服务 地址
      base-url: http://127.0.0.1:80

# enabled: 表示是否开启监控告警的功能, true: 开启, false: 不开启
# n9e.nid: 夜莺的节点ID
# n9e.user-token: 用户的密钥，在夜莺的个人设置中
# n9e.mon.base-url: 监控地址
# n9e.sink.base-url: 数据上报地址
# n9e.rdb.base-url: 用户资源中心地址

notify: # 通知的功能
  kafka: # 默认通知发送到kafka的指定Topic中
    cluster-id: 95 # Topic的集群ID
    topic-name: didi-kafka-notify # Topic名称
  order: # 部署的KM的地址
    detail-url: http://127.0.0.1
```
