# Kafka服务端—权限控制

[TOC]


资源类型：
- UNKNOWN： 未知
- ANY：任意的资源
- TOPIC：Topic
- GROUP：消费组
- CLUSTER：整个集群
- TRANSACTIONAL_ID：事物ID
- DELEGATION_TOKEN：Token


资源操作：
- UNKNOWN：未知
- ANY：任意的操作
- ALL：所有的操作
- READ：读
- WRITE：写
- CREATE：创建
- DELETE：删除
- ALTER：修改
- DESCRIBE：描述，查看
- CLUSTER_ACTION：集群动作
- DESCRIBE_CONFIGS：查看配置
- ALTER_CONFIGS：修改配置
- IDEMPOTENT_WRITE：幂等写
  

资源书写类型：
- UNKNOWN：未知
- ANY：任意
- MATCH：满足LITERAL、PREFIXED或者*的任意中的一个即可
- LITERAL：全匹配，完全按照原文匹配
- PREFIXED：前缀匹配


认证结果：
- ALLOWED：允许
- DENIED：拒绝
