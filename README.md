
---

![kafka-manager-logo](./docs/assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

## 产品简介
滴滴Logi-KafkaManager脱胎于滴滴内部多年的Kafka运营实践经验，是面向Kafka用户、Kafka运维人员打造的共享多租户Kafka云平台。专注于Kafka运维管控、监控告警、资源治理等核心场景，经历过大规模集群、海量大数据的考验。内部满意度高达90%的同时，还与多家知名企业达成商业化合作。

### 快速体验地址
- 体验地址 http://117.51.146.109:8080  账号密码 admin/admin

### 核心优势
- **高效的问题定位**：监控多项核心指标，统计不同分位数据，提供种类丰富的指标监控报表，帮助用户、运维人员快速高效定位问题
- **便捷的集群运维**：按照Region定义集群资源划分单位，将逻辑集群根据保障等级划分。在方便资源隔离、提高扩展能力的同时，实现对服务端的强管控
- **专业的资源治理**：基于滴滴内部多年运营实践，沉淀资源治理方法，建立健康分体系。针对Topic分区热点、分区不足等高频常见问题，实现资源治理专家化
- **友好的运维生态**：与滴滴夜莺监控告警系统打通，集成监控告警、集群部署、集群升级等能力。形成运维生态，凝练专家服务，使运维更高效

### 体验地图
相比较于同类产品的用户视角单一（大多为管理员视角），滴滴Logi-KafkaManager建立了基于分角色、多场景视角的体验地图。分别是：**用户体验地图、运维体验地图、运营体验地图**

#### 用户体验地图
- **平台租户申请**：申请应用（App）作为Kafka中的用户名，并用 AppID+password作为身份验证
- **集群资源申请**：按需申请、按需使用。可使用平台提供的共享集群，也可为应用申请独立的集群
- **Topic创建**：可根据应用（App）创建Topic，或者申请其他topic的读写权限
- **Topic运维**：Topic数据采样、调整配额、申请分区等操作
- **指标监控**：基于Topic生产消费各环节耗时统计，监控不同分位数性能指标

#### 运维体验地图
- **多版本集群管控**：支持从`0.10.2`到`2.x`版本
- **集群监控**：集群Topic、Broker等多维度历史与实时关键指标查看，建立健康分体系
- **集群运维**：划分部分Broker作为Region，使用Region定义资源划分单位，并按照业务、保障能力区分逻辑集群
- **Broker运维**：包括优先副本选举、leader rebalance等操作
- **Topic运维**：包括创建、查询、扩容、修改属性、迁移、下线等
- **消费组运维**：支持将消费偏移重置至指定时间或指定位置


#### 运营体验地图
- **资源治理**：沉淀资源治理方法。针对Topic分区热点、分区不足等高频常见问题，沉淀资源治理方法，实现资源治理专家化
- **资源审批**：工单体系。Topic创建、调整配额、申请分区等操作，由专业运维人员审批，规范资源使用，保障平台平稳运行
- **账单体系**：成本控制。Topic资源、集群资源按需申请、按需使用。根据流量核算费用，帮助企业建设大数据成本核算体系



### kafka-manager架构图

![kafka-manager-arch](https://img-ys011.didistatic.com/static/dicloudpub/do1_xgDHNDLj2ChKxctSuf72)


## 相关文档

- [kafka-manager 安装手册](docs/install_guide/install_guide_cn.md)
- [kafka-manager 接入集群](docs/user_guide/add_cluster/add_cluster.md)
- [kafka-manager 用户使用手册](docs/user_guide/user_guide_cn.md)
- [kafka-manager FAQ](docs/user_guide/faq.md)

## 钉钉交流群

![dingding_group](./docs/assets/images/common/dingding_group.jpg)
  钉钉群ID：32821440
  
## OCE认证
OCE是一个认证机制和交流平台，为Logi-KafkaManager生产用户量身打造，我们会为OCE企业提供更好的技术支持，比如专属的技术沙龙、企业一对一的交流机会、专属的答疑群等，如果贵司Logi-KafkaManager上了生产，[快来加入吧](http://obsuite.didiyun.com/open/openAuth)


## 项目成员

### 内部核心人员

`iceyuhui`、`liuyaguang`、`limengmonty`、`zhangliangmike`、`nullhuangyiming`、`zengqiao`、`eilenexuzhe`、`huangjiaweihjw`、`zhaoyinrui`、`marzkonglingxu`、`joysunchao`


### 外部贡献者

`fangjunyu`、`zhoutaiyang`


## 协议

`kafka-manager`基于`Apache-2.0`协议进行分发和使用，更多信息参见[协议文件](./LICENSE)
