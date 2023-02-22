---
order: 7
title: '7.更新日志'
toc: menu
---

## 7.1、v3.2.0

**问题修复**

- 修复健康巡检结果更新至 DB 时，出现死锁问题；
- 修复 KafkaJMXClient 类中，logger 错误的问题；
- 后端修复 Topic 过期策略在 0.10.1.0 版本能多选的问题，实际应该只能二选一；
- 修复接入集群时，不填写集群配置会报错的问题；
- 升级 spring-context 至 5.3.19 版本，修复安全漏洞；
- 修复 Broker & Topic 修改配置时，多版本兼容配置的版本信息错误的问题；
- 修复 Topic 列表的健康分为健康状态；
- 修复 Broker LogSize 指标存储名称错误导致查询不到的问题；
- 修复 Prometheus 中，缺少 Group 部分指标的问题；
- 修复因缺少健康状态指标导致集群数错误的问题；
- 修复后台任务记录操作日志时，因缺少操作用户信息导致出现异常的问题；
- 修复 Replica 指标查询时，DSL 错误的问题；
- 关闭 errorLogger，修复错误日志重复输出的问题；
- 修复系统管理更新用户信息失败的问题；

**产品优化**

- 优化健康巡检为按照资源维度多线程并发处理；
- 统一日志输出格式，并优化部分输出的日志；
- 优化 ZK 四字命令结果解析过程中，容易引起误解的 WARN 日志；
- 优化 Zookeeper 详情中，目录结构的搜索文案；
- 优化线程池的名称，方便第三方系统进行相关问题的分析；
- 去除 ESClient 的并发访问控制，降低 ESClient 创建数及提升利用率；
- 优化 Topic Messages 抽屉文案；
- 优化 ZK 健康巡检失败时的错误日志信息；
- 提高 Offset 信息获取的超时时间，降低并发过高时出现请求超时的概率；
- 优化 Topic & Partition 元信息的更新策略，降低对 DB 连接的占用；
- 优化 Sonar 代码扫码问题；
- 优化分区 Offset 指标的采集；
- 优化前端图表相关组件逻辑;
- 优化产品主题色;
- Consumer 列表刷新按钮新增 hover 提示；
- 优化配置 Topic 的消息大小时的测试弹框体验；

**功能新增**

- 新增页面无数据排查文档；
- 增加 ES 索引删除的功能；

**Kafka Connect Beta 版 (v3.2.0 版本新增发布)**

- Connect 集群的纳管；
- Connector 的增删改查；
- Connect 集群 & Connector 的指标大盘；

## 7.2、v3.1.0

**Bug 修复**

- 修复重置 Group Offset 的提示信息中，缺少 Dead 状态也可进行重置的描述；
- 修复新建 Topic 后，立即查看 Topic Messages 信息时，会提示 Topic 不存在的问题；
- 修复副本变更时，优先副本选举未被正常处罚执行的问题；
- 修复 git 目录不存在时，打包不能正常进行的问题；
- 修复 KRaft 模式的 Kafka 集群，JMX PORT 显示 -1 的问题；

**体验优化**

- 优化 Cluster、Broker、Topic、Group 的健康分为健康状态；
- 去除健康巡检配置中的权重信息；
- 错误提示页面展示优化；
- 前端打包编译依赖默认使用 taobao 镜像；

**新增**

- 个人头像下拉信息中，新增产品版本信息；
- 多集群列表页面，新增集群健康状态分布信息；

**Kafka ZK 部分 (v3.1.0 版本正式发布)**

- 新增 ZK 集群的指标大盘信息；
- 新增 ZK 集群的服务状态概览信息；
- 新增 ZK 集群的服务节点列表信息；
- 新增 Kafka 在 ZK 的存储数据查看功能；
- 新增 ZK 的健康巡检及健康状态计算；

## 7.3、v3.0.0-beta.2

**文档**

- 新增登录系统对接文档
- 优化前端工程打包构建部分文档说明
- FAQ 补充 KnowStreaming 连接特定 JMX IP 的说明

**Bug 修复**

