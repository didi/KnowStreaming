
---

![logikm_logo](https://user-images.githubusercontent.com/71620349/125024570-9e07a100-e0b3-11eb-8ebc-22e73e056771.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

`LogiKM开源至今备受关注，考虑到开源项目应该更贴合Apache Kafka未来发展方向，经项目组慎重考虑，预计22年5月份将其品牌升级成Know Streaming，届时项目名称和Logo也将统一更新，感谢大家一如既往的支持，敬请期待！`

阅读本README文档，您可以了解到滴滴Logi-KafkaManager的用户群体、产品定位等信息，并通过体验地址，快速体验Kafka集群指标监控与运维管控的全流程。


## 1 产品简介
滴滴Logi-KafkaManager脱胎于滴滴内部多年的Kafka运营实践经验，是面向Kafka用户、Kafka运维人员打造的共享多租户Kafka云平台。专注于Kafka运维管控、监控告警、资源治理等核心场景，经历过大规模集群、海量大数据的考验。内部满意度高达90%的同时，还与多家知名企业达成商业化合作。

### 1.1 快速体验地址

- 体验地址 http://117.51.150.133:8080  账号密码 admin/admin

### 1.2 体验地图
相比较于同类产品的用户视角单一（大多为管理员视角），滴滴Logi-KafkaManager建立了基于分角色、多场景视角的体验地图。分别是：**用户体验地图、运维体验地图、运营体验地图**

#### 1.2.1 用户体验地图
- 平台租户申请&nbsp;&nbsp;：申请应用（App）作为Kafka中的用户名，并用 AppID+password作为身份验证
- 集群资源申请&nbsp;&nbsp;：按需申请、按需使用。可使用平台提供的共享集群，也可为应用申请独立的集群
- Topic&nbsp;&nbsp;&nbsp;申&nbsp;&nbsp;&nbsp;请&nbsp;&nbsp;：可根据应用（App）创建Topic，或者申请其他topic的读写权限
- Topic&nbsp;&nbsp;&nbsp;运&nbsp;&nbsp;&nbsp;维&nbsp;&nbsp;：Topic数据采样、调整配额、申请分区等操作
- 指&nbsp;&nbsp;&nbsp;标&nbsp;&nbsp;监&nbsp;&nbsp;&nbsp;控&nbsp;&nbsp;：基于Topic生产消费各环节耗时统计，监控不同分位数性能指标
- 消&nbsp;费&nbsp;组&nbsp;运&nbsp;维&nbsp;：支持将消费偏移重置至指定时间或指定位置

#### 1.2.2 运维体验地图
- 多版本集群管控&nbsp;&nbsp;：支持从`0.10.2`到`2.x`版本
- 集&nbsp;&nbsp;&nbsp;&nbsp;群&nbsp;&nbsp;&nbsp;&nbsp;监&nbsp;&nbsp;&nbsp;控&nbsp;&nbsp;：集群Topic、Broker等多维度历史与实时关键指标查看，建立健康分体系
- 集&nbsp;&nbsp;&nbsp;&nbsp;群&nbsp;&nbsp;&nbsp;&nbsp;运&nbsp;&nbsp;&nbsp;维&nbsp;&nbsp;：划分部分Broker作为Region，使用Region定义资源划分单位，并按照业务、保障能力区分逻辑集群
- Broker&nbsp;&nbsp;&nbsp;&nbsp;运&nbsp;&nbsp;&nbsp;&nbsp;维&nbsp;&nbsp;：包括优先副本选举等操作
- Topic&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运&nbsp;&nbsp;&nbsp;&nbsp;维&nbsp;&nbsp;：包括创建、查询、扩容、修改属性、迁移、下线等


#### 1.2.3 运营体验地图
- 资&nbsp;&nbsp;源&nbsp;&nbsp;治&nbsp;&nbsp;理&nbsp;&nbsp;：沉淀资源治理方法。针对Topic分区热点、分区不足等高频常见问题，沉淀资源治理方法，实现资源治理专家化
- 资&nbsp;&nbsp;源&nbsp;&nbsp;审&nbsp;&nbsp;批&nbsp;&nbsp;：工单体系。Topic创建、调整配额、申请分区等操作，由专业运维人员审批，规范资源使用，保障平台平稳运行
- 账&nbsp;&nbsp;单&nbsp;&nbsp;体&nbsp;&nbsp;系&nbsp;&nbsp;：成本控制。Topic资源、集群资源按需申请、按需使用。根据流量核算费用，帮助企业建设大数据成本核算体系

### 1.3 核心优势
- 高&nbsp;效&nbsp;的&nbsp;问&nbsp;题&nbsp;定&nbsp;位&nbsp;&nbsp;：监控多项核心指标，统计不同分位数据，提供种类丰富的指标监控报表，帮助用户、运维人员快速高效定位问题
- 便&nbsp;捷&nbsp;的&nbsp;集&nbsp;群&nbsp;运&nbsp;维&nbsp;&nbsp;：按照Region定义集群资源划分单位，将逻辑集群根据保障等级划分。在方便资源隔离、提高扩展能力的同时，实现对服务端的强管控
- 专&nbsp;业&nbsp;的&nbsp;资&nbsp;源&nbsp;治&nbsp;理&nbsp;&nbsp;：基于滴滴内部多年运营实践，沉淀资源治理方法，建立健康分体系。针对Topic分区热点、分区不足等高频常见问题，实现资源治理专家化
- 友&nbsp;好&nbsp;的&nbsp;运&nbsp;维&nbsp;生&nbsp;态&nbsp;&nbsp;：与滴滴夜莺监控告警系统打通，集成监控告警、集群部署、集群升级等能力。形成运维生态，凝练专家服务，使运维更高效

### 1.4 滴滴Logi-KafkaManager架构图

![kafka-manager-arch](https://img-ys011.didistatic.com/static/dicloudpub/do1_xgDHNDLj2ChKxctSuf72)


## 2 相关文档

### 2.1 产品文档
- [滴滴LogiKM 安装手册](docs/install_guide/install_guide_cn.md)
- [滴滴LogiKM 接入集群](docs/user_guide/add_cluster/add_cluster.md)
- [滴滴LogiKM 用户使用手册](docs/user_guide/user_guide_cn.md)
- [滴滴LogiKM FAQ](docs/user_guide/faq.md)

### 2.2 社区文章
- [滴滴云官网产品介绍](https://www.didiyun.com/production/logi-KafkaManager.html)
- [7年沉淀之作--滴滴Logi日志服务套件](https://mp.weixin.qq.com/s/-KQp-Qo3WKEOc9wIR2iFnw)
- [滴滴LogiKM 一站式Kafka监控与管控平台](https://mp.weixin.qq.com/s/9qSZIkqCnU6u9nLMvOOjIQ)
- [滴滴LogiKM 开源之路](https://xie.infoq.cn/article/0223091a99e697412073c0d64)
- [滴滴LogiKM 系列视频教程](https://space.bilibili.com/442531657/channel/seriesdetail?sid=571649)
- [kafka最强最全知识图谱](https://www.szzdzhp.com/kafka/)
- [滴滴LogiKM新用户入门系列文章专栏 --石臻臻](https://www.szzdzhp.com/categories/LogIKM/)
- [kafka实践（十五）：滴滴开源Kafka管控平台 LogiKM研究--A叶子叶来](https://blog.csdn.net/yezonggang/article/details/113106244)
- [基于云原生应用管理平台Rainbond安装 滴滴LogiKM](https://www.rainbond.com/docs/opensource-app/logikm/?channel=logikm)

## 3 滴滴Logi开源用户交流群

![image](https://user-images.githubusercontent.com/5287750/111266722-e531d800-8665-11eb-9242-3484da5a3099.png)  

想跟各个大佬交流Kafka Es 等中间件/大数据相关技术请 加微信进群。

微信加群：添加<font color=red>mike_zhangliang</font>、<font color=red>danke-xie</font>的微信号备注Logi加群或关注公众号 云原生可观测性 回复 "Logi加群"

## 4 知识星球

<img width="447" alt="image" src="https://user-images.githubusercontent.com/71620349/147314042-843a371a-48c0-4d9a-a65e-ca40236f3300.png">

<br>
<center>
  ✅我们正在组建国内最大最权威的
</center>
<br>
<center>
<font color=red size=5><b>【Kafka中文社区】</b></font>
</center>
  
在这里你可以结交各大互联网Kafka大佬以及近2000+Kafka爱好者，一起实现知识共享，实时掌控最新行业资讯，期待您的加入中～https://z.didi.cn/5gSF9  

<font color=red size=5>有问必答~！  </font>

<font color=red size=5>互动有礼~！  </font>

PS:提问请尽量把问题一次性描述清楚，并告知环境信息情况哦～！如使用版本、操作步骤、报错/警告信息等，方便大V们快速解答～

## 5 项目成员

### 5.1 内部核心人员

`iceyuhui`、`liuyaguang`、`limengmonty`、`zhangliangmike`、`zhaoqingrong`、`xiepeng`、`nullhuangyiming`、`zengqiao`、`eilenexuzhe`、`huangjiaweihjw`、`zhaoyinrui`、`marzkonglingxu`、`joysunchao`、`石臻臻`


### 5.2 外部贡献者

`fangjunyu`、`zhoutaiyang`


## 6 协议

`LogiKM`基于`Apache-2.0`协议进行分发和使用，更多信息参见[协议文件](./LICENSE)
