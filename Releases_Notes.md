
## v3.4.0



**问题修复**
- [Bugfix]修复 Overview 指标文案错误的错误 ([#1190](https://github.com/didi/KnowStreaming/issues/1190))
- [Bugfix]修复删除 Kafka 集群后，Connect 集群任务出现 NPE 问题 ([#1129](https://github.com/didi/KnowStreaming/issues/1129))
- [Bugfix]修复在 Ldap 登录时，设置 auth-user-registration: false 会导致空指针的问题 ([#1117](https://github.com/didi/KnowStreaming/issues/1117))
- [Bugfix]修复 Ldap 登录，调用 user.getId() 出现 NPE 的问题 ([#1108](https://github.com/didi/KnowStreaming/issues/1108))
- [Bugfix]修复前端新增角色失败等问题 ([#1107](https://github.com/didi/KnowStreaming/issues/1107))
- [Bugfix]修复 ZK 四字命令解析错误的问题
- [Bugfix]修复 zk standalone 模式下，状态获取错误的问题
- [Bugfix]修复 Broker 元信息解析方法未调用导致接入集群失败的问题 ([#993](https://github.com/didi/KnowStreaming/issues/993))
- [Bugfix]修复 ConsumerAssignment 类型转换错误的问题
- [Bugfix]修复对 Connect 集群的 clusterUrl 的动态更新导致配置不生效的问题 ([#1079](https://github.com/didi/KnowStreaming/issues/1079))
- [Bugfix]修复消费组不支持重置到最旧 Offset 的问题 ([#1059](https://github.com/didi/KnowStreaming/issues/1059))
- [Bugfix]后端增加查看 User 密码的权限点 ([#1095](https://github.com/didi/KnowStreaming/issues/1095))
- [Bugfix]修复 Connect-JMX 端口维护信息错误的问题 ([#1146](https://github.com/didi/KnowStreaming/issues/1146))
- [Bugfix]修复系统管理子应用无法正常启动的问题 ([#1167](https://github.com/didi/KnowStreaming/issues/1167))
- [Bugfix]修复 Security 模块，权限点缺失问题 ([#1069](https://github.com/didi/KnowStreaming/issues/1069)), ([#1154](https://github.com/didi/KnowStreaming/issues/1154))
- [Bugfix]修复 Connect-Worker Jmx 不生效的问题 ([#1067](https://github.com/didi/KnowStreaming/issues/1067))
- [Bugfix]修复权限 ACL 管理中，消费组列表展示错误的问题 ([#1037](https://github.com/didi/KnowStreaming/issues/1037))
- [Bugfix]修复 Connect 模块没有默认勾选指标的问题（[#1022](https://github.com/didi/KnowStreaming/issues/1022)）
- [Bugfix]修复 es 索引 create/delete 死循环的问题 ([#1021](https://github.com/didi/KnowStreaming/issues/1021))
- [Bugfix]修复 Connect-GroupDescription 解析失败的问题 ([#1015](https://github.com/didi/KnowStreaming/issues/1015))
- [Bugfix]修复 Prometheus 开放接口中，Partition 指标 tag 缺失的问题 ([#1014](https://github.com/didi/KnowStreaming/issues/1014))
- [Bugfix]修复 Topic 消息展示，offset 为 0 不显示的问题 ([#1192](https://github.com/didi/KnowStreaming/issues/1192))
- [Bugfix]修复重置offset接口调用过多问题
- [Bugfix]Connect 提交任务变更为只保存用户修改的配置，并修复 JSON 模式下配置展示不全的问题 ([#1158](https://github.com/didi/KnowStreaming/issues/1158))
- [Bugfix]修复消费组 Offset 重置后，提示重置成功，但是前端不刷新数据，Offset 无变化的问题 ([#1090](https://github.com/didi/KnowStreaming/issues/1090))
- [Bugfix]修复未勾选系统管理查看权限，但是依然可以查看系统管理的问题 ([#1105](https://github.com/didi/KnowStreaming/issues/1105))


**产品优化**
- [Optimize]补充接入集群时，可选的 Kafka 版本列表 ([#1204](https://github.com/didi/KnowStreaming/issues/1204))
- [Optimize]GroupTopic 信息修改为实时获取 ([#1196](https://github.com/didi/KnowStreaming/issues/1196))
- [Optimize]增加 AdminClient 观测信息 ([#1111](https://github.com/didi/KnowStreaming/issues/1111))
- [Optimize]增加 Connector 运行状态指标 ([#1110](https://github.com/didi/KnowStreaming/issues/1110))
- [Optimize]统一 DB 元信息更新格式 ([#1127](https://github.com/didi/KnowStreaming/issues/1127)), ([#1125](https://github.com/didi/KnowStreaming/issues/1125)), ([#1006](https://github.com/didi/KnowStreaming/issues/1006))
- [Optimize]日志输出增加支持 MDC，方便用户在 logback.xml 中 json 格式化日志 ([#1032](https://github.com/didi/KnowStreaming/issues/1032))
- [Optimize]Jmx 相关日志优化 ([#1082](https://github.com/didi/KnowStreaming/issues/1082))
- [Optimize]Topic-Partitions增加主动超时功能 ([#1076](https://github.com/didi/KnowStreaming/issues/1076))
- [Optimize]Topic-Messages页面后端增加按照Partition和Offset纬度的排序 ([#1075](https://github.com/didi/KnowStreaming/issues/1075))
- [Optimize]Connect-JSON模式下的JSON格式和官方API的格式不一致 ([#1080](https://github.com/didi/KnowStreaming/issues/1080)), ([#1153](https://github.com/didi/KnowStreaming/issues/1153)), ([#1192](https://github.com/didi/KnowStreaming/issues/1192))
- [Optimize]登录页面展示的 star 数量修改为最新的数量
- [Optimize]Group 列表的 maxLag 指标调整为实时获取 ([#1074](https://github.com/didi/KnowStreaming/issues/1074))
- [Optimize]Connector增加重启、编辑、删除等权限点 ([#1066](https://github.com/didi/KnowStreaming/issues/1066)), ([#1147](https://github.com/didi/KnowStreaming/issues/1147))
- [Optimize]优化 pom.xml 中，KS版本的标签名
- [Optimize]优化集群Brokers中, Controller显示存在延迟的问题 ([#1162](https://github.com/didi/KnowStreaming/issues/1162))
- [Optimize]bump jackson version to 2.13.5
- [Optimize]权限新增 ACL，自定义权限配置，资源 TransactionalId 优化 ([#1192](https://github.com/didi/KnowStreaming/issues/1192))
- [Optimize]Connect 样式优化
- [Optimize]消费组详情控制数据实时刷新


**功能新增**
- [Feature]新增删除 Group 或 GroupOffset 功能 ([#1064](https://github.com/didi/KnowStreaming/issues/1064)), ([#1084](https://github.com/didi/KnowStreaming/issues/1084)), ([#1040](https://github.com/didi/KnowStreaming/issues/1040)), ([#1144](https://github.com/didi/KnowStreaming/issues/1144))
- [Feature]增加 Truncate 数据功能 ([#1062](https://github.com/didi/KnowStreaming/issues/1062)), ([#1043](https://github.com/didi/KnowStreaming/issues/1043)), ([#1145](https://github.com/didi/KnowStreaming/issues/1145))
- [Feature]支持指定 Server 的具体 Jmx 端口 ([#965](https://github.com/didi/KnowStreaming/issues/965))


**文档更新**
- [Doc]FAQ 补充 ES 8.x 版本使用说明 ([#1189](https://github.com/didi/KnowStreaming/issues/1189))
- [Doc]补充启动失败的说明 ([#1126](https://github.com/didi/KnowStreaming/issues/1126))
- [Doc]补充 ZK 无数据排查说明 ([#1004](https://github.com/didi/KnowStreaming/issues/1004))
- [Doc]无数据排查文档，补充 ES 集群 Shard 满的异常日志
- [Doc]README 补充页面无数据排查手册链接
- [Doc]补充连接特定 Jmx 端口的说明 ([#965](https://github.com/didi/KnowStreaming/issues/965))
- [Doc]补充 zk_properties 字段的使用说明 ([#1003](https://github.com/didi/KnowStreaming/issues/1003))


---


## v3.3.0

**问题修复**
- 修复 Connect 的 JMX-Port 配置未生效问题;
- 修复 不存在 Connector 时，OverView 页面的数据一直处于加载中的问题;
- 修复 Group 分区信息，分页时展示不全的问题；
- 修复采集副本指标时，参数传递错误的问题；
- 修复用户信息修改后，用户列表会抛出空指针异常的问题；
- 修复 Topic 详情页面，查看消息时，选择分区不生效问题；
- 修复对 ZK 客户端进行配置后不生效的问题；
- 修复 connect 模块，指标中缺少健康巡检项通过数的问题；
- 修复 connect 模块，指标获取方法存在映射错误的问题；
- 修复 connect 模块，max 纬度指标获取错误的问题；
- 修复 Topic 指标大盘 TopN 指标显示信息错误的问题；
- 修复 Broker Similar Config 显示错误的问题；
- 修复解析 ZK 四字命令时，数据类型设置错误导致空指针的问题；
- 修复新增 Topic 时，清理策略选项版本控制错误的问题；
- 修复新接入集群时 Controller-Host 信息不显示的问题；
- 修复 Connector 和 MM2 列表搜索不生效的问题；
- 修复 Zookeeper 页面，Leader 显示存在异常的问题；
- 修复前端打包失败的问题；


**产品优化**
- ZK Overview 页面补充默认展示的指标；
- 统一初始化 ES 索引模版的脚本为 init_es_template.sh，同时新增缺失的 connect 索引模版初始化脚本，去除多余的 replica 和 zookeper 索引模版初始化脚本；
- 指标大盘页面，优化指标筛选操作后，无指标数据的指标卡片由不显示改为显示，并增加无数据的兜底；
- 删除从 ES 读写 replica 指标的相关代码；
- 优化 Topic 健康巡检的日志，明确错误的原因；
- 优化无 ZK 模块时，巡检详情忽略对 ZK 的展示；
- 优化本地缓存大小为可配置；
- Task 模块中的返回中，补充任务的分组信息；
- FAQ 补充 Ldap 的配置说明；
- FAQ 补充接入 Kerberos 认证的 Kafka 集群的配置说明；
- ks_km_kafka_change_record 表增加时间纬度的索引，优化查询性能；
- 优化 ZK 健康巡检的日志，便于问题的排查；

**功能新增**
- 新增基于滴滴 Kafka 的 Topic 复制功能（需使用滴滴 Kafka 才可具备该能力）；
- Topic 指标大盘，新增 Topic 复制相关的指标；
- 新增基于 TestContainers 的单测；


**Kafka MM2 Beta版 (v3.3.0版本新增发布)**
- MM2 任务的增删改查；
- MM2 任务的指标大盘；
- MM2 任务的健康状态；

---


## v3.2.0

**问题修复**
- 修复健康巡检结果更新至 DB 时，出现死锁问题；
- 修复 KafkaJMXClient 类中，logger错误的问题；
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
- 修复因原AR信息丢失，导致迁移任务一直处于执行中的错误；
- 修复集群 Topic 列表实时数据查询时，出现失败的问题；
- 修复集群 Topic 列表，页面白屏问题；
- 修复副本变更时，因AR数据异常，导致数组访问越界的问题；


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
- 优化 Overview 页面 TopN 查询的流程；


**功能新增**
- 新增页面无数据排查文档；
- 增加 ES 索引删除的功能；
- 支持拆分API服务和Job服务部署；


**Kafka Connect Beta版 (v3.2.0版本新增发布)**
- Connect 集群的纳管；
- Connector 的增删改查；
- Connect 集群 & Connector 的指标大盘；


---


## v3.1.0

**Bug修复**
- 修复重置 Group Offset 的提示信息中，缺少Dead状态也可进行重置的描述；
- 修复新建 Topic 后，立即查看 Topic Messages 信息时，会提示 Topic 不存在的问题；
- 修复副本变更时，优先副本选举未被正常处罚执行的问题；
- 修复 git 目录不存在时，打包不能正常进行的问题；
- 修复 KRaft 模式的 Kafka 集群，JMX PORT 显示 -1 的问题；


**体验优化**
- 优化Cluster、Broker、Topic、Group的健康分为健康状态；
- 去除健康巡检配置中的权重信息；
- 错误提示页面展示优化；
- 前端打包编译依赖默认使用 taobao 镜像；
- 重新设计优化导航栏的 icon ；


**新增**
- 个人头像下拉信息中，新增产品版本信息；
- 多集群列表页面，新增集群健康状态分布信息；


**Kafka ZK 部分 (v3.1.0版本正式发布)**
- 新增 ZK 集群的指标大盘信息；
- 新增 ZK 集群的服务状态概览信息；
- 新增 ZK 集群的服务节点列表信息；
- 新增 Kafka 在 ZK 的存储数据查看功能；
- 新增 ZK 的健康巡检及健康状态计算；



---


## v3.0.1

**Bug修复**
- 修复重置 Group Offset 时，提示信息中缺少 Dead 状态也可进行重置的信息；
- 修复 Ldap 某个属性不存在时，会直接抛出空指针导致登陆失败的问题；
- 修复集群 Topic 列表页，健康分详情信息中，检查时间展示错误的问题；
- 修复更新健康检查结果时，出现死锁的问题；
- 修复 Replica 索引模版错误的问题；
- 修复 FAQ 文档中的错误链接；
- 修复 Broker 的 TopN 指标不存在时，页面数据不展示的问题；
- 修复 Group 详情页，图表时间范围选择不生效的问题；


**体验优化**
- 集群 Group 列表按照 Group 维度进行展示；
- 优化避免因 ES 中该指标不存在，导致日志中出现大量空指针的问题；
- 优化全局 Message & Notification 展示效果；
- 优化 Topic 扩分区名称 & 描述展示；


**新增**
- Broker 列表页面，新增 JMX 是否成功连接的信息；


**ZK 部分(未完全发布)**
- 后端补充 Kafka ZK 指标采集，Kafka ZK 信息获取相关功能；
- 增加本地缓存，避免同一采集周期内 ZK 指标重复采集；
- 增加 ZK 节点采集失败跳过策略，避免不断对存在问题的节点不断尝试；
- 修复 zkAvgLatency 指标转 Long 时抛出异常问题；
- 修复 ks_km_zookeeper 表中，role 字段类型错误问题；

---

## v3.0.0

**Bug修复**
- 修复 Group 指标防重复采集不生效问题
- 修复自动创建 ES 索引模版失败问题
- 修复 Group+Topic 列表中存在已删除Topic的问题
- 修复使用 MySQL-8 ，因兼容问题， start_time 信息为 NULL 时，会导致创建任务失败的问题
- 修复 Group 信息表更新时，出现死锁的问题
- 修复图表补点逻辑与图表时间范围不适配的问题


**体验优化**
- 按照资源类别，拆分健康巡检任务
- 优化 Group 详情页的指标为实时获取
- 图表拖拽排序支持用户级存储
- 多集群列表 ZK 信息展示兼容无 ZK 情况
- Topic 详情消息预览支持复制功能
- 部分内容大数字支持千位分割符展示


**新增**
- 集群信息中，新增 Zookeeper 客户端配置字段
- 集群信息中，新增 Kafka 集群运行模式字段
- 新增 docker-compose 的部署方式

---

## v3.0.0-beta.3

**文档**
- FAQ 补充权限识别失败问题的说明
- 同步更新文档，保持与官网一致


**Bug修复**
- Offset 信息获取时，过滤掉无 Leader 的分区
- 升级 oshi-core 版本至 5.6.1 版本，修复 Windows 系统获取系统指标失败问题
- 修复 JMX 连接被关闭后，未进行重建的问题
- 修复因 DB 中 Broker 信息不存在导致 TotalLogSize 指标获取时抛空指针问题
- 修复 dml-logi.sql 中，SQL 注释错误的问题
- 修复 startup.sh 中，识别操作系统类型错误的问题
- 修复配置管理页面删除配置失败的问题
- 修复系统管理应用文件引用路径
- 修复 Topic Messages 详情提示信息点击跳转 404 的问题
- 修复扩副本时，当前副本数不显示问题


**体验优化**
- Topic-Messages 页面，增加返回数据的排序以及按照Earliest/Latest的获取方式
- 优化 GroupOffsetResetEnum 类名为 OffsetTypeEnum，使得类名含义更准确
- 移动 KafkaZKDAO 类，及 Kafka Znode 实体类的位置，使得 Kafka Zookeeper DAO 更加内聚及便于识别
- 后端补充 Overview 页面指标排序的功能
- 前端 Webpack 配置优化
- Cluster Overview 图表取消放大展示功能
- 列表页增加手动刷新功能
- 接入/编辑集群，优化 JMX-PORT，Version 信息的回显，优化JMX信息的展示
- 提高登录页面图片展示清晰度
- 部分样式和文案优化

---

## v3.0.0-beta.2

**文档**
- 新增登录系统对接文档
- 优化前端工程打包构建部分文档说明
- FAQ补充KnowStreaming连接特定JMX IP的说明


**Bug修复**
- 修复logi_security_oplog表字段过短，导致删除Topic等操作无法记录的问题
- 修复ES查询时，抛java.lang.NumberFormatException: For input string: "{"value":0,"relation":"eq"}" 问题
- 修复LogStartOffset和LogEndOffset指标单位错误问题
- 修复进行副本变更时，旧副本数为NULL的问题
- 修复集群Group列表，在第二页搜索时，搜索时返回的分页信息错误问题
- 修复重置Offset时，返回的错误信息提示不一致的问题
- 修复集群查看，系统查看，LoadRebalance等页面权限点缺失问题
- 修复查询不存在的Topic时，错误信息提示不明显的问题
- 修复Windows用户打包前端工程报错的问题
- package-lock.json锁定前端依赖版本号，修复因依赖自动升级导致打包失败等问题
- 系统管理子应用，补充后端返回的Code码拦截，解决后端接口返回报错不展示的问题
- 修复用户登出后，依旧可以访问系统的问题
- 修复巡检任务配置时，数值显示错误的问题
- 修复Broker/Topic Overview 图表和图表详情问题
- 修复Job扩缩副本任务明细数据错误的问题
- 修复重置Offset时，分区ID，Offset数值无限制问题
- 修复扩缩/迁移副本时，无法选中Kafka系统Topic的问题
- 修复Topic的Config页面，编辑表单时不能正确回显当前值的问题
- 修复Broker Card返回数据后依旧展示加载态的问题



**体验优化**
- 优化默认用户密码为 admin/admin
- 缩短新增集群后，集群信息加载的耗时
- 集群Broker列表，增加Controller角色信息
- 副本变更任务结束后，增加进行优先副本选举的操作
- Task模块任务分为Metrics、Common、Metadata三类任务，每类任务配备独立线程池，减少对Job模块的线程池，以及不同类任务之间的相互影响
- 删除代码中存在的多余无用文件
- 自动新增ES索引模版及近7天索引，减少用户搭建时需要做的事项
- 优化前端工程打包流程
- 优化登录页文案，页面左侧栏内容，单集群详情样式，Topic列表趋势图等
- 首次进入Broker/Topic图表详情时，进行预缓存数据从而优化体验
- 优化Topic详情Partition Tab的展示
- 多集群列表页增加编辑功能
- 优化副本变更时，迁移时间支持分钟级别粒度
- logi-security版本升级至2.10.13
- logi-elasticsearch-client版本升级至1.0.24


**能力提升**
- 支持Ldap登录认证

---

## v3.0.0-beta.1

**文档**
- 新增Task模块说明文档
- FAQ补充 `Specified key was too long; max key length is 767 bytes ` 错误说明
- FAQ补充 `出现ESIndexNotFoundException报错` 错误说明


**Bug修复**
- 修复 Consumer 点击 Stop 未停止检索的问题
- 修复创建/编辑角色权限报错问题
- 修复多集群管理/单集群详情均衡卡片状态错误问题
- 修复版本列表未排序问题
- 修复Raft集群Controller信息不断记录问题
- 修复部分版本消费组描述信息获取失败问题
- 修复分区Offset获取失败的日志中，缺少Topic名称信息问题
- 修复GitHub图地址错误，及图裂问题
- 修复Broker默认使用的地址和注释不一致问题
- 修复 Consumer 列表分页不生效问题
- 修复操作记录表operation_methods字段缺少默认值问题
- 修复集群均衡表中move_broker_list字段无效的问题
- 修复KafkaUser、KafkaACL信息获取时，日志一直重复提示不支持问题
- 修复指标缺失时，曲线出现掉底的问题


**体验优化**
- 优化前端构建时间和打包体积，增加依赖打包的分包策略
- 优化产品样式和文案展示
- 优化ES客户端数为可配置
- 优化日志中大量出现的MySQL Key冲突日志


**能力提升**
- 增加周期任务，用于主动创建缺少的ES模版及索引的能力，减少额外的脚本操作
- 增加JMX连接的Broker地址可选择的能力

---

## v3.0.0-beta.0

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

---

## v2.6.0

版本上线时间：2022-01-24

### 能力提升
- 增加简单回退工具类

### 体验优化
- 补充周期任务说明文档
- 补充集群安装部署使用说明文档
- 升级Swagger、SpringFramework、SpringBoot、EChats版本
- 优化Task模块的日志输出
- 优化corn表达式解析失败后退出无任何日志提示问题
- Ldap用户接入时，增加部门及邮箱信息等
- 对Jmx模块，增加连接失败后的回退机制及错误日志优化
- 增加线程池、客户端池可配置
- 删除无用的jmx_prometheus_javaagent-0.14.0.jar
- 优化迁移任务名称
- 优化创建Region时，Region容量信息不能立即被更新问题
- 引入lombok
- 更新视频教程
- 优化kcm_script.sh脚本中的LogiKM地址为可通过程序传入
- 第三方接口及网关接口，增加是否跳过登录的开关
- extends模块相关配置调整为非必须在application.yml中配置

### bug修复
- 修复批量往DB写入空指标数组时报SQL语法异常的问题
- 修复网关增加配置及修改配置时，version不变化问题
- 修复集群列表页，提示框遮挡问题
- 修复对高版本Broker元信息协议解析失败的问题
- 修复Dockerfile执行时提示缺少application.yml文件的问题
- 修复逻辑集群更新时，会报空指针的问题


## v2.5.0

版本上线时间：2021-07-10

### 体验优化
- 更改产品名为LogiKM
- 更新产品图标


## v2.4.1+

版本上线时间：2021-05-21

### 能力提升
- 增加直接增加权限和配额的接口(v2.4.1)
- 增加接口调用可绕过登录的功能(v2.4.1)

### 体验优化
- Tomcat 版本提升至8.5.66(v2.4.2)
- op接口优化，拆分util接口为topic、leader两类接口(v2.4.1)
- 简化Gateway配置的Key长度(v2.4.1)

### bug修复
- 修复页面展示版本错误问题(v2.4.2)


## v2.4.0

版本上线时间：2021-05-18


### 能力提升

- 增加App与Topic自动化审批开关
- Broker元信息中增加Rack信息
- 升级MySQL 驱动，支持MySQL 8+
- 增加操作记录查询界面

### 体验优化

- FAQ告警组说明优化
- 用户手册共享及 独享集群概念优化
- 用户管理界面，前端限制用户删除自己

### bug修复

- 修复op-util类中创建Topic失败的接口
- 周期同步Topic到DB的任务修复，将Topic列表查询从缓存调整为直接查DB
- 应用下线审批失败的功能修复，将权限为0(无权限)的数据进行过滤
- 修复登录及权限绕过的漏洞
- 修复研发角色展示接入集群、暂停监控等按钮的问题


## v2.3.0

版本上线时间：2021-02-08


### 能力提升

- 新增支持docker化部署
- 可指定Broker作为候选controller
- 可新增并管理网关配置
- 可获取消费组状态
- 增加集群的JMX认证  

### 体验优化

- 优化编辑用户角色、修改密码的流程
- 新增consumerID的搜索功能
- 优化“Topic连接信息”、“消费组重置消费偏移”、“修改Topic保存时间”的文案提示
- 在相应位置增加《资源申请文档》链接 

### bug修复

- 修复Broker监控图表时间轴展示错误的问题
- 修复创建夜莺监控告警规则时，使用的告警周期的单位不正确的问题



## v2.2.0

版本上线时间：2021-01-25



### 能力提升

- 优化工单批量操作流程 
- 增加获取Topic75分位/99分位的实时耗时数据
- 增加定时任务，可将无主未落DB的Topic定期写入DB

### 体验优化

- 在相应位置增加《集群接入文档》链接
- 优化物理集群、逻辑集群含义
- 在Topic详情页、Topic扩分区操作弹窗增加展示Topic所属Region的信息
- 优化Topic审批时，Topic数据保存时间的配置流程
- 优化Topic/应用申请、审批时的错误提示文案
- 优化Topic数据采样的操作项文案
- 优化运维人员删除Topic时的提示文案
- 优化运维人员删除Region的删除逻辑与提示文案
- 优化运维人员删除逻辑集群的提示文案
- 优化上传集群配置文件时的文件类型限制条件

### bug修复

- 修复填写应用名称时校验特殊字符出错的问题
- 修复普通用户越权访问应用详情的问题
- 修复由于Kafka版本升级，导致的数据压缩格式无法获取的问题
- 修复删除逻辑集群或Topic之后，界面依旧展示的问题
- 修复进行Leader rebalance操作时执行结果重复提示的问题


## v2.1.0

版本上线时间：2020-12-19



### 体验优化

- 优化页面加载时的背景样式
- 优化普通用户申请Topic权限的流程
- 优化Topic申请配额、申请分区的权限限制
- 优化取消Topic权限的文案提示
- 优化申请配额表单的表单项名称
- 优化重置消费偏移的操作流程
- 优化创建Topic迁移任务的表单内容
- 优化Topic扩分区操作的弹窗界面样式
- 优化集群Broker监控可视化图表样式
- 优化创建逻辑集群的表单内容
- 优化集群安全协议的提示文案

### bug修复

- 修复偶发性重置消费偏移失败的问题