- 修复 logi_security_oplog 表字段过短，导致删除 Topic 等操作无法记录的问题
- 修复 ES 查询时，抛 java.lang.NumberFormatException: For input string: "{"value":0,"relation":"eq"}" 问题
- 修复 LogStartOffset 和 LogEndOffset 指标单位错误问题
- 修复进行副本变更时，旧副本数为 NULL 的问题
- 修复集群 Group 列表，在第二页搜索时，搜索时返回的分页信息错误问题
- 修复重置 Offset 时，返回的错误信息提示不一致的问题
- 修复集群查看，系统查看，LoadRebalance 等页面权限点缺失问题
- 修复查询不存在的 Topic 时，错误信息提示不明显的问题
- 修复 Windows 用户打包前端工程报错的问题
- package-lock.json 锁定前端依赖版本号，修复因依赖自动升级导致打包失败等问题
- 系统管理子应用，补充后端返回的 Code 码拦截，解决后端接口返回报错不展示的问题
- 修复用户登出后，依旧可以访问系统的问题
- 修复巡检任务配置时，数值显示错误的问题
- 修复 Broker/Topic Overview 图表和图表详情问题
- 修复 Job 扩缩副本任务明细数据错误的问题
- 修复重置 Offset 时，分区 ID，Offset 数值无限制问题
- 修复扩缩/迁移副本时，无法选中 Kafka 系统 Topic 的问题
- 修复 Topic 的 Config 页面，编辑表单时不能正确回显当前值的问题
- 修复 Broker Card 返回数据后依旧展示加载态的问题

**体验优化**

- 优化默认用户密码为 admin/admin
- 缩短新增集群后，集群信息加载的耗时
- 集群 Broker 列表，增加 Controller 角色信息
- 副本变更任务结束后，增加进行优先副本选举的操作
- Task 模块任务分为 Metrics、Common、Metadata 三类任务，每类任务配备独立线程池，减少对 Job 模块的线程池，以及不同类任务之间的相互影响
- 删除代码中存在的多余无用文件
- 自动新增 ES 索引模版及近 7 天索引，减少用户搭建时需要做的事项
- 优化前端工程打包流程
- 优化登录页文案，页面左侧栏内容，单集群详情样式，Topic 列表趋势图等
- 首次进入 Broker/Topic 图表详情时，进行预缓存数据从而优化体验
- 优化 Topic 详情 Partition Tab 的展示
- 多集群列表页增加编辑功能
- 优化副本变更时，迁移时间支持分钟级别粒度
- logi-security 版本升级至 2.10.13
- logi-elasticsearch-client 版本升级至 1.0.24

**能力提升**

- 支持 Ldap 登录认证

## 7.4、v3.0.0-beta.1

**文档**

- 新增 Task 模块说明文档
- FAQ 补充 `Specified key was too long; max key length is 767 bytes ` 错误说明
- FAQ 补充 `出现ESIndexNotFoundException报错` 错误说明

**Bug 修复**

- 修复 Consumer 点击 Stop 未停止检索的问题
- 修复创建/编辑角色权限报错问题
- 修复多集群管理/单集群详情均衡卡片状态错误问题
- 修复版本列表未排序问题
- 修复 Raft 集群 Controller 信息不断记录问题
- 修复部分版本消费组描述信息获取失败问题
- 修复分区 Offset 获取失败的日志中，缺少 Topic 名称信息问题
- 修复 GitHub 图地址错误，及图裂问题
- 修复 Broker 默认使用的地址和注释不一致问题
- 修复 Consumer 列表分页不生效问题
- 修复操作记录表 operation_methods 字段缺少默认值问题
- 修复集群均衡表中 move_broker_list 字段无效的问题
- 修复 KafkaUser、KafkaACL 信息获取时，日志一直重复提示不支持问题
- 修复指标缺失时，曲线出现掉底的问题

**体验优化**

- 优化前端构建时间和打包体积，增加依赖打包的分包策略
- 优化产品样式和文案展示
- 优化 ES 客户端数为可配置
- 优化日志中大量出现的 MySQL Key 冲突日志

**能力提升**

- 增加周期任务，用于主动创建缺少的 ES 模版及索引的能力，减少额外的脚本操作
- 增加 JMX 连接的 Broker 地址可选择的能力

---

## 7.5、v3.0.0-beta.0

**1、多集群管理**

