CREATE DATABASE IF NOT EXISTS `kafka_manager`;

USE `kafka_manager`;

CREATE TABLE `account` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `role` int(16) NOT NULL DEFAULT '0' COMMENT '角色类型, 0:普通用户',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '0标识使用中，-1标识已废弃',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账号表';
INSERT INTO account(username, password, role) VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 2);


CREATE TABLE `alarm_rule` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `alarm_name` varchar(128) NOT NULL DEFAULT '' COMMENT '告警名字',
  `strategy_expressions` text COMMENT '表达式',
  `strategy_filters` text COMMENT '过滤条件',
  `strategy_actions` text COMMENT '响应',
  `principals` varchar(512) NOT NULL DEFAULT '' COMMENT '负责人',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '-1:逻辑删除,  0:关闭,  1:正常',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_alarm_name` (`alarm_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='告警规则表';


CREATE TABLE `broker` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `broker_id` int(11) NOT NULL DEFAULT '-1' COMMENT 'BrokerID',
  `host` varchar(128) NOT NULL DEFAULT '' COMMENT 'Broker主机名',
  `port` int(32) NOT NULL DEFAULT '-1' COMMENT 'Broker端口',
  `timestamp` bigint(128) NOT NULL DEFAULT '-1' COMMENT '启动时间',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态0有效，-1无效',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_broker_id` (`cluster_id`,`broker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Broker信息表';


