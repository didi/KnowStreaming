# Kafka-GroupCoordinator 详解

[TOC]

## 1、GroupCoordinator介绍

### 1.1、功能介绍

###  1.2、相关请求

```java
// 消费offset提交 & 消费offset获取
case ApiKeys.OFFSET_COMMIT => handleOffsetCommitRequest(request)
case ApiKeys.OFFSET_FETCH => handleOffsetFetchRequest(request)

// 获取coordinator, 包括group-coordinator 和 transaction-coordinator
case ApiKeys.FIND_COORDINATOR => handleFindCoordinatorRequest(request)

case ApiKeys.JOIN_GROUP => handleJoinGroupRequest(request)
case ApiKeys.HEARTBEAT => handleHeartbeatRequest(request)
case ApiKeys.LEAVE_GROUP => handleLeaveGroupRequest(request)
case ApiKeys.SYNC_GROUP => handleSyncGroupRequest(request)
case ApiKeys.DESCRIBE_GROUPS => handleDescribeGroupRequest(request)
case ApiKeys.LIST_GROUPS => handleListGroupsRequest(request)
case ApiKeys.OFFSET_FOR_LEADER_EPOCH => handleOffsetForLeaderEpochRequest(request)
case ApiKeys.DELETE_GROUPS => handleDeleteGroupsRequest(request)
case ApiKeys.OFFSET_DELETE => handleOffsetDeleteRequest(request)



```

## 2、