- 增加健康监测体系、关键组件&指标 GUI 展示
- 增加 2.8.x 以上 Kafka 集群接入，覆盖 0.10.x-3.x
- 删除逻辑集群、共享集群、Region 概念

**2、Cluster 管理**

- 增加集群概览信息、集群配置变更记录
- 增加 Cluster 健康分，健康检查规则支持自定义配置
- 增加 Cluster 关键指标统计和 GUI 展示，支持自定义配置
- 增加 Cluster 层 I/O、Disk 的 Load Reblance 功能，支持定时均衡任务（企业版）
- 删除限流、鉴权功能
- 删除 APPID 概念

**3、Broker 管理**

- 增加 Broker 健康分
- 增加 Broker 关键指标统计和 GUI 展示，支持自定义配置
- 增加 Broker 参数配置功能，需重启生效
- 增加 Controller 变更记录
- 增加 Broker Datalogs 记录
- 删除 Leader Rebalance 功能
- 删除 Broker 优先副本选举

**4、Topic 管理**

- 增加 Topic 健康分
- 增加 Topic 关键指标统计和 GUI 展示，支持自定义配置
- 增加 Topic 参数配置功能，可实时生效
- 增加 Topic 批量迁移、Topic 批量扩缩副本功能
- 增加查看系统 Topic 功能
- 优化 Partition 分布的 GUI 展示
- 优化 Topic Message 数据采样
- 删除 Topic 过期概念
- 删除 Topic 申请配额功能

**5、Consumer 管理**

- 优化了 ConsumerGroup 展示形式，增加 Consumer Lag 的 GUI 展示

**6、ACL 管理**

- 增加原生 ACL GUI 配置功能，可配置生产、消费、自定义多种组合权限
- 增加 KafkaUser 功能，可自定义新增 KafkaUser

**7、消息测试（企业版）**

- 增加生产者消息模拟器，支持 Data、Flow、Header、Options 自定义配置（企业版）
- 增加消费者消息模拟器，支持 Data、Flow、Header、Options 自定义配置（企业版）

**8、Job**

- 优化 Job 模块，支持任务进度管理

**9、系统管理**

- 优化用户、角色管理体系，支持自定义角色配置页面及操作权限
- 优化审计日志信息
- 删除多租户体系
- 删除工单流程

## 7.6、v2.6.0

**1、能力提升**

- 增加简单回退工具类

**2、体验优化**

- 补充周期任务说明文档
- 补充集群安装部署使用说明文档
- 升级 Swagger、SpringFramework、SpringBoot、EChats 版本
- 优化 Task 模块的日志输出
- 优化 corn 表达式解析失败后退出无任何日志提示问题
- Ldap 用户接入时，增加部门及邮箱信息等
- 对 Jmx 模块，增加连接失败后的回退机制及错误日志优化
- 增加线程池、客户端池可配置
- 删除无用的 jmx_prometheus_javaagent-0.14.0.jar
- 优化迁移任务名称
- 优化创建 Region 时，Region 容量信息不能立即被更新问题
- 引入 lombok
- 更新视频教程
- 优化 kcm_script.sh 脚本中的 LogiKM 地址为可通过程序传入
- 第三方接口及网关接口，增加是否跳过登录的开关
- extends 模块相关配置调整为非必须在 application.yml 中配置

**3、bug 修复**

- 修复批量往 DB 写入空指标数组时报 SQL 语法异常的问题
- 修复网关增加配置及修改配置时，version 不变化问题
- 修复集群列表页，提示框遮挡问题
- 修复对高版本 Broker 元信息协议解析失败的问题
- 修复 Dockerfile 执行时提示缺少 application.yml 文件的问题
- 修复逻辑集群更新时，会报空指针的问题

## 7.7、v2.5.0

**1、体验优化**

- 修复 bug getAttributes 运行时错误问题
- 修复 "集群概览"下查看最近 7 天"历史流量"的时候，Tooltip 中的时间只包含日期
- 新增了 Linux 下系统启动和关闭的脚本
- "我的审批"列表增加"通过时间"列，并支持按该列排序
- tomcat 依赖包升级到 8.5.72
- 修复 [前端+后端问题] Topic 管理-更多-编辑-备注没有数据回显; 后端接口也没有返回值 问题