CREATE TABLE `broker_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `broker_id` int(11) NOT NULL DEFAULT '-1' COMMENT 'BrokerID',
  `bytes_in` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒字节流入',
  `bytes_out` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒字节流出',
  `bytes_rejected` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒被拒绝字节数',
  `messages_in` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒消息数流入',
  `fail_fetch_request` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒消费失败数',
  `fail_produce_request` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒失败生产数',
  `fetch_consumer_request` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒消费请求数',
  `produce_request` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒生产数',
  `request_handler_idl_percent` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '请求处理器繁忙百分比',
  `network_processor_idl_percent` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '网络处理器繁忙百分比',
  `request_queue_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '请求列表大小',
  `response_queue_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '响应列表大小',
  `log_flush_time` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '刷日志时间',
  `total_time_produce_mean` double(53,2) NOT NULL DEFAULT '0.00' COMMENT 'produce请求处理总时间-平均值',
  `total_time_produce_99th` double(53,2) NOT NULL DEFAULT '0.00' COMMENT 'produce请求处理总时间-99分位',
  `total_time_fetch_consumer_mean` double(53,2) NOT NULL DEFAULT '0.00' COMMENT 'fetch请求总时间-平均值',
  `total_time_fetch_consumer_99th` double(53,2) NOT NULL DEFAULT '0.00' COMMENT 'fetch请求总时间-99分位',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_broker_id_gmt_create` (`cluster_id`,`broker_id`,`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='BrokerMetric信息表';


CREATE TABLE `cluster` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '集群ID',
  `cluster_name` varchar(128) NOT NULL DEFAULT '' COMMENT '集群名称',
  `zookeeper` varchar(512) NOT NULL DEFAULT '' COMMENT 'ZK地址',
  `bootstrap_servers` varchar(512) NOT NULL DEFAULT '' COMMENT 'Server地址',
  `kafka_version` varchar(32) NOT NULL DEFAULT '' COMMENT 'Kafka版本',
  `alarm_flag` int(4) NOT NULL DEFAULT '0' COMMENT '0:不开启告警, 1开启告警',
  `security_protocol` varchar(512) NOT NULL DEFAULT '' COMMENT '安全协议',
  `sasl_mechanism` varchar(512) NOT NULL DEFAULT '' COMMENT '安全机制',
  `sasl_jaas_config` varchar(512) NOT NULL DEFAULT '' COMMENT 'Jaas配置',
  `status` int(4) NOT NULL DEFAULT '0' COMMENT '删除标记, 0表示未删除, -1表示删除',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_name` (`cluster_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cluster表';


CREATE TABLE `cluster_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群ID',
  `topic_num` int(11) NOT NULL DEFAULT '0' COMMENT 'Topic数',
  `partition_num` int(11) NOT NULL DEFAULT '0' COMMENT '分区数',
  `broker_num` int(11) NOT NULL DEFAULT '0' COMMENT 'Broker数',
  `bytes_in` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒流入(B)',
  `bytes_out` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒流出(B)',
  `bytes_rejected` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒拒绝(B)',
  `messages_in` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒消息数(条)',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_gmt_create` (`cluster_id`,`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ClusterMetrics信息';


CREATE TABLE `controller` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `broker_id` int(11) NOT NULL DEFAULT '-1' COMMENT 'BrokerId',
  `host` varchar(256) NOT NULL DEFAULT '' COMMENT '主机名',
  `timestamp` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Controller变更时间',
  `version` int(11) NOT NULL DEFAULT '-1' COMMENT 'Controller格式版本',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_broker_id_timestamp` (`cluster_id`,`broker_id`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Controller历史变更记录表';

CREATE TABLE `migration_task` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群ID',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `reassignment_json` text COMMENT '任务参数',
  `real_throttle` bigint(20) NOT NULL DEFAULT '0' COMMENT '实际限流值(B/s)',
  `operator` varchar(128) NOT NULL DEFAULT '' COMMENT '操作人',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '备注说明',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '任务状态',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Topic迁移信息';

CREATE TABLE `operation_history` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `operator` varchar(128) NOT NULL DEFAULT '' COMMENT '操作人',
  `operation` varchar(256) NOT NULL DEFAULT '' COMMENT '操作描述',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作记录表';


CREATE TABLE `order_partition` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `cluster_name` varchar(128) NOT NULL DEFAULT '' COMMENT '集群名称',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `broker_list` varchar(256) NOT NULL DEFAULT '' COMMENT 'Broker列表, 逗号分割',
  `partition_num` int(11) NOT NULL DEFAULT 0 COMMENT '新增分区数',
  `applicant` varchar(128) NOT NULL DEFAULT '' COMMENT '申请人',
  `peak_bytes_in` bigint(20) NOT NULL DEFAULT '0' COMMENT '峰值流量',
  `description` text COMMENT '备注信息',
  `order_status` int(16) NOT NULL DEFAULT '0' COMMENT '工单状态',
  `approver` varchar(128) NOT NULL DEFAULT '' COMMENT '审批人',
  `opinion` varchar(256) NOT NULL DEFAULT '' COMMENT '审批意见',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态，0标识有效，-1无效',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分区申请工单';

CREATE TABLE `order_topic` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `cluster_name` varchar(128) NOT NULL DEFAULT '' COMMENT '集群名称',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `retention_time` bigint(20) NOT NULL DEFAULT '-1' COMMENT '保留时间(ms)',
  `partition_num` int(16) NOT NULL DEFAULT '-1' COMMENT '分区数',
  `replica_num` int(16) NOT NULL DEFAULT '-1' COMMENT '副本数',
  `regions` varchar(128) NOT NULL DEFAULT '' COMMENT 'RegionId列表',
  `brokers` varchar(128) NOT NULL DEFAULT '' COMMENT 'Broker列表',
  `peak_bytes_in` bigint(20) NOT NULL DEFAULT '0' COMMENT '峰值流入流量(KB)',
  `applicant` varchar(128) NOT NULL DEFAULT '' COMMENT '申请人',
  `principals` varchar(256) NOT NULL DEFAULT '' COMMENT '负责人',
  `description` text COMMENT '备注信息',
  `order_status` int(16) NOT NULL DEFAULT '0' COMMENT '工单状态',
  `approver` varchar(128) NOT NULL DEFAULT '' COMMENT '审批人',
  `opinion` varchar(256) NOT NULL DEFAULT '' COMMENT '审批意见',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '状态，0标识有效，-1无效',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Topic申请工单';

CREATE TABLE `region` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `region_name` varchar(128) NOT NULL DEFAULT '' COMMENT 'Region名称',
  `broker_list` varchar(256) NOT NULL DEFAULT '' COMMENT 'Broker列表',
  `level` int(16) NOT NULL DEFAULT '0' COMMENT 'Region重要等级, 0级普通, 1极重要，2级极重要',
  `operator` varchar(45) NOT NULL DEFAULT '' COMMENT '操作人',
  `description` text COMMENT '备注说明',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态，0正常，-1废弃，1容量已满',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_region_name` (`cluster_id`,`region_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Region信息表';

CREATE TABLE `topic` (
  `id` bigint(128) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `applicant` varchar(256) NOT NULL DEFAULT '' COMMENT '申请人',
  `principals` varchar(256) NOT NULL DEFAULT '' COMMENT '负责人',
  `description` text COMMENT '备注信息',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '0标识使用中，-1标识已废弃',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_topic_name` (`cluster_id`,`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Topic信息表';


CREATE TABLE `topic_favorite` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增Id',
  `username` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '删除标记, 0表示未删除, -1表示删除',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_username_cluster_id_topic_name` (`username`,`cluster_id`,`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户收藏的Topic表';

CREATE TABLE `topic_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `messages_in` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒进入消息条数',
  `bytes_in` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒字节流入',
  `bytes_out` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒字节流出',
  `bytes_rejected` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒拒绝字节数',
  `total_produce_requests` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒请求数',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_topic_name_gmt_create` (`cluster_id`,`topic_name`,`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='TopicMetrics表';